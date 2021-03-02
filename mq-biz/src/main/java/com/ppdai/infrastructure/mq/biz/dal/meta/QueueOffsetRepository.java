package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.OffsetVersionEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface QueueOffsetRepository extends BaseRepository<QueueOffsetEntity> {
	List<QueueOffsetEntity> getByConsumerGroupIds(@Param("consumerGroupIds") List<Long> consumerGroupIds);

	void updateConsumerId(QueueOffsetEntity entity);

	int commitOffset(QueueOffsetEntity entity);
	int commitOffsetAndUpdateVersion(QueueOffsetEntity entity);
	int commitOffsetById(QueueOffsetEntity entity);

	void deRegister(long consumerId);

	public void setConsumserIdsToNull(@Param("consumerIds") List<Long> consumerIds);

	void deleteByConsumerGroupId(@Param("consumerGroupId") long consumerGroupId);

	void deleteByConsumerGroupIdAndOriginTopicName(@Param("consumerGroupId") long consumerGroupId,
			@Param("originTopicName") String originTopicName);

	List<QueueOffsetEntity> getByConsumerGroupTopic(@Param("consumerGroupId") long consumerGroupId,
			@Param("topicId") long topicId);

	void updateStopFlag(@Param("id") long id, @Param("stopFlag") int stopFlag, @Param("updateBy") String updateBy);

	int updateQueueOffset(Map<String, Object> parameterMap);

	List<QueueOffsetEntity> getUnSubscribeData();

	// 获取基本的字段
	List<QueueOffsetEntity> getAllBasic();

	List<OffsetVersionEntity> getOffsetVersion();

	LastUpdateEntity getLastUpdate();

	long countBy(Map<String, Object> conditionMap);

	List<QueueOffsetEntity> getListBy(Map<String, Object> conditionMap);

	Long getOffsetSumByIds(@Param("ids") List<Long> ids);
}
