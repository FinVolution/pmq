package com.ppdai.infrastructure.mq.client.bootstrap;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;

@RunWith(JUnit4.class)
public class MqClientShutdownListenerTest {

	@Test
	public void closeTest() {
		//ConfigurableEnvironment environment=mock(ConfigurableEnvironment.class);
		IMqClientBase moClientBase = mock(IMqClientBase.class);
		when(moClientBase.start()).thenReturn(true);
		when(moClientBase.getContext()).thenReturn(new MqContext());
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
		MqClientShutdownListener mqClientShutdownListener=new MqClientShutdownListener();
		mqClientShutdownListener.onApplicationEvent(null);
	}
	@Test
	public void closeErrorTest() {
		//ConfigurableEnvironment environment=mock(ConfigurableEnvironment.class);
		IMqClientBase moClientBase = mock(IMqClientBase.class);	
		doThrow(new RuntimeException()).when(moClientBase).close();
		//when(moClientBase.close()).thenThrow(new RuntimeException());
		when(moClientBase.start()).thenReturn(true);		
		when(moClientBase.getContext()).thenReturn(new MqContext());
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
		MqClientShutdownListener mqClientShutdownListener=new MqClientShutdownListener();
		mqClientShutdownListener.onApplicationEvent(null);
	}
}
