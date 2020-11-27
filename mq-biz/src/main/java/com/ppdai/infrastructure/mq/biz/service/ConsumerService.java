package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */

public interface ConsumerService extends BaseService<ConsumerEntity> {	
	List<ConsumerGroupConsumerEntity> getConsumerGroupByConsumerGroupIds(List<Long> consumerGroupIds);
	List<ConsumerGroupConsumerEntity> getConsumerGroupByConsumerIds(List<Long> consumerIds);	
	ConsumerRegisterResponse register(ConsumerRegisterRequest request);
	ConsumerGroupRegisterResponse registerConsumerGroup(ConsumerGroupRegisterRequest request);	
	ConsumerDeRegisterResponse deRegister(ConsumerDeRegisterRequest deRegisterRequest);
	PublishMessageResponse publish(PublishMessageRequest request);
	PullDataResponse pullData(PullDataRequest request);
	FailMsgPublishAndUpdateResultResponse publishAndUpdateResultFailMsg(FailMsgPublishAndUpdateResultRequest request);
	GetMessageCountResponse getMessageCount(GetMessageCountRequest request);
	int heartbeat(List<Long> ids);
	List<ConsumerEntity> findByHeartTimeInterval(long heartTimeInterval);
	boolean deleteByConsumers(List<ConsumerEntity>consumers);
	ConsumerEntity getConsumerByConsumerGroupId(Long consumerGroupId);
	long countBy(Map<String, Object> conditionMap);
	List<ConsumerEntity> getListBy(Map<String, Object> conditionMap);
}
