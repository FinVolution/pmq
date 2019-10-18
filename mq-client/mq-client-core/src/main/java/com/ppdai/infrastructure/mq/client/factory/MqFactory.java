package com.ppdai.infrastructure.mq.client.factory;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.core.impl.ConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.impl.MqBrokerUrlRefreshService;
import com.ppdai.infrastructure.mq.client.core.impl.MqCheckService;
import com.ppdai.infrastructure.mq.client.core.impl.MqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.core.impl.MqHeartbeatService;
import com.ppdai.infrastructure.mq.client.core.impl.MqMeticReporterService;
import com.ppdai.infrastructure.mq.client.core.impl.MqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.core.impl.MqTopicQueueRefreshService;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;
import com.ppdai.infrastructure.mq.client.resource.MqResource;

public class MqFactory implements IMqFactory {

	@Override
	public MqBrokerUrlRefreshService createMqBrokerUrlRefreshService(IMqClientBase mqClientBase) {
		return new MqBrokerUrlRefreshService(mqClientBase); 
	}

	@Override
	public MqCheckService createMqCheckService(IMqClientBase mqClientBase) {
		return new MqCheckService(mqClientBase);
	}

	@Override
	public MqGroupExcutorService createMqGroupExcutorService(IMqClientBase mqClientBase) {
		return new MqGroupExcutorService(mqClientBase);
	}

	@Override
	public MqHeartbeatService createMqHeartbeatService(IMqClientBase mqClientBase) {
		return new MqHeartbeatService(mqClientBase);
	}

	@Override
	public MqMeticReporterService createMqMeticReporterService(IMqClientBase mqClientBase) {
		return MqMeticReporterService.getInstance(mqClientBase);
	}

	@Override
	public MqQueueExcutorService createMqQueueExcutorService(IMqClientBase mqClientBase, String consumerGroupName,
			ConsumerQueueDto consumerQueue) {
		return new MqQueueExcutorService(mqClientBase, consumerGroupName, consumerQueue);
	}

	@Override
	public MqTopicQueueRefreshService createMqTopicQueueRefreshService(IMqClientBase mqClientBase) {
		return MqTopicQueueRefreshService.getInstance(mqClientBase);
	}

	@Override
	public ConsumerPollingService createConsumerPollingService(IMqClientBase mqClientBase) {
		return new ConsumerPollingService(mqClientBase);
	}

	@Override
	public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
		// TODO Auto-generated method stub
		return new MqResource(url, connectionTimeOut, readTimeOut);
	}

}
