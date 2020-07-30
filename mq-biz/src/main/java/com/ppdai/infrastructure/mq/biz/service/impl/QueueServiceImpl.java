package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.BrokerTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.PortalTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.QueueRepository;
import com.ppdai.infrastructure.mq.biz.dto.AnalyseDto;
import com.ppdai.infrastructure.mq.biz.dto.ReadWriteEnum;
import com.ppdai.infrastructure.mq.biz.dto.UiResponseHelper;
import com.ppdai.infrastructure.mq.biz.dto.response.UiResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.exceptions.ConcurrentException;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;
import com.ppdai.infrastructure.mq.biz.service.common.MqReadMap;

/**
 * @author dal-generator
 */
@Service
public class QueueServiceImpl extends AbstractBaseService<QueueEntity>
		implements CacheUpdateService, QueueService, TimerService, PortalTimerService, BrokerTimerService {
	private Logger log = LoggerFactory.getLogger(QueueServiceImpl.class);
	@Autowired
	private QueueRepository queueRepository;

	@Autowired
	private DbNodeService dbNodeService;

	@Autowired
	private TopicService topicService;

	@Autowired
	private EmailUtil emailUtil;

	@Autowired
	private Message01Service message01Service;
	private volatile boolean isRunning = true;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private AuditLogService uiAuditLogService;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private AtomicBoolean startPortalFlag = new AtomicBoolean(false);
	private volatile boolean isPortal = true;
	private ThreadPoolExecutor executor = null;
	private ThreadPoolExecutor executorPortal = null;
	private AtomicLong lastVersion = new AtomicLong(0);
	// 记录上次获取最大值的时间
	private volatile long lastMaxTime = System.currentTimeMillis();

	// 记录上次获取最大值的时间
	private volatile long lastUpdateTime = System.currentTimeMillis();

	@PostConstruct
	private void init() {
		super.setBaseRepository(queueRepository);
	}

	private AtomicReference<Map<String, List<QueueEntity>>> topicQueueMap = new AtomicReference<>(
			new ConcurrentHashMap<>());

	private AtomicReference<Map<String, List<QueueEntity>>> topicWriteQueueMap = new AtomicReference<>(
			new ConcurrentHashMap<>());
	private AtomicReference<Map<Long, QueueEntity>> queueIdMapRef = new AtomicReference<>(
			new ConcurrentHashMap<>(30000));
	private AtomicReference<Map<Long, Long>> queueIdMaxIdMapRef = new AtomicReference<>(new ConcurrentHashMap<>());
	private AtomicReference<List<QueueEntity>> queueList = new AtomicReference<>(new LinkedList<>());
	private TraceMessage initMaxTrace = TraceFactory.getInstance("initMaxTrace");

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			updateCache();
			executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(50),
					SoaThreadFactory.create("QueueService", true), new ThreadPoolExecutor.DiscardOldestPolicy());
			executor.execute(() -> {
				// 因为第一次的时候，会由topic和dbnode 触发初始化，所以自身初始化可以减少一次
				checkChanged();
				while (isRunning) {
					try {
						updateCache();
						lastUpdateTime = System.currentTimeMillis();

					} catch (Exception e) {
						log.error("QueueServiceImpl_initCache_error", e);
					}
					Util.sleep(soaConfig.getMqQueueCacheInterval());
				}
			});
		}
	}

	@Override
	public void startPortal() {
		if (startPortalFlag.compareAndSet(false, true)) {
			executorPortal = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(50), SoaThreadFactory.create("QueueService-portal", true),
					new ThreadPoolExecutor.DiscardOldestPolicy());
			executorPortal.execute(() -> {
				while (isRunning) {
					try {
						if (System.currentTimeMillis() - lastMaxTime < soaConfig.getMqQueueMaxRebuildInterval() * 1.6) {
							TraceMessageItem traceMessageItem = new TraceMessageItem();
							initMax();
							traceMessageItem.status = String.valueOf(System.currentTimeMillis() - lastMaxTime);
							traceMessageItem.msg = String.valueOf(soaConfig.getMqQueueMaxRebuildInterval() * 1.6);
							initMaxTrace.add(traceMessageItem);
							lastUpdateTime = System.currentTimeMillis();
						}
					} catch (Exception e) {
						log.error("QueueServiceImpl_initMax_error", e);
					}
					Util.sleep(soaConfig.getMqQueueMaxRebuildInterval());
				}
			});
		}
	}

	private AtomicBoolean updateFlag = new AtomicBoolean(false);

	@Override
	public void updateCache() {
		if (updateFlag.compareAndSet(false, true)) {
			if (checkChanged() || isPortal) {
				forceUpdateCache();
			}
			updateFlag.set(false);
		}
	}

	@Override
	public void forceUpdateCache() {
		Transaction transaction = Tracer.newTransaction("Timer", "Queue-updateCache");
		transaction.setStatus(Transaction.SUCCESS);
		try {
			List<QueueEntity> data = queueRepository.getAll();
			Map<String, TopicEntity> topicCache = topicService.getCache();
			int count = topicCache.size();
			if (count == 0) {
				List<TopicEntity> topics = topicService.getList();
				topicCache = new HashMap<>(topics.size());
				for (TopicEntity t1 : topics) {
					topicCache.put(t1.getName(), t1);
				}
				count = topicCache.size();
				log.warn("topicCache_lost");
			}
			MqReadMap<String, List<QueueEntity>> topicQueueMap1 = new MqReadMap<>(count);
			MqReadMap<String, List<QueueEntity>> topicWriteQueueMap1 = new MqReadMap<>(count);
			MqReadMap<Long, QueueEntity> queueIdMap = new MqReadMap<>(data.size());
			Map<Long, DbNodeEntity> dbNodeCache = dbNodeService.getCache();
			if (dbNodeCache.size() == 0) {
				List<DbNodeEntity> dbNodes = dbNodeService.getList();
				dbNodeCache = new HashMap<>(dbNodes.size());
				for (DbNodeEntity t1 : dbNodes) {
					dbNodeCache.put(t1.getId(), t1);
				}
				log.warn("dbNodeCache_lost");
			}
			if (!CollectionUtils.isEmpty(data) && dbNodeCache.size() > 0) {
				for (QueueEntity t1 : data) {
					if (!StringUtils.isEmpty(t1.getTopicName())) {
						if (!topicQueueMap1.containsKey(t1.getTopicName())) {
							topicQueueMap1.put(t1.getTopicName(), new ArrayList<>());
						}
						if (!topicWriteQueueMap1.containsKey(t1.getTopicName())) {
							topicWriteQueueMap1.put(t1.getTopicName(), new ArrayList<>());
						}
						if (checkWrite(t1, dbNodeCache)) {
							topicWriteQueueMap1.get(t1.getTopicName()).add(t1);
						}
						topicQueueMap1.get(t1.getTopicName()).add(t1);
					}
					queueIdMap.put(t1.getId(), t1);
				}
			}
			if (!isPortal || "1".equals(soaConfig.getLogPortalTopic())) {
				checkTopic(topicWriteQueueMap1, topicQueueMap1, topicCache);
			}
			topicWriteQueueMap1.setOnlyRead();
			topicQueueMap1.setOnlyRead();
			queueIdMap.setOnlyRead();
			if (queueIdMap.size() > 0 && data.size() > 0) {
				topicWriteQueueMap.set(topicWriteQueueMap1);
				topicQueueMap.set(topicQueueMap1);
				queueIdMapRef.set(queueIdMap);
				queueList.set(data);
			} else {
				lastUpdateEntity = null;
			}
			lastVersion.incrementAndGet();
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
			lastUpdateEntity = null;
		} finally {
			transaction.complete();
		}
	}

	private void checkTopic(Map<String, List<QueueEntity>> topicWriteQueueMap1,
			Map<String, List<QueueEntity>> topicQueueMap1, Map<String, TopicEntity> topicCache) {
		int topicCount = topicCache.size();
		topicWriteQueueMap1.entrySet().forEach(t1 -> {
			if (t1.getValue().size() == 0) {
				emailUtil.sendErrorMail(t1.getKey() + ",没有可以写入的队列", "topic:" + t1.getKey() + "没有可以写入的队列，请注意！");
			}
		});
		if (topicQueueMap1.size() - topicCount > 1) {
			StringBuilder rs = new StringBuilder();
			for (String topic : topicQueueMap1.keySet()) {
				if (!topicService.NEED_DELETED_TOPIC_NANE.equals(topic)) {
					if (!topicCache.containsKey(topic)) {
						rs.append("topic:" + topic + "在queue中，不在topic表中，请注意！\n");
					}
				}
			}
			for (String topic : topicCache.keySet()) {
				if (!topicQueueMap1.containsKey(topic)) {
					rs.append("topic:" + topic + "在topic表中，不在queue表中，请注意！\n");
				}
			}
			rs.append("因为缓存的异步性，可能会出现短暂的不一致。缓存保证最终一致性。");
			emailUtil.sendWarnMail(
					"topic数量(" + topicCount + ")与queue中topic的数量(" + topicWriteQueueMap1.size() + ")不一致，请注意！",
					rs.toString());
		}
	}

	private volatile LastUpdateEntity lastUpdateEntity = null;
	private long lastTime = System.currentTimeMillis();

	private boolean checkChanged() {
		boolean flag = doCheckChanged();
		if (!flag) {
			if (System.currentTimeMillis() - lastTime > soaConfig.getMqMetaRebuildMaxInterval()) {
				lastTime = System.currentTimeMillis();
				return true;
			}
		} else {
			lastTime = System.currentTimeMillis();
		}
		return flag;
	}

	private boolean doCheckChanged() {
		Transaction transaction = Tracer.newTransaction("Timer", "Queue-checkChanged");
		boolean flag = false;
		try {
			LastUpdateEntity temp = queueRepository.getLastUpdate();
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
		if (!flag && queueIdMapRef.get().size() == 0) {
			log.warn("queue数据为空，请注意！");
			return true;
		}
		return flag;
	}

	private boolean checkWrite(QueueEntity temp, Map<Long, DbNodeEntity> dbNodeCache) {
		if (dbNodeCache.containsKey(temp.getDbNodeId())) {
			// 读写状态： 1读写 2只读 3不可读不可写
			if (dbNodeCache.get(temp.getDbNodeId()).getReadOnly() == 1) {
				// 读写状态：1读写 2只读
				if (temp.getReadOnly() == 1) {
					return true;
				}
			}
		}
		return false;
	}

	@PreDestroy
	private void close() {
		try {
			executor.shutdown();
			isRunning = false;
		} catch (Exception e) {
		}
	}

	private Lock cacheLock = new ReentrantLock();
	private AtomicBoolean first = new AtomicBoolean(true);

	@Override
	public Map<String, List<QueueEntity>> getAllLocatedTopicQueue() {
		// TODO Auto-generated method stub
		// return topicQueueMap.get();

		Map<String, List<QueueEntity>> rs = topicQueueMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = topicQueueMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = topicQueueMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	/*
	 * 获取可写的
	 */
	@Override
	public Map<String, List<QueueEntity>> getAllLocatedTopicWriteQueue() {
		// TODO Auto-generated method stub
		// return topicWriteQueueMap.get();

		Map<String, List<QueueEntity>> rs = topicWriteQueueMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = topicWriteQueueMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = topicWriteQueueMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public Map<Long, QueueEntity> getAllQueueMap() {
		// return queueIdMapRef.get();

		Map<Long, QueueEntity> rs = queueIdMapRef.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = queueIdMapRef.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = queueIdMapRef.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public List<QueueEntity> getAllLocatedQueue() {
		// return queueList.get();
		List<QueueEntity> rs = queueList.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = queueList.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = queueList.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	// @Override
	public List<QueueEntity> getTopUndistributedNodes(int topNum, int nodeType, List<Long> nodeIds) {
		Map<String, Object> queryMap = new HashMap<>();
		// queryMap.put("topNum", topNum);
		queryMap.put("nodeType", nodeType);
		queryMap.put("nodeIds", nodeIds);
		// 注意返回值只有db_node_id,ip
		List<QueueEntity> dataEntities = queueRepository.getTopUndistributedNodes(queryMap);
		List<QueueEntity> rs = new ArrayList<QueueEntity>(dataEntities.size());
		Map<String, Integer> counter = new HashMap<String, Integer>();
		dataEntities.forEach(t1 -> {
			if (!counter.containsKey(t1.getIp())) {
				counter.put(t1.getIp(), 0);
			}
			if (counter.get(t1.getIp()) < topNum * 2) {
				rs.add(t1);
				counter.put(t1.getIp(), counter.get(t1.getIp()) + 1);
			}
		});
		return dataEntities;
	}

	@Override
	public List<Long> getTopDistributedNodes(Long topicId) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("topicId", topicId);
		return queueRepository.getTopDistributedNodes(queryMap);
	}

	@Override
	public List<QueueEntity> getDistributedList(List<Long> nodeIds, Long topicId) {
		return queueRepository.getDistributedList(nodeIds, topicId);
	}

	// @Override
//	public List<QueueEntity> getUndistributedList(List<Long> nodeIds, int nodeType) {
//		return queueRepository.getUndistributedListByNodeIds(nodeIds, nodeType);
//	}

	@Override
	public void updateWithLock(QueueEntity queueEntity) {
		int result = queueRepository.updateWithLock(queueEntity);
		if (result == 0) {
			throw new ConcurrentException("并发错误：数据已被他人修改，请刷新后再次重试");
		}
	}

	@Override
	public Map<Long, Long> getMax() {
		lastMaxTime = System.currentTimeMillis();
		if (System.currentTimeMillis() - lastUpdateTime > soaConfig.getMqQueueMaxRebuildInterval()) {
			lastMaxTime = System.currentTimeMillis() - soaConfig.getMqQueueMaxRebuildInterval() * 10;
			lastUpdateTime = System.currentTimeMillis();
			synchronized (this) {
				if (System.currentTimeMillis() - lastUpdateTime > soaConfig.getMqQueueMaxRebuildInterval()) {
					TraceMessageItem traceMessageItem = new TraceMessageItem();
					initMax();
					traceMessageItem.status = String.valueOf(System.currentTimeMillis() - lastUpdateTime);
					traceMessageItem.msg = String.valueOf(soaConfig.getMqQueueMaxRebuildInterval() * 1.6);
					initMaxTrace.add(traceMessageItem);
					lastUpdateTime = System.currentTimeMillis();
					lastMaxTime = System.currentTimeMillis() - soaConfig.getMqQueueMaxRebuildInterval() * 10;
				}
			}
		}
		return queueIdMaxIdMapRef.get();
	}

	private void initMax() {
		Transaction transaction = Tracer.newTransaction("Timer", "Queue-initMax");
		try {
			Map<Long, QueueEntity> data = queueIdMapRef.get();
			// key为ip+db+tb,value 为id
			Map<String, Long> queueMap = new HashMap<>(data.size());
			// key为ip，value为dbnodeid
			Map<String, Long> dbIpIdMap = new HashMap<>(100);
			// 第一层的key为数据库实例的key,第二层的key为数据库名，第三层dekey为表名
			Map<Long, Long> maxMap = new ConcurrentHashMap<>(data.size());
			data.entrySet().forEach(t1 -> {
				queueMap.put(getKey(t1.getValue().getIp(), t1.getValue().getDbName(), t1.getValue().getTbName()),
						t1.getKey());
				dbIpIdMap.put(t1.getValue().getIp(), t1.getValue().getDbNodeId());
			});
			// Map<Long, Long> maxMap = new HashMap<>(data.size());
			dbIpIdMap.entrySet().forEach(t1 -> {
				message01Service.setDbId(t1.getValue());
				Map<String, Map<String, Long>> maxNode = message01Service.getMaxId();
				maxNode.entrySet().forEach(t2 -> {
					t2.getValue().entrySet().forEach(t3 -> {
						String key = getKey(t1.getKey(), t2.getKey(), t3.getKey());
						if (queueMap.containsKey(key)) {
							maxMap.put(queueMap.get(key), t3.getValue());
						}
					});
				});
			});
			data.entrySet().forEach(t1 -> {
				if (!maxMap.containsKey(t1.getKey())) {
					Transaction transaction1 = Tracer.newTransaction("Timer", "Queue-initMax-maxId");
					try {
						message01Service.setDbId(t1.getValue().getDbNodeId());
						maxMap.put(t1.getKey(), message01Service.getMaxId(t1.getValue().getTbName()));
					} catch (Exception e) {
						transaction1.setStatus(e);
					} finally {
						transaction1.complete();
					}
				}
			});
			queueIdMaxIdMapRef.set(maxMap);
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}

	}

	private String getKey(String ip, String db, String tb) {
		return ip + "_" + db + "_" + tb;
	}

	@Override
	public void updateForDbNodeChange(String ip, String dbName, String oldIp, String oldDbName) {
		if (StringUtils.hasLength(oldIp) && StringUtils.hasLength(oldDbName)) {
			queueRepository.updateForDbNodeChange(ip, dbName, oldIp, oldDbName);
		}
	}

	@Override
	public List<String> getTableNamesByDbNode(Long dbNodeId) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put("dbNodeId", dbNodeId);
		conditionMap.put("isActive", 0);
		List<QueueEntity> queueEntityList = queueRepository.getList(conditionMap);
		List<String> tableNames = new ArrayList<>();
		if (!CollectionUtils.isEmpty(queueEntityList)) {
			queueEntityList.stream().forEach(queueEntity -> {
				tableNames.add(queueEntity.getTbName());
			});
		}
		return tableNames;
	}

	@Override
	public List<AnalyseDto> countTopicByNodeId(Long id, Long page, Long limit) {
		Long start = (page - 1) * limit;
		return queueRepository.countTopicByNodeId(id, start, limit);
	}

	@Override
	public List<AnalyseDto> getDistributedNodes(Long dbNodeId) {
		return queueRepository.getDistributedNodes(dbNodeId);
	}

	@Override
	public Map<Long, AnalyseDto> getQueueQuantity() {
		Map<Long, AnalyseDto> quantityMap = new HashMap<>();
		queueRepository.getQueueQuantity().stream().forEach(e -> quantityMap.put(e.getTopicId(), e));
		return quantityMap;
	}

	@Override
	@PreDestroy
	public void stop() {
		isRunning = false;
	}

	@Override
	public int updateMinId(Long id, Long minId) {
		return queueRepository.updateMinId(id, minId);
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getMaxId(long queueId, String tbName) {
		long maxId = message01Service.getMaxId(tbName);
		queueIdMaxIdMapRef.get().put(queueId, maxId);
		return maxId;
	}

	@Override
	public void stopPortal() {
		isRunning = false;
	}

	@Override
	public long getLastVersion() {
		// TODO Auto-generated method stub
		return lastVersion.get();
	}

	@Override
	public void startBroker() {
		isPortal = false;
	}

	@Override
	public void stopBroker() {

	}

	@Override
	public String getCacheJson() {
		// TODO Auto-generated method stub
		return JsonUtil.toJsonNull(getAllQueueMap());
	}

	@Override
	public void resetCache() {
		lastUpdateEntity = null;
	}

	@Override
	public List<QueueEntity> getQueuesByTopicId(long topicId) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(QueueEntity.FdTopicId, topicId);
		return getList(conditionMap);
	}

	/**
	 * 解绑失败topic并且清理失败消息
	 *
	 * @param queueEntities
	 */
	@Override
	public void deleteMessage(List<QueueEntity> queueEntities, long consumerGroupId) {
		for (QueueEntity queueEntity : queueEntities) {
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupId,
					"清空consumerGroup下的失败topic对应的Queue，" + JsonUtil.toJson(queueEntity));
			doDeleteMessage(queueEntity);
		}
	}

	// 此处表示真正的清空消息
	@Override
	public void truncate(QueueEntity queueEntity) {
		uiAuditLogService.recordAudit(QueueEntity.TABLE_NAME, queueEntity.getId(),
				"待truncate 的queue为：" + JsonUtil.toJson(queueEntity));
		// 动态切换数据源
		message01Service.setDbId(queueEntity.getDbNodeId());
		// 此处需要将truncate操作变成异步操作
		message01Service.truncate(queueEntity.getTbName());
		uiAuditLogService.recordAudit(QueueEntity.TABLE_NAME, queueEntity.getId(), "truncate完成");
		truncateQueueProperty(queueEntity);
	}

	private void truncateQueueProperty(QueueEntity queueEntity) {
		queueEntity.setTopicId(0);
		queueEntity.setTopicName("");
		queueEntity.setReadOnly(ReadWriteEnum.READ_WRITE.getCode());
		queueEntity.setMinId(0L);
		update(queueEntity);
	}

	// 次数需要将truncate 队列操作的时候,先将数据库标志位改为0
	@Override
	public void doDeleteMessage(QueueEntity queueEntity) {
		uiAuditLogService.recordAudit(QueueEntity.TABLE_NAME, queueEntity.getId(),
				"待删除之前的queue为：" + JsonUtil.toJson(queueEntity));
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, queueEntity.getTopicId(),
				"待删除之前的queue为：" + JsonUtil.toJson(queueEntity));
		// 将队列变为不可用
		// queueEntity.setIsActive(1);
		clearQueueProperty(queueEntity);
		uiAuditLogService.recordAudit(QueueEntity.TABLE_NAME, queueEntity.getId(), "删除完成");
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, queueEntity.getTopicId(), "删除完成");

	}

	/**
	 * 在队列中解绑失败topic
	 *
	 * @param queueEntity
	 * @return
	 */
	public UiResponse clearQueueProperty(QueueEntity queueEntity) {
		queueEntity.setTopicId(Long.MAX_VALUE);
		queueEntity.setTopicName(TopicService.NEED_DELETED_TOPIC_NANE);
		queueEntity.setReadOnly(ReadWriteEnum.READ_WRITE.getCode());
		update(queueEntity);
		return UiResponseHelper.buildSuccessUiResp();
	}

	@Override
	public List<QueueEntity> getTopUndistributed(int topNum, int nodeType, Long topicId) {
		// List<Long> nodeIds;
		// 获取可分配的节点
		List<Long> preNodeIds = getPreNodeIds(topicId, nodeType);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, String.format("备选节点： %s", preNodeIds));
		log.info("getTopUndistributed; preNodeIds:" + preNodeIds);
		if (CollectionUtils.isEmpty(preNodeIds)) {
			return new ArrayList<QueueEntity>();
		}
		// 根据可分配节点中的剩余队列数进行倒序
		List<QueueEntity> sortDbNodeIdIp = getTopUndistributedNodes(topNum, nodeType, preNodeIds);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, String.format("备选节点排序： %s",
				sortDbNodeIdIp.stream().map(QueueEntity::getDbNodeId).collect(Collectors.toList())));
