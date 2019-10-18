package com.ppdai.infrastructure.mq.client.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;

@RunWith(JUnit4.class)
public class MqClientStartupTest {
	@Test
	public void updateConfigTest() {
		ConfigurableEnvironment environment=mock(ConfigurableEnvironment.class);

		IMqClientBase moClientBase = mock(IMqClientBase.class); 
		when(moClientBase.start()).thenReturn(true);
		MqContext mqContext = new MqContext();
		when(moClientBase.getContext()).thenReturn(mqContext);
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
		MqStartProcessor mqStartProcessor = new MqStartProcessor();
		ReflectionTestUtils.setField(mqStartProcessor, "environment", environment);

		MqClientStartup.mqLogOrig = "-2";
		MqClientStartup.rbTimes = "4";
		MqClientStartup.pbTimes = "0";
		MqClientStartup.metaMode = "-2";
		MqClientStartup.asynCapacity = "-2";
		
		System.setProperty("mq.broker.url", "http://localhost");
		System.setProperty("mq.asyn.capacity", "2000");
		System.setProperty("mq.rb.times", "4");
		System.setProperty("mq.pb.retry.times", "10");
		System.setProperty("mq.http.timeout", "10000");
		System.setProperty("mq.broker.metaMode", "true");

		mqStartProcessor.postProcessBeforeInitialization(new Object(), "test");
		MqClientStartup.updateConfig();

		assertEquals(2000, mqContext.getConfig().getAsynCapacity());
		assertEquals(4, mqContext.getConfig().getRbTimes());
		assertEquals(10, mqContext.getConfig().getPbRetryTimes());
		assertEquals(10000, mqContext.getConfig().getReadTimeOut());
		assertEquals(true, mqContext.getConfig().isMetaMode());

		System.setProperty("mq.asyn.capacity", "1000");
		System.setProperty("mq.rb.times", "-1");
		System.setProperty("mq.pb.retry.times", "0");
		System.setProperty("mq.broker.metaMode", "21313");
		MqClientStartup.updateConfig();
		assertEquals(2000, mqContext.getConfig().getAsynCapacity());
		//最小值为0
		assertEquals(0, mqContext.getConfig().getRbTimes());
		//最小值为2
		assertEquals(2, mqContext.getConfig().getPbRetryTimes());		
		assertEquals(true, mqContext.getConfig().isMetaMode());
		
		System.setProperty("mq.broker.metaMode", "false");
		System.setProperty("mq.pb.retry.times", "abc");
		System.setProperty("mq.rb.times", "fafa");
		System.setProperty("mq.asyn.capacity", "asfafs");
		MqClientStartup.updateConfig();
		assertEquals(2, mqContext.getConfig().getPbRetryTimes());		
		assertEquals(0, mqContext.getConfig().getRbTimes());
		assertEquals(false, mqContext.getConfig().isMetaMode());
		assertEquals(2000, mqContext.getConfig().getAsynCapacity());
		
		MqClientStartup.close();
	}
}
