package com.ppdai.infrastructure.mq.client.core.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupMetaDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupResponse;
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
import com.ppdai.infrastructure.mq.client.AbstractMockMqFactory;
import com.ppdai.infrastructure.mq.client.AbstractMockResource;
import com.ppdai.infrastructure.mq.client.AbstractTest;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

@RunWith(JUnit4.class)
public class ConsumerPollingServiceTest extends AbstractTest{

	static long sleepTime = 1000L; 

	@Test
	public void startTest()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		MqPollingResource mqPollingResource = new MqPollingResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mqPollingResource);

		ConsumerPollingService consumerPollingService = new ConsumerPollingService(mockMqClientBase, mqPollingResource);
		consumerPollingService.start();
		Util.sleep(sleepTime + 1000L);
		Field field = ConsumerPollingService.class.getDeclaredField("mqExcutors");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, IMqGroupExcutorService> mqExcutors = (Map<String, IMqGroupExcutorService>) (field
				.get(consumerPollingService));
		MockMqGroupExcutorService mockMqGroupExcutorService = (MockMqGroupExcutorService) (mqExcutors
				.get(consumerGroupName));
		Util.sleep(sleepTime + 2000L);
		assertNotEquals("start error", 0, mockMqGroupExcutorService.getStartFlag());
		consumerPollingService.close();
	}

	@Test
	public void rbOrUpdateTest() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
			SecurityException, NoSuchMethodException, InvocationTargetException {
		MqPollingResource mqPollingResource = new MqPollingResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mqPollingResource);
		ConsumerPollingService consumerPollingService = new ConsumerPollingService(mockMqClientBase, mqPollingResource);
		consumerPollingService.start();
		Util.sleep(sleepTime + 1000L);
