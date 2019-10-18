package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.OffsetVersionEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */

public interface QueueOffsetService extends BaseService<QueueOffsetEntity> {
	List<QueueOffsetEntity> getByConsumerGroupIds(List<Long> consumerGroupIds);

	void updateConsumerId(QueueOffsetEntity entity);
	
	int commitOffset(QueueOffsetEntity entity);	
	
	int commitOffsetById(QueueOffsetEntity entity);	
	
	void deRegister(long consumerId);
	
	/*
	 * 第一层的key是congsumergroupname,第二层的key为topicname
	 * */
	Map<String,Map<String, List<QueueOffsetEntity>>> getCache();

	/**
	 * 清除consumerId
	 * @param consumerIds
	 */
	void setConsumserIdsToNull(List<Long> consumerIds);
	List<QueueOffsetEntity> getCacheData();
	void deleteByConsumerGroupId(long consumerGroupId);
	void deleteByConsumerGroupIdAndOriginTopicName(ConsumerGroupTopicEntity consumerGroupTopicEntity);
	List<QueueOffsetEntity> getByConsumerGroupTopic(long consumerGroupId,long topicId);
	void updateStopFlag(long id, int stopFlag,String updateBy);
	int updateQueueOffset(Map<String, Object> parameterMap);
	Map<String, QueueOffsetEntity> getUqCache();
	Map<Long, OffsetVersionEntity> getOffsetVersion();
	List<QueueOffsetEntity> getUnSubscribeData();	
	List<QueueOffsetEntity> getAllBasic();	
	long getLastVersion();	
	void updateCache();	
	BaseUiResponse createQueueOffset(ConsumerGroupTopicEntity consumerGroupTopicEntity);

	long countBy(Map<String, Object> conditionMap);

	List<QueueOffsetEntity> getListBy(Map<String, Object> conditionMap, long page, long pageSize);
	
	long getOffsetSumByIds(List<Long> ids);

}
