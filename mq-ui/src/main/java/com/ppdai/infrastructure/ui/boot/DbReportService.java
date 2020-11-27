package com.ppdai.infrastructure.ui.boot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Gauge;
import com.ppdai.infrastructure.mq.biz.common.metric.MetricSingleton;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;

@Component
public class DbReportService {
	@Autowired
	private Message01Service message01Service;
	@Autowired
	private DbNodeService dbNodeService;

	private AtomicReference<Map<String, Integer>> conMapRef = new AtomicReference<Map<String, Integer>>(
			new HashMap<>());
	private ExecutorService executorService = Executors
			.newSingleThreadExecutor(SoaThreadFactory.create("DbReportService", true));

	private volatile boolean isRunning = true;
	private volatile Map<String, Boolean> metricMap = new ConcurrentHashMap<>();

	private AtomicBoolean startFlag = new AtomicBoolean(false);

	@PostConstruct
	public void report() {
		if (startFlag.compareAndSet(false, true)) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					while (isRunning) {
						Map<Long, DbNodeEntity> dbNodeMap = dbNodeService.getCache();
						Map<String, DbNodeEntity> dataSourceMap = new HashMap<>();
						Map<String, Integer> conMap = new HashMap<>();
						try {
							for (long dbId : dbNodeMap.keySet()) {
								if (!dataSourceMap.containsKey(dbNodeMap.get(dbId).getIp())) {
									dataSourceMap.put(dbNodeMap.get(dbId).getIp(), dbNodeMap.get(dbId));
								}
							}
							for (String ip : dataSourceMap.keySet()) {
								message01Service.setDbId(dataSourceMap.get(ip).getId());
								int conCount = message01Service.getConnectionsCount();
								conMap.put(ip, conCount);
								if (!metricMap.containsKey(ip)) {
									//System.out.println(ip);
									metricMap.put(ip, true);
									MetricSingleton.getMetricRegistry().register("mq.ip.con.count?ip=" + ip,
											new Gauge<Integer>() {
												@Override
												public Integer getValue() {
													if (conMapRef.get().containsKey(ip)) {
														return conMapRef.get().get(ip);
													} else {
														return 0;
													}
												}
											});
								}

							}
							conMapRef.set(conMap);
						} catch (Throwable e) {
							// TODO: handle exception
						}
						Util.sleep(10000);
					}
				}
			});
		}
	}

	@PreDestroy
	private void close() {
		isRunning = false;
		executorService.shutdown();
	}
}
