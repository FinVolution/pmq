package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.BrokerTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.QueueOffsetRepository;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.OffsetVersionEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;
import com.ppdai.infrastructure.mq.biz.service.common.MqReadMap;

/**
 * @author dal-generator
 */
@Service
public class QueueOffsetServiceImpl extends AbstractBaseService<QueueOffsetEntity>
		implements CacheUpdateService, QueueOffsetService, TimerService, BrokerTimerService {
	private Logger log = LoggerFactory.getLogger(QueueOffsetServiceImpl.class);
	@Autowired
	private QueueOffsetRepository queueOffsetRepository;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private AuditLogService uiAuditLogService;
	@Autowired
	private QueueService queueService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private UserInfoHolder userInfoHolder;
	@Autowired
	private Message01Service message01Service;
	
	private volatile boolean isRunning = true;
	private volatile boolean isPortal = true;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(50), SoaThreadFactory.create("QueueOffsetService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());

	private AtomicReference<Map<String, Map<String, List<QueueOffsetEntity>>>> cacheDataMap = new AtomicReference<>(
			new HashMap<>(2000));

	private AtomicReference<Map<String, QueueOffsetEntity>> offsetUqMap = new AtomicReference<>(new HashMap<>(10000));
	private AtomicReference<Map<Long, OffsetVersionEntity>> idOffsetMap = new AtomicReference<>(
			new ConcurrentHashMap<>(10000));
	private AtomicReference<List<QueueOffsetEntity>> cacheDataList = new AtomicReference<>(new LinkedList<>());
	private AtomicLong lastVersion = new AtomicLong(0);
	private TraceMessage queueOffsetCacheTrace = TraceFactory.getInstance("queueOffsetCache");

	@PostConstruct
	private void init() {
		super.setBaseRepository(queueOffsetRepository);
	}

	@Override
	public List<QueueOffsetEntity> getByConsumerGroupIds(List<Long> consumerGroupIds) {
		if (CollectionUtils.isEmpty(consumerGroupIds)) {
			return new ArrayList<>();
		}
		return queueOffsetRepository.getByConsumerGroupIds(consumerGroupIds);
	}

	@Override
	public void updateConsumerId(QueueOffsetEntity entity) {
		queueOffsetRepository.updateConsumerId(entity);
	}

	@Override
	public int commitOffset(QueueOffsetEntity entity) {		
		int flag = queueOffsetRepository.commitOffset(entity);	
		return flag;
	}

	@Override
	public void deRegister(long consumerId) {
		queueOffsetRepository.deRegister(consumerId);
	}

	private Lock cacheLock = new ReentrantLock();
	private AtomicBoolean first = new AtomicBoolean(true);

	@Override
	public Map<String, Map<String, List<QueueOffsetEntity>>> getCache() {
		// TODO Auto-generated method stub
		// return cacheDataMap.get();
		Map<String, Map<String, List<QueueOffsetEntity>>> rs = cacheDataMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = cacheDataMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = cacheDataMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public Map<String, QueueOffsetEntity> getUqCache() {
		// TODO Auto-generated method stub
		// return offsetUqMap.get();

		Map<String, QueueOffsetEntity> rs = offsetUqMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = offsetUqMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = offsetUqMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			// updateCache();
			executor.execute(() -> {
				while (isRunning) {
					updateCache();
					Util.sleep(soaConfig.getMqQueueOffsetCacheInterval());
				}
			});
		}
	}

	private AtomicBoolean updateFlag = new AtomicBoolean(false);

	@Override
	public void updateCache() {
		if (updateFlag.compareAndSet(false, true)) {
			// 如果是portal 界面则强制更新
			if (checkChanged() || isPortal) {
				forceUpdateCache();
			}
			updateFlag.set(false);
		}
	}

	@Override
	public void forceUpdateCache() {
		Transaction transaction = Tracer.newTransaction("Timer", "QueueOffset-updateCache");
		TraceMessageItem traceMessageItem = new TraceMessageItem();
		try {
			// List<QueueOffsetEntity> data =
			// queueOffsetRepository.getAllBasic();
			List<QueueOffsetEntity> data = null;
			if (isPortal) {
				data = queueOffsetRepository.getAll();
			} else {
				data = queueOffsetRepository.getAllBasic();
			}
			MqReadMap<String, Map<String, List<QueueOffsetEntity>>> cacheMap = new MqReadMap<>(2000);
			MqReadMap<String, QueueOffsetEntity> offsetUqMap1 = new MqReadMap<>(data.size());
			// Map<String, QueueOffsetEntity> offsetUqMap1 = offsetUqMap.get();
			if (!CollectionUtils.isEmpty(data)) {
				data.forEach(t1 -> {
					if (!StringUtils.isEmpty(t1.getConsumerGroupName()) && !StringUtils.isEmpty(t1.getTopicName())) {
						if (!cacheMap.containsKey(t1.getConsumerGroupName())) {
							cacheMap.put(t1.getConsumerGroupName(), new HashMap<>());
						}
						if (!cacheMap.get(t1.getConsumerGroupName()).containsKey(t1.getTopicName())) {
							cacheMap.get(t1.getConsumerGroupName()).put(t1.getTopicName(), new ArrayList<>());
						}
						cacheMap.get(t1.getConsumerGroupName()).get(t1.getTopicName()).add(t1);
					}
					offsetUqMap1.put(t1.getConsumerGroupName() + "_" + t1.getTopicName() + "_" + t1.getQueueId(), t1);
				});
			}
			cacheMap.setOnlyRead();
			offsetUqMap1.setOnlyRead();
			cacheDataMap.set(cacheMap);
			cacheDataList.set(data);
			offsetUqMap.set(offsetUqMap1);
			traceMessageItem.status = "count-" + offsetUqMap1.size();
			queueOffsetCacheTrace.add(traceMessageItem);
			lastVersion.incrementAndGet();
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
			lastUpdateEntity = null;
		} finally {
			transaction.complete();
		}
	}

	private void updateOffsetCache() {
		List<OffsetVersionEntity> lstData = queueOffsetRepository.getOffsetVersion();
		Map<Long, OffsetVersionEntity> dataCache = new ConcurrentHashMap<>(10000);
		lstData.forEach(t1 -> {
			dataCache.put(t1.getId(), t1);
		});
		idOffsetMap.set(dataCache);
	}

	private volatile LastUpdateEntity lastUpdateEntity = null;
	private long lastTime=System.currentTimeMillis();
	private boolean checkChanged() {		
		boolean flag= doCheckChanged();
		if(!flag){
			if(System.currentTimeMillis()-lastTime>soaConfig.getMqMetaRebuildMaxInterval()){
				lastTime=System.currentTimeMillis();
				return true;
			}
		}else{
			lastTime=System.currentTimeMillis();
		}
		return flag;
	}
	private boolean doCheckChanged() {
		Transaction transaction = Tracer.newTransaction("Timer", "QueueOffset-checkChanged");
		updateOffsetCache();
		boolean flag = false;
		try {
			LastUpdateEntity temp = queueOffsetRepository.getLastUpdate();
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
		if(!flag && cacheDataMap.get().size()==0){
			log.warn("queueOffset数据为空，请注意！");			
			return true;
		}
		return flag;
	}

	@Override
	public List<QueueOffsetEntity> getCacheData() {
		// return cacheDataList.get();

		List<QueueOffsetEntity> rs = cacheDataList.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = cacheDataList.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = cacheDataList.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public void setConsumserIdsToNull(List<Long> consumerIds) {
		queueOffsetRepository.setConsumserIdsToNull(consumerIds);
	}

	@Override
	public void deleteByConsumerGroupId(long consumerGroupId) {
		queueOffsetRepository.deleteByConsumerGroupId(consumerGroupId);
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupId,
				"删除consumerGroup在queueOffset表中的信息");
	}


	@Override
	public void deleteByConsumerGroupIdAndOriginTopicName(ConsumerGroupTopicEntity consumerGroupTopicEntity) {
		queueOffsetRepository.deleteByConsumerGroupIdAndOriginTopicName(consumerGroupTopicEntity.getConsumerGroupId(), consumerGroupTopicEntity.getOriginTopicName());
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupTopicEntity.getConsumerGroupId(),
				"取消" + consumerGroupTopicEntity.getConsumerGroupName() + "对" + consumerGroupTopicEntity.getTopicName()
						+ "订阅时，删除queueOffset，对应的consumerGroupTopic为：" + JsonUtil.toJson(consumerGroupTopicEntity));
	}

	@Override
	public List<QueueOffsetEntity> getByConsumerGroupTopic(long consumerGroupId, long topicId) {
		return queueOffsetRepository.getByConsumerGroupTopic(consumerGroupId, topicId);
	}

	@Override
	public void updateStopFlag(long id, int stopFlag, String updateBy) {
		queueOffsetRepository.updateStopFlag(id, stopFlag, updateBy);
	}

	@Override
	public int updateQueueOffset(Map<String, Object> parameterMap) {
		return queueOffsetRepository.updateQueueOffset(parameterMap);
	}

	@Override
	@PreDestroy
	public void stop() {
		isRunning = false;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int commitOffsetById(QueueOffsetEntity entity) {
		return queueOffsetRepository.commitOffsetById(entity);
	}

	@Override
	public List<QueueOffsetEntity> getUnSubscribeData() {
		// TODO Auto-generated method stub
		return queueOffsetRepository.getUnSubscribeData();
	}

	@Override
	public List<QueueOffsetEntity> getAllBasic() {
		// TODO Auto-generated method stub
		return queueOffsetRepository.getAllBasic();
	}

	

	@Override
	public long getLastVersion() {
		// TODO Auto-generated method stub
		return lastVersion.get();
	}

	@Override
	public String getCacheJson() {
		// TODO Auto-generated method stub
		return JsonUtil.toJsonNull(getCacheData());
	}

	@Override
	public Map<Long, OffsetVersionEntity> getOffsetVersion() {
		// TODO Auto-generated method stub
		// return idOffsetMap.get();
		Map<Long, OffsetVersionEntity> rs = idOffsetMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = idOffsetMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = idOffsetMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public void startBroker() {
		isPortal = false;

	}

	@Override
	public void stopBroker() {
		// TODO Auto-generated method stub

	}


	@Override
	public BaseUiResponse createQueueOffset(ConsumerGroupTopicEntity consumerGroupTopicEntity) {

		List<QueueEntity> queueEntityList = queueService.getQueuesByTopicId(consumerGroupTopicEntity.getTopicId());
		ConsumerGroupEntity consumerGroup = consumerGroupService.get(consumerGroupTopicEntity.getConsumerGroupId());
		if (queueEntityList.size() == 0) {
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupTopicEntity.getConsumerGroupId(),
					"topicName:" + consumerGroupTopicEntity.getTopicName() + "," + consumerGroupTopicEntity.getTopicId()
							+ "下没有任何的queue");
		}
		for (QueueEntity queueEntity : queueEntityList) {
			QueueOffsetEntity queueOffsetEntity = new QueueOffsetEntity();
			queueOffsetEntity.setConsumerGroupId(consumerGroup.getId());
			queueOffsetEntity.setConsumerGroupName(consumerGroup.getName());
			//设置消费者组的原始name
			queueOffsetEntity.setOriginConsumerGroupName(consumerGroup.getOriginName());
			//设置消费者组的消费模式
			queueOffsetEntity.setConsumerGroupMode(consumerGroup.getMode());
			queueOffsetEntity.setTopicId(consumerGroupTopicEntity.getTopicId());
			queueOffsetEntity.setTopicName(consumerGroupTopicEntity.getTopicName());
			queueOffsetEntity.setOriginTopicName(consumerGroupTopicEntity.getOriginTopicName());
			queueOffsetEntity.setTopicType(consumerGroupTopicEntity.getTopicType());
			queueOffsetEntity.setQueueId(queueEntity.getId());
			queueOffsetEntity
					.setDbInfo(queueEntity.getIp() + " | " + queueEntity.getDbName() + " | " + queueEntity.getTbName());
			String userId = userInfoHolder.getUserId();
			queueOffsetEntity.setInsertBy(userId);
			message01Service.setDbId(queueEntity.getDbNodeId());
			long maxId = queueService.getMaxId(queueEntity.getId(), queueEntity.getTbName());
			// 正常topic的起始偏移为：当前的最大Id
			if (consumerGroupTopicEntity.getTopicType() == 1) {
				queueOffsetEntity.setOffset(maxId - 1);
				queueOffsetEntity.setStartOffset(maxId - 1);
			}

			if (!getUqCache().containsKey(queueOffsetEntity.getConsumerGroupName() + "_"
					+ queueOffsetEntity.getTopicName() + "_" + queueOffsetEntity.getQueueId())) {
				insert(queueOffsetEntity);
				uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,
						consumerGroupTopicEntity.getConsumerGroupId(),
						"添加" + consumerGroupTopicEntity.getConsumerGroupName() + "订阅"
								+ consumerGroupTopicEntity.getTopicName() + "时的起始偏移："
								+ queueOffsetEntity.getStartOffset() + "。添加订阅时增加queueOffset："
								+ JsonUtil.toJson(queueOffsetEntity) + " 同时queue信息为：" + JsonUtil.toJson(queueEntity));
		
			} else {
				uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,
						consumerGroupTopicEntity.getConsumerGroupId(), consumerGroupTopicEntity.getConsumerGroupName()
								+ "已经订阅了" + consumerGroupTopicEntity.getTopicName());
			}
		}

		return new BaseUiResponse();
	}

	@Override
	public long countBy(Map<String, Object> conditionMap){
		return queueOffsetRepository.countBy(conditionMap);
	}

	@Override
	public List<QueueOffsetEntity> getListBy(Map<String, Object> conditionMap, long page, long pageSize){
		conditionMap.put("start1", (page - 1) * pageSize);
		conditionMap.put("offset1", pageSize);
		return  queueOffsetRepository.getListBy(conditionMap);
	}

	@Override
	public long getOffsetSumByIds(List<Long> ids) {
		if(CollectionUtils.isEmpty(ids))return 0;
		Long sum=queueOffsetRepository.getOffsetSumByIds(ids);
		if(sum==null)return 0;
		return sum;
	}

}
