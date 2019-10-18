package com.ppdai.infrastructure.mq.client.factory;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqBrokerUrlRefreshService;
import com.ppdai.infrastructure.mq.client.core.IMqCheckService;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqHeartbeatService;
import com.ppdai.infrastructure.mq.client.core.IMqMeticReporterService;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqTopicQueueRefreshService;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public interface IMqFactory {
	IMqBrokerUrlRefreshService createMqBrokerUrlRefreshService(IMqClientBase mqClientBase);

	IMqCheckService createMqCheckService(IMqClientBase mqClientBase);

	IMqGroupExcutorService createMqGroupExcutorService(IMqClientBase mqClientBase);

	IMqHeartbeatService createMqHeartbeatService(IMqClientBase mqClientBase);

	IMqMeticReporterService createMqMeticReporterService(IMqClientBase mqClientBase);

	IMqQueueExcutorService createMqQueueExcutorService(IMqClientBase mqClientBase, String consumerGroupName,
			ConsumerQueueDto consumerQueue);

	IMqTopicQueueRefreshService createMqTopicQueueRefreshService(IMqClientBase mqContext);
	
	IConsumerPollingService createConsumerPollingService(IMqClientBase mqClientBase);
	
	IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut);
}
