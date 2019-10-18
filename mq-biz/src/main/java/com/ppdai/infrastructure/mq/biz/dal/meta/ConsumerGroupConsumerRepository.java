package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;

/**
 * @author dal-generator
 */
@Mapper
public interface ConsumerGroupConsumerRepository extends BaseRepository<ConsumerGroupConsumerEntity> {

	List<ConsumerGroupConsumerEntity> getByConsumerGroupIds(@Param("consumerGroupIds") List<Long> consumerGroupIds);

	List<ConsumerGroupConsumerEntity> getByConsumerIds(@Param("consumerIds") List<Long> consumerIds);

	void deleteByConsumerId(@Param("consumerId") long consumerId);

	void deleteByConsumerIds(@Param("consumerIds") List<Long> consumerId);

	int deleteUnActiveConsumer();
}
