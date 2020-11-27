package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codahale.metrics.MetricFilter;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.core.IMqMeticReporterService;
import com.ppdai.infrastructure.mq.client.metric.MetricSingleton;

public class MqMeticReporterService implements IMqMeticReporterService {
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private MqMetricReporter reporter;
	private volatile static MqMeticReporterService instance = null;

	/**
	 * 获取单例
	 */
	public static MqMeticReporterService getInstance() {
		if (instance == null) {
			synchronized (MqTopicQueueRefreshService.class) {
				if (instance == null) {
					instance = new MqMeticReporterService();
				}
			}
		}
		return instance;
	}

	private MqMeticReporterService() {
		reporter = new MqMetricReporter(MetricSingleton.getMetricRegistry(), "mq-client", MetricFilter.ALL,
				TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS, null, MqClient.getContext());
	}

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			// 每30s上报数据
			reporter.start(30, TimeUnit.SECONDS);
		}
	}

	@Override
	public void close() {
		startFlag.set(false);
		instance = null;
		if (reporter != null) {
			try {
				reporter.stop();
				reporter = null;
			} catch (Throwable e) {
			}
		}

	}
}
