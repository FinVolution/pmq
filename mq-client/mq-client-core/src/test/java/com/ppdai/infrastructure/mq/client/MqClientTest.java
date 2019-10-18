package com.ppdai.infrastructure.mq.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GroupTopicDto;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.event.PostHandleListener;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupTopicVo;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
import com.ppdai.infrastructure.mq.client.event.RegisterConsumerGroupListener;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.factory.MqFactory;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

@RunWith(JUnit4.class)
public class MqClientTest {

	private void init() {

		MqClient.registerInitEvent(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		});
		MqClient.registerPreHandleEvent(new PreHandleListener() {
			@Override
			public boolean preHandle(ConsumerQueueDto consumerQueue) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		MqClient.registerPostHandleEvent(new PostHandleListener() {

			@Override
			public void postHandle(ConsumerQueueDto consumerQueue, Boolean isSuc) {
				// TODO Auto-generated method stub

			}
		});
		MqClient.registerConsumerGroupEvent(new RegisterConsumerGroupListener() {

			@Override
			public void complete(ConsumerGroupVo consumerGroupVo) {
				// TODO Auto-generated method stub

			}
		});

		MqClient.registerCompletedEvent(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

			}
		});
	}

	@Test
	public void startTest() {
		IMqResource resource = mock(IMqResource.class);
		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response);
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				// TODO Auto-generated method stub
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		init();
		boolean rs = MqClient.start("http://localhost");
		assertEquals("start error", true, rs);
		MqClient.close();
	}

	@Test
	public void start1Test() {
		IMqResource resource = mock(IMqResource.class);
		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response);
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				// TODO Auto-generated method stub
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqClient.init(mqConfig);
		assertEquals("start error", true, MqClient.hasInit());
		init();
		boolean rs = MqClient.start();
		assertEquals("start error", true, rs);
		MqClient.close();
	}

	@Test
	public void start2Test() {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				// TODO Auto-generated method stub
				return resource;
			}
		};
		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response);
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");

		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		init();
		boolean rs = MqClient.start(mqConfig);
		assertEquals("start error", true, rs);
		MqClient.close();
	}

	@Test
	public void getGroupTopicTest() {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				// TODO Auto-generated method stub
				return resource;
			}
		};
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");		
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqClient.init(mqConfig);		
		GetGroupTopicResponse response=new GetGroupTopicResponse();
		response.setGroupTopics(Arrays.asList(new GroupTopicDto()));
	
		when(resource.getGroupTopic(any(GetGroupTopicRequest.class))).thenReturn(response);
		assertEquals(response.getGroupTopics(),MqClient.getGroupTopic(Arrays.asList("1")));
		
		MqClient.close();
	}
	@Test
	public void reStartTest() {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				// TODO Auto-generated method stub
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqClient.reStart();		
		MqClient.close();
	}

	@Test
	public void isAsynAvailableTest() throws MqNotInitException, ContentExceed65535Exception {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");
		boolean rs = false;
		try {
			MqClient.isAsynAvailable();
		} catch (Exception e) {
			rs = true;
		}
		assertEquals("isAsynAvailableTest error", true, rs);
		MqClient.init(mqConfig);
		assertEquals("isAsynAvailableTest error", true, MqClient.isAsynAvailable());
		for (int i = 0; i < 4001; i++) {
			MqClient.publishAsyn("test" + i, "", new ProducerDataDto());
		}
		assertEquals("isAsynAvailableTest error", false, MqClient.isAsynAvailable());
		MqClient.close();
	}

	@Test
	public void registerConsumerGroupTest() {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");

		ConsumerGroupVo consumerGroup = new ConsumerGroupVo();
		consumerGroup.setGroupName("test");
		Map<String, ConsumerGroupTopicVo> topics = new HashMap<>();
		topics.put("test", new ConsumerGroupTopicVo("test", null));
		consumerGroup.setTopics(topics);

		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response);

		MqClient.start(mqConfig);
		assertEquals("registerConsumerGroupTest error", true, MqClient.registerConsumerGroup(consumerGroup));

		response.setSuc(false);
		// when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response);
		assertEquals("registerConsumerGroupTest error", false, MqClient.registerConsumerGroup(consumerGroup));
		MqClient.close();
	}

	@Test
	public void publishTest() throws MqNotInitException, ContentExceed65535Exception {
		IMqResource resource = mock(IMqResource.class);
		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");
		boolean rs = true;
		try {
			MqClient.publish("Test", "", new ProducerDataDto());
		} catch (Exception e) {
			rs = false;
		}
		assertEquals("publishTest error", false, rs);
		MqClient.start(mqConfig);
		rs = true;
		try {
			StringBuilder body = new StringBuilder();
			for (int i = 0; i < 100000; i++) {
				body.append(i);
			}
			MqClient.publish("Test", "", new ProducerDataDto(body.toString()));
		} catch (Exception e) {
			rs = false;
		}
		assertEquals("publishTest error", false, rs);
		
		MqClient.publish("Test", "", new ProducerDataDto("dfadsf"));
		
		
		MqClient.close();
	}
	
	@Test
	public void publishAynTest() throws MqNotInitException, ContentExceed65535Exception {
		IMqResource resource = mock(IMqResource.class);
		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		when(resource.publish(any(PublishMessageRequest.class), anyInt())).thenReturn(true);
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");	
		MqClient.start(mqConfig);
		MqClient.publishAsyn("Test", "", new ProducerDataDto("fa"));
		Util.sleep(1000);
		verify(resource).publish(any(PublishMessageRequest.class), anyInt());
		
		MqClient.publishAsyn("Test", "", new ProducerDataDto("fa"),null);
		Util.sleep(1000);
		verify(resource,times(2)).publish(any(PublishMessageRequest.class), anyInt());
		MqClient.close();
	}

	@Test
	public void getContextTest() {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		assertEquals("getContextTest error", true, MqClient.getContext() != null);
	}

	@Test
	public void getMessageCountTest() {
		IMqResource resource = mock(IMqResource.class);
		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		ConsumerGroupRegisterResponse response1 = new ConsumerGroupRegisterResponse();
		response1.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response1);		
		GetMessageCountResponse response = new GetMessageCountResponse();
		response.setCount(1L);
		when(resource.getMessageCount(any(GetMessageCountRequest.class))).thenReturn(response);
		
		
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");
		MqClient.start(mqConfig);
				
		assertEquals("getMessageCountTest error", 1, MqClient.fetchMessageCount("test", Arrays.asList("testt")));
		
		boolean rs=true;
		try {
			MqClient.fetchMessageCount("", Arrays.asList("testt"));
		}catch (Exception e) {
			rs=false;
		}
		assertEquals("getMessageCountTest error", false, rs);
		
		MqClient.close();
	}

	@Test
	public void closeTest() {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqClient.close();
	}

	@Test
	public void stopTest() {
		IMqResource resource = mock(IMqResource.class);
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));
		MqClient.stop();
	}

	@Test
	public void getTopicTest() {
		IMqResource resource = mock(IMqResource.class);		
		when(resource.register(any(ConsumerRegisterRequest.class))).thenReturn(1L);
		ConsumerGroupRegisterResponse response1 = new ConsumerGroupRegisterResponse();
		response1.setSuc(true);
		when(resource.registerConsumerGroup(any(ConsumerGroupRegisterRequest.class))).thenReturn(response1);
		
		GetTopicResponse response = new GetTopicResponse();
		response.setTopics(Arrays.asList("Test"));
		when(resource.getTopic(any(GetTopicRequest.class))).thenReturn(response);
		
		
		IMqFactory mqFactory = new MqFactory() {
			@Override
			public IMqResource createMqResource(String url, long connectionTimeOut, long readTimeOut) {
				return resource;
			}
		};
		
		MqClient.setInstance(new MqClient.MqClientBase(mqFactory));

		MqConfig mqConfig = new MqConfig();
		mqConfig.setUrl("http://localhost");
		MqClient.start(mqConfig);
		
		assertEquals("getTopicTest error", 1, MqClient.getTopic("test").size());
		MqClient.close();
	}
}
