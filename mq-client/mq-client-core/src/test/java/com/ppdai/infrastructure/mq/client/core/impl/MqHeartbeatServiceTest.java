package com.ppdai.infrastructure.mq.client.core.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatRequest;
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
import com.ppdai.infrastructure.mq.client.AbstractMockResource;
import com.ppdai.infrastructure.mq.client.AbstractTest;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;

@RunWith(JUnit4.class)
public class MqHeartbeatServiceTest extends AbstractTest {

	@Test
	public void testStartConsumer0() {
		MockMqClientBase mqMockMqClientBase = new MockMqClientBase();
		MqHeartbeatResource mqHeartbeatResource = new MqHeartbeatResource();
		mqMockMqClientBase.getContext().setConsumerId(0);
		MqHeartbeatService mqHeartbeatService = new MqHeartbeatService(mqMockMqClientBase, mqHeartbeatResource);
		mqHeartbeatService.start();
		Util.sleep(5000);
		assertEquals("testConsumreNotExist error", 0, mqHeartbeatResource.getHeartbeatFlag());
		mqHeartbeatService.close();
	}

	@Test
	public void testStartConsumer1() {
		MockMqClientBase mqMockMqClientBase = new MockMqClientBase();
		MqHeartbeatResource mqHeartbeatResource = new MqHeartbeatResource();
		mqMockMqClientBase.getContext().setConsumerId(1);
		MqHeartbeatService mqHeartbeatService = new MqHeartbeatService(mqMockMqClientBase, mqHeartbeatResource);
		mqHeartbeatService.start();
		mqHeartbeatService.close();
	}

	@Test
	public void testStop() {
		MockMqClientBase mqMockMqClientBase = new MockMqClientBase();
		MqHeartbeatResource mqHeartbeatResource = new MqHeartbeatResource();
		mqMockMqClientBase.getContext().setConsumerId(1);
		MqHeartbeatService mqHeartbeatService = new MqHeartbeatService(mqMockMqClientBase, mqHeartbeatResource);
		mqHeartbeatService.start();
		mqHeartbeatService.close();
	}

	@Test
	public void testConsumreNotExist() {
		MockMqClientBase mqMockMqClientBase = new MockMqClientBase();
		MqHeartbeatResource mqHeartbeatResource = new MqHeartbeatResource();
		mqMockMqClientBase.getContext().setConsumerId(0);
		MqHeartbeatService mqHeartbeatService = new MqHeartbeatService(mqMockMqClientBase, mqHeartbeatResource);
		mqHeartbeatService.doHeartbeat();
		assertEquals("testConsumreNotExist error", 0, mqHeartbeatResource.getHeartbeatFlag());
	}

	@Test
	public void testConsumreExist() {
		MockMqClientBase mqMockMqClientBase = new MockMqClientBase();
		MqHeartbeatResource mqHeartbeatResource = new MqHeartbeatResource();
		MqHeartbeatService mqHeartbeatService = new MqHeartbeatService(mqMockMqClientBase, mqHeartbeatResource);
		mqHeartbeatService.doHeartbeat();
		assertEquals("testConsumreNotExist error", 1, mqHeartbeatResource.getHeartbeatFlag());
		mqHeartbeatService.doHeartbeat();
		assertEquals("testConsumreNotExist error", 2, mqHeartbeatResource.getHeartbeatFlag());
	}

	private static class MqHeartbeatResource extends AbstractMockResource {
		private volatile int heartbeatFlag = 0;

		@Override
		public void heartbeat(HeartbeatRequest request) {
			heartbeatFlag++;
		}

		public int getHeartbeatFlag() {
			return heartbeatFlag;
		}

		@Override
		public String getBrokerIp() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static class MockMqClientBase extends AbstractMockMqClientBase {
		private MqContext mqContext = new MqContext();

		public MockMqClientBase() {
			mqContext.setConsumerId(1);
			mqContext.setConsumerName("test");
			Map<String, Long> consumerGroupVersionMap = new HashMap<String, Long>();
			consumerGroupVersionMap.put(consumerGroupName, 0L);
			ConsumerGroupOneDto consumerGroupOneDto = buildConsumerGroupOne();
			mqContext.getConsumerGroupMap().put(consumerGroupName, consumerGroupOneDto);
			mqContext.setConsumerGroupVersion(consumerGroupVersionMap);
			mqContext.getConfig().setRbTimes(0);
		}

		@Override
		public MqContext getContext() {
			// TODO Auto-generated method stub
			return mqContext;
		}

		@Override
		public boolean publish(String topic, String token, ProducerDataDto message,
				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean publish(String topic, String token, List<ProducerDataDto> messages,
				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
			// TODO Auto-generated method stub
			return false;
		}
	};
}
