package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerGroupUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupRepository;
import com.ppdai.infrastructure.mq.biz.dto.UserRoleEnum;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupCreateResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupDeleteResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupEditResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;
import com.ppdai.infrastructure.mq.biz.service.common.AuditUtil;
import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
import com.ppdai.infrastructure.mq.biz.service.common.MessageType;
import com.ppdai.infrastructure.mq.biz.service.common.MqReadMap;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;

/**
 * @author dal-generator
 */
@Service
public class ConsumerGroupServiceImpl extends AbstractBaseService<ConsumerGroupEntity>
		implements CacheUpdateService, ConsumerGroupService, TimerService {
	private Logger log = LoggerFactory.getLogger(ConsumerGroupServiceImpl.class);

	@Autowired
	private ConsumerGroupRepository consumerGroupRepository;
	@Autowired
	private NotifyMessageService notifyMessageService;
	@Autowired
	private ConsumerGroupTopicService consumerGroupTopicService;
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private UserInfoHolder userInfoHolder;
	@Autowired
	private AuditLogService uiAuditLogService;
	@Autowired
	private TopicService topicService;
	@Autowired
	private ConsumerService consumerService;
	@Autowired
	private RoleService roleService;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private AtomicBoolean updateFlag = new AtomicBoolean(false);
	protected volatile boolean isRunning = true;
	protected AtomicReference<Map<String, ConsumerGroupEntity>> consumerGroupRefMap = new AtomicReference<>(
			new HashMap<>());
	protected AtomicReference<Map<Long, ConsumerGroupEntity>> consumerGroupByIdRefMap = new AtomicReference<>(
			new HashMap<>());

	private AtomicReference<List<String>> subEnvList = new AtomicReference<>(new ArrayList<>());

	protected ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("ConsumerGroupService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());

	private TraceMessage consumerGroupCacheTrace = TraceFactory.getInstance("consumerGroupCache");

	@PostConstruct
	protected void init() {
		super.setBaseRepository(consumerGroupRepository);
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
							log.error("ConsumerGroupServiceImpl_doUpdateCache_error", e);
						}
						Util.sleep(soaConfig.getMqConsumerGroupCacheInterval());
					}
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
		Transaction transaction = Tracer.newTransaction("Timer", "ConsumerGroup-updateCache");
		TraceMessageItem traceMessageItem = new TraceMessageItem();
		try {
			List<ConsumerGroupEntity> consumerGroupEntities = getList();
			MqReadMap<String, ConsumerGroupEntity> dataMap = new MqReadMap<>(consumerGroupEntities.size());
			MqReadMap<Long, ConsumerGroupEntity> dataIdMap = new MqReadMap<>(consumerGroupEntities.size());
			List<String> envList = new ArrayList<>();
			envList.add(MqConst.DEFAULT_SUBENV);
			consumerGroupEntities.forEach(t1 -> {
				dataMap.put(t1.getName(), t1);
				dataIdMap.put(t1.getId(), t1);
				if (!envList.contains(t1.getSubEnv())) {
					envList.add(t1.getSubEnv());
				}
			});
			dataMap.setOnlyRead();
			dataIdMap.setOnlyRead();
			if (dataMap.size() > 0 && dataIdMap.size() > 0) {
				consumerGroupRefMap.set(dataMap);
				consumerGroupByIdRefMap.set(dataIdMap);
				subEnvList.set(envList);
			}else {
				lastUpdateEntity = null;
			}
			traceMessageItem.status = "count-" + dataMap.size();
			consumerGroupCacheTrace.add(traceMessageItem);			
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
			lastUpdateEntity = null;
		} finally {
			transaction.complete();
		}
	}

	protected volatile LastUpdateEntity lastUpdateEntity = null;
	protected long lastTime = System.currentTimeMillis();

	protected boolean checkChanged() {
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
		Transaction transaction = Tracer.newTransaction("Timer", "ConsumerGroup-checkChanged");
		boolean flag = false;
		try {
			LastUpdateEntity temp = consumerGroupRepository.getLastUpdate();
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
		if (!flag && consumerGroupRefMap.get().size() == 0) {
			log.warn("consumerGroup数据为空，请注意！");
			return true;
		}
		return flag;
	}

	@Override
	public List<ConsumerGroupEntity> getLastMetaConsumerGroup(long minMessageId, long maxMessageId) {
		return consumerGroupRepository.getLastConsumerGroup(minMessageId, maxMessageId, MessageType.Meta);
	}

	@Override
	public List<ConsumerGroupEntity> getLastRbConsumerGroup(long minMessageId, long maxMessageId) {
		return consumerGroupRepository.getLastConsumerGroup(minMessageId, maxMessageId, MessageType.Rb);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rb(List<QueueOffsetEntity> queueOffsetEntities) {
		Map<Long, String> idsMap = new HashMap<>(30);
		List<NotifyMessageEntity> notifyMessageEntities = new ArrayList<>(30);
		queueOffsetEntities.forEach(t1 -> {
			idsMap.put(t1.getConsumerGroupId(), "");
			NotifyMessageEntity notifyMessageEntity = new NotifyMessageEntity();
			notifyMessageEntity.setConsumerGroupId(t1.getConsumerGroupId());
			notifyMessageEntity.setMessageType(MessageType.Meta);
			notifyMessageEntities.add(notifyMessageEntity);
			// 更新consumerid 和consumername
			queueOffsetService.updateConsumerId(t1);
		});
		// 更新重平衡版本,注意这个代码非常的重要，这个可以保证客户端能够拿到最新的重平衡版本号
		updateRbVersion(new ArrayList<>(idsMap.keySet()));
		// 批量插入消息事件
		notifyMessageService.insertBatch(notifyMessageEntities);

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void notifyRb(long id) {
		notifyRb(Arrays.asList(id));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void notifyRb(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids))
			return;
		updateRbVersion(ids);
		List<NotifyMessageEntity> notifyMessageEntities = new ArrayList<>();
		ids.forEach(id -> {
			NotifyMessageEntity notifyMessageEntity = new NotifyMessageEntity();
			notifyMessageEntity.setConsumerGroupId(id);
			notifyMessageEntity.setMessageType(MessageType.Rb);
			notifyMessageEntities.add(notifyMessageEntity);

			notifyMessageEntity = new NotifyMessageEntity();
			notifyMessageEntity.setConsumerGroupId(id);
			notifyMessageEntity.setMessageType(MessageType.Meta);
			notifyMessageEntities.add(notifyMessageEntity);
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,id,"此消费者组需要重平衡！");
		});
		notifyMessageService.insertBatch(notifyMessageEntities);
	}

	protected void updateRbVersion(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids))
			return;
		consumerGroupRepository.updateRbVersion(ids);
		ids.forEach(id->{
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,id,"此消费者组需要重平衡！");
		});

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void notifyMeta(long id) {
		updateMetaVersion(Arrays.asList(id));
		NotifyMessageEntity notifyMessageEntity = new NotifyMessageEntity();
		notifyMessageEntity.setConsumerGroupId(id);
		notifyMessageEntity.setMessageType(MessageType.Meta);
		notifyMessageService.insert(notifyMessageEntity);
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,id,"此消费者组元数据发生变更！");
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void notifyMeta(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids))
			return;
		updateMetaVersion(ids);
		List<NotifyMessageEntity> notifyMessageEntities = new ArrayList<>();
		ids.forEach(id -> {
			NotifyMessageEntity notifyMessageEntity = new NotifyMessageEntity();
			notifyMessageEntity.setConsumerGroupId(id);
			notifyMessageEntity.setMessageType(MessageType.Meta);
			notifyMessageEntities.add(notifyMessageEntity);
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,id,"此消费者组元数据发生变更！");

		});
		notifyMessageService.insertBatch(notifyMessageEntities);
	}

	protected void updateMetaVersion(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids))
			return;
		consumerGroupRepository.updateMetaVersion(ids);
		ids.forEach(id->{
			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME,id,"此消费者组元数据发生变更！");
		});
	}

	@Override
	public Map<String, ConsumerGroupEntity> getByNames(List<String> names) {
		if (CollectionUtils.isEmpty(names)) {
			return new HashMap<>();
		}
		List<ConsumerGroupEntity> consumerGroupEntities = consumerGroupRepository.getByNames(names);
		Map<String, ConsumerGroupEntity> map = new HashMap<>(consumerGroupEntities.size());
		consumerGroupEntities.forEach(t1 -> {
			map.put(t1.getName(), t1);
		});
		return map;
	}

	private Lock cacheLock = new ReentrantLock();
	private AtomicBoolean first = new AtomicBoolean(true);

	@Override
	public Map<String, ConsumerGroupEntity> getCache() {
		// TODO Auto-generated method stub
		Map<String, ConsumerGroupEntity> rs = consumerGroupRefMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = consumerGroupRefMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = consumerGroupRefMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public Map<Long, ConsumerGroupEntity> getIdCache() {
		// TODO Auto-generated method stub
		Map<Long, ConsumerGroupEntity> rs = consumerGroupByIdRefMap.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = consumerGroupByIdRefMap.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = consumerGroupByIdRefMap.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public List<String> getSubEnvList() {
		List<String> rs = subEnvList.get();
		if (rs.size() == 0) {
			cacheLock.lock();
			try {
				rs = subEnvList.get();
				if (rs.size() == 0) {
					if (first.compareAndSet(true, false)) {
						updateCache();
					}
					rs = subEnvList.get();
				}
			} finally {
				cacheLock.unlock();
			}
		}
		return rs;
	}

	@Override
	public void notifyOffset(long id) {
		notifyMeta(id);
	}

	@Override
	public List<ConsumerGroupTopicEntity> getGroupTopic() {
		return consumerGroupTopicService.getList();
	}

	@Override
	public ConsumerGroupTopicEntity getTopic(String consumerGroupName, String topicName) {
		Map<String, ConsumerGroupEntity> cache = getCache();
		if (!cache.containsKey(consumerGroupName)) {
			return null;
		}
		if (!consumerGroupTopicService.getCache().containsKey(cache.get(consumerGroupName).getId())) {
			return null;
		}
		return consumerGroupTopicService.getCache().get(cache.get(consumerGroupName).getId()).get(topicName);
	}

	@PreDestroy
	@Override
	public void stop() {
		isRunning = false;
	}

	@Override
	public List<ConsumerGroupEntity> getByOwnerNames(Map<String, Object> parameterMap) {
		return consumerGroupRepository.getByOwnerNames(parameterMap);
	}

	@Override
	public long countByOwnerNames(Map<String, Object> parameterMap) {
		return consumerGroupRepository.countByOwnerNames(parameterMap);
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyRbByNames(List<String> consumerGroupNames) {
		if (CollectionUtils.isEmpty(consumerGroupNames))
			return;
		List<Long> ids = new ArrayList<>();
		Map<String, ConsumerGroupEntity> cacheData = consumerGroupRefMap.get();
		if (cacheData == null || cacheData.size() == 0)
			return;
		consumerGroupNames.forEach(t1 -> {
			if (cacheData.containsKey(t1)) {
				ids.add(cacheData.get(t1).getId());
			}
		});
		notifyRb(ids);
	}
	@Override
	public void notifyMetaByNames(List<String> consumerGroupNames) {
		if (CollectionUtils.isEmpty(consumerGroupNames))
			return;
		List<Long> ids = new ArrayList<>();
		Map<String, ConsumerGroupEntity> cacheData = consumerGroupRefMap.get();
		if (cacheData == null || cacheData.size() == 0)
			return;
		consumerGroupNames.forEach(t1 -> {
			if (cacheData.containsKey(t1)) {
				ids.add(cacheData.get(t1).getId());
			}
		});
		notifyMeta(ids);
	}
	@Override
	public String getCacheJson() {
		// TODO Auto-generated method stub
		return JsonUtil.toJsonNull(getCache());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public ConsumerGroupCreateResponse createConsumerGroup(ConsumerGroupCreateRequest consumerGroupCreateRequest) {
		CacheUpdateHelper.updateCache();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupCreateRequest.setName(StringUtils.trim(consumerGroupCreateRequest.getName()));
		consumerGroupEntity.setName(consumerGroupCreateRequest.getName());
		consumerGroupEntity.setOwnerIds(consumerGroupCreateRequest.getOwnerIds());
		consumerGroupEntity.setOwnerNames(consumerGroupCreateRequest.getOwnerNames());
		consumerGroupEntity.setAlarmFlag(consumerGroupCreateRequest.getAlarmFlag());
		consumerGroupEntity.setTraceFlag(consumerGroupCreateRequest.getTraceFlag());
		consumerGroupEntity.setAlarmEmails(StringUtils.trim(consumerGroupCreateRequest.getAlarmEmails()));
		consumerGroupEntity.setAppId(consumerGroupCreateRequest.getAppId());
		consumerGroupEntity.setMode(consumerGroupCreateRequest.getMode());
		consumerGroupEntity.setPushFlag(consumerGroupCreateRequest.getPushFlag());
		consumerGroupEntity.setConsumerQuality(consumerGroupCreateRequest.getConsumerQuality());
		if (Util.isEmpty(consumerGroupCreateRequest.getSubEnv())) {
			consumerGroupCreateRequest.setSubEnv(MqConst.DEFAULT_SUBENV);
		}
		consumerGroupEntity.setSubEnv(consumerGroupCreateRequest.getSubEnv());
		if (consumerGroupCreateRequest.getMode() == 2) {// 广播模式
			consumerGroupEntity
					.setOriginName(ConsumerGroupUtil.getOriginConsumerName(consumerGroupCreateRequest.getName()));
		} else {
			consumerGroupEntity.setOriginName(consumerGroupCreateRequest.getName());
		}

		consumerGroupEntity.setTels(consumerGroupCreateRequest.getTels());
		consumerGroupEntity.setDptName(consumerGroupCreateRequest.getDptName());
		if (consumerGroupCreateRequest.getIpFlag() != null && consumerGroupCreateRequest.getIpFlag() == 1) {
			consumerGroupEntity.setIpWhiteList(null);
			consumerGroupEntity.setIpBlackList(StringUtils.trim(consumerGroupCreateRequest.getIpList()));
		} else if (consumerGroupCreateRequest.getIpFlag() != null && consumerGroupCreateRequest.getIpFlag() == 0) {
			consumerGroupEntity.setIpBlackList(null);
			consumerGroupEntity.setIpWhiteList(StringUtils.trim(consumerGroupCreateRequest.getIpList()));
		}
		consumerGroupEntity.setRemark(consumerGroupCreateRequest.getRemark());
		String userId = userInfoHolder.getUserId();
		if (StringUtils.isNotEmpty(consumerGroupCreateRequest.getId())) {
			consumerGroupEntity.setId(Long.valueOf(consumerGroupCreateRequest.getId()));
			consumerGroupEntity.setUpdateBy(userId);
			editConsumerGroup(consumerGroupEntity);
			notifyMeta(consumerGroupEntity.getId());
		} else {
			consumerGroupEntity.setInsertBy(userId);
			List<String> names = new ArrayList<>();
			names.add(consumerGroupEntity.getName());
			Map<String, ConsumerGroupEntity> checkEntityMap = getByNames(names);
			if (!checkEntityMap.isEmpty()) {
				throw new CheckFailException(
						"consumerGroup:" + consumerGroupEntity.getName() + "重复，检查是否有重名consumerGroup已经存在。");
			}
			try {
				insert(consumerGroupEntity);
			} catch (DuplicateKeyException e) {
				return new ConsumerGroupCreateResponse("1", "consumerGroup重复，检查是否有重名consumerGroup已经存在。");
			}
			// 查出新建consumerGroup的id,用于日志和更新元数据
			ArrayList<String> consumerGroupEntityNames = new ArrayList<>();
			consumerGroupEntityNames.add(consumerGroupEntity.getName());
			List<ConsumerGroupEntity> consumerGroupEntityList = consumerGroupRepository
					.getByNames(consumerGroupEntityNames);
			long consumerGroupId = consumerGroupEntityList.get(0).getId();

			uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupId, "新建consumerGroup："
					+ consumerGroupEntity.getName() + "." + JsonUtil.toJson(consumerGroupEntityList.get(0)));
			notifyMeta(consumerGroupId);
		}
		return new ConsumerGroupCreateResponse();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ConsumerGroupEditResponse editConsumerGroup(ConsumerGroupEntity consumerGroupEntity) {
		CacheUpdateHelper.updateCache();
		ConsumerGroupEntity oldConsumerGroupEntity = get(consumerGroupEntity.getId());
		if (roleService.getRole(userInfoHolder.getUserId(), oldConsumerGroupEntity.getOwnerIds()) >= UserRoleEnum.USER
				.getRoleCode()) {
			return new ConsumerGroupEditResponse("1", "没有操作权限，请进行权限检查。");
		}
		consumerGroupEntity.setTopicNames(oldConsumerGroupEntity.getTopicNames());
		consumerGroupEntity.setRbVersion(oldConsumerGroupEntity.getRbVersion());
		consumerGroupEntity.setMetaVersion(oldConsumerGroupEntity.getMetaVersion());
		consumerGroupEntity.setVersion(oldConsumerGroupEntity.getVersion());
		consumerGroupEntity.setInsertBy(oldConsumerGroupEntity.getInsertBy());
		consumerGroupEntity.setIsActive(oldConsumerGroupEntity.getIsActive());

		// 注意处于编辑模式此两项值不变。
		consumerGroupEntity.setSubEnv(oldConsumerGroupEntity.getSubEnv());
		consumerGroupEntity.setOriginName(oldConsumerGroupEntity.getOriginName());

		String userId = userInfoHolder.getUserId();
		consumerGroupEntity.setUpdateBy(userId);

		// 如果是广播模式则需要更新原始消费者组和镜像消费者组
		if (consumerGroupEntity.getMode() == 2) {
			// 根据OriginName去更新原始消费者组和镜像消费者组
			updateByOriginName(consumerGroupEntity);
		} else {
			update(consumerGroupEntity);
		}
		// 如果修改了appid字段，则需要修改consumerGroup对应的失败topic的AppId
		if (!StringUtils.equals(oldConsumerGroupEntity.getAppId(), consumerGroupEntity.getAppId())) {
			topicService.updateFailTopic(consumerGroupEntity);
		}

		// 如果修改了消费模式，则对应的queueoffset的消费模式也许修改
		if (consumerGroupEntity.getMode() != oldConsumerGroupEntity.getMode()) {
			Map<String, List<QueueOffsetEntity>> queueOffsetMap = queueOffsetService.getConsumerGroupQueueOffsetMap();
			for (QueueOffsetEntity queueOffset : queueOffsetMap.get(consumerGroupEntity.getName())) {
				queueOffset.setConsumerGroupMode(consumerGroupEntity.getMode());
				queueOffsetService.update(queueOffset);
			}
		}

		consumerGroupTopicService.updateEmailByGroupName(consumerGroupEntity.getName(),
				consumerGroupEntity.getAlarmEmails());
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, oldConsumerGroupEntity.getId(),
				"更新ConsumerGroup:" + consumerGroupEntity.getName() + ".更新信息："
						+ AuditUtil.diff(oldConsumerGroupEntity, consumerGroupEntity));
		notifyMeta(oldConsumerGroupEntity.getId());
		// 修改黑白名单触发重平衡
		if (!StringUtils.equals(consumerGroupEntity.getIpBlackList(), oldConsumerGroupEntity.getIpBlackList())
				|| !StringUtils.equals(consumerGroupEntity.getIpWhiteList(), oldConsumerGroupEntity.getIpWhiteList())) {
			checkVersionAndRb(oldConsumerGroupEntity.getId());
		}
		return new ConsumerGroupEditResponse();
	}

	private void updateByOriginName(ConsumerGroupEntity consumerGroupEntity) {
		consumerGroupRepository.updateByOriginName(consumerGroupEntity);
	}

	private void checkVersionAndRb(long id) {
//		ConsumerEntity consumerEntity = consumerService.getConsumerByConsumerGroupId(id);
//		// 只有高于4.1版本的客户端才会触发重平衡
//		if (consumerEntity != null && consumerEntity.getSdkVersion().compareTo("4.1") > 0) {
//			notifyRb(id);
//		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ConsumerGroupDeleteResponse deleteConsumerGroup(long consumerGroupId, boolean checkOnline) {
		CacheUpdateHelper.updateCache();
		// 缓存数据
		Map<String, ConsumerGroupEntity> consumerGroupMap = getCache();
		ConsumerGroupEntity consumerGroupEntity = get(consumerGroupId);

		// 如果是广播模式的原始组，判断是否存在镜像组。
		if (consumerGroupEntity.getMode() == 2
				&& consumerGroupEntity.getOriginName().equals(consumerGroupEntity.getName())) {
			for (String key : consumerGroupMap.keySet()) {
				// 如果存在镜像组时，不能删除原始组
				if (consumerGroupMap.get(key) != null) {
					if (consumerGroupMap.get(key).getOriginName().equals(consumerGroupEntity.getName())
							&& consumerGroupMap.get(key).getId() != consumerGroupEntity.getId()) {
						return new ConsumerGroupDeleteResponse("1", "存在镜像组时，不能删除原始组。");
					}
				}

			}
		}

		return doDelete(consumerGroupEntity, checkOnline);
	}

	private ConsumerGroupDeleteResponse doDelete(ConsumerGroupEntity consumerGroupEntity, boolean checkOnline) {
		if (checkOnline) {
			if (roleService.getRole(userInfoHolder.getUserId(), consumerGroupEntity.getOwnerIds()) >= UserRoleEnum.USER
					.getRoleCode()) {
				throw new RuntimeException();
			}
		}
		List<Long> consumerGroupIds = new ArrayList<>();
		consumerGroupIds.add(consumerGroupEntity.getId());
		if (checkOnline && consumerService.getConsumerGroupByConsumerGroupIds(consumerGroupIds).size() > 0) {
			return new ConsumerGroupDeleteResponse("1", "有消费者正在消费，不能删除消费者组。");
		}

		// 获取该消费者组下所有失败topic对应的originTopicName
		List<String> failTopicNames = consumerGroupTopicService.getFailTopicNames(consumerGroupEntity.getId());
		topicService.deleteFailTopic(failTopicNames, consumerGroupEntity.getId());
		queueOffsetService.deleteByConsumerGroupId(consumerGroupEntity.getId());
		consumerGroupTopicService.deleteByConsumerGroupId(consumerGroupEntity.getId());
		delete(consumerGroupEntity.getId());
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupEntity.getId(),
				"删除consumerGroup:" + consumerGroupEntity.getName() + "." + JsonUtil.toJson(consumerGroupEntity));
		notifyMeta(consumerGroupEntity.getId());
		return new ConsumerGroupDeleteResponse();
	}

	@Override
	public BaseUiResponse<Void> addTopicNameToConsumerGroup(ConsumerGroupTopicEntity consumerGroupTopicEntity) {
		ConsumerGroupEntity consumerGroupEntity = get(consumerGroupTopicEntity.getConsumerGroupId());
		String oldTopicNames = consumerGroupEntity.getTopicNames();

		// 如果mq3的group的订阅关系中，没有该topic则添加
		if (StringUtils.isNotEmpty(oldTopicNames)) {
			List<String> oldTopicNameList = Arrays.asList(oldTopicNames.split(","));
			if (!oldTopicNameList.contains(consumerGroupTopicEntity.getTopicName())) {
				consumerGroupEntity.setTopicNames(oldTopicNames + "," + consumerGroupTopicEntity.getTopicName());
			}
		} else {
			consumerGroupEntity.setTopicNames(consumerGroupTopicEntity.getTopicName());
		}
		update(consumerGroupEntity);
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupEntity.getId(),
				"新增订阅，修改consumerGroup的topic字段，从" + oldTopicNames + "变为：" + consumerGroupEntity.getTopicNames());

		return new BaseUiResponse<Void>();
	}

	private long lastCheckTime=System.currentTimeMillis();
	@Override
	public void deleteUnuseBroadConsumerGroup() {
		if(System.currentTimeMillis()-lastCheckTime>24*60*60*1000){
			lastCheckTime=System.currentTimeMillis();
			List<ConsumerGroupEntity> unuse=consumerGroupRepository.getUnuseBroadConsumerGroup();
			if(unuse.size()>0){
				unuse.forEach(t->{
					try{
					  deleteConsumerGroup(t.getId(),true);
						log.info("删除广播消费者组"+t.getName());
					}catch (Throwable e){
						e.printStackTrace();
					}
				});
				log.info("删除完毕！");
			}
		}
	}

	@Override
	public BaseUiResponse<Void> deleteTopicNameFromConsumerGroup(ConsumerGroupTopicEntity consumerGroupTopicEntity) {
		ConsumerGroupEntity consumerGroupEntity = get(consumerGroupTopicEntity.getConsumerGroupId());
		String oldTopicNames = consumerGroupEntity.getTopicNames();
		String[] names = oldTopicNames.split(",");
		String finalTopicNames = "";
		for (String name : names) {
			if (!name.equals(consumerGroupTopicEntity.getOriginTopicName())) {
				finalTopicNames += name;
				finalTopicNames += ",";
			}
		}
		if (StringUtils.isNotEmpty(finalTopicNames)) {
			finalTopicNames = finalTopicNames.substring(0, finalTopicNames.length() - 1);
		}
		consumerGroupEntity.setTopicNames(finalTopicNames);
		update(consumerGroupEntity);
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupEntity.getId(),
				"取消订阅，修改consumerGroup的topic字段，从" + oldTopicNames + "变为：" + finalTopicNames);
		return new BaseUiResponse<Void>();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, propagation= Propagation.NESTED)
	public void copyAndNewConsumerGroup(ConsumerGroupEntity consumerGroupEntityOld,
			ConsumerGroupEntity consumerGroupEntityNew) {
		consumerGroupEntityNew.setId(0);
		insert(consumerGroupEntityNew);
		Map<String, ConsumerGroupEntity> consumerGroupMap = getConsumerGroupByName(consumerGroupEntityOld.getName());
		Map<Long, Map<String, ConsumerGroupTopicEntity>> ctMap = consumerGroupTopicService.getCache();
		Map<String, ConsumerGroupTopicEntity> consumerTopics = ctMap.get(consumerGroupEntityOld.getId());
		if (consumerTopics != null) {
			for (Map.Entry<String, ConsumerGroupTopicEntity> entry : consumerTopics.entrySet()) {
				if (entry.getValue().getTopicType() == 1) {
					ConsumerGroupTopicCreateRequest request2 = new ConsumerGroupTopicCreateRequest();
					request2.setAlarmEmails(entry.getValue().getAlarmEmails());
					request2.setConsumerBatchSize(entry.getValue().getConsumerBatchSize());
					request2.setConsumerGroupId(consumerGroupEntityNew.getId());
					request2.setConsumerGroupName(consumerGroupEntityNew.getName());
					request2.setDelayProcessTime(entry.getValue().getDelayProcessTime());
					request2.setDelayPullTime(entry.getValue().getMaxPullTime());
					request2.setMaxLag(entry.getValue().getMaxLag());
					request2.setOriginTopicName(entry.getValue().getOriginTopicName());
					request2.setPullBatchSize(entry.getValue().getPullBatchSize());
					request2.setRetryCount(entry.getValue().getRetryCount());
					request2.setTag(entry.getValue().getTag());
					request2.setThreadSize(entry.getValue().getThreadSize());
					request2.setTopicId(entry.getValue().getTopicId());
					request2.setTopicName(entry.getValue().getTopicName());
					request2.setTopicType(entry.getValue().getTopicType());
					request2.setTimeOut(entry.getValue().getTimeOut());
				    consumerGroupTopicService.subscribe(request2,consumerGroupMap);					
				}
			}
		}
	}

	@Override
	public Map<String, ConsumerGroupEntity> getData() {
		List<ConsumerGroupEntity> consumerGroupEntities = getList();
		Map<String, ConsumerGroupEntity> dataMap = new HashMap<>(consumerGroupEntities.size());
		consumerGroupEntities.forEach(t1 -> {
			dataMap.put(t1.getName(), t1);
		});
		return dataMap;
	}
	private Map<String, ConsumerGroupEntity> getConsumerGroupByName(String name) {
		Map<String,Object> condition=new HashMap<>();
		condition.put(ConsumerGroupEntity.FdOriginName,name);
		List<ConsumerGroupEntity> consumerGroupEntities=  getList(condition);
		Map<String,ConsumerGroupEntity> map=new HashMap<>();
		consumerGroupEntities.forEach(t->{
			map.put(t.getName(),t);
		});
		return map;
	}
}
