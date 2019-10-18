package com.ppdai.infrastructure.mq.client.core.impl;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
import com.ppdai.infrastructure.mq.client.AbstractMockMqFactory;
import com.ppdai.infrastructure.mq.client.AbstractMockResource;
import com.ppdai.infrastructure.mq.client.AbstractTest;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;

@RunWith(JUnit4.class)
public class MqGroupExcutorServiceTest extends AbstractTest {

	@Test
	public void testRbOrUpdateInit()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MqGroupResource mqResource = new MqGroupResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqGroupExcutorService mqGroupExcutorService = new MqGroupExcutorService(mockMqClientBase, mqResource);
		ConsumerGroupOneDto consumerGroupOne = buildModifyConsumerGroupOne();
		mqGroupExcutorService.rbOrUpdate(consumerGroupOne, "111");
		Field field = MqGroupExcutorService.class.getDeclaredField("localConsumerGroup");
		field.setAccessible(true);
		ConsumerGroupOneDto localConsumerGroupOneDto = (ConsumerGroupOneDto) field.get(mqGroupExcutorService);
		assertEquals("testRbOrUpdateAndNotStart error", JsonUtil.toJson(consumerGroupOne),
				JsonUtil.toJson(localConsumerGroupOneDto));

		MockMqFactory mockMqFactory = (MockMqFactory) mockMqClientBase.getMqFactory();
		assertEquals("test UpdateQueueMeta error", 0,
				mockMqFactory.getMqQueueExcutorService().getUpdateQueueMetaFlag());
	}

	@Test
	public void testRbOrUpdateAndStart()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MqGroupResource mqResource = new MqGroupResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqGroupExcutorService mqGroupExcutorService = new MqGroupExcutorService(mockMqClientBase, mqResource);
		ConsumerGroupOneDto consumerGroupOne = buildModifyConsumerGroupOne();
		mqGroupExcutorService.rbOrUpdate(consumerGroupOne, "111");
		mqGroupExcutorService.start();
		Field field = MqGroupExcutorService.class.getDeclaredField("localConsumerGroup");
		field.setAccessible(true);
		ConsumerGroupOneDto localConsumerGroupOneDto = (ConsumerGroupOneDto) field.get(mqGroupExcutorService);
		assertEquals("testRbOrUpdateAndNotStart error", JsonUtil.toJson(consumerGroupOne),
				JsonUtil.toJson(localConsumerGroupOneDto));

		MockMqFactory mockMqFactory = (MockMqFactory) mockMqClientBase.getMqFactory();
		assertEquals("test MqQueueExcutorService start error", 1,
				mockMqFactory.getMqQueueExcutorService().getStartFlag());

		assertEquals("test MqQueueExcutorService UpdateQueueMetaFlag error", 0,
				mockMqFactory.getMqQueueExcutorService().getUpdateQueueMetaFlag());

		consumerGroupOne=JsonUtil.copy(consumerGroupOne, ConsumerGroupOneDto.class);
		consumerGroupOne.getMeta().setMetaVersion(consumerGroupOne.getMeta().getMetaVersion() + 1);		
		mqGroupExcutorService.rbOrUpdate(consumerGroupOne, "111");
		mqGroupExcutorService.start();

		assertEquals("test MqQueueExcutorService UpdateQueueMetaFlag error", 1,
				mockMqFactory.getMqQueueExcutorService().getUpdateQueueMetaFlag());
		
		assertEquals("test MqQueueExcutorService close error", 0,
				mockMqFactory.getMqQueueExcutorService().getCloseFlag());

		assertEquals("test MqQueueExcutorService start error", 1,
				mockMqFactory.getMqQueueExcutorService().getStartFlag());

		assertEquals("test MqQueueExcutorService commit error", 0, mqResource.getCommitFlag());
		
		
		consumerGroupOne=JsonUtil.copy(consumerGroupOne, ConsumerGroupOneDto.class);		
		consumerGroupOne.getMeta().setRbVersion(consumerGroupOne.getMeta().getRbVersion() + 1);
		mqGroupExcutorService.rbOrUpdate(consumerGroupOne, "111");
		mqGroupExcutorService.start();

		assertEquals("test MqQueueExcutorService UpdateQueueMetaFlag error", 1,
				mockMqFactory.getMqQueueExcutorService().getUpdateQueueMetaFlag());

		assertEquals("test MqQueueExcutorService close error", 1,
				mockMqFactory.getMqQueueExcutorService().getCloseFlag());

		assertEquals("test MqQueueExcutorService start error", 2,
				mockMqFactory.getMqQueueExcutorService().getStartFlag());

		assertEquals("test MqQueueExcutorService commit error", 1, mqResource.getCommitFlag());
	}

	private static class MqGroupResource extends AbstractMockResource {
		private volatile int commitFlag = 0;

		@Override
		public void commitOffset(CommitOffsetRequest request) {
			commitFlag++;

		}

		public int getCommitFlag() {
			return commitFlag;
		}

		@Override
		public String getBrokerIp() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static class MqQueueExcutorService implements IMqQueueExcutorService {

		private volatile int startFlag = 0;
		private volatile int closeFlag = 0;
		private volatile int updateQueueMetaFlag = 0;

		public int getStartFlag() {
			return startFlag;
		}

		public int getCloseFlag() {
			return closeFlag;
		}

		public int getUpdateQueueMetaFlag() {
			return updateQueueMetaFlag;
		}

		@Override
		public void start() {
			startFlag++;

		}

		@Override
		public void close() {
			closeFlag++;

		}

		@Override
		public void updateQueueMeta(ConsumerQueueDto consumerQueue) {
			updateQueueMetaFlag++;

		}

	}

	private static class MockMqFactory extends AbstractMockMqFactory {
		MqQueueExcutorService mqQueueExcutorService = new MqQueueExcutorService();

		public MqQueueExcutorService getMqQueueExcutorService() {
			return mqQueueExcutorService;
		}

		@Override
		public IMqQueueExcutorService createMqQueueExcutorService(IMqClientBase mqClientBase, String consumerGroupName,
				ConsumerQueueDto consumerQueue) {
			// TODO Auto-generated method stub
			return mqQueueExcutorService;
		}

	}

	private static class MockMqClientBase extends AbstractMockMqClientBase {
		private MqContext mqContext = new MqContext();
		private IMqFactory iFactory = new MockMqFactory();

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
		public IMqFactory getMqFactory() {
			// TODO Auto-generated method stub
			return iFactory;
		}

		@Override
		public MqContext getContext() {
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
