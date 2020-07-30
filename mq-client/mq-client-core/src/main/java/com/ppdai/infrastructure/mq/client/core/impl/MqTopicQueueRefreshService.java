package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsResponse;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqTopicQueueRefreshService;

//用来定时刷新topic 对应的 queue
public class MqTopicQueueRefreshService implements IMqTopicQueueRefreshService {
	private Logger log = LoggerFactory.getLogger(MqBrokerUrlRefreshService.class);
	private ScheduledExecutorService executor = null;
	private GetTopicQueueIdsRequest request = new GetTopicQueueIdsRequest();
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private MqContext mqContext;
	private volatile boolean isStop = false;
	private volatile boolean runStatus = false;
	private AtomicReference<Map<String, List<Long>>> topicQueueRef = new AtomicReference<>(new ConcurrentHashMap<>());
	private long lastTime = System.currentTimeMillis();
	private volatile static MqTopicQueueRefreshService instance = null;

	/**
	 * 获取单例
	 */
	public static MqTopicQueueRefreshService getInstance() {
		if (instance == null) {
			synchronized (MqTopicQueueRefreshService.class) {
				if (instance == null) {
					instance = new MqTopicQueueRefreshService();
				}
			}
		}
		return instance;
	}

	protected MqTopicQueueRefreshService() {
		this.mqContext = MqClient.getContext();
	}

	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			isStop = false;
			runStatus = false;
			executor = Executors.newScheduledThreadPool(1,
					SoaThreadFactory.create("mq-MqTopicQueueRefreshService-pool-%d", Thread.MAX_PRIORITY - 1, true));
			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (!isStop) {
						runStatus = true;
						try {
							if (System.currentTimeMillis() - lastTime > 60000) {
								topicQueueRef.set(new ConcurrentHashMap<>());
							}
							doUpdateQueue();
						} catch (Exception e) {
							log.error("Update_MqTopicQueue_error", e);
						}
						runStatus = false;
					}
				}

			}, 1, 20, TimeUnit.SECONDS);
		}
	}

	protected void doUpdateQueue() {
		if (topicQueueRef.get().size() > 0) {
			request = new GetTopicQueueIdsRequest();
			request.setTopicNames(new ArrayList<>(topicQueueRef.get().keySet()));
			GetTopicQueueIdsResponse response = mqContext.getMqResource().getTopicQueueIds(request);
			if (response != null && response.getTopicQueues() != null) {
				topicQueueRef.get().putAll(response.getTopicQueues());
			}
		}
	}

	public List<Long> getTopicQueueIds(String topicName) {
		if (Util.isEmpty(topicName))
			return new ArrayList<>();
		Map<String, List<Long>> data = topicQueueRef.get();
		if (!data.containsKey(topicName)) {
			GetTopicQueueIdsRequest rqueueIdsRequest = new GetTopicQueueIdsRequest();
			rqueueIdsRequest.setTopicNames(Arrays.asList(topicName));
			GetTopicQueueIdsResponse response = mqContext.getMqResource().getTopicQueueIds(rqueueIdsRequest);
			if (response != null && response.getTopicQueues() != null) {
				data.putAll(response.getTopicQueues());
			}
		}
		return data.get(topicName);
	}

	@Override
	public void close() {
		isStop = true;
		long start = System.currentTimeMillis();
		// 这是为了等待有未完成的任务
		while (runStatus) {
			Util.sleep(10);
			// System.out.println("closing...................."+isRunning);
			if (System.currentTimeMillis() - start > 5000) {
				break;
			}
		}
		try {
			executor.shutdown();
		} catch (Exception e) {
		}
		startFlag.set(false);
		executor = null;
		instance = null;
	}

}
