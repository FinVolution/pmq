package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.client.MsgNotifyRequest;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMsgNotifyService;

public class MsgNotifyService implements IMsgNotifyService {
	@Override
	public void notify(MsgNotifyRequest request) {
		IConsumerPollingService consumerPollingService = MqClient.getMqFactory().createConsumerPollingService();
		Map<String, IMqGroupExcutorService> groups = consumerPollingService.getMqExcutors();
		if (groups != null && request != null && request.getMsgNotifyDtos() != null) {
			request.getMsgNotifyDtos().forEach(msgNotifyDto -> {
				if (groups.containsKey(msgNotifyDto.getConsumerGroupName())) {
					IMqGroupExcutorService iMqGroupExcutorService = groups.get(msgNotifyDto.getConsumerGroupName());
					Map<Long, IMqQueueExcutorService> queues = iMqGroupExcutorService.getQueueEx();
					if (queues.containsKey(msgNotifyDto.getQueueId())) {
						queues.get(msgNotifyDto.getQueueId()).notifyMsg();
					}
				}
			});
		}
	}
}
