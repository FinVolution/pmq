package com.ppdai.infrastructure.mq.client.factory;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.client.core.*;
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
	IMqCommitService createCommitService();
}
