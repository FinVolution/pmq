package com.ppdai.infrastructure.rest.mq.controller.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
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
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.bootstrap.MqClientStartup;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;

@RestController
@RequestMapping(MqConstanst.CONSUMERPRE)
public class ConsumerController {

	@Autowired
	private ConsumerService consumerService;

	@Autowired
	private QueueService queueService;

	@Autowired
	private SoaConfig soaConfig;

	@Autowired
	private Environment env;

	@PostMapping("/register")
	public ConsumerRegisterResponse register(@RequestBody ConsumerRegisterRequest request) {
		ConsumerRegisterResponse response = consumerService.register(request);
		return response;
	}

	@PostMapping("/registerConsumerGroup")
	public ConsumerGroupRegisterResponse consumerGroupRegister(@RequestBody ConsumerGroupRegisterRequest request) {
		ConsumerGroupRegisterResponse response = consumerService.registerConsumerGroup(request);
		return response;
	}

	@PostMapping("/deRegister")
	public ConsumerDeRegisterResponse deRegister(@RequestBody ConsumerDeRegisterRequest request) {
		ConsumerDeRegisterResponse response = consumerService.deRegister(request);
		return response;
	}

	@PostMapping("/publish")
	public PublishMessageResponse publish(@RequestBody PublishMessageRequest request) {
		try {
			PublishMessageResponse response = consumerService.publish(request);
			if (response != null && !response.isSuc() && !Util.isEmpty(request.getTopicName())) {
				Map<String, List<QueueEntity>> topicQueueMap = queueService.getAllLocatedTopicQueue();
				if (!Util.isEmpty(soaConfig.getSysPubFailTopic())
						&& topicQueueMap.containsKey(soaConfig.getSysPubFailTopic())) {
					ProducerDataDto msg = new ProducerDataDto(request.getTopicName(), JsonUtil.toJsonNull(request));
					Map<String, String> head = new HashMap<>();
					head.put("result", response.getMsg());
					msg.setHead(head);
					if (!MqClient.hasInit()) {
						MqClientStartup.init(env);
						MqClient.start();
					}
					MqClient.publishAsyn(soaConfig.getSysPubFailTopic(), "", msg);
				}
			}
			return response;
		} catch (Exception e) {
			if (!Util.isEmpty(request.getTopicName())) {
				Map<String, List<QueueEntity>> topicQueueMap = queueService.getAllLocatedTopicQueue();
				if (topicQueueMap.containsKey(soaConfig.getSysPubFailTopic())) {
					ProducerDataDto msg = new ProducerDataDto(request.getTopicName(), JsonUtil.toJsonNull(request));
					Map<String, String> head = new HashMap<>();
					head.put("result", e.getMessage());
					msg.setHead(head);
					if (!MqClient.hasInit()) {
						MqClientStartup.init(env);
						MqClient.start();
					}
					try {
						MqClient.publishAsyn(soaConfig.getSysPubFailTopic(), "", msg);
					} catch (MqNotInitException | ContentExceed65535Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			throw new RuntimeException(e);
		}
	}

	@PostMapping("/pullData")
	public PullDataResponse pullData(@RequestBody PullDataRequest request) {
		try {
			PullDataResponse response = consumerService.pullData(request);
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}

	@PostMapping("/publishAndUpdateResultFailMsg")
	public FailMsgPublishAndUpdateResultResponse publishAndUpdateResultFailMsg(
			@RequestBody FailMsgPublishAndUpdateResultRequest request) {
		try {
			FailMsgPublishAndUpdateResultResponse response = consumerService.publishAndUpdateResultFailMsg(request);
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}

	@PostMapping("/getMessageCount")
	public GetMessageCountResponse getMessageCount(@RequestBody GetMessageCountRequest request) {
		GetMessageCountResponse response = consumerService.getMessageCount(request);
		return response;
	}

	@Deprecated
	@PostMapping("/getConsumerGroupCount")
	public GetMessageCountResponse getConsumerGroupCount(@RequestBody GetMessageCountRequest request) {
		GetMessageCountResponse response = consumerService.getMessageCount(request);
		return response;
	}
}
