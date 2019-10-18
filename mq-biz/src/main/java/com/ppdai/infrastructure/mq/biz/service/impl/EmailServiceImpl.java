package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.EmailService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private TopicService topicService;

	@Autowired
	private ConsumerGroupService consumerGroupService;

	@Autowired
	private EmailUtil emailUtil;

	// private final String[] arrInfo = { "info", "warn", "error" };

	@Override
	public void sendConsumerMail(SendMailRequest request) {
		// 使用以前的告警方式
		sendByConsumer(request);
		addLogMsg(request);
	}

	@Override
	public void sendProduceMail(SendMailRequest request) {
		// 使用以前的告警方式
		sendByProducer(request);
		addLogMsg(request);
	}

	private void addLogMsg(SendMailRequest request) {
		
	}

	/**
	 * 原生的根据consumerGroup告警
	 * 
	 * @param request
	 */
	private void sendByConsumer(SendMailRequest request) {
		if (request == null || !StringUtils.isEmpty(request.getConsumerGroupName())
				|| !StringUtils.isEmpty(request.getTopicName())) {
			if (request.getType() > 0 && request.getType() < 3) {
				Map<String, ConsumerGroupEntity> map = consumerGroupService.getCache();
				if (map.containsKey(request.getConsumerGroupName())) {
					ConsumerGroupTopicEntity topicEntity = consumerGroupService.getTopic(request.getConsumerGroupName(),
							request.getTopicName());
					String alarms = map.get(request.getConsumerGroupName()).getAlarmEmails();
					if (topicEntity != null) {
						alarms += "," + topicEntity.getAlarmEmails() + ",";
					}
					alarms = alarms.replaceAll(",,", ",");
					emailUtil.sendMail(request.getSubject(), request.getContent(), Arrays.asList(alarms.split(",")),
							request.getType());
				}
			}
		} else if (request != null && request.getType() > 0 && request.getType() < 3) {
			emailUtil.sendMail(request.getSubject(), request.getContent(), null, request.getType());
		}
	}

	/**
	 * 原生的根据producer告警
	 * 
	 * @param request
	 */
	private void sendByProducer(SendMailRequest request) {
		if (request == null || !StringUtils.isEmpty(request.getTopicName())) {
			if (request.getType() > 0 && request.getType() < 3) {
				Map<String, TopicEntity> map = topicService.getCache();
				if (map.containsKey(request.getTopicName())) {
					TopicEntity topicEntity = map.get(request.getTopicName());
					String alarms = topicEntity.getEmails() + "";
					emailUtil.sendMail(request.getSubject(), request.getContent(), Arrays.asList(alarms.split(",")),
							request.getType());
				}
			}
		}
	}
}
