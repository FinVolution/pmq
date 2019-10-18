package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupTopicCreateResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupTopicDeleteResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */
@Service
public interface ConsumerGroupTopicService extends BaseService<ConsumerGroupTopicEntity> {
	/*
	 * key为consumergroupid，内层key为topicname 
	 * */
	Map<Long,Map<String, ConsumerGroupTopicEntity>> getCache();
	void deleteByConsumerGroupId(long consumerGroupId);
	void deleteByOriginTopicName(long consumerGroupId,String originTopicName);
	List<String> getFailTopicNames(long consumerGroupId);
	ConsumerGroupTopicEntity getCorrespondConsumerGroupTopic(Map<String, Object> parameterMap);
	Map<String, ConsumerGroupTopicEntity> getGroupTopic();
	void updateCache();
	void updateEmailByGroupName(String groupName,String alarmEmails);
	ConsumerGroupTopicCreateResponse subscribe(ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest);
	ConsumerGroupTopicDeleteResponse deleteConsumerGroupTopic(long consumerGroupTopicId);
	ConsumerGroupTopicEntity createConsumerGroupTopic(
			ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest);

	Map<String, List<ConsumerGroupTopicEntity>> getTopicSubscribeMap();
}
