//package com.ppdai.infrastructure.mq.client.core.impl;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//
//import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
//import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
//import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsRequest;
//import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsResponse;
//import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
//import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
//import com.ppdai.infrastructure.mq.client.AbstractMockResource;
//import com.ppdai.infrastructure.mq.client.AbstractTest;
//import com.ppdai.infrastructure.mq.client.MqContext;
//import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
//import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
//
//@RunWith(JUnit4.class)
//public class MqTopicQueueRefreshServiceTest extends AbstractTest {
//
//	@Test
//	public void doUpdateQueueNullTest() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqResource mqResource = new MqResource();
//		mqResource.setFlag(false);
//		mockMqClientBase.getContext().setMqResource(mqResource);
//		MqTopicQueueRefreshService mqTopicQueueRefreshService = new MqTopicQueueRefreshService(mockMqClientBase);
//		mqTopicQueueRefreshService.getTopicQueueIds(topicName);
//		mqTopicQueueRefreshService.doUpdateQueue();
//		assertEquals("doUpdateQueueNullTest error", null, mqTopicQueueRefreshService.getTopicQueueIds(topicName));
//	}
//
//	@Test
//	public void doUpdateQueueNotNullTest() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqResource mqResource = new MqResource();
//		mockMqClientBase.getContext().setMqResource(mqResource);
//		MqTopicQueueRefreshService mqTopicQueueRefreshService = new MqTopicQueueRefreshService(mockMqClientBase);
//		mqTopicQueueRefreshService.getTopicQueueIds(topicName);
//		mqTopicQueueRefreshService.doUpdateQueue();
//		assertEquals("doUpdateQueueNotNullTest error", true,
//				mqTopicQueueRefreshService.getTopicQueueIds(topicName).size() > 0);
//	}
//
//	@Test
//	public void getInstanceTest() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqTopicQueueRefreshService mqTopicQueueRefreshService = MqTopicQueueRefreshService
//				.getInstance(mockMqClientBase);
//		assertEquals("getInstanceTest error", true, mqTopicQueueRefreshService != null);
//	}
//
//	@Test
//	public void startTest() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqResource mqResource = new MqResource();
//		mockMqClientBase.getContext().setMqResource(mqResource);
//		MqTopicQueueRefreshService mqTopicQueueRefreshService = new MqTopicQueueRefreshService(mockMqClientBase);
//		mqTopicQueueRefreshService.start();
//	}
//
//	@Test
//	public void closeTest() {
//		MockMqClientBase mockMqClientBase = new MockMqClientBase();
//		MqResource mqResource = new MqResource();
//		mockMqClientBase.getContext().setMqResource(mqResource);
//		MqTopicQueueRefreshService mqTopicQueueRefreshService = new MqTopicQueueRefreshService(mockMqClientBase);
//		mqTopicQueueRefreshService.start();
//		mqTopicQueueRefreshService.close();
//	}
//
//	private static class MqResource extends AbstractMockResource {
//		private volatile boolean flag = true;
//
//		public void setFlag(boolean flag) {
//			this.flag = flag;
//		}
//
//		@Override
//		public GetTopicQueueIdsResponse getTopicQueueIds(GetTopicQueueIdsRequest request) {
//			if (flag) {
//				GetTopicQueueIdsResponse response = new GetTopicQueueIdsResponse();
//				Map<String, List<Long>> topicQueues = new HashMap<String, List<Long>>();
//				topicQueues.put(topicName, Arrays.asList(1L));
//				response.setTopicQueues(topicQueues);
//				return response;
//			} else {
//				return null;
//			}
//		}
//
//		@Override
//		public String getBrokerIp() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	}
//
//	private static class MockMqClientBase extends AbstractMockMqClientBase {
//		private MqContext mqContext = new MqContext();
//
//		public MockMqClientBase() {
//			mqContext.setConsumerId(1);
//			mqContext.setConsumerName("test");
//			Map<String, Long> consumerGroupVersionMap = new HashMap<String, Long>();
//			consumerGroupVersionMap.put(consumerGroupName, 0L);
//			ConsumerGroupOneDto consumerGroupOneDto = buildConsumerGroupOne();
//			mqContext.getConsumerGroupMap().put(consumerGroupName, consumerGroupOneDto);
//			mqContext.setConsumerGroupVersion(consumerGroupVersionMap);
//			mqContext.getConfig().setRbTimes(0);
//		}
//
//		@Override
//		public MqContext getContext() {
//			// TODO Auto-generated method stub
//			return mqContext;
//		}
//
//		@Override
//		public boolean publish(String topic, String token, ProducerDataDto message,
//				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public boolean publish(String topic, String token, List<ProducerDataDto> messages,
//				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//	};
//}
