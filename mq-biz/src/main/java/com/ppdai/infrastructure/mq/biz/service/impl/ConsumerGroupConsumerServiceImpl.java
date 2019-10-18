package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupConsumerRepository;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupConsumerService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;

/**
 * @author dal-generator
 */
@Service
public class ConsumerGroupConsumerServiceImpl extends AbstractBaseService<ConsumerGroupConsumerEntity>
		implements ConsumerGroupConsumerService {
	@Autowired
	private ConsumerGroupConsumerRepository consumerGroupConsumerRepository;

	@PostConstruct
	protected void init() {
		super.setBaseRepository(consumerGroupConsumerRepository);
	}

	@Override
	public List<ConsumerGroupConsumerEntity> getByConsumerGroupIds(List<Long> consumerGroupIds) {
		if (CollectionUtils.isEmpty(consumerGroupIds)) {
			return new ArrayList<>();
		}
		// TODO Auto-generated method stub
		return consumerGroupConsumerRepository.getByConsumerGroupIds(consumerGroupIds);
	}
	
	@Override
	public int deleteUnActiveConsumer() {		
		// TODO Auto-generated method stub
		return consumerGroupConsumerRepository.deleteUnActiveConsumer();
	}

	@Override
	public List<ConsumerGroupConsumerEntity> getByConsumerIds(List<Long> consumerIds) {
		if (CollectionUtils.isEmpty(consumerIds)) {
			return new ArrayList<>();
		}
		return consumerGroupConsumerRepository.getByConsumerIds(consumerIds);
	}

	@Override
	public void deleteByConsumerId(long consumerId) {
		consumerGroupConsumerRepository.deleteByConsumerId(consumerId);
	}

	@Override
	public void deleteByConsumerIds(List<Long> consumerIds) {
		consumerGroupConsumerRepository.deleteByConsumerIds(consumerIds);
	}
}