//		Method longPolling= ConsumerPollingService.class.getDeclaredMethod("longPolling");
//		longPolling.setAccessible(true);
//		longPolling.invoke(consumerPollingService);		
		Field field = ConsumerPollingService.class.getDeclaredField("mqExcutors");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, IMqGroupExcutorService> mqExcutors = (Map<String, IMqGroupExcutorService>) (field
				.get(consumerPollingService));
		MockMqGroupExcutorService mockMqGroupExcutorService = (MockMqGroupExcutorService) (mqExcutors
				.get(consumerGroupName));
		int count = mockMqGroupExcutorService.getRbOrUpdateFlag();
		mqPollingResource.getConsumerGroupMetaDto()
				.setVersion(mqPollingResource.getConsumerGroupMetaDto().getVersion() + 1);
		mqPollingResource.setRunFlag(true);
		Util.sleep(sleepTime + 1000L);
		// longPolling.invoke(consumerPollingService);
		assertEquals("getRbOrUpdateFlag error", 1, mockMqGroupExcutorService.getRbOrUpdateFlag() - count);
		consumerPollingService.close();
	}

	@Test
	public void rbStartTest()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		MqPollingResource mqPollingResource = new MqPollingResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mqPollingResource);
		ConsumerPollingService consumerPollingService = new ConsumerPollingService(mockMqClientBase, mqPollingResource);
		consumerPollingService.start();
		int restart = mockMqClientBase.getReStart();
		mqPollingResource.setConsumerDelete(1);
		mqPollingResource.setRunFlag(true);
		Util.sleep(sleepTime + 1000L);
		assertEquals("ReStart error", 1, mockMqClientBase.getReStart() - restart);
		consumerPollingService.close();
	}

	@Test
	public void closeTest()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		MqPollingResource mqPollingResource = new MqPollingResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mqPollingResource);
		ConsumerPollingService consumerPollingService = new ConsumerPollingService(mockMqClientBase, mqPollingResource);
		consumerPollingService.start();
		Util.sleep(sleepTime + 1000L);
		Field field = ConsumerPollingService.class.getDeclaredField("mqExcutors");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, IMqGroupExcutorService> mqExcutors = (Map<String, IMqGroupExcutorService>) (field
				.get(consumerPollingService));
		MockMqGroupExcutorService mockMqGroupExcutorService = (MockMqGroupExcutorService) (mqExcutors
				.get(consumerGroupName));

		int count = mockMqGroupExcutorService.getCloseFlag();
		consumerPollingService.close();
		assertEquals("close error", 1, mockMqGroupExcutorService.getCloseFlag() - count);
	}

	private static class MockMqGroupExcutorService implements IMqGroupExcutorService {

		int startFlag = 0;
		int closeFlag = 0;
		int rbOrUpdateFlag = 0;

		public int getStartFlag() {
			return startFlag;
		}

		public int getCloseFlag() {
			return closeFlag;
		}

		public int getRbOrUpdateFlag() {
			return rbOrUpdateFlag;
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
		public void rbOrUpdate(ConsumerGroupOneDto consumerGroupOne, String serverIp) {
			rbOrUpdateFlag++;

		}

	}

	private static class MockMqFactory extends AbstractMockMqFactory {
		@Override
		public IMqGroupExcutorService createMqGroupExcutorService(IMqClientBase mqClientBase) {
			// TODO Auto-generated method stub
			return new MockMqGroupExcutorService();
		}
	}

	private static class MqPollingResource extends AbstractMockResource {

		private int consumerDelete = 0;
		private ConsumerGroupMetaDto consumerGroupMetaDto = new ConsumerGroupMetaDto();
		private boolean runFlag = true;

		public MqPollingResource() {
			consumerGroupMetaDto.setMetaVersion(1);
			consumerGroupMetaDto.setName(consumerGroupName);
			consumerGroupMetaDto.setRbVersion(2);
			consumerGroupMetaDto.setVersion(3);
		}

		public boolean isRunFlag() {
			return runFlag;
		}

		public void setRunFlag(boolean runFlag) {
			this.runFlag = runFlag;
		}

		public ConsumerGroupMetaDto getConsumerGroupMetaDto() {
			return consumerGroupMetaDto;
		}

		public void setConsumerDelete(int consumerDelete) {
			this.consumerDelete = consumerDelete;
		}

		@Override
		public GetConsumerGroupResponse getConsumerGroup(GetConsumerGroupRequest request) {
			Util.sleep(sleepTime);
			if (isRunFlag()) {
				GetConsumerGroupResponse getConsumerGroupResponse = new GetConsumerGroupResponse();
				getConsumerGroupResponse.setBrokerMetaMode(1);
				getConsumerGroupResponse.setConsumerDeleted(consumerDelete);
				Map<String, ConsumerGroupOneDto> consumerGroups = new HashMap<String, ConsumerGroupOneDto>();
				if (consumerDelete != 1) {
					ConsumerGroupOneDto consumerGroupOneDto = new ConsumerGroupOneDto();
					consumerGroupOneDto.setMeta(consumerGroupMetaDto);
					consumerGroups.put(consumerGroupName, consumerGroupOneDto);
					Map<Long, ConsumerQueueDto> queues = new HashMap<Long, ConsumerQueueDto>();
					queues.put(1L, new ConsumerQueueDto());
					consumerGroupOneDto.setQueues(queues);
				}
				getConsumerGroupResponse.setConsumerGroups(consumerGroups);
				setRunFlag(false);
				// getConsumerGroupResponse.set
				return getConsumerGroupResponse;
			} else {
				return null;
			}
		}

		@Override
		public String getBrokerIp() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static class MockMqClientBase extends AbstractMockMqClientBase {
		private int reStart = 0;
		private IMqResource poIMqResource;
		MqContext mqContext = new MqContext();
		IMqFactory iFactory=new MockMqFactory();
		public MockMqClientBase(IMqResource mqResource) {
			poIMqResource = mqResource;
			mqContext.setConsumerId(1);
			mqContext.setMqPollingResource(poIMqResource);
			Map<String, Long> consumerGroupVersionMap = new HashMap<String, Long>();
			consumerGroupVersionMap.put(consumerGroupName, 1L);
			mqContext.setConsumerGroupVersion(consumerGroupVersionMap);
		}

		public int getReStart() {
			return reStart;
		}

		@Override
		public void reStart() {
			reStart++;
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
