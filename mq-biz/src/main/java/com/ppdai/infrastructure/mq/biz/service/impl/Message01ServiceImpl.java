package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.ppdai.infrastructure.mq.biz.common.metric.MetricSingleton;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dal.msg.Message01Repository;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.TableInfoEntity;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;

/**
 * @author dal-generator
 */
@Service
// @DependsOn({ "dbNodeServiceImpl" })
public class Message01ServiceImpl implements Message01Service {
	private Logger log = LoggerFactory.getLogger(Message01ServiceImpl.class);
	private Counter publishFailCounter = MetricSingleton.getMetricRegistry().counter("mq.publish.fail.count");
	private Timer publishTimer = MetricSingleton.getMetricRegistry().timer("mq.publish.time");
	private Counter pullFailCounter = MetricSingleton.getMetricRegistry().counter("mq.pull.fail.count");
	private Timer pullTimer = MetricSingleton.getMetricRegistry().timer("mq.pull.time");

	private Counter otherFailCounter = MetricSingleton.getMetricRegistry().counter("mq.pull.fail.other.count");
	private AtomicInteger dbCounter = new AtomicInteger(0);
	private ThreadLocal<Long> dbId = new ThreadLocal<>();
	private ThreadLocal<Boolean> isMaster = new ThreadLocal<>();
	@Autowired
	private Message01Repository message01Repository;
	@Autowired
	private DbNodeService dbNodeService;


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void insertBatchDy(String topic, String tbName, List<Message01Entity> entities) {
		Timer.Context context = publishTimer.time();
		// Transaction transaction=Tracer.newTransaction("Msg", getDbName() +
		// "." + tbName);
		try {
			setMaster(true);
			message01Repository.insertBatchDy(getDbName() + "." + tbName, entities);
			// publishCounter.inc(entities.size());
			MetricSingleton.getMetricRegistry().counter("mq.publish.count?topic=" + topic).inc(entities.size());
			MetricSingleton.getMetricRegistry().counter("mq.publish.countsum").inc(entities.size());
			long size = 0;
			for (Message01Entity t1 : entities) {
				size += (t1.getBody() + "").length();
			}
			// publishSizeCounter.inc(size);
			MetricSingleton.getMetricRegistry().counter("mq.publish.size?topic=" + topic).inc(size);
			MetricSingleton.getMetricRegistry().counter("mq.publish.sizesum").inc(size);
			// transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			publishFailCounter.inc();
			// transaction.setStatus(e);
			throw new RuntimeException(e);
		} finally {
			clearDbId();
			// transaction.complete();
			context.stop();
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void deleteDy(String tbName, long start, long end) {
		try {
			setMaster(true); 
			message01Repository.deleteDy(getDbName() + "." + tbName, start, end);
		} catch (Exception e) {
			otherFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public List<Message01Entity> getListDy(String topic, String tbName, long start, long end) {
		List<Message01Entity> rs = new ArrayList<>();
		setMaster(false);
		Timer.Context context = pullTimer.time();
		try {
			rs = message01Repository.getListDy(getDbName() + "." + tbName, start, end);
			MetricSingleton.getMetricRegistry().counter("mq.pull.count?topic=" + topic).inc(rs.size());
			MetricSingleton.getMetricRegistry().counter("mq.pull.countsum").inc(rs.size());
			long size = 0L;
			for (Message01Entity t1 : rs) {
				size += (t1.getBody() + "").length();
			}
			MetricSingleton.getMetricRegistry().counter("mq.pull.size?topic=" + topic).inc(size);
			MetricSingleton.getMetricRegistry().counter("mq.pull.sizesum").inc(size);
		} catch (Exception e) {
			pullFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
			context.stop();
		}
		return rs;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public List<Message01Entity> getListByPage(Map<String, Object> parameterMap) {
		List<Message01Entity> rs = new ArrayList<>();
		setMaster(false);
		Timer.Context context = pullTimer.time();
		try {
			rs = message01Repository.getListByPageSize(parameterMap);
		} catch (Exception e) {
			pullFailCounter.inc();	
			throw new RuntimeException(e);
		} finally {
			clearDbId();
			context.stop();
		}
		return rs;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public long countByPage(Map<String, Object> parameterMap) {
		try {
			setMaster(false);
			return message01Repository.countByPage(parameterMap);
		} finally {
			clearDbId();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public Message01Entity getMessageById(String tbName, long id) {
		setMaster(false);
		Message01Entity message01Entity = null;
		try {
			message01Entity = message01Repository.getMessageById(getDbName() + "." + tbName, id);
		} catch (Exception e) {
			otherFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
		}
		return message01Entity;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public List<Message01Entity> getMessageByIds(String tbName, List<Long> ids) {
		setMaster(false);
		List<Message01Entity> message01Entitys = null;
		try {
			message01Entitys = message01Repository.getMessageByIds(getDbName() + "." + tbName, ids);
		} catch (Exception e) {
			otherFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
		}
		return message01Entitys;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public Long getTableMinId(String tbName){
		return message01Repository.getTableMinId(getDbName() + "." + tbName);
	}


	// 最大值为系统当前最大值的下一个值
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public Long getMaxId(String tbName) {
		setMaster(true);
		Long maxId = 0L;
		try {
			// maxId = message01Repository.getMaxIdByTb(getDbName() + "." +
			// tbName);
			maxId = message01Repository.getMaxId(getDbName(), tbName);
			// log.info("dbId_{}_dbName_{}_tbName_{}_maxId_{} and dbUrl is
			// {},and thread id is {}", dbId.get(), dbName,
			// tbName, maxId, ((DruidDataSource) getDataSource()).getUrl(),
			// Thread.currentThread().getId());
		} catch (Exception e) {
			otherFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
		}
		if (maxId == null)
			return 1L;
		// 如果是读取字典表则不用+1
		// return maxId + 1;
		return maxId;
	}

	@Override
	public DataSource getDataSource() {
		return dbNodeService.getDataSource(dbId.get(), isMaster.get());
	}

	@Override
	public void setDbId(long dbNodeId) {
		dbId.set(dbNodeId);
		isMaster.set(false);
		dbCounter.incrementAndGet();
		// log.info("set_dbId_current_threadId_" +
		// Thread.currentThread().getId() + "_and_dbId_is_" + dbId.get());
	}

	private void setMaster(boolean master) {
		isMaster.set(master);
	}

	@Override
	public void clearDbId() {
		try {
			dbId.remove();
			isMaster.remove();
			dbCounter.decrementAndGet();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public String getDbName() {
		Map<Long, DbNodeEntity> cache = dbNodeService.getCache();
		long id = dbId.get();
		if (cache.containsKey(id)) {
			return cache.get(id).getDbName();
		}
		log.error("dbName_is_null_and_id_is_" + id + ",and Json is " + JsonUtil.toJsonNull(cache));
		return null;
	}

	/*
	 * 注意是某个数据库实例下面的数据库名和表名对应的 最大id
	 */
	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Map<String, Long>> getMaxId() {
		setMaster(true); 
		Map<String, Map<String, Long>> map = new HashMap<>();
		try {
			if (getDataSource() == null) {
				// throw new RuntimeException("db_is_null_and_id_is_" +
				// dbId.get());
				return map;
			}
			List<TableInfoEntity> dataLst = message01Repository.getMaxIdByDb();
			dataLst.forEach(t1 -> {
				if (!map.containsKey(t1.getTableSchema())) {
					map.put(t1.getTableSchema(), new HashMap<>());
				}
				// map.putIfAbsent(t1.getTableSchema(), new HashMap<>());
				if (!map.get(t1.getTableSchema()).containsKey(t1.getTbName())) {
					map.get(t1.getTableSchema()).put(t1.getTbName(),
							t1.getAutoIncrement() == null ? 1 : t1.getAutoIncrement());
				}
				// map.get(t1.getTableSchema()).putIfAbsent(t1.getTbName(),
				// t1.getAutoIncrement() == null ? 1 : t1.getAutoIncrement());

			});
		} catch (Exception e) {
			log.error("getMaxId_error", e);
			otherFailCounter.inc();
		} finally {
			clearDbId();
		}
		return map;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void truncate(String tbName) {
		try {
			setMaster(true);
			message01Repository.truncate(getDbName() + "." + tbName);
		} catch (Exception e) {
			otherFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
		}
	}

	@Override
	public int getTableQuantityByDbName(String dbName) {
		Map<String, Map<String, Long>> data = getMaxId();
		if (data.containsKey(dbName)) {
			return data.get(dbName).size();
		} else {
			return 0;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public List<String> getTableNamesByDbName(String dbName) {
		Map<String, Map<String, Long>> data = getMaxId();
		List<String> tableNames = new ArrayList<>();
		if (data.containsKey(dbName)) {
			return new ArrayList<>(data.get(dbName).keySet());
		}
		return tableNames;
	}	

	@Override
	public void createMessageTable(String tbName) {
		setMaster(true);
		message01Repository.createMessageTable(tbName);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public List<Message01Entity> getListByTime(String tbName, String insertTime) {
		List<Message01Entity> rs = new ArrayList<>();
		try {
			setMaster(false);
			rs = message01Repository.getListByTime(getDbName() + "." + tbName, insertTime);
		} catch (Exception e) {
			otherFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
		}
		return rs;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public Message01Entity getNearByMessageById(String tbName, long id) {
		Message01Entity message01Entity = null;
		setMaster(false);
		try {
			message01Entity = message01Repository.getNearByMessageById(getDbName() + "." + tbName, id);
		} catch (Exception e) {
			otherFailCounter.inc();
			throw new RuntimeException(e);
		} finally {
			clearDbId();
		}
		return message01Entity;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public String getMaxConnectionsCount() {
		try {
			setMaster(true);
			// TODO Auto-generated method stub
			Map<String, String> map = message01Repository.getMaxConnectionsCount();
			if (map.size() == 0)
				return "0";
			else
				return map.get("Value");
		} finally {
			clearDbId();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public Integer getConnectionsCount() {
		try {
			setMaster(true);
			// TODO Auto-generated method stub
			Integer count = message01Repository.getConnectionsCount();
			if (count == null)
				return 0;
			return count;
		} finally {
			clearDbId();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void updateFailMsgResult(String tbName, List<Long> ids, int retryCount) {
		try {
			setMaster(true);
			message01Repository.updateFailMsgResult(getDbName() + "." + tbName, ids, retryCount);
		} finally {
			clearDbId();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public int deleteOldFailMsg(String tbName, long id, int retryCount) {
		try {
			setMaster(true);
			return message01Repository.deleteOldFailMsg(getDbName() + "." + tbName, id, retryCount);
		} finally {
			clearDbId();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public void deleteByIds(String tbName, List<Long> ids) {
		try {
			setMaster(true);
			message01Repository.deleteByIds(getDbName() + "." + tbName, ids);
		} catch (Exception e) {
			log.error("deleteByIds_error",e);
		} finally {
			clearDbId();
		}
	}

}
