package com.ppdai.infrastructure.mq.biz.polling;

import java.util.List;

import javax.annotation.PostConstruct;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.EmailService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;

@Component
public class NoSubscribeNotifyService extends AbstractTimerService {

	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private EmailService emailService;

	@PostConstruct
	private void init() {
		super.init(Constants.NO_SUBSCRIBE, soaConfig.getNoSubscribeInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getNoSubscribeInterval();
			@Override
			public void run() {
				if (soaConfig.getNoSubscribeInterval() != interval) {
					interval = soaConfig.getNoSubscribeInterval();
					updateInterval(interval);
				}
			}
		});
	}

	@Override
	public void doStart() {
		if (soaConfig.isEnbaleNoSubsribe()) {
			List<QueueOffsetEntity> unSub3 = queueOffsetService.getUnSubscribeData();		
				unSub3.forEach(t1 -> {
					sendWarnEmail(t1);
				});
			
		}
	}

	private void sendWarnEmail(QueueOffsetEntity t1) {
		SendMailRequest sendMailRequest=new SendMailRequest();
		sendMailRequest.setConsumerGroupName(t1.getConsumerGroupName());
		sendMailRequest.setSubject(t1.getConsumerGroupName() + "存在队列未订阅");
		sendMailRequest.setContent("Consumer组:" + t1.getConsumerGroupName() + "下的Topic:"
				+ t1.getTopicName() + "中的队列:" + t1.getQueueId() + "没有被订阅!");		
		sendMailRequest.setType(1);
		sendMailRequest.setKey(t1.getConsumerGroupName()+"-"+t1.getTopicName()+"没有被订阅");
		emailService.sendConsumerMail(sendMailRequest);
//		ConsumerGroupEntity consumerGroup = consumergroupService.getCache().get(t1.getConsumerGroupName());
//		if (consumerGroup != null) {
//			
//			emailUtil
//					.sendWarnMail(
//							t1.getConsumerGroupName() + "存在队列未订阅", "Consumer组:" + t1.getConsumerGroupName() + "下的Topic:"
//									+ t1.getTopicName() + "中的队列:" + t1.getQueueId() + "没有被订阅!",
//							consumerGroup.getAlarmEmails());
//		}
	}
}
