package com.ppdai.infrastructure.mq.client.factory;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.client.core.IMqCommitService;
import com.ppdai.infrastructure.mq.client.core.IMsgNotifyService;
import com.ppdai.infrastructure.mq.client.core.impl.*;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;
import com.ppdai.infrastructure.mq.client.resource.MqResource;

public class MqFactory implements IMqFactory {

	private static final Object lockObj = new Object();

	private MqBrokerUrlRefreshService mqBrokerUrlRefreshService;

	@Override
	public MqBrokerUrlRefreshService createMqBrokerUrlRefreshService() {
		if (mqBrokerUrlRefreshService == null) {
			synchronized (lockObj) {
				if (mqBrokerUrlRefreshService == null) {
					mqBrokerUrlRefreshService = new MqBrokerUrlRefreshService();
				}
			}
		}
		return mqBrokerUrlRefreshService;
	}

	private MqCheckService mqCheckService;

	@Override
	public MqCheckService createMqCheckService() {
		if (mqCheckService == null) {
			synchronized (lockObj) {
				if (mqCheckService == null) {
					mqCheckService = new MqCheckService();
				}
			}
		}
		return mqCheckService;
	}

	@Override
	public MqGroupExcutorService createMqGroupExcutorService() {
		return new MqGroupExcutorService();
	}

	private MqHeartbeatService mqHeartbeatService;

	@Override
	public MqHeartbeatService createMqHeartbeatService() {
		if (mqHeartbeatService == null) {
			synchronized (lockObj) {
				if (mqHeartbeatService == null) {
					mqHeartbeatService = new MqHeartbeatService();
				}
			}
		}
		return mqHeartbeatService;
	}

	@Override
	public MqMeticReporterService createMqMeticReporterService() {
		return MqMeticReporterService.getInstance();
	}

	@Override
	public MqQueueExcutorService createMqQueueExcutorService(String consumerGroupName, ConsumerQueueDto consumerQueue) {
		return new MqQueueExcutorService(consumerGroupName, consumerQueue);
	}

	@Override
	public MqTopicQueueRefreshService createMqTopicQueueRefreshService() {
		return MqTopicQueueRefreshService.getInstance();
	}

	private ConsumerPollingService consumerPollingService;

	@Override
	public ConsumerPollingService createConsumerPollingService() {
		if (consumerPollingService == null) {
			synchronized (lockObj) {
				if (consumerPollingService == null) {
					consumerPollingService = new ConsumerPollingService();
				}
			}
		}
		return consumerPollingService;
	}

	@Override
	public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
		// TODO Auto-generated method stub
		return new MqResource(url, connectionTimeOut, readTimeOut);
	}

	private IMsgNotifyService msgNotifyService;

	@Override
	public IMsgNotifyService createMsgNotifyService() {
		if (msgNotifyService == null) {
			synchronized (lockObj) {
				if (msgNotifyService == null) {
					msgNotifyService = new MsgNotifyService();
				}
			}
		}
		return msgNotifyService;
	}

	private IMqCommitService iMqCommitService;

	@Override
	public IMqCommitService createCommitService() {
		if (iMqCommitService == null) {
			synchronized (lockObj) {
				if (iMqCommitService == null) {
					iMqCommitService = new MqCommitService();
				}
			}
		}
		return iMqCommitService;
	}
}
