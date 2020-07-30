package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.codahale.metrics.MetricFilter;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.metric.MetricSingleton;

@RunWith(JUnit4.class)
public class MqMetricReporterTest {

	@Test
	public void testReport() {
		@SuppressWarnings("resource")
		MqMetricReporter reporter = new MqMetricReporter(MetricSingleton.getMetricRegistry(), "mq-client",
				MetricFilter.ALL, TimeUnit.MILLISECONDS, TimeUnit.MILLISECONDS, null, new MqContext());
		reporter.report(MetricSingleton.getMetricRegistry().getGauges(null),
				MetricSingleton.getMetricRegistry().getCounters(null),
				MetricSingleton.getMetricRegistry().getHistograms(null),
				MetricSingleton.getMetricRegistry().getMeters(null),
				MetricSingleton.getMetricRegistry().getTimers(null));
	}
}
