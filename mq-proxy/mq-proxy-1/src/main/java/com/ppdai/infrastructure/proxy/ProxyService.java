package com.ppdai.infrastructure.proxy;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.config.ClientConfigHelper;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;

@Component
public class ProxyService {
	private static Logger log = LoggerFactory.getLogger(ProxyService.class);
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(1), SoaThreadFactory.create("ProxyService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	private volatile String xml = "";
	private volatile boolean isRunning = true;
	@Autowired
	private Environment environment;

	public void start() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (isRunning) {
					doProxy();
					Util.sleep(1000);
				}
			}

			private void doProxy() {
				if (!xml.equals(environment.getProperty("proxy.xml", ""))) {
					try {
						xml = environment.getProperty("proxy.xml", "");
						MqContext mqContext = MqClient.getContext();
						//MqEvent mqEvent=MqClient.getContext().getMqEvent();
						try{
							MqClient.close();
						} catch (Exception e) {							
						}
						Map<String, ConsumerGroupVo> map = ClientConfigHelper.getConfig(xml);						
						MqClient.start(mqContext.getConfig());
						map.values().forEach(t1 -> {
							MqClient.registerConsumerGroup(t1);
						});
					} catch (Exception e) {
						log.error("订阅失败", e);
					}
				}
			}
		});
	}

	@PreDestroy
	private void close() {
		try {
			isRunning = true;
			executor.shutdown();
			executor = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
