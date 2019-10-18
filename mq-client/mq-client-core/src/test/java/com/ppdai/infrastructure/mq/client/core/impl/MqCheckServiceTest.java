package com.ppdai.infrastructure.mq.client.core.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupResponse;
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
import com.ppdai.infrastructure.mq.client.AbstractMockResource;
import com.ppdai.infrastructure.mq.client.AbstractTest;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;

@RunWith(JUnit4.class)
public class MqCheckServiceTest extends AbstractTest {
	@Test
	public void testResponseNull() {
		// init();
		MqCheckResource mqresource = new MqCheckResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqCheckService mqCheckService = new MqCheckService(mockMqClientBase, mqresource);
		Map<String, Long> map = new HashMap<String, Long>();
		// Object rs = invoke(mqCheckService, map);
		String rs = mqCheckService.doCheck(map);
		assertEquals("response is null ", "the data is empty!", rs);
	}

	@Test
	public void testQueuesCount() {
		// init();
		MqCheckResource mqresource = new MqCheckResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqCheckService mqCheckService = new MqCheckService(mockMqClientBase, mqresource);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put(consumerGroupName, 0L);
		Map<String, ConsumerGroupOneDto> consumerGroups = JsonUtil.copy(
				mockMqClientBase.getContext().getConsumerGroupMap(),
				new TypeReference<Map<String, ConsumerGroupOneDto>>() {
				});
		consumerGroups.get(consumerGroupName).getQueues().put(2L, new ConsumerQueueDto());
		GetConsumerGroupResponse getConsumerGroupResponse = new GetConsumerGroupResponse();
		getConsumerGroupResponse.setSuc(true);
		getConsumerGroupResponse.setConsumerGroups(consumerGroups);
		mqresource.setGetConsumerGroupResponse(getConsumerGroupResponse);
		// Object rs = invoke(mqCheckService, map);
		String rs = mqCheckService.doCheck(map);
		assertEquals("queue count is error ", 1, search(rs.toString(), "!"));
	}

	@Test
	public void testMetaNull() {
		// init();
		MqCheckResource mqresource = new MqCheckResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqCheckService mqCheckService = new MqCheckService(mockMqClientBase, mqresource);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put(consumerGroupName, 0L);
		Map<String, ConsumerGroupOneDto> consumerGroups = new HashMap<String, ConsumerGroupOneDto>();
		ConsumerGroupOneDto consumerGroupOneDto = buildModifyConsumerGroupOne();
		consumerGroupOneDto.setMeta(null);
		consumerGroups.put(consumerGroupName, consumerGroupOneDto);
		GetConsumerGroupResponse getConsumerGroupResponse = new GetConsumerGroupResponse();
		getConsumerGroupResponse.setSuc(true);
		getConsumerGroupResponse.setConsumerGroups(consumerGroups);
		mqresource.setGetConsumerGroupResponse(getConsumerGroupResponse);
		// Object rs = invoke(mqCheckService, map);
		String rs = mqCheckService.doCheck(map);
		assertEquals("testMetaNull is error ", 1, search(rs.toString(), "!"));
	}

	@Test
	public void testVersion() {
		// init();
		MqCheckResource mqresource = new MqCheckResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqCheckService mqCheckService = new MqCheckService(mockMqClientBase, mqresource);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put(consumerGroupName, 0L);
		Map<String, ConsumerGroupOneDto> consumerGroups = new HashMap<String, ConsumerGroupOneDto>();
		ConsumerGroupOneDto consumerGroupOneDto = buildModifyConsumerGroupOne();
		consumerGroups.put(consumerGroupName, consumerGroupOneDto);
		GetConsumerGroupResponse getConsumerGroupResponse = new GetConsumerGroupResponse();
		getConsumerGroupResponse.setSuc(true);
		getConsumerGroupResponse.setConsumerGroups(consumerGroups);
		mqresource.setGetConsumerGroupResponse(getConsumerGroupResponse);
		// Object rs = invoke(mqCheckService, map);
		String rs = mqCheckService.doCheck(map);
		assertEquals("testVersion is error ", 2, search(rs.toString(), "!"));
	}

	@Test
	public void testQueueIdNotExist() {
		// init();
		MqCheckResource mqresource = new MqCheckResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqCheckService mqCheckService = new MqCheckService(mockMqClientBase, mqresource);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put(consumerGroupName, 0L);
		Map<String, ConsumerGroupOneDto> consumerGroups = new HashMap<String, ConsumerGroupOneDto>();
		ConsumerGroupOneDto consumerGroupOneDto = buildModifyConsumerGroupOne();
		consumerGroups.put(consumerGroupName, consumerGroupOneDto);
		consumerGroupOneDto.setMeta(buildConsumerGroupOne().getMeta());
		ConsumerQueueDto consumerQueueDto = consumerGroupOneDto.getQueues().get(1L);
		consumerGroupOneDto.getQueues().clear();
		consumerGroupOneDto.getQueues().put(10L, consumerQueueDto);
		GetConsumerGroupResponse getConsumerGroupResponse = new GetConsumerGroupResponse();
		getConsumerGroupResponse.setSuc(true);
		getConsumerGroupResponse.setConsumerGroups(consumerGroups);
		mqresource.setGetConsumerGroupResponse(getConsumerGroupResponse);
		// Object rs = invoke(mqCheckService, map);
		String rs = mqCheckService.doCheck(map);
		assertEquals("testVersion is error ", 1, search(rs.toString(), "!"));
	}

	@Test
	public void testCheckService() {
		// init();
		MqCheckResource mqresource = new MqCheckResource();
		MockMqClientBase mockMqClientBase = new MockMqClientBase();
		MqCheckService mqCheckService = new MqCheckService(mockMqClientBase, mqresource);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put(consumerGroupName, 0L);
		Map<String, ConsumerGroupOneDto> consumerGroups = new HashMap<String, ConsumerGroupOneDto>();
		ConsumerGroupOneDto consumerGroupOneDto = buildModifyConsumerGroupOne();
		consumerGroups.put(consumerGroupName, consumerGroupOneDto);
		consumerGroupOneDto.setMeta(buildConsumerGroupOne().getMeta());
		GetConsumerGroupResponse getConsumerGroupResponse = new GetConsumerGroupResponse();
		getConsumerGroupResponse.setSuc(true);
		getConsumerGroupResponse.setConsumerGroups(consumerGroups);
		mqresource.setGetConsumerGroupResponse(getConsumerGroupResponse);
		// Object rs = invoke(mqCheckService, map);
		String rs = mqCheckService.doCheck(map);
		assertEquals("testCheckService is error ", 11, search(rs.toString(), "!"));
	}

	private int search(String str, String strRes) {
		int n = 0;// 计数器
		int index = 0;// 指定字符的长度
		index = str.indexOf(strRes);
		while (index != -1) {
			n++;
			index = str.indexOf(strRes, index + 1);
		}

		return n;
	}

	private static class MqCheckResource extends AbstractMockResource {

		GetConsumerGroupResponse getConsumerGroupResponse = null;

		public void setGetConsumerGroupResponse(GetConsumerGroupResponse getConsumerGroupResponse) {
			this.getConsumerGroupResponse = getConsumerGroupResponse;
		}

		@Override
		public GetConsumerGroupResponse getConsumerGroup(GetConsumerGroupRequest request) {
			return getConsumerGroupResponse;
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
