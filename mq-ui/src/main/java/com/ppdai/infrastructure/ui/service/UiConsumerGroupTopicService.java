package com.ppdai.infrastructure.ui.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupTopicRepository;
import com.ppdai.infrastructure.mq.biz.dto.UserRoleEnum;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicGetListRequest;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AuditUtil;
import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupTopicEditResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupTopicGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.AuthFailException;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConsumerGroupTopicVo;

@Service
public class UiConsumerGroupTopicService {
	//private Logger log = LoggerFactory.getLogger(UiConsumerGroupTopicService.class);
	@Autowired
	private ConsumerGroupTopicRepository consumerGroupTopicRepository;
	@Autowired
	private ConsumerGroupTopicService consumerGroupTopicService;
	@Autowired
	private UiConsumerGroupService uiConsumerGroupService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private AuditLogService uiAuditLogService;
	@Autowired
	private RoleService roleService;

	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private UserInfoHolder userInfoHolder;

	

	/**
	 * 根据条件查询实例
	 *
	 * @param consumerGroupTopicGetListRequest
	 * @return
	 */
	public ConsumerGroupTopicGetListResponse findBy(ConsumerGroupTopicGetListRequest consumerGroupTopicGetListRequest) {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put(ConsumerGroupTopicEntity.FdConsumerGroupName,
				consumerGroupTopicGetListRequest.getConsumerGroupName());
		parameterMap.put(ConsumerGroupTopicEntity.FdTopicName, consumerGroupTopicGetListRequest.getTopicName());
		parameterMap.put(ConsumerGroupTopicEntity.FdConsumerGroupId,
				consumerGroupTopicGetListRequest.getConsumerGroupId());
		long count = consumerGroupTopicService.count(parameterMap);
		List<ConsumerGroupTopicEntity> consumerGroupTopicList = consumerGroupTopicService.getList(parameterMap);
		// 缓存数据
		Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
		List<ConsumerGroupTopicVo> consumerGroupTopicVoList = consumerGroupTopicList.stream()
				.map(consumerGroupTopicEntity -> {
					ConsumerGroupTopicVo consumerGroupTopicVo = new ConsumerGroupTopicVo(consumerGroupTopicEntity);
					consumerGroupTopicVo.setRole(roleService.getRole(userInfoHolder.getUserId(),
							consumerGroupMap.get(consumerGroupTopicEntity.getConsumerGroupName()).getOwnerIds()));
					return consumerGroupTopicVo;
				}).collect(Collectors.toList());
		return new ConsumerGroupTopicGetListResponse(count, consumerGroupTopicVoList);
	}

