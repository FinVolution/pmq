package com.ppdai.infrastructure.mq.client.factory;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqBrokerUrlRefreshService;
import com.ppdai.infrastructure.mq.client.core.IMqCheckService;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqHeartbeatService;
import com.ppdai.infrastructure.mq.client.core.IMqMeticReporterService;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqTopicQueueRefreshService;
import com.ppdai.infrastructure.mq.client.core.IMsgNotifyService;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public interface IMqFactory {
	IMqBrokerUrlRefreshService createMqBrokerUrlRefreshService();

	IMqCheckService createMqCheckService();

	IMqGroupExcutorService createMqGroupExcutorService();

	IMqHeartbeatService createMqHeartbeatService();

	IMqMeticReporterService createMqMeticReporterService();

	IMqQueueExcutorService createMqQueueExcutorService(String consumerGroupName,
			ConsumerQueueDto consumerQueue);

	IMqTopicQueueRefreshService createMqTopicQueueRefreshService();
	
	IConsumerPollingService createConsumerPollingService();
	
	IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut);
	IMsgNotifyService createMsgNotifyService();
}
