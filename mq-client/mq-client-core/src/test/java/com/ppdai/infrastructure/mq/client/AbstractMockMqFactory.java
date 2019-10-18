package com.ppdai.infrastructure.mq.client;

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
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;
import com.ppdai.infrastructure.mq.client.resource.MqResource;

public abstract class AbstractMockMqFactory implements IMqFactory{

	@Override
	public IMqBrokerUrlRefreshService createMqBrokerUrlRefreshService(IMqClientBase mqClientBase) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMqCheckService createMqCheckService(IMqClientBase mqClientBase) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMqGroupExcutorService createMqGroupExcutorService(IMqClientBase mqClientBase) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMqHeartbeatService createMqHeartbeatService(IMqClientBase mqClientBase) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMqMeticReporterService createMqMeticReporterService(IMqClientBase mqClientBase) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMqQueueExcutorService createMqQueueExcutorService(IMqClientBase mqClientBase, String consumerGroupName,
			ConsumerQueueDto consumerQueue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMqTopicQueueRefreshService createMqTopicQueueRefreshService(IMqClientBase mqContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConsumerPollingService createConsumerPollingService(IMqClientBase mqClientBase) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
		// TODO Auto-generated method stub
		return null;
	}

}
