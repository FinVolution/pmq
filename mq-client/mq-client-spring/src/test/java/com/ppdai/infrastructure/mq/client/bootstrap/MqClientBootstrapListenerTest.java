//package com.ppdai.infrastructure.mq.client.bootstrap;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import com.ppdai.infrastructure.mq.client.MqClient;
//import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
//import com.ppdai.infrastructure.mq.client.MqContext;
//
//@RunWith(JUnit4.class)
//public class MqClientBootstrapListenerTest {	
//
//	@Test
//	public void startErrorTest() {
//		ConfigurableEnvironment environment=mock(ConfigurableEnvironment.class);
//		IMqClientBase moClientBase = mock(IMqClientBase.class);
//		when(moClientBase.start()).thenReturn(true);
//		when(moClientBase.getContext()).thenReturn(new MqContext());
//		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
//		MqStartProcessor mqStartProcessor = new MqStartProcessor();
//		ReflectionTestUtils.setField(mqStartProcessor, "environment", environment);
//		System.setProperty("mq.broker.url", "");
//		boolean rs = true;
//		try {
//			MqStartProcessor.initFlag.set(false);
//			MqClientStartup.initFlag.set(false);
//			mqStartProcessor.postProcessBeforeInitialization(new Object(), "test");
//		} catch (Exception e) {
//			rs = false;
//		}
//		assertEquals(false, rs);		
//	}
//
//	@Test
//	public void startTest() {
//		ConfigurableEnvironment environment=mock(ConfigurableEnvironment.class);
//		IMqClientBase moClientBase = mock(IMqClientBase.class);
//		when(moClientBase.start()).thenReturn(true);
//		when(moClientBase.getContext()).thenReturn(new MqContext());
//		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
//		MqStartProcessor mqStartProcessor = new MqStartProcessor();
//		mqStartProcessor.getOrder();
//		ReflectionTestUtils.setField(mqStartProcessor, "environment", environment);
//		System.setProperty("mq.broker.url", "http://localhost");
//		System.setProperty("mq.asyn.capacity", "2000");
//		System.setProperty("mq.rb.times", "4");
//		System.setProperty("mq.pb.retry.times", "10");
//		System.setProperty("mq.http.timeout", "10000");
//		System.setProperty("mq.broker.metaMode", "true");	
//		//System.setProperty("mq.log.original", "2");
//		
//		mqStartProcessor.postProcessBeforeInitialization(new Object(), "test");
//		mqStartProcessor.postProcessAfterInitialization(new Object(), "test");
//		mqStartProcessor.setEnvironment(environment);
//		MqClientBootstrapListener mqClientBootstrapListener = new MqClientBootstrapListener();
//		mqClientBootstrapListener.getOrder();
//		mqClientBootstrapListener.onApplicationEvent(null);
//	}
//}
