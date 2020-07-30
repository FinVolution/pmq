package com.ppdai.infrastructure.mq.client.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;

@RunWith(JUnit4.class)
public class MqFactoryTest {

	@Test
	public void test() {

		MqFactory mqFactory = new MqFactory();
		boolean rs = true;
		try {
			mqFactory.createMqBrokerUrlRefreshService();
		} catch (Exception e) {
			rs = false;
		}
		assertEquals("createMqBrokerUrlRefreshService error", false, rs);

		rs = true;
		try {
			mqFactory.createMqBrokerUrlRefreshService();
		} catch (Exception e) {
			rs = false;
		}
		assertEquals("createMqBrokerUrlRefreshService error", false, rs);
		mqFactory.createMqBrokerUrlRefreshService();
		mqFactory.createMqCheckService();
		mqFactory.createConsumerPollingService();
		mqFactory.createMqGroupExcutorService();
		mqFactory.createMqHeartbeatService();
		mqFactory.createMqMeticReporterService();
		mqFactory.createMqQueueExcutorService("ttt", new ConsumerQueueDto());
		mqFactory.createMqTopicQueueRefreshService();
	}
}
