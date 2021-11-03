package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerEntity;
import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;

/**
 * @author dal-generator
 */
@Mapper
public interface ConsumerRepository extends BaseRepository<ConsumerEntity> {
	int heartbeat(@Param("ids") List<Long> ids);

	List<ConsumerEntity> findByHeartTimeInterval(@Param("heartTimeInterval") long heartTimeInterval);

	boolean deleteByConsumerId(@Param("consumerId") Long consumerId);

	long register(ConsumerEntity t);

	List<ConsumerEntity> getListByLike();
	
	ConsumerEntity getConsumerByConsumerGroupId(@Param("consumerGroupId") Long consumerGroupId);

	long countBy(Map<String, Object> conditionMap);

	List<ConsumerEntity> getListBy(Map<String, Object> conditionMap);


}
