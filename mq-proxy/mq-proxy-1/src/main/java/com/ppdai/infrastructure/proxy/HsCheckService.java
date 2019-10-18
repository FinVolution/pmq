package com.ppdai.infrastructure.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;

@Component
public class HsCheckService implements PreHandleListener {
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(1), SoaThreadFactory.create("HsCheckService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	// 暂时只能代理一个消费者实例
	private static Map<Integer, Boolean> status = new ConcurrentHashMap<>();
	private static HttpClient htCheckClient = new HttpClient(1500, 1500);
	private static String hsUrl = "";
	private static volatile boolean isRunning = true;
	private static AtomicBoolean firstFlag = new AtomicBoolean(false);

	@Override
	public boolean preHandle(ConsumerQueueDto consumerQueue) {		
		while (SpringUtil.getApplicationContext() == null) {
			Util.sleep(10);
		}
		if( SpringUtil.getApplicationContext().getEnvironment().getProperty("mq.client.pre","0").equals("1")){
			return true;
		}
		hsUrl = SpringUtil.getApplicationContext().getEnvironment().getProperty("mq.client.proxy.hs.url");
		if (!status.containsKey(0)) {
			status.put(0, htCheckClient.check(hsUrl));
		}		
		return status.get(0);
	}

	@PostConstruct
	private void init() {
		if (firstFlag.compareAndSet(false, true)) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					while (isRunning) {
						status.put(0, htCheckClient.check(hsUrl));						
						Util.sleep(20000);
					}
				}
			});
		}

	}

	@PreDestroy
	private void close() {
		isRunning = false;
	}

}
