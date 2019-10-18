package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */
public interface TopicService extends BaseService<TopicEntity> {
	Map<String, TopicEntity> getCache();

	public void distributeQueue(TopicEntity normalTopicEntity, QueueEntity queueEntity);

	public void distributeQueue(TopicEntity topicEntity, List<QueueEntity> queueEntityList);

	TopicEntity getTopicByName(String topicName);

	List<TopicEntity> getListWithUserName(Map<String, Object> conditionMap, long page, long pageSize);

	void updateCache();

	long countWithUserName(Map<String, Object> conditionMap);

	void updateFailTopic(ConsumerGroupEntity consumerGroupEntity);

	void deleteFailTopic(List<String> failTopicNames, long consumerGroupId);

	TopicEntity createFailTopic(TopicEntity topicEntity, ConsumerGroupEntity consumerGroup);

	void distributeQueueWithLock(TopicEntity topicEntity, int queueNum, int nodeType);

}
