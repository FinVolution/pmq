package com.ppdai.infrastructure.rest.mq.controller.client;

import java.util.HashMap;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.MqEnv;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
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
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.client.MqClient;

@RestController
@RequestMapping(MqConstanst.CONSUMERPRE)
public class ConsumerController {

	@Autowired
	private ConsumerService consumerService;
	@Autowired
	private SoaConfig soaConfig;

	@PostMapping("/register")
	public ConsumerRegisterResponse register(@RequestBody ConsumerRegisterRequest request) {
		ConsumerRegisterResponse response = consumerService.register(request);
		return response;
	}

	@PostMapping("/registerConsumerGroup")
	public ConsumerGroupRegisterResponse consumerGroupRegister(@RequestBody ConsumerGroupRegisterRequest request) {
		Transaction transaction= Tracer.newTransaction("ConsumerGroupRegist", "Group-"+ JsonUtil.toJsonNull(request.getConsumerGroupNames().keySet()));
		ConsumerGroupRegisterResponse response = consumerService.registerConsumerGroup(request);
		transaction.addData("clientIp",request.getClientIp());
		transaction.setStatus(Transaction.SUCCESS);
		transaction.complete();
		return response;
	}

	@PostMapping("/deRegister")
	public ConsumerDeRegisterResponse deRegister(@RequestBody ConsumerDeRegisterRequest request) {
		ConsumerDeRegisterResponse response = consumerService.deRegister(request);
		return response;
	}

	@PostMapping("/publish")
	public PublishMessageResponse publish(@RequestBody PublishMessageRequest request) {
		setSubEnv(request);
		PublishMessageResponse response = consumerService.publish(request);
		return response;

	}

	public boolean setSubEnvFlag() {
		return !soaConfig.isPro() && MqClient.getMqEnvironment() != null
				&& (MqClient.getMqEnvironment().getEnv() == MqEnv.FAT) && (soaConfig.getMqBrokerSetSubEnvFlag() == 1);
	}

	private void setSubEnv(PublishMessageRequest request) {
		if (setSubEnvFlag()) {
			for (ProducerDataDto t1 : request.getMsgs()) {
				if (t1.getHead() == null) {
					t1.setHead(new HashMap<>());
				}
				if (!Util.isEmpty(t1.getBizId()) && soaConfig.getMqTopicRouteMap() != null
						&& soaConfig.getMqTopicRouteMap().containsKey(request.getTopicName())
						&& soaConfig.getMqTopicRouteMap().get(request.getTopicName()).containsKey(t1.getBizId())) {
					if (t1.getHead().containsKey(MqConst.MQ_SUB_ENV_KEY)) {
						t1.getHead().put("mq_sub_env_orgin", t1.getHead().get(MqConst.MQ_SUB_ENV_KEY));
					} else {
						t1.getHead().put("mq_sub_env_orgin", MqConst.DEFAULT_SUBENV);
					}
					t1.getHead().put(MqConst.MQ_SUB_ENV_KEY,
							soaConfig.getMqTopicRouteMap().get(request.getTopicName()).get(t1.getBizId()));
				}
			}
		}
	}

	@PostMapping("/pullData")
	public PullDataResponse pullData(@RequestBody PullDataRequest request) {
		PullDataResponse response = consumerService.pullData(request);
		return response;
	}

	@PostMapping("/publishAndUpdateResultFailMsg")
	public FailMsgPublishAndUpdateResultResponse publishAndUpdateResultFailMsg(
			@RequestBody FailMsgPublishAndUpdateResultRequest request) {
		FailMsgPublishAndUpdateResultResponse response = consumerService.publishAndUpdateResultFailMsg(request);
		return response;

	}

	@PostMapping("/getMessageCount")
	public GetMessageCountResponse getMessageCount(@RequestBody GetMessageCountRequest request) {
		GetMessageCountResponse response = consumerService.getMessageCount(request);
		return response;
	}
}
