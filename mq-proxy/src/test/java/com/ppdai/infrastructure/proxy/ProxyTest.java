package com.ppdai.infrastructure.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.proxy.service.HsCheckService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MqProxyApplication.class })
public class ProxyTest {
	String proxyTopic = "proxyTestTopic";
	String proxyTopic1 = "proxyTestTopic1";
	String proxySub = "proxyTestSub";
	Map<String, Object> property = new ConcurrentHashMap<>();
	@Autowired
	private ConfigurableEnvironment env1;
	private MetaHelper metaHelper = null;

	@PostConstruct
	void init() {
		MapPropertySource mapPropertySource = new MapPropertySource("ProxyTest", property);
		env1.getPropertySources().addFirst(mapPropertySource);
		property.put("mq.proxy.data", "");
		property.put("mq.rb.times", "1");
		metaHelper = new MetaHelper(env1.getProperty("mq.portal.url"));
	}

	private int port = 1082;
	private ClientAndServer mockRegistryServer;

	private void initServer(boolean flag) {
		if (mockRegistryServer != null) {
			mockRegistryServer.stop();
		}
		mockRegistryServer = startClientAndServer(flag ? port : port + 1);
		new MockServerClient("localhost", flag ? port : port + 1).when(request().withMethod("GET").withPath("/test/hs"))
				.respond(response().withStatusCode(200).withBody("{}"));

		new MockServerClient("localhost", flag ? port : port + 1)
				.when(request().withMethod("POST").withPath("/test/proxy"))
				.respond(response().withStatusCode(200).withBody("{}"));

	}

	String template = "{\"%s\": {\"ipLst\": \"%s\",\"exeUrl\": \"%s\", \"hsUrl\": \"%s\"}}";

	private String getConfig(boolean flag) {
		String ipport = "http://" + IPUtil.getLocalIP() + ":" + port;
		if (flag) {
			return String.format(template, proxySub, IPUtil.getLocalIP(), ipport + "/test/proxy", ipport + "/test/hs");
		} else {
			return String.format(template, proxySub, IPUtil.getLocalIP(), "", "");
		}
	}

	@Test
	public void testProxy() throws MqNotInitException {
		try {
			reset();
			metaHelper.buildTopic(proxyTopic);
			metaHelper.buildConsumerGroup(proxySub);
			Util.sleep(5000);
			metaHelper.subscribe(proxySub, proxyTopic);
			Util.sleep(5000);
			List<String> topics = MqClient.getTopic(proxySub);
			assertEquals("订阅失败", topics.size(), 1);
			initServer(true);
			property.put("mq.proxy.data", getConfig(true));
			Util.sleep(1000);
			MqClient.publish(proxyTopic, "", new ProducerDataDto("1"));
			Util.sleep(1000);
			long count = getCount(0);
			assertEquals("消息代理消费异常", count, 0);

			initServer(false);
			MqClient.publish(proxyTopic, "", new ProducerDataDto("1"));
			Util.sleep(1000);
			count = getCount(0);
			assertEquals("消息代理消费异常", count, 0);

			MqClient.publish(proxyTopic, "", new ProducerDataDto("1"));
			Util.sleep(1000);
			count = getCount(1);
			assertEquals("消息代理消费异常", count, 1);

			metaHelper.buildTopic(proxyTopic1);
			Util.sleep(5000);
			metaHelper.subscribe(proxySub, proxyTopic1);
			Util.sleep(1000);
			MqClient.publish(proxyTopic1, "", new ProducerDataDto("1"));
			Util.sleep(5000);
			count = getCount(2);
			assertEquals("消息代理消费异常", count, 2);

			metaHelper.unSubscribe(proxySub, proxyTopic1);
			Util.sleep(5000);
			count = getCount(1);
			assertEquals("消息代理消费异常", count, 1);

			// property.put("mq.proxy.data", getConfig());
			initServer(true);
			count = getCount(0);
			assertEquals("消息代理消费异常", count, 0);
			
			//测试hs url 和exe url 为空的情况
			property.put("mq.proxy.data", getConfig(false));
			Util.sleep(5000);
			MqClient.publish(proxyTopic, "", new ProducerDataDto("1"));			
			count = getCount(1);
			assertEquals("消息代理消费异常", count, 1);
			
			property.put("mq.proxy.data", getConfig(true));
			Util.sleep(5000);
			count = getCount(0);
			assertEquals("消息代理消费异常", count, 0);

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			reset();
			// MqClient.close();
		}

	}

	private void reset() {
		try {
			metaHelper.unSubscribe(proxySub, proxyTopic);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			metaHelper.unSubscribe(proxySub, proxyTopic1);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			metaHelper.deleteTopic(proxyTopic);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			metaHelper.deleteTopic(proxyTopic1);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			metaHelper.deleteConsumerGroup(proxySub);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			mockRegistryServer.stop();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private long getCount(long expect) {
		long count = 0;
		for (int i = 0; i < 70; i++) {
			try {
				count = MqClient.fetchMessageCount(proxySub, Arrays.asList(proxyTopic, proxyTopic1));
			} catch (Exception e) {

			}
			if (count == expect) {
				break;
			} else {
				Util.sleep(2000);
			}
		}
		return count;
	}

	@Autowired
	private HsCheckService hsCheckService;

	
}
