package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.TopicUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.TopicRepository;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.exceptions.ConcurrentException;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;
import com.ppdai.infrastructure.mq.biz.service.common.MqReadMap;

/**
 * @author dal-generator
 */
@Service
public class TopicServiceImpl extends AbstractBaseService<TopicEntity>
		implements CacheUpdateService, TopicService, TimerService {
	private Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private ConsumerGroupTopicService consumerGroupTopicService;
	@Autowired
	private AuditLogService uiAuditLogService;
	@Autowired
	private UserInfoHolder userInfoHolder;
	private volatile boolean isRunning = true;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private AtomicBoolean updateFlag = new AtomicBoolean(false);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("TopicService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());

	private TraceMessage topicCacheTrace = TraceFactory.getInstance("topicCache");

	@Autowired
	private QueueService queueService;

	@Autowired
	private Message01Service message01Service;

	@PostConstruct
	private void init() {
		super.setBaseRepository(topicRepository);
		// System.out.println("topic init");
	}

	private AtomicReference<Map<String, TopicEntity>> topicCacheMapRef = new AtomicReference<>(
			new ConcurrentHashMap<>(2000));

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			updateCache();
			executor.execute(() -> {
				while (isRunning) {
					updateCache();
					Util.sleep(soaConfig.getMqTopicCacheInterval());
				}
			});
		}
	}

	@Override
	public void updateCache() {
		if (updateFlag.compareAndSet(false, true)) {
			if (checkChanged()) {
				forceUpdateCache();
			}
			updateFlag.set(false);
		}
	}

	@Override
	public void forceUpdateCache() {
		doForceUpdateCache();		
		updateQueueCache();
	}

	private void doForceUpdateCache() {
		Transaction transaction = Tracer.newTransaction("Timer", "Topic-initCache");
		try {
			TraceMessageItem traceMessageItem = new TraceMessageItem();
			List<TopicEntity> data = topicRepository.getAll();
			if (CollectionUtils.isEmpty(data)) {
				transaction.setStatus(Transaction.SUCCESS);
				return;
			}
			MqReadMap<String, TopicEntity> topicCacheMap = new MqReadMap<String, TopicEntity>(data.size());
			data.forEach(t1 -> {
				topicCacheMap.put(t1.getName(), t1);
			});
			topicCacheMap.setOnlyRead();
			topicCacheMapRef.set(topicCacheMap);			
			traceMessageItem.status = "count-" + topicCacheMapRef.get().size();
			topicCacheTrace.add(traceMessageItem);
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
			lastUpdateEntity = null;
		} finally {
			transaction.complete();
		}
	}

	private void updateQueueCache() {		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				queueService.forceUpdateCache();
			}
		});
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
		Transaction transaction = Tracer.newTransaction("Timer", "Topic-checkChanged");
		boolean flag = false;
		try {
			LastUpdateEntity temp = topicRepository.getLastUpdate();
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
		if(!flag && topicCacheMapRef.get().size()==0){
			log.warn("Topic数据为空，请注意！");			
			return true;
		}
		return flag;
	}

	private Lock cacheLock = new ReentrantLock();
	private AtomicBoolean first = new AtomicBoolean(true);

	@Override
	public Map<String, TopicEntity> getCache() {
		// return topicCacheMapRef.get();
		Map<String, TopicEntity> rs = topicCacheMapRef.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = topicCacheMapRef.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = topicCacheMapRef.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@PreDestroy
	private void close() {
		isRunning = false;
		try {
			executor.shutdown();
		} catch (Throwable e) {
			// TODO: handle exception
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void distributeQueue(TopicEntity topicEntity, List<QueueEntity> queueEntityList) {
		for (QueueEntity queueEntity : queueEntityList) {
			queueEntity.setTopicId(topicEntity.getId());
			queueEntity.setTopicName(topicEntity.getName());
			queueEntity.setReadOnly(1);
			queueService.updateWithLock(queueEntity);
		}
	}

	@Override
	public void distributeQueue(TopicEntity normalTopicEntity, QueueEntity queueEntity) {
		queueEntity.setTopicName(normalTopicEntity.getName());
		queueEntity.setTopicId(normalTopicEntity.getId());
		queueEntity.setReadOnly(1);
		queueService.updateWithLock(queueEntity);
	}

	@Override
	public TopicEntity getTopicByName(String topicName) {
		return topicRepository.getTopicByName(topicName);
	}

	@Override
	public List<TopicEntity> getListWithUserName(Map<String, Object> conditionMap, long page, long pageSize) {
		conditionMap.put("start1", (page - 1) * pageSize);
		conditionMap.put("offset1", pageSize);
		return topicRepository.getListWithUserName(conditionMap);
	}

	@Override
	public long countWithUserName(Map<String, Object> conditionMap) {
		return topicRepository.countWithUserName(conditionMap);

	}

	@PreDestroy
	@Override
	public void stop() {
		isRunning = false;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCacheJson() {
		// TODO Auto-generated method stub
		return JsonUtil.toJsonNull(getCache());
	}

	/**
	 * 更新失败topic的AppId
	 *
	 * @param consumerGroupEntity
	 */
	@Override
	public void updateFailTopic(ConsumerGroupEntity consumerGroupEntity) {
		// 获取该消费者组下所有失败topic对应的originTopicName
		List<String> failTopicNames = consumerGroupTopicService.getFailTopicNames(consumerGroupEntity.getId());
		if (failTopicNames.size() > 0) {
			for (String failTopicName : failTopicNames) {
				TopicEntity failTopicEntity = getTopicByName(failTopicName);
				if (failTopicEntity != null) {
					failTopicEntity.setAppId(consumerGroupEntity.getAppId());
					update(failTopicEntity);
				}
			}
		}
	}


	/**
	 * 删除失败topic，并且清理失败消息并且解绑失败topic
	 *
	 * @param failTopicNames
	 */
	@Override
	public void deleteFailTopic(List<String> failTopicNames, long consumerGroupId) {
		if (failTopicNames.size() > 0) {
			for (String failTopicName : failTopicNames) {
				TopicEntity failTopicEntity = getTopicByName(failTopicName);
				if (failTopicEntity != null) {
					List<QueueEntity> queueEntities = queueService.getQueuesByTopicId(failTopicEntity.getId());
					queueService.deleteMessage(queueEntities, consumerGroupId);
					delete(failTopicEntity.getId());
					uiAuditLogService.recordAudit("consumer_group", consumerGroupId,
							"删除consumerGroup下的失败topic，" + JsonUtil.toJson(failTopicEntity));
					
				}
			}
		}

	}

	/**
	 * 返回创建的失败topic
	 *
	 * @param topicEntity
	 * @return
	 */
	@Override
	public TopicEntity createFailTopic(TopicEntity topicEntity, ConsumerGroupEntity consumerGroup) {
		TopicEntity failTopicEntity = new TopicEntity();
		failTopicEntity.setName(TopicUtil.getFailTopicName(consumerGroup.getName(), topicEntity.getName()));
		failTopicEntity.setOriginName(topicEntity.getOriginName());
		failTopicEntity.setDptName(topicEntity.getDptName());
		failTopicEntity.setOwnerIds(consumerGroup.getOwnerIds());
		failTopicEntity.setOwnerNames(consumerGroup.getOwnerNames());
		failTopicEntity.setEmails(consumerGroup.getAlarmEmails());
		failTopicEntity.setTels(topicEntity.getTels());
		failTopicEntity.setBusinessType(topicEntity.getBusinessType());
		failTopicEntity.setSaveDayNum(soaConfig.getFailTopicSaveDayNum());
		failTopicEntity.setExpectDayCount(soaConfig.getExpectDayCount());
		failTopicEntity.setRemark(topicEntity.getRemark());
		failTopicEntity.setToken(topicEntity.getToken());
		failTopicEntity.setNormalFlag(topicEntity.getNormalFlag());
		failTopicEntity.setTopicType(2);
		failTopicEntity.setConsumerFlag(topicEntity.getConsumerFlag());
		failTopicEntity.setConsumerGroupNames(topicEntity.getConsumerGroupNames());
		failTopicEntity.setAppId(consumerGroup.getAppId());
		String userId = userInfoHolder.getUserId();
		failTopicEntity.setInsertBy(userId);
		if (getCache().containsKey(failTopicEntity.getName())) {
			uiAuditLogService.recordAudit("consumer_group", consumerGroup.getId(),
					"失败topic已经存在，" + JsonUtil.toJson(failTopicEntity));
			return getCache().get(failTopicEntity.getName());
		} else {
			insert(failTopicEntity);
			distributeQueueWithLock(failTopicEntity, 2, 2);
			uiAuditLogService.recordAudit("consumer_group", consumerGroup.getId(),
					"新建失败topic，" + JsonUtil.toJson(failTopicEntity));
			return getTopicByName(failTopicEntity.getName());
		}

	}

	@Override
	public void distributeQueueWithLock(TopicEntity topicEntity, int queueNum, int nodeType) {
		log.info("distributeQueueWithLock start; topicId: " + topicEntity.getId() + "; queueNum: " + queueNum);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
				String.format("开始分配, 分配队列 %d 条", queueNum));
		int retryTime = 10;
		int unselectedSize = queueNum;
		while (retryTime > 0) {
			try {
				while (unselectedSize > 0) {
					uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
							String.format("剩余分配队列 %d 条", unselectedSize));
					// 获取可分配的queue
					List<QueueEntity> preDistributedQueueList = queueService.getTopUndistributed(unselectedSize,
							nodeType, topicEntity.getId());
					log.info("ready to distribute queue queueId: " + JsonUtil.toJson(preDistributedQueueList));
					uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
							"ready to distribute queue queueId: " + JsonUtil.toJson(preDistributedQueueList.stream().map(QueueEntity::getId).collect(Collectors.toList())));
					if (CollectionUtils.isEmpty(preDistributedQueueList)) {
						throw new RuntimeException("topic创建成功，队列分配失败。数据节点不够分配，请联系管理员");
					}
					// 更新queue中的Topic字段
					distributeQueue(topicEntity, preDistributedQueueList);
					preDistributedQueueList.forEach(queueEntity -> {
						uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
								"分配queue: " + queueEntity.getId());
					});
					// 更新未分配的队列数
					unselectedSize -= preDistributedQueueList.size();
				}
				return;
			} catch (ConcurrentException e) {
				retryTime--;
			}
		}
		throw new RuntimeException("topic创建成功，队列分配失败。目前队列分配频繁，抢占严重，请稍后再手动启动分配队列");
	}

	@Override
	public long getMsgCount(String topicName, String start, String end) {
		List<QueueEntity> data= queueService.getAllLocatedTopicQueue().get(topicName);
		long count=0;
		if(data!=null&&start!=null&&end!=null) {
			for(QueueEntity queueEntity :data) {
				message01Service.setDbId(queueEntity.getDbNodeId());
				List<Message01Entity> msgs=message01Service.getListByTime(queueEntity.getTbName(),start);
				if(!CollectionUtils.isEmpty(msgs)) {
					count=count-msgs.get(0).getId();
				}
				message01Service.setDbId(queueEntity.getDbNodeId());
				msgs=message01Service.getListByTime(queueEntity.getTbName(),end);
				if(!CollectionUtils.isEmpty(msgs)) {
					count=count+msgs.get(0).getId();
				}
				Util.sleep(10);
			}
		}
		return count;
	}

}
