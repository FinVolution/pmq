package com.ppdai.infrastructure.mq.biz.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.PortalTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.DbNodeRepository;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;

/**
 * @author dal-generator
 */
@Service
// @Qualifier("dbNodeServiceImpl")
public class DbNodeServiceImpl extends AbstractBaseService<DbNodeEntity>
		implements CacheUpdateService, DbNodeService, TimerService, PortalTimerService {
	private Logger log = LoggerFactory.getLogger(DbNodeServiceImpl.class);
	@Autowired
	private DbNodeRepository dbNodeRepository;
	@Autowired
	private SoaConfig soaConfig;
	protected volatile boolean isRunning = true;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private AtomicBoolean updateFlag = new AtomicBoolean(false);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("DbNodeService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	protected Map<String, Boolean> dbCreated = new ConcurrentHashMap<>();
	private volatile int minIdle = 0;
	private volatile int maxActive = 0;
	private AtomicLong lastVersion = new AtomicLong(0);
	protected volatile boolean isPortal = false;
	private DataSourceFactory dataSourceFactory;

	public static interface DataSourceFactory {
		DruidDataSource createDataSource();
	}

	protected void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}

	@PostConstruct
	protected void init() {
		super.setBaseRepository(dbNodeRepository);
		dataSourceFactory = new DataSourceFactory() {
			@Override
			public DruidDataSource createDataSource() {
				// TODO Auto-generated method stub
				return new DruidDataSource();
			}
		};
		start();
		minIdle = soaConfig.getInitDbCount();
		maxActive = soaConfig.getMaxDbCount();
		registerDbConfigChanged();
		

	}

	private void registerDbConfigChanged() {
		soaConfig.registerChanged(new Runnable() {
			@Override
			public void run() {
				updateDbProperties();
			}
		});
	}

	protected void updateDbProperties() {
		if (minIdle != soaConfig.getInitDbCount() && maxActive != soaConfig.getMaxDbCount()) {
			log.warn("dataSource_MinIdle_changed,from {} to {}", minIdle, soaConfig.getInitDbCount());
			log.warn("dataSource_MaxActive_changed,from {} to {}", maxActive, soaConfig.getMaxDbCount());
			maxActive = soaConfig.getMaxDbCount();
			minIdle = soaConfig.getInitDbCount();
			cacheDataMap.get().values().forEach(dataSource -> {
				if (dataSource instanceof DruidDataSource) {
					((DruidDataSource) dataSource).setMinIdle(soaConfig.getInitDbCount());
					((DruidDataSource) dataSource).setMaxActive(soaConfig.getMaxDbCount());
				}
			});
		} else if (minIdle != soaConfig.getInitDbCount()) {
			log.warn("dataSource_MinIdle_changed,from {} to {}", minIdle, soaConfig.getInitDbCount());
			minIdle = soaConfig.getInitDbCount();
			cacheDataMap.get().values().forEach(dataSource -> {
				if (dataSource instanceof DruidDataSource) {
					((DruidDataSource) dataSource).setMinIdle(soaConfig.getInitDbCount());
				}
			});
		} else if (maxActive != soaConfig.getMaxDbCount()) {
			log.warn("dataSource_MaxActive_changed,from {} to {}", maxActive, soaConfig.getMaxDbCount());
			maxActive = soaConfig.getMaxDbCount();
			cacheDataMap.get().values().forEach(dataSource -> {
				if (dataSource instanceof DruidDataSource) {
					((DruidDataSource) dataSource).setMaxActive(soaConfig.getMaxDbCount());
				}
			});
		}
	}

	protected AtomicReference<Map<String, DataSource>> cacheDataMap = new AtomicReference<>(
			new ConcurrentHashMap<>(20));
	protected AtomicReference<Map<Long, DbNodeEntity>> cacheNodeMap = new AtomicReference<>(
			new ConcurrentHashMap<>(20));
	protected AtomicReference<Map<String, List<DbNodeEntity>>> cacheNodeIpMap = new AtomicReference<>(
			new ConcurrentHashMap<>(20));

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			updateCache();
			executor.execute(() -> {
				while (isRunning) {
					updateCache();
					Util.sleep(soaConfig.getMqDbNodeCacheInterval());
				}
			});
		}
	}

	@Override
	public void updateCache() {
		if (updateFlag.compareAndSet(false, true)) {
			try {
				if (checkChanged()) {
					forceUpdateCache();
				}
			} catch (Exception e) {

			}
			updateFlag.set(false);
		}
	}

	@Override
	public void checkDataSource(DbNodeEntity dbNodeEntity) {
		try {
			// 检查master
			checkMaster(dbNodeEntity);
			checkSlave(dbNodeEntity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void checkSlave(DbNodeEntity dbNodeEntity) throws SQLException {
		// 检查slave
		if (hasSlave(dbNodeEntity)) {
			DruidDataSource dataSource = dataSourceFactory.createDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUsername(dbNodeEntity.getDbUserNameBak());
			dataSource.setPassword(dbNodeEntity.getDbPassBak());
			dataSource.setUrl(getCon(dbNodeEntity, false));
			dataSource.setInitialSize(1);
			dataSource.setMinIdle(0);
			dataSource.setMaxActive(1);
			dataSource.init();
			dataSource = null;
		}
	}

	private void checkMaster(DbNodeEntity dbNodeEntity) throws SQLException {
		DruidDataSource dataSource = dataSourceFactory.createDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername(dbNodeEntity.getDbUserName());
		dataSource.setPassword(dbNodeEntity.getDbPass());
		dataSource.setUrl(getCon(dbNodeEntity, true));
		dataSource.setInitialSize(1);
		dataSource.setMinIdle(0);
		dataSource.setMaxActive(1);
		dataSource.init();
		dataSource = null;
	}

	@Override
	public boolean hasSlave(DbNodeEntity dbNodeEntity) {
		if (Util.isEmpty(dbNodeEntity.getIpBak()) || Util.isEmpty(dbNodeEntity.getDbPassBak())
				|| Util.isEmpty(dbNodeEntity.getDbUserNameBak()) || dbNodeEntity.getPortBak() == 0) {
			return false;
		}
		return true;
	}

	@Override
	public void createDataSource(DbNodeEntity t1) {
		createDataSource(t1, cacheDataMap.get());
	}

	private void createDataSource(DbNodeEntity t1, Map<String, DataSource> dataMap) {
		String key = getConKey(t1, true);
		if (!dbCreated.containsKey(key)) {
			synchronized (DbNodeServiceImpl.class) {
				if (!dbCreated.containsKey(key)) {
					Transaction transaction = Tracer.newTransaction("Timer", "DbNode-createDataSource");
					initDataSource(t1, key, dataMap, true, transaction);
					transaction.complete();
				}
			}
		}
		if (!dataMap.containsKey(key) && cacheDataMap.get().containsKey(key)) {
			dataMap.put(key, cacheDataMap.get().get(key));
		}
		if (hasSlave(t1)) {
			key = getConKey(t1, false);
			if (!dbCreated.containsKey(key)) {
				synchronized (DbNodeServiceImpl.class) {
					if (!dbCreated.containsKey(key)) {
						Transaction transaction = Tracer.newTransaction("Timer", "DbNode-createDataSource");
						initDataSource(t1, key, dataMap, false, transaction);
						transaction.complete();
					}
				}
			}
		}

	}

	String conF = "jdbc:mysql://%s:%s/information_schema?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&rewriteBatchedStatements=true";

	public void initDataSource(DbNodeEntity t1, String key, Map<String, DataSource> dataMap, boolean isMaster,
			Transaction transaction) {
		try {
			// if (soaConfig.isUseDruid())
			{
				DruidDataSource dataSource = dataSourceFactory.createDataSource();
				dataSource.setDriverClassName("com.mysql.jdbc.Driver");
				if (isMaster) {
					dataSource.setUsername(t1.getDbUserName());
					dataSource.setPassword(t1.getDbPass());
				} else {
					dataSource.setUsername(t1.getDbUserNameBak());
					dataSource.setPassword(t1.getDbPassBak());
				}
				// dataSource.setUrl(t1.getConStr());
				dataSource.setUrl(getCon(t1, isMaster));
				dataSource.setInitialSize(soaConfig.getInitDbCount());
				dataSource.setMinIdle(soaConfig.getInitDbCount());
				dataSource.setMaxActive(soaConfig.getMaxDbCount());
				dataSource.setConnectionInitSqls(Arrays.asList("set names utf8mb4;"));
				dataSource.init();
				dbCreated.put(key, true);
				log.info(dataSource.getUrl() + "数据源创建成功！dataSource_created");
				dataMap.put(key, dataSource);
				transaction.setStatus(Transaction.SUCCESS);
			}
		} catch (Exception e) {
			transaction.setStatus(e);
			log.error("initDataSource_error", e);
		}
	}

	private String getCon(DbNodeEntity t1, boolean isMaster) {
		if (isMaster) {
			return String.format(conF, t1.getIp(), t1.getPort());
		} else {
			return String.format(conF, t1.getIpBak(), t1.getPortBak());
		}
	}

//	public String getConKey(long dbNodeId, boolean isMaster) {
//		if (cacheNodeMap.get().containsKey(dbNodeId)) {
//			return getConKey(cacheNodeMap.get().get(dbNodeId), isMaster);
//		}
//		return ""; 
//	}

	protected String getConKey(DbNodeEntity t1, boolean isMaster) {
		if (isMaster) {
			return String.format("%s|%s|%s|%s", t1.getIp(), t1.getPort(), t1.getDbUserName(), t1.getDbPass());
		} else {
			return String.format("%s|%s|%s|%s", t1.getIpBak(), t1.getPortBak(), t1.getDbUserNameBak(),
					t1.getDbPassBak());
		}
	}

//	@Override
//	public String getIpFromKey(String key) {
//		// TODO Auto-generated method stub
//		return key.split("\\|")[0];
//	}

	private Lock cacheLock = new ReentrantLock();

	@Override
	public Map<Long, DbNodeEntity> getCache() {
		// return cacheNodeMap.get();
		Map<Long, DbNodeEntity> rs = cacheNodeMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = cacheNodeMap.get();
				if (rs.size() == 0) {
					updateCache();
					rs = cacheNodeMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public Map<String, List<DbNodeEntity>> getCacheByIp() {
		Map<String, List<DbNodeEntity>> nodeByIp = cacheNodeIpMap.get();
		if (nodeByIp.size() == 0) {
			cacheLock.lock();
			try {
				nodeByIp = cacheNodeIpMap.get();
				if (nodeByIp.size() == 0) {
					updateCache();
					nodeByIp = cacheNodeIpMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return nodeByIp;
	}

	@Override
	public void forceUpdateCache() {
		// dbCreated.clear();
		doForceUpdateCache();
		updateQueueCache();
	}

	private void doForceUpdateCache() {
		Transaction transaction = Tracer.newTransaction("Timer", "DbNode-doUpdateCache");
		try {
			List<DbNodeEntity> data = dbNodeRepository.getAll();
			Map<String, DataSource> dbCache = new ConcurrentHashMap<>(data.size());
			Map<Long, DbNodeEntity> dbNodeCache = new ConcurrentHashMap<>(data.size());
			Map<String, List<DbNodeEntity>> dbNodeIpCache = new ConcurrentHashMap<>(data.size());
			data.forEach(t1 -> {
				try {
					createDataSource(t1, dbCache);
					dbNodeCache.put(t1.getId(), t1);
					if (dbNodeIpCache.containsKey(t1.getIp())) {
						dbNodeIpCache.get(t1.getIp()).add(t1);
					} else {
						List<DbNodeEntity> list = new ArrayList<>();
						list.add(t1);
						dbNodeIpCache.put(t1.getIp(), list);
					}
				} catch (Exception ex) {

				}
			});
			cacheNodeMap.set(dbNodeCache);
			cacheNodeIpMap.set(dbNodeIpCache);
			cacheDataMap.set(dbCache);
			lastVersion.incrementAndGet();
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			log.error("dbNodeCache", e);
			lastUpdateEntity = null;
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
	}

	@Autowired
	private QueueService queueService;

	private void updateQueueCache() {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				queueService.forceUpdateCache();
			}
		});

	}

	protected volatile LastUpdateEntity lastUpdateEntity = null;

	protected boolean checkChanged() {
		Transaction transaction = Tracer.newTransaction("Timer", "DbNode-checkChanged");
		boolean flag = false;
		try {
			LastUpdateEntity temp = dbNodeRepository.getLastUpdate();
			if ((lastUpdateEntity == null && temp != null) || (lastUpdateEntity != null && temp == null)) {
				lastUpdateEntity = temp;
				flag = true;
			} else if (lastUpdateEntity != null && temp != null
					&& (temp.getMaxId() != lastUpdateEntity.getMaxId()
							|| temp.getLastDate().getTime() != lastUpdateEntity.getLastDate().getTime()
							|| temp.getCount() != lastUpdateEntity.getCount())) {
				lastUpdateEntity = temp;
				flag = true;
			}
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
		if (!flag && cacheDataMap.get().size() == 0) {
			log.warn("dbNode数据为空，请注意！");
			return true;
		}
		return flag;
	}

	@PreDestroy
	@Override
	public void stop() {
		isRunning = false;
	}

	@Override
	public DataSource getDataSource(long id, boolean isMaster) {
		Map<String, DataSource> cache = cacheDataMap.get();
		Map<Long, DbNodeEntity> data = cacheNodeMap.get(); 
		// 如果是broker，默认是禁止读写分离
		// 注意不是所示的portal 操作都是读取从库
		if (!isPortal) {
			isMaster = true;
		}
		// 如果没有备份配置，则转换为主库
		if (!isMaster && data.containsKey(id) && !hasSlave(data.get(id))) {
			isMaster = true;
		}
		if (!isMaster && "0".equals(soaConfig.getDbMasterSlave())) {
			isMaster = true;
		}
		Transaction transaction = null;
		if (data.containsKey(id)) {
			if ("1".equals(soaConfig.getDbIpCat())) {
				transaction = Tracer.newTransaction("Db", isMaster ? data.get(id).getIp() : data.get(id).getIpBak());
				transaction.setStatus(Transaction.SUCCESS);
				transaction.complete();
			}
			String key = getConKey(data.get(id), isMaster);
			if (cache.containsKey(key)) {
				// log.info("dbUrl is "+cache.get(key).getUrl());
				return cache.get(key);
			} else {
				key = getConKey(data.get(id), true); 
				if (cache.containsKey(key)) {
					// log.info("dbUrl is "+cache.get(key).getUrl());
					return cache.get(key);
				}
			}
		}
		log.error("dbNode_is_" + id + "_and_datasource_is_null_and_datasources_is_" + JsonUtil.toJson(cache.keySet()));
		return null;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public long getLastVersion() {
//		// TODO Auto-generated method stub
//		return lastVersion.get();
//	}

	@Override
	public Map<String, DataSource> getDataSource() {
		// TODO Auto-generated method stub
		return cacheDataMap.get();
	}

	@Override
	public void startPortal() {
		isPortal = true;
	}

	@Override
	public void stopPortal() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCacheJson() {
		// TODO Auto-generated method stub
		return null;
	}
}
