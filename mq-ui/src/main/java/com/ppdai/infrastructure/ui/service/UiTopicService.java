package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.Constants;
import com.ppdai.infrastructure.mq.biz.dto.ReadWriteEnum;
import com.ppdai.infrastructure.mq.biz.dto.request.TopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AuditUtil;
import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.TopicGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicClearTokenResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicCreateResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicDeleteResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicExpandResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicGenerateTokenResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicGetByIdResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicGetTopicNamesResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicManualExpandResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicQueueRemoveListResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicQueueRemoveResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.TopicReportResponse;
import com.ppdai.infrastructure.mq.biz.ui.enums.NodeTypeEnum;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.AuthFailException;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueRemoveInfoVo;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueVo;
import com.ppdai.infrastructure.mq.biz.ui.vo.TopicVo;
import com.ppdai.infrastructure.ui.util.DesUtil;


@Service
public class UiTopicService implements TimerService {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	@Autowired
	private TopicService topicService;
	@Autowired
	private UiQueueService uiQueueService;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private AuditLogService uiAuditLogService;
	@Autowired
	private DbNodeService dbNodeService;
	@Autowired
	private UiQueueOffsetService uiQueueOffsetService;
	@Autowired
	private QueueService queueService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private UiConsumerGroupTopicService uiConsumerGroupTopicService;
	@Autowired
	private ConsumerGroupTopicService consumerGroupTopicService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private Message01Service message01Service;
	@Autowired
	private UserInfoHolder userInfoHolder;
	private ThreadPoolExecutor executor = null;
	private volatile boolean isRunning = true;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private AtomicReference<List<TopicVo>> topicVoListRf = new AtomicReference<>(new ArrayList<>());
	private volatile long lastUpdateTime = 0;
	private final String shouldShrink = "应该缩容";
	private final String shouldExpand = "应该扩容";

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			lastUpdateTime = System.currentTimeMillis() - soaConfig.getMqReportInterval() * 2;
			executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(50),
					SoaThreadFactory.create("UiTopicService", true), new ThreadPoolExecutor.DiscardOldestPolicy());
			executor.execute(() -> {
				while (isRunning) {
					try {
						if (System.currentTimeMillis() - lastAccessTime < soaConfig.getMqReportInterval()
								|| System.currentTimeMillis() - lastAccessTime > 1000 * 60 * 60 * 60) {
							if (System.currentTimeMillis() - lastUpdateTime > soaConfig.getMqReportInterval()) {
								initCache();
								if (uiQueueService.getQueueListCount().size() > 0) {
									lastUpdateTime = System.currentTimeMillis();
									lastAccessTime = System.currentTimeMillis() - soaConfig.getMqReportInterval() * 2;
								}
							}
						}
					} catch (Exception e) {
						log.error("UiQueueServiceImpl_initCache_error", e);
					}
					if (uiQueueService.getQueueListCount().size() == 0) {
						Util.sleep(10 * 1000);
					} else {
						Util.sleep(2 * 1000);
					}
				}
			});
		}
	}

	private boolean initCache() {
		Transaction transaction = Tracer.newTransaction("UiQueueService", "initCache");
		try {
			Map<String, TopicEntity> topicMap = topicService.getCache();
			Map<String, List<QueueEntity>> topicQueueMap = queueService.getAllLocatedTopicQueue();
			Map<String, List<QueueVo>> queueVoMap = uiQueueService.getQueueListCount();
			List<TopicVo> topicVoList = new ArrayList<>();
			for (String topicName : topicMap.keySet()) {
				try {
					int queueCount = 0;
					TopicVo topicVo = new TopicVo(topicMap.get(topicName));
					long msgCount = 0;
					if (queueVoMap != null) {
						List<QueueVo> queueList = queueVoMap.get(topicName);
						if (queueList != null) {
							for (QueueVo queueVo : queueList) {
								msgCount += queueVo.getMsgCount();
							}
						}

					}
					topicVo.setMsgCount(msgCount);
					if (topicQueueMap.containsKey(topicName)) {
						queueCount = topicQueueMap.get(topicName).size();
					}

					topicVo.setQueueCount(queueCount);
					if (topicVo.getSaveDayNum() > 0) {
						topicVo.setAvgCount(topicVo.getMsgCount() / topicVo.getSaveDayNum());
					}
					if (topicVo.getQueueCount() > 0) {
						topicVo.setAvgCountOfQueue(topicVo.getAvgCount() / topicVo.getQueueCount());
					}
					if (topicVo.getAvgCountOfQueue() < 500000 && topicVo.getQueueCount() > 1) {
						topicVo.setIsReasonable(shouldShrink);
					} else if (topicVo.getAvgCountOfQueue() > 1000000) {
						topicVo.setIsReasonable(shouldExpand);
					}
					// 根据topic每天的平均消息量，计算该topic合理的队列数量（向上取整算法）
					long reasonableQueueCount = ((topicVo.getAvgCount() + 999999)
							- (topicVo.getAvgCount() + 999999) % 1000000) / 1000000;
					topicVo.setManageQueueCount(reasonableQueueCount - topicVo.getQueueCount());
					topicVoList.add(topicVo);
				} catch (Exception e) {
					throw new RuntimeException(topicName, e);
				}
			}
			topicSort(topicVoList);
			topicVoListRf.set(topicVoList);
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			log.error("UiQueueService_initCache_error", e);
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
		return true;
	}

	private void topicSort(List<TopicVo> topicVoList) {
		// 按照消息总量
		Collections.sort(topicVoList, new Comparator<TopicVo>() {
			@Override
			public int compare(TopicVo q1, TopicVo q2) {
				long i = q1.getMsgCount() - q2.getMsgCount();
				if (i == 0) {
					return 0;
				} else if (i > 0) {
					return -1;
				} else {
					return 1;
				}
			}
		});
	}

	public TopicGetListResponse queryByPage(TopicGetListRequest topicGetListRequest) {
		Map<String, List<QueueEntity>> topicQueueMap = queueService.getAllLocatedTopicQueue();
		Map<String, Object> conditionMap = new HashMap<>();
		if (StringUtils.isNotBlank(topicGetListRequest.getName())) {
			conditionMap.put("name", topicGetListRequest.getName());
		}
		if (StringUtils.isNotBlank(topicGetListRequest.getId())) {
			conditionMap.put("id", Long.valueOf(topicGetListRequest.getId()));
		}
		if (StringUtils.isNotBlank(topicGetListRequest.getOwnerName())) {
			conditionMap.put(TopicEntity.FdOwnerNames, topicGetListRequest.getOwnerName());
		}
		if (StringUtils.isNotBlank(topicGetListRequest.getTopicType())) {
			conditionMap.put(TopicEntity.FdTopicType, topicGetListRequest.getTopicType());
		}
		long count = topicService.countWithUserName(conditionMap);
		if (count == 0) {
			return new TopicGetListResponse(count, null);
		}
		List<TopicEntity> topicEntityList = topicService.getListWithUserName(conditionMap,
				Long.valueOf(topicGetListRequest.getPage()), Long.valueOf(topicGetListRequest.getLimit()));
		String currentUserId = userInfoHolder.getUserId();
		List<TopicVo> topicVoList = topicEntityList.stream().map(topicEntity -> {
			TopicVo topicVo = new TopicVo(topicEntity);
			topicVo.setRole(roleService.getRole(currentUserId, topicEntity.getOwnerIds()));
			int queueCount = 0;
			if (topicQueueMap.containsKey(topicEntity.getName())) {
				queueCount = topicQueueMap.get(topicEntity.getName()).size();
			} else {
				queueCount = topicEntity.getExpectDayCount() / Constants.NUMS_OF_MESSAGE_PER_QUEUE_ONEDAY;
			}

			topicVo.setQueueCount(queueCount);
			return topicVo;
		}).collect(Collectors.toList());

		return new TopicGetListResponse(count, topicVoList);
	}

	private boolean hasAuth(String userId, TopicEntity topicEntity) {
		return Arrays.asList(topicEntity.getOwnerIds().split(",")).contains(userId) || roleService.isAdmin(userId);
	}

	private boolean isAdmin(String userId) {
		return roleService.isAdmin(userId);
	}

	public TopicCreateResponse createOrUpdateTopic(TopicCreateRequest topicCreateRequest) {
		CacheUpdateHelper.updateCache();
		String name = topicCreateRequest.getName();
		if (name.length() > 4 && "fail".equals(name.substring(name.length() - 4).toLowerCase())) {
			throw new CheckFailException("topic名称:" + name + "不能以fail结尾");
		}
		TopicEntity topicEntity = new TopicEntity();
		topicCreateRequest.setName(StringUtils.trim(topicCreateRequest.getName()));
		topicEntity.setName(topicCreateRequest.getName());
		topicEntity.setOwnerIds(topicCreateRequest.getOwnerIds());
		topicEntity.setOwnerNames(topicCreateRequest.getOwnerNames());
		topicEntity.setExpectDayCount(topicCreateRequest.getExpectDayCount());
		topicEntity.setEmails(StringUtils.trim(topicCreateRequest.getEmails()));
		topicEntity.setBusinessType(topicCreateRequest.getBusinessType());
		topicEntity.setMaxLag(topicCreateRequest.getMaxLag());
		topicEntity.setRemark(topicCreateRequest.getRemark());
		topicEntity.setDptName(topicCreateRequest.getDptName());
		topicEntity.setOriginName(topicCreateRequest.getName());
		topicEntity.setNormalFlag(topicCreateRequest.getNormalFlag());
		topicEntity.setSaveDayNum(topicCreateRequest.getSaveDayNum());
		topicEntity.setTels(topicCreateRequest.getTels());
		topicEntity.setIsActive(1);
		topicEntity.setTopicType(topicCreateRequest.getTopicType());
		topicEntity.setConsumerFlag(topicCreateRequest.getConsumerFlag());
		topicEntity.setConsumerGroupNames(topicCreateRequest.getConsumerGroupList());
		topicEntity.setAppId(topicCreateRequest.getAppId());
		String userId = userInfoHolder.getUserId();
		if (StringUtils.isNotEmpty(topicCreateRequest.getId())) {
			topicEntity.setId(Long.valueOf(topicCreateRequest.getId()));
			topicEntity.setUpdateBy(userId);
			updateTopic(topicEntity);
		} else {
			topicEntity.setInsertBy(userId);
			createSuccessTopic(topicEntity);
		}
		// 创建或者更新topic时，同步到mq2
		// synService32.synTopic32(topicCreateRequest, topicEntity);
		return new TopicCreateResponse();
	}

	private void updateTopic(TopicEntity topicEntity) {
		String currentUserId = userInfoHolder.getUserId();
		TopicEntity oldTopicEntity = baseCheckRequest(topicEntity.getId(), currentUserId);
		// 鉴于安全原因，token 不能传给前端，只能在服务端传递
		topicEntity.setToken(oldTopicEntity.getToken());
		topicService.update(topicEntity);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
				"更新topic，" + AuditUtil.diff(oldTopicEntity, topicEntity));
	}

	private void createSuccessTopic(TopicEntity topicEntity) {
		TopicEntity entity= topicService.getTopicByName(topicEntity.getName());
		if(entity!=null){
			throw new CheckFailException("topic:"+topicEntity.getName()+"重复，检查是否有重名topic已经存在。");
		}
		try {
			topicService.insert(topicEntity);
		} catch (DuplicateKeyException e) {
			throw new CheckFailException("topic:" + topicEntity.getName() + "重复，检查是否有重名topic已经存在。");
		}
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
				"新建topic，" + JsonUtil.toJson(topicEntity));
		// 计算分配的队列数
		int expectDayCount = topicEntity.getExpectDayCount() * 10000;
		int successQueueNum = expectDayCount / Constants.MSG_NUMS_OF_ONE_QUEUE;
		// 分配队列
		topicService.distributeQueueWithLock(topicEntity, successQueueNum,
				NodeTypeEnum.SUCCESS_NODE_TYPE.getTypeCode());
	}

	public TopicDeleteResponse deleteTopic(Long topicId) {
		CacheUpdateHelper.updateCache();
		String currentUserId = userInfoHolder.getUserId();
		Map<String, List<QueueVo>> queueVoMap = uiQueueService.getQueueListCount();
		TopicEntity topicEntity = baseCheckRequest(topicId, currentUserId);

		if (uiConsumerGroupTopicService.findByTopicId(topicId).size() > 0) {
			throw new CheckFailException("目前有消费者订阅，不能删除，请通知取消订阅后再删除");
		}

		// 如果topic下存在消息量大于阈值的queue，则不允许删除
		if (queueVoMap != null && soaConfig.isPro()) {
			List<QueueVo> queueList = queueVoMap.get(topicEntity.getName());
			if (queueList != null) {
				for (QueueVo queueVo : queueList) {
					if (queueVo.getMsgCount() > soaConfig.getTopicDeleteLimitCount() * 10000) {
						throw new CheckFailException("topic:" + topicEntity.getName() + "存在消息量大于"
								+ soaConfig.getTopicDeleteLimitCount() + "万的queue,不能直接删除");
					}
				}
			}
		}

		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
				"删除topic之前" + JsonUtil.toJson(topicEntity));
		doDeleteTopic(topicEntity);
		// 删除topic后，同步到mq2
		// synService32.synTopicDelete(topicEntity);
		return new TopicDeleteResponse();
	}

	private void doDeleteTopic(TopicEntity topicEntity) {
		Long topicId = topicEntity.getId();
		List<QueueEntity> queueEntities = queueService.getQueuesByTopicId(topicId);
		if (soaConfig.isPro() && !isAdmin(userInfoHolder.getUserId())) {
			queueEntities.forEach(queueEntity -> uiQueueService.remove(queueEntity.getId()));
		} else {
			queueEntities.forEach(queueEntity -> uiQueueService.forceRemove(queueEntity.getId()));
		}
		topicService.delete(topicId);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(),
				"删除topic，" + JsonUtil.toJson(topicEntity));
	}

	public List<TopicEntity> getFailTopic(String topicName) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(TopicEntity.FdOriginName, topicName);
		conditionMap.put(TopicEntity.FdTopicType, NodeTypeEnum.FAIL_NODE_TYPE.getTypeCode());
		return topicService.getList(conditionMap);
	}

	public TopicExpandResponse expandTopic(Long topicId) {
		String currentUserId = userInfoHolder.getUserId();
		TopicEntity topicEntity = baseCheckRequest(topicId, currentUserId);
		if (roleService.getRole(userInfoHolder.getUserId()) > 0) {
			List<QueueEntity> queueEntities = queueService.getQueuesByTopicId(topicId);
			checkQueueMessageCount(queueEntities);
			checkQueueMax(queueEntities);
		}
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "开始扩容, 扩容队列 1 条");
		List<QueueEntity> normalQueueList = queueService.getTopUndistributed(1, topicEntity.getTopicType(), topicId);
		if (CollectionUtils.isEmpty(normalQueueList)) {
			uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "数据节点不够分配");
			throw new CheckFailException("数据节点不够分配，请联系管理员");
		}
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId,
				String.format("对备选队列进行分配： %s", normalQueueList.get(0).getId()));
		topicService.distributeQueue(topicEntity, normalQueueList.get(0));
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "扩容结束");
		uiQueueOffsetService.createQueueOffsetForExpand(normalQueueList.get(0), topicId, topicEntity);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId,
				"分配queue，" + JsonUtil.toJson(normalQueueList.get(0).getId()));
		consumerGroupService.notifyRb(uiConsumerGroupTopicService.findByTopicId(topicId).stream()
				.map(ConsumerGroupTopicEntity::getConsumerGroupId).collect(Collectors.toList()));

		return new TopicExpandResponse();
	}

	private void checkQueueMessageCount(List<QueueEntity> queueEntities) {
		if (!soaConfig.getMaxTableMessageSwitch()) {
			return;
		}
		if (CollectionUtils.isEmpty(queueEntities)) {
			return;
		}
		Long allMessageCount = 0L;

		for (QueueEntity queueEntity : queueEntities) {
			allMessageCount += getQueueMessage(queueEntity);
		}

		if (allMessageCount / queueEntities.size() > soaConfig.getMaxTableMessage()) {
			throw new CheckFailException("每队列消息量未达到最大值，不允许扩容，可联系管理员强制扩容");
		}
	}

	private void checkQueueMax(List<QueueEntity> queueEntities) {
		int maxQueue = soaConfig.getMaxQueuePerTopic();
		if (CollectionUtils.isEmpty(queueEntities)) {
			return;
		}
		if (queueEntities.size() >= maxQueue) {
			throw new CheckFailException("topic内队列数量达到上限，不允许扩容，可联系管理员强制扩容");
		}
	}

	private Long getQueueMessage(QueueEntity queueEntity) {
		message01Service.setDbId(queueEntity.getDbNodeId());
		Long maxId = message01Service.getMaxId(queueEntity.getTbName());
		Long minId = queueEntity.getMinId();
		return maxId - minId - 1;
	}

	public TopicGenerateTokenResponse generateToken(Long topicId) {
		String currentUserId = userInfoHolder.getUserId();
		TopicEntity topicEntity = baseCheckRequest(topicId, currentUserId);

		String token = DesUtil.getUuidToken();
		topicEntity.setToken(token);
		topicEntity.setUpdateBy(currentUserId);
		topicService.update(topicEntity);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "生成token");
		return new TopicGenerateTokenResponse();
	}

	public TopicClearTokenResponse clearToken(Long topicId) {
		String currentUserId = userInfoHolder.getUserId();
		TopicEntity topicEntity = baseCheckRequest(topicId, currentUserId);
		String token = topicEntity.getToken();
		topicEntity.setToken("");
		topicEntity.setUpdateBy(currentUserId);
		topicService.update(topicEntity);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "清除token:" + token);
		return new TopicClearTokenResponse();
	}

	private TopicEntity baseCheckRequest(Long topicId, String currentUserId) {
		TopicEntity topicEntity = topicService.get(topicId);
		if (topicEntity == null) {
			throw new CheckFailException("topic已经被删除，请刷新重试。 ");
		}
		if (!hasAuth(currentUserId, topicEntity)) {
			throw new AuthFailException("没有操作权限，请进行权限检查。");
		}
		return topicEntity;
	}

	public List<TopicEntity> getSelectSearch(String keyword, int offset, int limit, String consumerGroupName) {
		Map<String, ConsumerGroupEntity> consumerGroupEntityMap = consumerGroupService.getCache();
		Map<String, TopicEntity> topicMap = topicService.getCache();
		List<TopicEntity> topicInfos = new ArrayList<>();
		List<String> subscribedTopics = new ArrayList<>();
		// 该消费者组已经订阅过的topic
		ConsumerGroupEntity consumerGroupEntity = consumerGroupEntityMap.get(consumerGroupName);
		String topicNames = consumerGroupEntity.getTopicNames();
		if (StringUtils.isNotEmpty(topicNames)) {
			subscribedTopics = Arrays.asList(topicNames.split(","));
		}
		for (String key : topicMap.keySet()) {
			if (key.toLowerCase().startsWith(keyword.toLowerCase()) && topicMap.get(key).getTopicType() == 1) {
				// 排除已经订阅过的topic
				if (!subscribedTopics.contains(key)) {
					// 如果该topic允许所有人订阅，或者该消费者组拥有订阅该topic的权限
					if (topicMap.get(key).getConsumerFlag() == 1 || Arrays
							.asList(topicMap.get(key).getConsumerGroupNames().split(",")).contains(consumerGroupName)) {
						topicInfos.add(topicMap.get(key));
					}

				}
			}
			if (topicInfos.size() >= limit) {
				break;
			}
		}
		return topicInfos;
	}

	public TopicQueueRemoveListResponse queueRemoveList(Long topicId) {
		CacheUpdateHelper.updateCache();
		Transaction catTransaction = Tracer.newTransaction("UiTopicService", "all-process-queueRemoveList");
		try {
			Transaction catTransaction1 = Tracer.newTransaction("UiTopicService", "getQueuesByTopicId");
			List<QueueEntity> queueEntityList = queueService.getQueuesByTopicId(topicId);
			if (CollectionUtils.isEmpty(queueEntityList)) {
				return new TopicQueueRemoveListResponse(0L, null);
			}
			catTransaction1.setStatus(Transaction.SUCCESS);
			catTransaction1.complete();
			Long bestQueueId = 0L;
			if (queueEntityList.size() > 2) {
				Transaction catTransaction2 = Tracer.newTransaction("UiTopicService", "getBestRemoveQueue");
				List<QueueEntity> bestQueueList = uiQueueService.getBestRemoveQueue(topicId);
				catTransaction2.setStatus(Transaction.SUCCESS);
				catTransaction2.complete();
				Transaction catTransaction3 = Tracer.newTransaction("UiTopicService", "findMinLeftMessage");
				if (!CollectionUtils.isEmpty(bestQueueList)) {
					bestQueueId = findMinLeftMessage(bestQueueList).getId();
				} else {
					bestQueueId = findMinLeftMessage(queueEntityList).getId();
				}
				catTransaction3.setStatus(Transaction.SUCCESS);
				catTransaction3.complete();
			}
			Transaction catTransaction4 = Tracer.newTransaction("UiTopicService", "buildQueueRemoveInfoVoList");
			List<QueueRemoveInfoVo> queueRemoveInfoVoList = new ArrayList<>();
			for (QueueEntity queueEntity : queueEntityList) {
				QueueRemoveInfoVo queueRemoveInfoVo = new QueueRemoveInfoVo();
				queueRemoveInfoVo.setId(queueEntity.getId());
				queueRemoveInfoVo.setTopicId(topicId);
				queueRemoveInfoVo.setQueueReadOnly(queueEntity.getReadOnly());
				DbNodeEntity dbNodeEntity = dbNodeService.get(queueEntity.getDbNodeId());
				queueRemoveInfoVo.setDbReadOnly(dbNodeEntity.getReadOnly());
				if (dbNodeEntity.getReadOnly() != 1) {
					queueRemoveInfoVo.setReadStatus(dbNodeEntity.getReadOnly());
				} else {
					queueRemoveInfoVo.setReadStatus(queueEntity.getReadOnly());
				}
				queueRemoveInfoVo.setConsumerGroups(String.join(",", getConsumerGroups(queueEntity.getId())));
				queueRemoveInfoVo.setLeftMessage(getLeftMessage(queueEntity.getId()));
				queueRemoveInfoVo.setDbNodeId(queueEntity.getDbNodeId());
				if (queueEntity.getId() == bestQueueId) {
					queueRemoveInfoVo.setIsBestRemove(1);
				}
				queueRemoveInfoVoList.add(queueRemoveInfoVo);
			}
			catTransaction4.setStatus(Transaction.SUCCESS);
			catTransaction4.complete();
			catTransaction.setStatus(Transaction.SUCCESS);
			return new TopicQueueRemoveListResponse((long) queueRemoveInfoVoList.size(), queueRemoveInfoVoList);

		} catch (Exception e) {
			catTransaction.setStatus(e);
			throw e;
		} finally {
			catTransaction.complete();
		}

	}

	private QueueEntity findMinLeftMessage(List<QueueEntity> queueEntities) {
		QueueEntity queueEntity = null;
		Long minLeftMessage = 999999999999999999L;
		for (QueueEntity queueEntity1 : queueEntities) {
			Long currentLeftMessage = getLeftMessage(queueEntity1.getId());
			if (currentLeftMessage <= minLeftMessage) {
				minLeftMessage = currentLeftMessage;
				queueEntity = queueEntity1;
			}
		}
		return queueEntity;

	}

	private Long getLeftMessage(Long queueId) {
		List<QueueOffsetEntity> queueOffsetEntityList = uiQueueOffsetService.findByQueueId(queueId);
		if (CollectionUtils.isEmpty(queueOffsetEntityList)) {
			return 0L;
		}
		Long maxId = queueService.getMax().get(queueId);
		Long leftMessage = 0L;
		for (QueueOffsetEntity queueOffsetEntity : queueOffsetEntityList) {
			Long left = maxId - 1 - queueOffsetEntity.getOffset();
			leftMessage += left > 0 ? left : 0;
		}
		return leftMessage;
	}

	private List<String> getConsumerGroups(Long queueId) {
		List<QueueOffsetEntity> queueOffsetEntityList = uiQueueOffsetService.findByQueueId(queueId);
		List<String> consumerGroups = new ArrayList<>();
		queueOffsetEntityList
				.forEach(queueOffsetEntity -> consumerGroups.add(queueOffsetEntity.getConsumerGroupName()));
		return consumerGroups;
	}

	public TopicGetByIdResponse getById(Long topicId) {
		return new TopicGetByIdResponse(topicService.get(topicId));
	}

	public void updateSaveDayNum(Long topicId, int saveDayNum) {
		CacheUpdateHelper.updateCache();
		TopicEntity topicEntity = topicService.get(topicId);
		if (!hasAuth(userInfoHolder.getUserId(), topicEntity)) {
			throw new AuthFailException("没有操作权限，请进行权限检查。");
		}
		int oldSaveDayNum = topicEntity.getSaveDayNum();
		topicEntity.setSaveDayNum(saveDayNum);
		topicService.update(topicEntity);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId,
				"更新[保留时间]: {" + oldSaveDayNum + "->" + saveDayNum + "}");
		// synService32.synTopicSaveDayNum32(topicEntity);
	}

	@Transactional(rollbackFor = Exception.class)
	public TopicQueueRemoveResponse queueRemove(Long queueId, Long topicId) {
		CacheUpdateHelper.updateCache();
		TopicEntity topicEntity = topicService.get(topicId);
		if (roleService.getRole(userInfoHolder.getUserId(), topicEntity.getOwnerIds()) > 0) {
			throw new AuthFailException("没有操作权限，请进行权限检查。");
		}

		Map<String, List<QueueVo>> queueVoMap = uiQueueService.getQueueListCount();
//		 如果queue下的消息量大于阈值，则不允许缩容
		if (queueVoMap != null) {
			List<QueueVo> queueList = queueVoMap.get(topicEntity.getName());
			if (queueList != null) {
				for (QueueVo queueVo : queueList) {
					if(queueId==queueVo.getId()&&queueVo.getMsgCount()>soaConfig.getTopicDeleteLimitCount() * 10000){
						throw new CheckFailException("topic:"+topicEntity.getName()+"存在消息量大于"+soaConfig.getTopicDeleteLimitCount() * 10000+"的queue,不能直接缩容");
					}
				}
			}

		}

		// List<QueueEntity> queueEntities = queueService.getQueuesByTopicId(topicId);
		uiQueueService.remove(queueId);
		consumerGroupService.notifyRb(uiConsumerGroupTopicService.findByTopicId(topicId).stream()
				.map(ConsumerGroupTopicEntity::getConsumerGroupId).collect(Collectors.toList()));
		// topic缩容时，同步到mq2.0
		// synService32.synQueueRemove(queueService.get(queueId), topicEntity);
		return new TopicQueueRemoveResponse();
	}

	public TopicManualExpandResponse manualExpand(Long topicId, Long queueId) {
		CacheUpdateHelper.updateCache();
		String currentUserId = userInfoHolder.getUserId();
		TopicEntity topicEntity = baseCheckRequest(topicId, currentUserId);

		QueueEntity queueEntity = queueService.get(queueId);
		if (topicEntity.getTopicType() != queueEntity.getNodeType()) {
			throw new CheckFailException("所选择的队列类型与主题不一致！请重新选择！");
		}
		if (queueEntity.getTopicId() != 0) {
			throw new CheckFailException("队列已经被分配！请重新选择！");
		}
		if (ReadWriteEnum.READ_WRITE.getCode() != dbNodeService.get(queueEntity.getDbNodeId()).getReadOnly()) {
			throw new CheckFailException("队列所在节点不可写，请重新选择！");
		}
		topicService.distributeQueue(topicEntity, queueEntity);
		uiQueueOffsetService.createQueueOffsetForExpand(queueEntity, topicId, topicEntity);
		uiQueueService.queueChangeRb(topicId);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId,
				"手工分配queue，" + JsonUtil.toJson(queueEntity.getId()));

		return new TopicManualExpandResponse();
	}

	public TopicGetTopicNamesResponse getTopicNames(String keyword, int offset, int limit) {
		Map<String, TopicEntity> topicMap = topicService.getCache();
		List<String> topicList = new LinkedList<>();
		for (String topicName : topicMap.keySet()) {
			if (topicName.toLowerCase().startsWith(keyword.toLowerCase())) {
				topicList.add(topicName);
			}
		}

		if (offset + limit > topicList.size()) {
			limit = topicList.size() - offset;
		}
		return new TopicGetTopicNamesResponse(new Long(topicList.subList(offset, limit).size()),
				topicList.subList(offset, limit));

	}

	public TopicGetTopicNamesResponse getTopicNamesForMessageTool(String keyword, int offset, int limit) {
		Map<String, TopicEntity> topicMap = topicService.getCache();
		List<String> topicList = new LinkedList<>();
		for (String topicName : topicMap.keySet()) {
			// 如果是超级管理员或者apollo配置了允许所有人发送，则展示所有topic
			if (roleService.getRole(userInfoHolder.getUserId(), null) == 0
					|| ("0".equals(soaConfig.getToolTopicFilterFlag()))) {
				if (topicName.toLowerCase().startsWith(keyword.toLowerCase())) {
					topicList.add(topicName);
				}
			} else {
				// 否则只展示负责人名下的topic，即只允许负责人发送
				if (topicName.toLowerCase().startsWith(keyword.toLowerCase())
						&& topicMap.get(topicName).getOwnerIds().contains(userInfoHolder.getUserId())) {
					topicList.add(topicName);
				}
			}

		}

		if (offset + limit > topicList.size()) {
			limit = topicList.size() - offset;
		}
		return new TopicGetTopicNamesResponse(new Long(topicList.subList(offset, limit).size()),
				topicList.subList(offset, limit));

	}

	private volatile long lastAccessTime = System.currentTimeMillis() * 2;

	public TopicReportResponse getTopicReport(TopicGetListRequest topicGetListRequest) {
		lastAccessTime = System.currentTimeMillis();
		int page = Integer.parseInt(topicGetListRequest.getPage());
		int pageSize = Integer.parseInt(topicGetListRequest.getLimit());
		List<TopicVo> topicVoList = new ArrayList<>();

		Map<String, List<ConsumerGroupTopicEntity>> topicSubscribeMap = consumerGroupTopicService
				.getTopicSubscribeMap();
		for (TopicVo topicVo : topicVoListRf.get()) {
			if (StringUtils.isNotEmpty(topicGetListRequest.getName())) {
				if (!topicGetListRequest.getName().equals(topicVo.getName())) {
					continue;
				}
			}

			if (StringUtils.isNotEmpty(topicGetListRequest.getTopicExceptionType())) {
				// 过滤掉非僵尸topic
				if ("1".equals(topicGetListRequest.getTopicExceptionType())) {
					if (topicVo.getMsgCount() > 0) {// 过滤有消息的topic
						continue;
					}
					if (topicSubscribeMap.containsKey(topicVo.getOriginName())) {// 过滤已经被订阅的topic
						continue;
					}
					if (topicVo.getInsertTime().getTime() - System.currentTimeMillis() < 3 * 24 * 60 * 60 * 1000) {
						continue;
					}
				}

				// 过滤掉负责人正常的topic
				if ("2".equals(topicGetListRequest.getTopicExceptionType())) {
					List<String> ownerIds = Arrays.asList(topicVo.getOwnerIds().split(","));
					if (uiQueueOffsetService.isOwnerAvailable(ownerIds)) {
						continue;
					}
				}
			}

			if (StringUtils.isNotEmpty(topicGetListRequest.getQueueManagementType())) {
				if ("1".equals(topicGetListRequest.getQueueManagementType())) {
					if (!shouldShrink.equals(topicVo.getIsReasonable())) {
						continue;
					}
				}

				if ("2".equals(topicGetListRequest.getQueueManagementType())) {
					if (!shouldExpand.equals(topicVo.getIsReasonable())) {
						continue;
					}
				}
			}

			topicVoList.add(topicVo);
		}

		int t = topicVoList.size();
		if ((page * pageSize) > topicVoList.size()) {
			topicVoList = topicVoList.subList((page - 1) * pageSize, topicVoList.size());
		} else {
			topicVoList = topicVoList.subList((page - 1) * pageSize, page * pageSize);
		}
		return new TopicReportResponse(new Long(t), topicVoList);

	}

	public List<TopicVo> getTopicVos(){
		return topicVoListRf.get();
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

}