//		log.info("getTopUndistributed; NodeIds:" + nodeIds);
		// 可分配的节点已满
		if (CollectionUtils.isEmpty(sortDbNodeIdIp)) {
			return new ArrayList<QueueEntity>();
		}
		// Map<Long, DbNodeEntity> dbMap = dbNodeService.getCache();
		Set<Long> nodeIdSet = new HashSet<>();
		Set<String> nodeIpSet = new HashSet<>();
		Map<Long, QueueEntity> queueMap = new HashMap<Long, QueueEntity>();
		// 获取所有可分配节点下的未分配队列
		List<QueueEntity> preQueueList = getSortAndUndistributedList(sortDbNodeIdIp, nodeType);

		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, String.format("根据节点获得备选队列： %s",
				preQueueList.stream().map(QueueEntity::getId).collect(Collectors.toList())));
		Map<Long, DbNodeEntity> dbNodeCache = dbNodeService.getCache();
		int count = 0;
		while (count < 5 && queueMap.size() < topNum) {
			nodeIpSet.clear();
			// nodeIdSet.clear();
			for (QueueEntity queueEntity : preQueueList) {
				// 选出可分配的队列
				// if (!nodeIdSet.contains(queueEntity.getDbNodeId())) {
				if (!nodeIdSet.contains(queueEntity.getDbNodeId()) && !nodeIpSet.contains(queueEntity.getIp())
						&& !queueMap.containsKey(queueEntity.getId()) && queueMap.size() < topNum
						&& checkWrite(queueEntity, dbNodeCache)) {
					uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId,
							String.format("节点[%d]上的队列[%d]获得资格", queueEntity.getDbNodeId(), queueEntity.getId()));
					nodeIdSet.add(queueEntity.getDbNodeId());
					nodeIpSet.add(queueEntity.getIp());
					queueMap.put(queueEntity.getId(), queueEntity);
				}
			}
			count++;
		}
		return new ArrayList<QueueEntity>(queueMap.values());
	}

	private List<QueueEntity> getSortAndUndistributedList(List<QueueEntity> sortDbNodeIdIp, int nodeType) {
		List<QueueEntity> allUnLocatedQueue = queueRepository.getUndistributedListByNodeIds(
				sortDbNodeIdIp.stream().map(QueueEntity::getDbNodeId).collect(Collectors.toList()), nodeType);
		List<QueueEntity> rs = new ArrayList<QueueEntity>(allUnLocatedQueue.size());
		Map<Long, List<QueueEntity>> qMap = new HashMap<Long, List<QueueEntity>>();
		allUnLocatedQueue.forEach(t1 -> {
			if (!qMap.containsKey(t1.getDbNodeId())) {
				qMap.put(t1.getDbNodeId(), new ArrayList<QueueEntity>(10));
			}
			if (qMap.get(t1.getDbNodeId()).size() < 10) {
				qMap.get(t1.getDbNodeId()).add(t1);
			}
		});
		sortDbNodeIdIp.forEach(t1 -> {
			if (qMap.containsKey(t1.getDbNodeId())) {
				rs.addAll(qMap.get(t1.getDbNodeId()));
			}
		});
		return rs;
	}

	private List<Long> getPreNodeIds(Long topicId, int nodeType) {
		Set<Long> normalNodeIdSet = new HashSet<>();
		Map<Long, DbNodeEntity> dbNodeEntityMap = dbNodeService.getCache();
		Map<Long, QueueEntity> queueCache = getAllQueueMap();
		Collection<QueueEntity> queueEntities = queueCache.values();
		dbNodeEntityMap.values().forEach(dbNodeEntity -> {
			// 过滤普通节点上为读写数据库，和Topic类型相匹配的节点
			if (dbNodeEntity.getNormalFlag() == 1 && dbNodeEntity.getReadOnly() == ReadWriteEnum.READ_WRITE.getCode()
					&& dbNodeEntity.getNodeType() == nodeType) {
				// TODO 判断节点下队列是否已满
				if (queueEntities.stream().filter(queueEntity -> queueEntity.getDbNodeId() == dbNodeEntity.getId()
						&& queueEntity.getTopicId() == 0).count() > 0) {
					normalNodeIdSet.add(dbNodeEntity.getId());
				} else {
					log.info(queueEntities.size() + "");
				}
			}
		});
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, String.format("可使用节点：%s", normalNodeIdSet));
		// 从数据库中查找topic已分配的队列
		List<QueueEntity> queueEntityList = getQueuesByTopicId(topicId);
		// 获取已分配的节点
		List<Long> nodeIds = queueEntityList.stream().map(QueueEntity::getDbNodeId).collect(Collectors.toList());
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId,
				String.format("已使用节点：%s", new HashSet<>(nodeIds)));
		// 获取可分配的节点
		Set<Long> resultIds = new HashSet<>(normalNodeIdSet);
		resultIds.removeAll(nodeIds);
		if (!CollectionUtils.isEmpty(resultIds)) {
			return new ArrayList<Long>(resultIds);
		} else {
			// 所有基本条件符合的节点都分配过了，直接返回所有符合基本条件的节点
			return new ArrayList<Long>(normalNodeIdSet);
		}
	}

	@Override
	public List<QueueEntity> getListBy(Map<String, Object> conditionMap, long page, long pageSize) {
		conditionMap.put("start1", (page - 1) * pageSize);
		conditionMap.put("offset1", pageSize);
		return queueRepository.getListBy(conditionMap);
	}

	@Override
	public long countBy(Map<String, Object> conditionMap) {
		return queueRepository.countBy(conditionMap);
	}

}
