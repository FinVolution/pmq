package com.ppdai.infrastructure.proxy.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;
import com.ppdai.infrastructure.proxy.ProxyService;

@Component
public class HsCheckService implements PreHandleListener {
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(1), SoaThreadFactory.create("HsCheckService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	// 暂时只能代理一个消费者实例
	private static AtomicReference<Map<String, Boolean>> statusRef = new AtomicReference<>(new HashMap<>());
	private static AtomicReference<Map<String, String>> hsRef = new AtomicReference<>(new HashMap<>());
	private static HttpClient htCheckClient = new HttpClient(1500, 1500);
	private static volatile boolean isRunning = true;
	private static AtomicBoolean firstFlag = new AtomicBoolean(false);
	@Autowired
	private Environment environment;
	@Override
	public boolean preHandle(ConsumerQueueDto consumerQueue) {
		String hsUrl = ProxyService.getHsUrl(consumerQueue.getConsumerGroupName());
		Map<String, Boolean> status = statusRef.get();
		if (!status.containsKey(consumerQueue.getConsumerGroupName())) {
			status.put(consumerQueue.getConsumerGroupName(), htCheckClient.check(hsUrl));
		}
		return status.get(consumerQueue.getConsumerGroupName());
	}

	@PostConstruct
	private void init() {
		if (firstFlag.compareAndSet(false, true)) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					while (isRunning) {
						Map<String, String> urlMap = hsRef.get();
						Map<String, Boolean> status = statusRef.get();
						for (String t : urlMap.keySet()) {
							if (!status.get(t)) {
								status.put(t, htCheckClient.check(urlMap.get(t)));
								if (!status.get(t)) {
									status.put(t, htCheckClient.check(urlMap.get(t)));
								}
							}
						}
						Util.sleep(getUrlCheckTime());
					}
				}
			});
		}
	}

	public static void updateHsUrl(Map<String, String> urlMap) {
		hsRef.set(urlMap);
	}

	public static void updateStatus(Map<String, Boolean> status) {
		statusRef.set(status);
	}

	public static void updateHs(String consumerGroupName, boolean value) {
		Map<String, Boolean> status = statusRef.get();
		status.put(consumerGroupName, value);
	}

	@PreDestroy
	private void close() {
		isRunning = false;
	}
	
	private volatile String _getUrlCheckTime = "";
	private volatile int getUrlCheckTime = 0;
	private final String env_getUrlCheckTime_key = "mq.check.time";
	private final String env_getUrlCheckTime_defaultValue = "5000";
	private final String env_getUrlCheckTime_des = "代理检查间隔等待时间";

	// 数据库失败等待时间
	private int getUrlCheckTime() {
		try {
			if (!_getUrlCheckTime
					.equals(environment.getProperty(env_getUrlCheckTime_key, env_getUrlCheckTime_defaultValue))) {
				_getUrlCheckTime = environment.getProperty(env_getUrlCheckTime_key, env_getUrlCheckTime_defaultValue);
				getUrlCheckTime = Integer
						.parseInt(environment.getProperty(env_getUrlCheckTime_key, env_getUrlCheckTime_defaultValue));
				if (getUrlCheckTime < 3000) {
					getUrlCheckTime = 3000;
				}
			
			}
		} catch (Exception e) {
			getUrlCheckTime = 5000;			
		}
		return getUrlCheckTime;
	}

}
