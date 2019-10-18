package com.ppdai.infrastructure.mq.client.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;

@RunWith(JUnit4.class)
public class MqFactoryTest {

	@Test
	public void test() {
		IMqClientBase mqClientBase = new AbstractMockMqClientBase() {
		};
		MqFactory mqFactory = new MqFactory();
		boolean rs = true;
		try {
			mqFactory.createMqBrokerUrlRefreshService(mqClientBase);
		} catch (Exception e) {
			rs = false;
		}
		assertEquals("createMqBrokerUrlRefreshService error", false, rs);

		mqClientBase = new AbstractMockMqClientBase() {
			@Override
			public MqContext getContext() {
				// TODO Auto-generated method stub
				return new MqContext();
			}
		};
		rs = true;
		try {
			mqFactory.createMqBrokerUrlRefreshService(mqClientBase);
		} catch (Exception e) {
			rs = false;
		}
		assertEquals("createMqBrokerUrlRefreshService error", false, rs);
		mqClientBase = new AbstractMockMqClientBase() {
			@Override
			public MqContext getContext() {
				MqContext mqContext=new MqContext();
				mqContext.getConfig().setUrl("localhost");
				return mqContext;
			}
			@Override
			public IMqFactory getMqFactory() {
				// TODO Auto-generated method stub
				return mqFactory;
			}
		};
		mqFactory.createMqBrokerUrlRefreshService(mqClientBase); 
		mqFactory.createMqCheckService(mqClientBase);
		mqFactory.createConsumerPollingService(mqClientBase);
		mqFactory.createMqGroupExcutorService(mqClientBase);
		mqFactory.createMqHeartbeatService(mqClientBase);
		mqFactory.createMqMeticReporterService(mqClientBase);
		mqFactory.createMqQueueExcutorService(mqClientBase, "ttt", new ConsumerQueueDto());
		mqFactory.createMqTopicQueueRefreshService(mqClientBase);
	}
}
