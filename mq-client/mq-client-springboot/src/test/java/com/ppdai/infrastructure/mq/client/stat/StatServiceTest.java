package com.ppdai.infrastructure.mq.client.stat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jetty.server.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.common.util.IHttpClient;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.stat.StatService.ServerFactory;

@RunWith(JUnit4.class)
public class StatServiceTest {

	@Test
	public void checkStatNormalTest() {
		IMqClientBase moClientBase=mock(IMqClientBase.class);
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);

		MqContext mqContext=new MqContext();
		when(moClientBase.getContext()).thenReturn(mqContext);
		mqContext.getConfig().setServerPort("8080");
		
		IHttpClient httpClient=mock(IHttpClient.class);
		when(httpClient.check(anyString())).thenReturn(true);
		
		StatService statService=new StatService();
		ReflectionTestUtils.setField(statService, "httpClient", httpClient);
		statService.checkStat();	
		verify(httpClient).check(anyString());
	}
	
	@Test
	public void checkStatUnNormalTest() throws Exception {
		IMqClientBase moClientBase=mock(IMqClientBase.class);
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
		MqContext mqContext=new MqContext();
		when(moClientBase.getContext()).thenReturn(mqContext);
		mqContext.getConfig().setServerPort("8080");
		
		IHttpClient httpClient=mock(IHttpClient.class);
		when(httpClient.check(anyString())).thenReturn(false);
		
		ServerFactory serverFactory=mock(ServerFactory.class);
		Server server=mock(Server.class);
		when(serverFactory.createServer(8080)).thenReturn(server);		
		StatService statService=new StatService();
		ReflectionTestUtils.setField(statService, "httpClient", httpClient);
		ReflectionTestUtils.setField(statService, "serverFactory", serverFactory);
		statService.checkStat();	
		verify(server).setHandler(any());		
	}
	
	@Test
	public void checkStatErrorTest() throws Exception {
		IMqClientBase moClientBase=mock(IMqClientBase.class);
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
		MqContext mqContext=new MqContext();
		when(moClientBase.getContext()).thenReturn(mqContext);
		mqContext.getConfig().setServerPort("8080");
		
		IHttpClient httpClient=mock(IHttpClient.class);
		when(httpClient.check(anyString())).thenReturn(false);
		
		ServerFactory serverFactory=mock(ServerFactory.class);
		//Server server=mock(Server.class);
		when(serverFactory.createServer(8080)).thenThrow(new RuntimeException("tet"));			
		StatService statService=new StatService();
		ReflectionTestUtils.setField(statService, "httpClient", httpClient);
		ReflectionTestUtils.setField(statService, "serverFactory", serverFactory);
		statService.checkStat();		
	}
	
	@Test
	public void stopTest() throws Exception {
		IMqClientBase moClientBase=mock(IMqClientBase.class);
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);
		MqContext mqContext=new MqContext();
		when(moClientBase.getContext()).thenReturn(mqContext);
		mqContext.getConfig().setServerPort("8080");
		
		IHttpClient httpClient=mock(IHttpClient.class);
		when(httpClient.check(anyString())).thenReturn(false);
		
		ServerFactory serverFactory=mock(ServerFactory.class);
		Server server=mock(Server.class);
		when(serverFactory.createServer(8080)).thenReturn(server);		
		StatService statService=new StatService();
		ReflectionTestUtils.setField(statService, "httpClient", httpClient);
		ReflectionTestUtils.setField(statService, "serverFactory", serverFactory);
		statService.close();	
		//verify(server).stop();		
	}
	@Test
	public void startTest() {
		IMqClientBase moClientBase=mock(IMqClientBase.class);
		ReflectionTestUtils.setField(MqClient.class, "instance", moClientBase, null);

		MqContext mqContext=new MqContext();
		when(moClientBase.getContext()).thenReturn(mqContext);
		mqContext.getConfig().setServerPort("8080");
		
		IHttpClient httpClient=mock(IHttpClient.class);
		when(httpClient.check(anyString())).thenReturn(true);
		
		StatService statService=new StatService();
		ReflectionTestUtils.setField(statService, "httpClient", httpClient);
		statService.start();		
	}
}