	public List<ConsumerGroupTopicEntity> findByTopicId(Long topicId) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(ConsumerGroupTopicEntity.FdTopicId, topicId);
		return consumerGroupTopicService.getList(conditionMap);
	}

	@Transactional(rollbackFor = Exception.class)
	public ConsumerGroupTopicEditResponse editConsumerGroupTopic(ConsumerGroupTopicEntity consumerGroupTopicEntity) {
		CacheUpdateHelper.updateCache();
		// 缓存数据
		Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();

		String key = consumerGroupTopicEntity.getConsumerGroupName();
		ConsumerGroupEntity consumerGroupEntity = consumerGroupMap.get(key);
		// 如果是广播模式，并且是原始消费者组，则对原始组下的所有镜像组编辑更新
		if (consumerGroupEntity.getMode() == 2
				&& consumerGroupEntity.getOriginName().equals(consumerGroupEntity.getName())) {
			doUpdateByOrigin(consumerGroupMap, consumerGroupTopicEntity, consumerGroupEntity);
		}

		doUpdate(consumerGroupTopicEntity, consumerGroupMap);

		return new ConsumerGroupTopicEditResponse();
	}

	private void doUpdate(ConsumerGroupTopicEntity consumerGroupTopicEntity,
			Map<String, ConsumerGroupEntity> consumerGroupMap) {
		ConsumerGroupTopicEntity originConsumerGroupTopic = consumerGroupTopicService
				.get(consumerGroupTopicEntity.getId());
		if (roleService.getRole(userInfoHolder.getUserId(),
				consumerGroupMap.get(originConsumerGroupTopic.getConsumerGroupName())
						.getOwnerIds()) >= UserRoleEnum.USER.getRoleCode()) {
			throw new AuthFailException("没有操作权限，请进行权限检查。");
		}

		// 如果orginConsumerGroupTopic是正常topic的ConsumerGroupTopic，则correspondConsumerGroupTopicEntity为对应的失败topic的ConsumerGroupTopic，反之亦反。
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("consumerGroupId", originConsumerGroupTopic.getConsumerGroupId());
		parameterMap.put("originTopicName", originConsumerGroupTopic.getOriginTopicName());
		parameterMap.put("topicType", originConsumerGroupTopic.getTopicType());
		ConsumerGroupTopicEntity correspondConsumerGroupTopicEntity = consumerGroupTopicService
				.getCorrespondConsumerGroupTopic(parameterMap);
		// 为了使得正常topic和失败topic的重试次数一致
		correspondConsumerGroupTopicEntity.setRetryCount(consumerGroupTopicEntity.getRetryCount());
		correspondConsumerGroupTopicEntity.setTag(consumerGroupTopicEntity.getTag());
		correspondConsumerGroupTopicEntity.setAlarmEmails(consumerGroupTopicEntity.getAlarmEmails());
		correspondConsumerGroupTopicEntity.setTimeOut(consumerGroupTopicEntity.getTimeOut());

		String userId = userInfoHolder.getUserId();
		consumerGroupTopicEntity.setUpdateBy(userId);

		consumerGroupTopicService.update(consumerGroupTopicEntity);
		consumerGroupTopicService.update(correspondConsumerGroupTopicEntity);
		uiAuditLogService.recordAudit(ConsumerGroupEntity.TABLE_NAME, consumerGroupTopicEntity.getConsumerGroupId(),
				"编辑" + consumerGroupTopicEntity.getConsumerGroupName() + "下的consumerGroupTopic"
						+ AuditUtil.diff(originConsumerGroupTopic, consumerGroupTopicEntity));
		consumerGroupService.notifyMeta(originConsumerGroupTopic.getConsumerGroupId());
	}

	private void doUpdateByOrigin(Map<String, ConsumerGroupEntity> consumerGroupMap,
			ConsumerGroupTopicEntity originConsumerGroupTopicEntity, ConsumerGroupEntity originConsumerGroupEntity) {
		Map<String, List<ConsumerGroupTopicEntity>> topicSubscribeMap = consumerGroupTopicService
				.getTopicSubscribeMap();
		// 被编辑的consumerGroupTopic下的topic, 所对应的consumerGroupTopic列表
		List<ConsumerGroupTopicEntity> groupTopicList = topicSubscribeMap
				.get(originConsumerGroupTopicEntity.getOriginTopicName());
		for (ConsumerGroupTopicEntity groupTopic : groupTopicList) {
			// 如果是镜像消费者组
			if (consumerGroupMap.get(groupTopic.getConsumerGroupName()).getOriginName()
					.equals(originConsumerGroupEntity.getName())) {
				// 排除原始组的编辑
				if (groupTopic.getId() != originConsumerGroupTopicEntity.getId()) {
					groupTopic.setRetryCount(originConsumerGroupTopicEntity.getRetryCount());
					groupTopic.setMaxLag(originConsumerGroupTopicEntity.getMaxLag());
					groupTopic.setTag(originConsumerGroupTopicEntity.getTag());
					groupTopic.setDelayProcessTime(originConsumerGroupTopicEntity.getDelayProcessTime());
					groupTopic.setMaxPullTime(originConsumerGroupTopicEntity.getMaxPullTime());
					groupTopic.setThreadSize(originConsumerGroupTopicEntity.getThreadSize());
					groupTopic.setPullBatchSize(originConsumerGroupTopicEntity.getPullBatchSize());
					groupTopic.setConsumerBatchSize(originConsumerGroupTopicEntity.getConsumerBatchSize());
					groupTopic.setAlarmEmails(originConsumerGroupTopicEntity.getAlarmEmails());
					groupTopic.setTimeOut(originConsumerGroupTopicEntity.getTimeOut());
					doUpdate(groupTopic, consumerGroupMap);
				}
			}
		}
	}

	public ConsumerGroupTopicEntity findById(long consumerGroupTopicId) {
		return consumerGroupTopicRepository.getById(consumerGroupTopicId);
	}

	public ConsumerGroupTopicCreateRequest initConsumerGroupTopic(long consumerGroupId) {
		ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest = new ConsumerGroupTopicCreateRequest();
		ConsumerGroupEntity consumerGroupEntity = uiConsumerGroupService.findById(consumerGroupId);

		// String userId = userInfoHolder.getUserId();
		String email = userInfoHolder.getUser().getEmail();
		consumerGroupTopicCreateRequest.setAlarmEmails(email);
		consumerGroupTopicCreateRequest.setConsumerGroupId(consumerGroupId);
		consumerGroupTopicCreateRequest.setConsumerGroupName(consumerGroupEntity.getName());
		consumerGroupTopicCreateRequest.setThreadSize(soaConfig.getConsumerGroupTopicThreadSize());
		consumerGroupTopicCreateRequest.setRetryCount(soaConfig.getConsumerGroupTopicRetryCount());
		consumerGroupTopicCreateRequest.setMaxLag(soaConfig.getConsumerGroupTopicLag());
		consumerGroupTopicCreateRequest.setDelayProcessTime(soaConfig.getDelayProcessTime());
		consumerGroupTopicCreateRequest.setPullBatchSize(soaConfig.getPullBatchSize());
		consumerGroupTopicCreateRequest.setConsumerBatchSize(soaConfig.getConsumerBatchSize());
		consumerGroupTopicCreateRequest.setDelayPullTime(soaConfig.getMaxDelayPullTime());
		consumerGroupTopicCreateRequest.setTimeOut(0);
		return consumerGroupTopicCreateRequest;
	}

	public void updateEmailByGroupName(ConsumerGroupEntity consumerGroupEntity) {
		consumerGroupTopicService.updateEmailByGroupName(consumerGroupEntity.getName(),
				consumerGroupEntity.getAlarmEmails());
	}

}
