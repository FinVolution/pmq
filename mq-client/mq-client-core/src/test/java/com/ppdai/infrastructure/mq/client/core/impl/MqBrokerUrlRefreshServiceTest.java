package com.ppdai.infrastructure.mq.client.core.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupResponse;
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.client.AbstractMockMqClientBase;
import com.ppdai.infrastructure.mq.client.AbstractMockMqFactory;
import com.ppdai.infrastructure.mq.client.AbstractMockResource;
import com.ppdai.infrastructure.mq.client.AbstractTest;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqMeticReporterService;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

@RunWith(JUnit4.class)
public class MqBrokerUrlRefreshServiceTest extends AbstractTest {
	@Test
	public void testMetricStart() {
		// init();
		MqBrokerUrlRefreshResource mqresource = new MqBrokerUrlRefreshResource();
		MockMqFactory mockMqFactory = new MockMqFactory();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mockMqFactory, mqresource);
		MqBrokerUrlRefreshService service = new MqBrokerUrlRefreshService(mockMqClientBase, mqresource);

		GetMetaGroupResponse getMetaGroupResponse = new GetMetaGroupResponse();
		getMetaGroupResponse.setSuc(true);
		getMetaGroupResponse.setMetricUrl("fsdff");
		mqresource.setGetMetaGroupResponse(getMetaGroupResponse);
		int count = mockMqFactory.getMockMqMeticReporterService().getStartCount();
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("metric start error", 1, mockMqFactory.getMockMqMeticReporterService().getStartCount() - count);
		getMetaGroupResponse.setMetricUrl("");
		count = mockMqFactory.getMockMqMeticReporterService().getCloseCount();
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("metric close error", 1, mockMqFactory.getMockMqMeticReporterService().getCloseCount() - count);
	}

	@Test
	public void testBrokerModeTrue() {
		// init();
		MqBrokerUrlRefreshResource mqresource = new MqBrokerUrlRefreshResource();
		MockMqFactory mockMqFactory = new MockMqFactory();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mockMqFactory, mqresource);
		MqBrokerUrlRefreshService service = new MqBrokerUrlRefreshService(mockMqClientBase, mqresource);
		// invoke(service);
		service.doUpdateBrokerUrls();

		GetMetaGroupResponse getMetaGroupResponse = new GetMetaGroupResponse();
		getMetaGroupResponse.setSuc(true);
		getMetaGroupResponse.setBrokerMetaMode(1);
		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(Arrays.asList("2", "1"));
		mqresource.setGetMetaGroupResponse(getMetaGroupResponse);
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 2, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 2, mockMqClientBase.getContext().getLstGroup2().size());

		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(null);
		mqresource.setGetMetaGroupResponse(getMetaGroupResponse);
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 2, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 2, mockMqClientBase.getContext().getLstGroup2().size());

		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(new ArrayList<String>());
		mqresource.setGetMetaGroupResponse(getMetaGroupResponse);
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 2, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 2, mockMqClientBase.getContext().getLstGroup2().size());

	}

	@Test
	public void testBrokerModeFalse() {
		// init();
		MqBrokerUrlRefreshResource mqresource = new MqBrokerUrlRefreshResource();
		MockMqFactory mockMqFactory = new MockMqFactory();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mockMqFactory, mqresource);
		MqBrokerUrlRefreshService service = new MqBrokerUrlRefreshService(mockMqClientBase, mqresource);
		// invoke(service);
		service.doUpdateBrokerUrls();

		GetMetaGroupResponse getMetaGroupResponse = new GetMetaGroupResponse();
		getMetaGroupResponse.setSuc(true);
		getMetaGroupResponse.setBrokerMetaMode(-1);
		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(Arrays.asList("2", "1"));
		mqresource.setGetMetaGroupResponse(getMetaGroupResponse);
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 0, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 0, mockMqClientBase.getContext().getLstGroup2().size());
	}

	@Test
	public void testBrokerMode0LocalTrue() {
		// init();
		MqBrokerUrlRefreshResource mqresource = new MqBrokerUrlRefreshResource();
		MockMqFactory mockMqFactory = new MockMqFactory();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mockMqFactory, mqresource);
		MqBrokerUrlRefreshService service = new MqBrokerUrlRefreshService(mockMqClientBase, mqresource);
		// invoke(service);
		service.doUpdateBrokerUrls();

		GetMetaGroupResponse getMetaGroupResponse = new GetMetaGroupResponse();
		getMetaGroupResponse.setSuc(true);
		getMetaGroupResponse.setBrokerMetaMode(0);
		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(Arrays.asList("2", "1"));
		mqresource.setGetMetaGroupResponse(getMetaGroupResponse);
		mockMqClientBase.getContext().getConfig().setMetaMode(true);
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 2, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 2, mockMqClientBase.getContext().getLstGroup2().size());

		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(null);
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 2, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 2, mockMqClientBase.getContext().getLstGroup2().size());

		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(new ArrayList<String>());
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 2, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 2, mockMqClientBase.getContext().getLstGroup2().size());
	}

	@Test
	public void testBrokerMode0LocalFalse() {
		// init();
		MqBrokerUrlRefreshResource mqresource = new MqBrokerUrlRefreshResource();
		MockMqFactory mockMqFactory = new MockMqFactory();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mockMqFactory, mqresource);
		MqBrokerUrlRefreshService service = new MqBrokerUrlRefreshService(mockMqClientBase, mqresource);
		// invoke(service);
		service.doUpdateBrokerUrls();

		GetMetaGroupResponse getMetaGroupResponse = new GetMetaGroupResponse();
		getMetaGroupResponse.setSuc(true);
		getMetaGroupResponse.setBrokerMetaMode(0);
		getMetaGroupResponse.setBrokerIpG1(Arrays.asList("1", "2"));
		getMetaGroupResponse.setBrokerIpG2(Arrays.asList("2", "1"));
		mqresource.setGetMetaGroupResponse(getMetaGroupResponse);
		mockMqClientBase.getContext().getConfig().setMetaMode(false);
		// invoke(service);
		service.doUpdateBrokerUrls();
		assertEquals("g1 size error", 0, mockMqClientBase.getContext().getLstGroup1().size());
		assertEquals("g2 size error", 0, mockMqClientBase.getContext().getLstGroup2().size());
	}

	@Test
	public void testStart() {
		MqBrokerUrlRefreshResource mqresource = new MqBrokerUrlRefreshResource();
		MockMqFactory mockMqFactory = new MockMqFactory();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mockMqFactory, mqresource);
		MqBrokerUrlRefreshService service = new MqBrokerUrlRefreshService(mockMqClientBase, mqresource);
		service.start();
	}

	@Test
	public void testClose() {
		MqBrokerUrlRefreshResource mqresource = new MqBrokerUrlRefreshResource();
		MockMqFactory mockMqFactory = new MockMqFactory();
		MockMqClientBase mockMqClientBase = new MockMqClientBase(mockMqFactory, mqresource);
		MqBrokerUrlRefreshService service = new MqBrokerUrlRefreshService(mockMqClientBase, mqresource);
		service.start();
		service.close();
	}

	private static class MqBrokerUrlRefreshResource extends AbstractMockResource {

		GetMetaGroupResponse getMetaGroupResponse = null;

		public void setGetMetaGroupResponse(GetMetaGroupResponse getMetaGroupResponse) {
			this.getMetaGroupResponse = getMetaGroupResponse;
		}

		@Override
		public GetMetaGroupResponse getMetaGroup(GetMetaGroupRequest request) {
			return getMetaGroupResponse;
		}

		@Override
		public String getBrokerIp() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private static class MockMqMeticReporterService implements IMqMeticReporterService {
		private int startCount = 0;

		private int closeCount = 0;

		public int getStartCount() {
			return startCount;
		}

		public int getCloseCount() {
			return closeCount;
		}

		@Override
		public void start() {
			startCount++;

		}

		@Override
		public void close() {
			closeCount++;

		}

	}

	private static class MockMqFactory extends AbstractMockMqFactory {
		MockMqMeticReporterService mockMqMeticReporterService = new MockMqMeticReporterService();

		public MockMqMeticReporterService getMockMqMeticReporterService() {
			return mockMqMeticReporterService;
		}

		public IMqMeticReporterService createMqMeticReporterService(IMqClientBase mqClientBase) {
			return mockMqMeticReporterService;
		}

	}

	private static class MockMqClientBase extends AbstractMockMqClientBase {
		private IMqResource iMqResource;
		private MqContext mqContext = new MqContext();
		private IMqFactory iFactory = new MockMqFactory();

		public MockMqClientBase(IMqFactory mqFactory, IMqResource mqResource) {
			iMqResource = mqResource;
			this.iFactory = mqFactory;
			mqContext.setConsumerId(1);
			mqContext.setMqPollingResource(iMqResource);
			Map<String, Long> consumerGroupVersionMap = new HashMap<String, Long>();
			consumerGroupVersionMap.put("", 1L);
			mqContext.setConsumerGroupVersion(consumerGroupVersionMap);
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
