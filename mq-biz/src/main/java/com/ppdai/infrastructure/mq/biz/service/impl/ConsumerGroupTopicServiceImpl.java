package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupTopicRepository;
import com.ppdai.infrastructure.mq.biz.dto.UserRoleEnum;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupTopicCreateResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupTopicDeleteResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;
import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
import com.ppdai.infrastructure.mq.biz.service.common.MqReadMap;

/**
 * @author dal-generator
 */
@Service
public class ConsumerGroupTopicServiceImpl extends AbstractBaseService<ConsumerGroupTopicEntity>
		implements CacheUpdateService, ConsumerGroupTopicService, TimerService {
	private Logger log = LoggerFactory.getLogger(ConsumerGroupTopicServiceImpl.class);
	@Autowired
	private ConsumerGroupTopicRepository consumerGroupTopicRepository;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private AuditLogService uiAuditLogService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserInfoHolder userInfoHolder;
	@Autowired
	private TopicService topicService;
	@Autowired
	private QueueOffsetService queueOffsetService;
	
	protected volatile boolean isRunning = true; 
	protected AtomicReference<Map<Long, Map<String, ConsumerGroupTopicEntity>>> consumerGroupTopicRefMap = new AtomicReference<>(
			new HashMap<>());

	protected AtomicReference<Map<String, ConsumerGroupTopicEntity>> groupTopicRefMap = new AtomicReference<>(
			new HashMap<>());

	protected AtomicReference<Map<String, List<ConsumerGroupTopicEntity>>> topicSubscribeRefMap = new AtomicReference<>(
			new HashMap<>());

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("ConsumerGroupTopicService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	private TraceMessage consumerGroupTopicCacheTrace = TraceFactory.getInstance("consumerGroupTopicCache");
	
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	//private TraceMessage traceUiConsumerGroupTopic = TraceFactory.getInstance("consumerGroupTopicServiceImpl");

	@PostConstruct
	protected void init() {
		super.setBaseRepository(consumerGroupTopicRepository);
	}

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			updateCache();
			executor.submit(new Runnable() {
				@Override
				public void run() {
					while (isRunning) {
						try {
							updateCache();
						} catch (Throwable e) {
							log.error("ConsumerGroupTopicService_updateCache_error", e);
						}
						Util.sleep(soaConfig.getMqConsumerGroupTopicCacheInterval());
					}
				}
			});
		}
	}

	private AtomicBoolean updateFlag = new AtomicBoolean(false);

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
		Transaction transaction = Tracer.newTransaction("Timer", "ConsumerGroupTopic-updateCache");
		TraceMessageItem traceMessageItem = new TraceMessageItem();
		try {
			List<ConsumerGroupTopicEntity> consumerGroupEntities = getList();
			MqReadMap<Long, Map<String, ConsumerGroupTopicEntity>> dataMap = new MqReadMap<>(consumerGroupEntities.size()/3);
			MqReadMap<String, ConsumerGroupTopicEntity> groupTopicMap = new MqReadMap<>(consumerGroupEntities.size());
			MqReadMap<String, List<ConsumerGroupTopicEntity>> topicSubscribeMap = new MqReadMap<>(consumerGroupEntities.size());

			consumerGroupEntities.forEach(t1 -> {
				if (!dataMap.containsKey(t1.getConsumerGroupId())) {
					dataMap.put(t1.getConsumerGroupId(), new HashMap<>());
				}
				dataMap.get(t1.getConsumerGroupId()).put(t1.getTopicName(), t1);

				if (!groupTopicMap.containsKey(t1.getConsumerGroupName() + "_" + t1.getTopicName())) {
					groupTopicMap.put(t1.getConsumerGroupName() + "_" + t1.getTopicName(), t1);
				}

				if(!topicSubscribeMap.containsKey(t1.getTopicName())){
					topicSubscribeMap.put(t1.getTopicName(),new ArrayList<>());
				}
				topicSubscribeMap.get(t1.getTopicName()).add(t1);

			});
			dataMap.setOnlyRead();
			groupTopicMap.setOnlyRead();
			consumerGroupTopicRefMap.set(dataMap);
			groupTopicRefMap.set(groupTopicMap);
			topicSubscribeRefMap.set(topicSubscribeMap);
			traceMessageItem.status = "count-" + groupTopicMap.size();
			consumerGroupTopicCacheTrace.add(traceMessageItem);
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
			lastUpdateEntity = null;
		} finally {
			transaction.complete();
		}
	}

	protected volatile LastUpdateEntity lastUpdateEntity = null;
	protected long lastTime=System.currentTimeMillis();
	protected boolean checkChanged() {		
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
		Transaction transaction = Tracer.newTransaction("Timer", "ConsumerGroupTopic-checkChanged");
		boolean flag = false;
		try {
			LastUpdateEntity temp = consumerGroupTopicRepository.getLastUpdate();
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
		if(!flag && consumerGroupTopicRefMap.get().size()==0){
			log.warn("consumerGroupTopic数据为空，请注意！");			
			return true;
		}
		return flag;
	}

	private Lock cacheLock = new ReentrantLock();
	protected AtomicBoolean first = new AtomicBoolean(true);

	//第一级key为consumergroupid，第二级key为topic名称，value为ConsumerGroupTopicEntity
	@Override
	public Map<Long, Map<String, ConsumerGroupTopicEntity>> getCache() {
		// TODO Auto-generated method stub
		// return consumerGroupRefMap.get();
		Map<Long, Map<String, ConsumerGroupTopicEntity>> rs = consumerGroupTopicRefMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = consumerGroupTopicRefMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = consumerGroupTopicRefMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public Map<String, ConsumerGroupTopicEntity> getGroupTopic() {
		// TODO Auto-generated method stub
		// return groupTopicRefMap.get();

		Map<String, ConsumerGroupTopicEntity> rs = groupTopicRefMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = groupTopicRefMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = groupTopicRefMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public void deleteByConsumerGroupId(long consumerGroupId) {
		consumerGroupTopicRepository.deleteByConsumerGroupId(consumerGroupId);
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupId,
				"取消consumerGroup的所有topic订阅，即删除该consumerGroup在consumerGroupTopic表中的信息");
	}


	@Override
	public void deleteByOriginTopicName(long consumerGroupId, String originTopicName) {
		consumerGroupTopicRepository.deleteByOriginTopicName(consumerGroupId, originTopicName);
	}

	@Override
	public List<String> getFailTopicNames(long consumerGroupId) {
		return consumerGroupTopicRepository.getFailTopicNames(consumerGroupId);
	}

	@Override
	public ConsumerGroupTopicEntity getCorrespondConsumerGroupTopic(Map<String, Object> parameterMap) {
		return consumerGroupTopicRepository.getCorrespondConsumerGroupTopic(parameterMap);
	}

	@Override
	public void updateEmailByGroupName(String groupName, String alarmEmails) {
		consumerGroupTopicRepository.updateEmailByGroupName(groupName, alarmEmails);
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

	@Override
	public Map<String, List<ConsumerGroupTopicEntity>> getTopicSubscribeMap(){
		return topicSubscribeRefMap.get();
	}

	/**
	 * 添加订阅
	 *
	 * @param consumerGroupTopicCreateRequest
	 * @return
	 */
	 @Transactional(rollbackFor = Exception.class)
	 @Override
	 public ConsumerGroupTopicCreateResponse subscribe(ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest) {
	     return subscribe(consumerGroupTopicCreateRequest, consumerGroupService.getCache());

     }
	 @Override
	 public ConsumerGroupTopicCreateResponse subscribe(ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest,Map<String, ConsumerGroupEntity> consumerGroupMap) {
	    // 如果是广播模式，并且为原始消费者组，则为镜像消费者组添加订阅
	    ConsumerGroupEntity consumerGroupEntity = consumerGroupMap
	                .get(consumerGroupTopicCreateRequest.getConsumerGroupName());
	    if (consumerGroupEntity.getMode() == 2
	                && consumerGroupEntity.getOriginName().equals(consumerGroupEntity.getName())) {
	           createConsumerGroupTopicByOrigin(consumerGroupTopicCreateRequest, consumerGroupMap);
	     }

	     return createConsumerGroupTopicAndFailTopic(consumerGroupTopicCreateRequest, consumerGroupMap);
	 }	    

	/**
	 * 镜像消费者组添加订阅
	 * @param consumerGroupTopicCreateRequest
	 * @param consumerGroupMap
	 */
	private void createConsumerGroupTopicByOrigin(ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest, Map<String, ConsumerGroupEntity> consumerGroupMap){
		for (String key : consumerGroupMap.keySet()) {
			//原始消费者组的订阅不在此处实现
			if(consumerGroupMap.get(key)!=null){
				if(consumerGroupMap.get(key).getOriginName().equals(consumerGroupTopicCreateRequest.getConsumerGroupName())&&
						consumerGroupMap.get(key).getId()!= consumerGroupTopicCreateRequest.getConsumerGroupId()){
					ConsumerGroupTopicCreateRequest request=new ConsumerGroupTopicCreateRequest();
					request.setConsumerGroupName(consumerGroupMap.get(key).getName());
					request.setConsumerGroupId(consumerGroupMap.get(key).getId());
					request.setTopicId(consumerGroupTopicCreateRequest.getTopicId());
					request.setTopicName(consumerGroupTopicCreateRequest.getTopicName());
					request.setOriginTopicName(consumerGroupTopicCreateRequest.getOriginTopicName());
					request.setTopicType(consumerGroupTopicCreateRequest.getTopicType());
					request.setRetryCount(consumerGroupTopicCreateRequest.getRetryCount());
					request.setThreadSize(consumerGroupTopicCreateRequest.getThreadSize());
					request.setMaxLag(consumerGroupTopicCreateRequest.getMaxLag());
					request.setTag(consumerGroupTopicCreateRequest.getTag());
					request.setDelayProcessTime(consumerGroupTopicCreateRequest.getDelayProcessTime());
					request.setPullBatchSize(consumerGroupTopicCreateRequest.getPullBatchSize());
					request.setAlarmEmails(consumerGroupTopicCreateRequest.getAlarmEmails());
					request.setDelayPullTime(consumerGroupTopicCreateRequest.getDelayPullTime());
					request.setTimeOut(consumerGroupTopicCreateRequest.getTimeOut());
					createConsumerGroupTopicAndFailTopic(request,consumerGroupMap);
				}
			}


		}
	}


	protected ConsumerGroupTopicCreateResponse createConsumerGroupTopicAndFailTopic(
            ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest, Map<String, ConsumerGroupEntity> consumerGroupMap) {
		CacheUpdateHelper.updateCache();
		if (roleService.getRole(userInfoHolder.getUserId(), consumerGroupMap
				.get(consumerGroupTopicCreateRequest.getConsumerGroupName()).getOwnerIds()) >= UserRoleEnum.USER
				.getRoleCode()) {
			throw new RuntimeException(userInfoHolder.getUserId() + "没有操作权限，请进行权限检查。");
		}
		// 创建正常topic对应的consumerGroupTopic
		if (StringUtils.isEmpty(consumerGroupTopicCreateRequest.getTopicName())) {
			return new ConsumerGroupTopicCreateResponse("1", "主题不能为空");
		}
		ConsumerGroupTopicEntity consumerGroupTopicEntity = createConsumerGroupTopic(consumerGroupTopicCreateRequest);
		ConsumerGroupEntity consumerGroupEntity = consumerGroupService
				.get(consumerGroupTopicCreateRequest.getConsumerGroupId());
		TopicEntity topicEntity = topicService.get(consumerGroupTopicCreateRequest.getTopicId());
		TopicEntity failTopicEntity = topicService.createFailTopic(topicEntity, consumerGroupEntity);
		// 创建失败topic的consumerGroupTopic
		consumerGroupTopicCreateRequest.setTopicId(failTopicEntity.getId());
		consumerGroupTopicCreateRequest.setTopicName(failTopicEntity.getName());
		consumerGroupTopicCreateRequest.setTopicType(failTopicEntity.getTopicType());
		consumerGroupTopicCreateRequest.setDelayProcessTime(soaConfig.getFailTopicDelayProcessTime());
		consumerGroupTopicCreateRequest.setThreadSize(soaConfig.getFailTopicThreadSize());
		ConsumerGroupTopicEntity failConsumerGroupTopicEntity = createConsumerGroupTopic(
                consumerGroupTopicCreateRequest);

		ConsumerGroupTopicCreateResponse consumerGroupTopicCreateResponse=new ConsumerGroupTopicCreateResponse();
		try{
			// 创建正常topic对应的queueOffset
			queueOffsetService.createQueueOffset(consumerGroupTopicEntity);
			// 创建失败topic的queueOffset
			queueOffsetService.createQueueOffset(failConsumerGroupTopicEntity);
			consumerGroupService.addTopicNameToConsumerGroup(consumerGroupTopicEntity);
			consumerGroupService.notifyMeta(consumerGroupTopicCreateRequest.getConsumerGroupId());
			consumerGroupService.notifyRb(consumerGroupTopicCreateRequest.getConsumerGroupId());
		}catch (Exception e){
			consumerGroupTopicCreateResponse.setMsg(e.getMessage());
			consumerGroupTopicCreateResponse.setCode("1");
			throw new RuntimeException(e);
		}

		return consumerGroupTopicCreateResponse;
	}

	@Override
	public ConsumerGroupTopicEntity createConsumerGroupTopic(
			ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest) {
		ConsumerGroupTopicEntity consumerGroupTopicEntity = new ConsumerGroupTopicEntity();
		consumerGroupTopicEntity.setConsumerGroupId(consumerGroupTopicCreateRequest.getConsumerGroupId());
		consumerGroupTopicEntity.setConsumerGroupName(consumerGroupTopicCreateRequest.getConsumerGroupName());
		consumerGroupTopicEntity.setTopicId(consumerGroupTopicCreateRequest.getTopicId());
		consumerGroupTopicEntity.setTopicName(consumerGroupTopicCreateRequest.getTopicName());
		consumerGroupTopicEntity.setOriginTopicName(consumerGroupTopicCreateRequest.getOriginTopicName());
		consumerGroupTopicEntity.setTopicType(consumerGroupTopicCreateRequest.getTopicType());
		consumerGroupTopicEntity.setMaxPullTime(consumerGroupTopicCreateRequest.getDelayPullTime());
		consumerGroupTopicEntity.setTimeOut(consumerGroupTopicCreateRequest.getTimeOut());
		if (consumerGroupTopicCreateRequest.getRetryCount() != null) {
			consumerGroupTopicEntity.setRetryCount(consumerGroupTopicCreateRequest.getRetryCount());
		}
		if (consumerGroupTopicCreateRequest.getThreadSize() != null) {
			consumerGroupTopicEntity.setThreadSize(consumerGroupTopicCreateRequest.getThreadSize());
		}
		if (consumerGroupTopicCreateRequest.getMaxLag() != null) {
			consumerGroupTopicEntity.setMaxLag(consumerGroupTopicCreateRequest.getMaxLag());
		}
		if (StringUtils.isNotEmpty(consumerGroupTopicCreateRequest.getTag())) {
			consumerGroupTopicEntity.setTag(consumerGroupTopicCreateRequest.getTag());
		}
		if (consumerGroupTopicCreateRequest.getDelayProcessTime() != null) {
			consumerGroupTopicEntity.setDelayProcessTime(consumerGroupTopicCreateRequest.getDelayProcessTime());
		}
		if (consumerGroupTopicCreateRequest.getPullBatchSize() != null) {
			consumerGroupTopicEntity.setPullBatchSize(consumerGroupTopicCreateRequest.getPullBatchSize());
		}
		consumerGroupTopicEntity.setConsumerBatchSize(consumerGroupTopicCreateRequest.getConsumerBatchSize());
		String userId = userInfoHolder.getUserId();
		consumerGroupTopicEntity.setInsertBy(userId);
		consumerGroupTopicEntity.setAlarmEmails(consumerGroupTopicCreateRequest.getAlarmEmails());
		Map<String, ConsumerGroupTopicEntity> groupTopicMap = getGroupTopic();
		if (groupTopicMap.containsKey(consumerGroupTopicCreateRequest.getConsumerGroupName() + "_"
				+ consumerGroupTopicCreateRequest.getTopicName())) {
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupTopicEntity.getConsumerGroupId(),
					consumerGroupTopicEntity.getConsumerGroupName() + "," + consumerGroupTopicEntity.getTopicName()
							+ ",已经存在！");
			return groupTopicMap.get(consumerGroupTopicCreateRequest.getConsumerGroupName() + "_"
					+ consumerGroupTopicCreateRequest.getTopicName());
		} else {
			insert(consumerGroupTopicEntity);
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupTopicEntity.getConsumerGroupId(),
					"添加" + consumerGroupTopicEntity.getConsumerGroupName() + "对"
							+ consumerGroupTopicEntity.getTopicName() + "订阅."
							+ JsonUtil.toJson(consumerGroupTopicEntity));
			return consumerGroupTopicEntity;
		}

	}

	/**
	 * 取消订阅
	 *
	 * @param consumerGroupTopicId
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public ConsumerGroupTopicDeleteResponse deleteConsumerGroupTopic(long consumerGroupTopicId) {
		CacheUpdateHelper.updateCache();
		Transaction catTransactionAll = Tracer.newTransaction("ConsumerGroupTopic", "deleteConsumerGroupTopic");
		ConsumerGroupTopicDeleteResponse response=new ConsumerGroupTopicDeleteResponse();
		response.setCode("0");
		try {
			ConsumerGroupTopicEntity consumerGroupTopicEntity = consumerGroupTopicRepository
					.getById(consumerGroupTopicId);			
			if(consumerGroupTopicEntity==null){
				return new ConsumerGroupTopicDeleteResponse();
			}
			// 缓存数据
			Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
			String key = consumerGroupTopicEntity.getConsumerGroupName();
			if (roleService.getRole(userInfoHolder.getUserId(), consumerGroupMap
					.get(key).getOwnerIds()) >= UserRoleEnum.USER
					.getRoleCode()) {
				throw new RuntimeException("没有操作权限，请进行权限检查。");
			}
			ConsumerGroupEntity consumerGroupEntity=consumerGroupMap.get(key);
			//如果是广播模式，并且是原始消费者组，则对原始组下的所有镜像组取消订阅
			if(consumerGroupEntity.getMode()==2&&consumerGroupEntity.getOriginName().equals(consumerGroupEntity.getName())){
				deleteByOrigin(consumerGroupMap,consumerGroupEntity,consumerGroupTopicEntity);
			}
            doDelete(consumerGroupTopicEntity);
			catTransactionAll.setStatus(Transaction.SUCCESS);
			CacheUpdateHelper.updateCache();
		} catch (Exception e) {
			catTransactionAll.setStatus(e);
			response.setCode("1");
			response.setMsg(e.getMessage());
		} finally {
			catTransactionAll.complete();
		}		
		return response;
	}

	private void  deleteByOrigin(Map<String, ConsumerGroupEntity> consumerGroupMap,ConsumerGroupEntity originConsumerGroupEntity,
								 ConsumerGroupTopicEntity originConsumerGroupTopicEntity){
			Map<String, List<ConsumerGroupTopicEntity>> topicSubscribeMap= getTopicSubscribeMap();
			//被取消订阅的topic，所对应的的consumerGroupTopic
			List<ConsumerGroupTopicEntity> groupTopicList=topicSubscribeMap.get(originConsumerGroupTopicEntity.getOriginTopicName());
			for (ConsumerGroupTopicEntity groupTopic: groupTopicList) {
				//如果是镜像消费者组
				if(consumerGroupMap.get(groupTopic.getConsumerGroupName()).getOriginName().equals(originConsumerGroupEntity.getName())){
					//排除原始组的 取消订阅实现
					if(groupTopic.getId()!=originConsumerGroupTopicEntity.getId()){
						doDelete(groupTopic);
					}
				}
			}

	}


	/**
	 * 删除consumerGroupTopic
	 * @param consumerGroupTopicEntity
	 */
	protected void doDelete(ConsumerGroupTopicEntity consumerGroupTopicEntity){
		List<String> failTopicNames = new ArrayList<>();
		// 删除失败topic，并且清理失败消息并且解绑失败topic
		failTopicNames.add(TopicUtil.getFailTopicName(consumerGroupTopicEntity.getConsumerGroupName(),consumerGroupTopicEntity.getOriginTopicName()));
		Transaction catTransaction1 = Tracer.newTransaction("ConsumerGroupTopic", "deleteFailTopic");
		try {
			topicService.deleteFailTopic(failTopicNames, consumerGroupTopicEntity.getConsumerGroupId());
			catTransaction1.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			catTransaction1.setStatus(e);
			throw new RuntimeException("操作失败请重试。");
		} finally {
			catTransaction1.complete();
		}

		Transaction catTransaction2 = Tracer.newTransaction("ConsumerGroupTopic",
				"deleteByConsumerGroupIdAndOriginTopicName");
		try {
			// 清除topic和失败topic的queueOffset
			queueOffsetService.deleteByConsumerGroupIdAndOriginTopicName(consumerGroupTopicEntity);
			catTransaction2.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			catTransaction2.setStatus(e);
			throw new RuntimeException("操作失败请重试。");
		} finally {
			catTransaction2.complete();
		}

		Transaction catTransaction3 = Tracer.newTransaction("ConsumerGroupTopic",
				"deleteTopicNameFromConsumerGroup");
		try {
			// 更新consumerGroup中的topic字段
			consumerGroupService.deleteTopicNameFromConsumerGroup(consumerGroupTopicEntity);
			catTransaction3.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			catTransaction3.setStatus(e);
			throw new RuntimeException("操作失败请重试。");
		} finally {
			catTransaction3.complete();
		}

		Transaction catTransaction4 = Tracer.newTransaction("ConsumerGroupTopic", "deleteByOriginTopicName");
		try {
			// 删除正常topic和失败topic的consumerGroupTopic
			deleteByOriginTopicName(consumerGroupTopicEntity.getConsumerGroupId(),
					consumerGroupTopicEntity.getOriginTopicName());
			catTransaction4.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			catTransaction4.setStatus(e);
			throw new RuntimeException("操作失败请重试。");
		} finally {
			catTransaction4.complete();
		}

		Transaction catTransaction5 = Tracer.newTransaction("ConsumerGroupTopic", "deleteByOriginTopicName");
		try {
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,
					consumerGroupTopicEntity.getConsumerGroupId(),
					"取消consumerGroup：" + consumerGroupTopicEntity.getConsumerGroupName() + "对主题："
							+ consumerGroupTopicEntity.getOriginTopicName() + "的订阅"
							+ JsonUtil.toJson(consumerGroupTopicEntity));
			consumerGroupService.notifyMeta(consumerGroupTopicEntity.getConsumerGroupId());
			consumerGroupService.notifyRb(consumerGroupTopicEntity.getConsumerGroupId());
			catTransaction5.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			catTransaction5.setStatus(e);
			throw new RuntimeException("操作失败请重试。");
		} finally {
			catTransaction5.complete();
		}

	}

}
