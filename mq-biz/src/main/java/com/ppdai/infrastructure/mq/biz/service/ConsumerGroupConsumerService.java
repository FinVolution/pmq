package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */
public interface ConsumerGroupConsumerService extends BaseService<ConsumerGroupConsumerEntity> {

	List<ConsumerGroupConsumerEntity> getByConsumerGroupIds(List<Long> consumerGroupIds);

	List<ConsumerGroupConsumerEntity> getByConsumerIds(List<Long> consumerIds);

	void deleteByConsumerId(long consumerId);

	void deleteByConsumerIds(List<Long> consumerIds);

	int deleteUnActiveConsumer();
}
