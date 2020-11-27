package com.ppdai.infrastructure.rest.mq.controller.client;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatResponse;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;

@RestController
@RequestMapping(MqConstanst.CONSUMERPRE)
public class ConsumerHeartbeatController {
	private static final Logger log = LoggerFactory.getLogger(ConsumerHeartbeatController.class);
	private final Map<Long, Boolean> mapAppPolling = new ConcurrentHashMap<>(1000);

	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private ConsumerService consumerService;

	private ThreadPoolExecutor executor = null;
	private ThreadPoolExecutor executorRun = null;
	private volatile int heartBeatThreadSize = 3;
	// @Autowired
	// private Util util;

	@PostConstruct
	private void init() {
		heartBeatThreadSize = soaConfig.getHeartBeatThreadSize();
		executor = new ThreadPoolExecutor(1, 1, 3L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
				SoaThreadFactory.create("heartbeat", Thread.MAX_PRIORITY, true), new ThreadPoolExecutor.CallerRunsPolicy());
		executorRun = new ThreadPoolExecutor(heartBeatThreadSize, heartBeatThreadSize, 10L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(5000), SoaThreadFactory.create("heartbeat-run", Thread.MAX_PRIORITY - 1, true),
				new ThreadPoolExecutor.CallerRunsPolicy());
		soaConfig.registerChanged(new Runnable() {
			@Override
			public void run() {
				if (heartBeatThreadSize != soaConfig.getHeartBeatThreadSize()) {
					heartBeatThreadSize = soaConfig.getHeartBeatThreadSize();
					executorRun.setCorePoolSize(heartBeatThreadSize);
					executorRun.setMaximumPoolSize(heartBeatThreadSize);
				}
			}
		});
		executor.execute(() -> {
			heartbeat();
		});
	}

	private AtomicLong heartBeatCounter = new AtomicLong(0);

	private void heartbeat() {
		while (true) {
			try {
				exeHeartBeat();
			} catch (Throwable e) {
			}
			// 通过随机的方式来避免数据库的洪峰压力
			Util.sleep(soaConfig.getHeartbeatSleepTime());
		}
	}

	private void exeHeartBeat() {
		if (mapAppPolling.size() > 0) {
			heartBeatCounter.compareAndSet(Long.MAX_VALUE, 0);
			long counter = heartBeatCounter.incrementAndGet();
			log.info("doHeartBeat_start");
			Transaction catTransaction = null;
			try {
				catTransaction = Tracer.newTransaction("Timer-service",
						"heartbeatBatch-" + soaConfig.getHeartbeatBatchSize());
				Map<Long, Boolean> map = new HashMap<>(mapAppPolling);
				List<List<Long>> idss = Util.split(new ArrayList<>(map.keySet()), soaConfig.getHeartbeatBatchSize());
				for (List<Long> ids : idss) {
					if (counter == heartBeatCounter.get()) {
						executorRun.execute(() -> {
							doHeartbeat(ids);
						});
						for (Long id : ids) {
							mapAppPolling.remove(id);
						}
					}
				}
				catTransaction.setStatus(Transaction.SUCCESS);
			} catch (Throwable e) {
				catTransaction.setStatus(e);
			} finally {
				catTransaction.complete();
			}
			log.info("doHeartBeat_end");
		}
	}

	private void doHeartbeat(List<Long> ids) {
		Transaction catTransaction = null;
		try {
			catTransaction = Tracer.newTransaction("Service",
					MqConstanst.CONSUMERPRE + "/heartbeat-" + soaConfig.getHeartbeatBatchSize());
			consumerService.heartbeat(ids);
			catTransaction.setStatus(Transaction.SUCCESS);

		} catch (Throwable e) {
			log.error("heartBeatfail失败", e);
			catTransaction.setStatus(e);
		}
		catTransaction.complete();
	}

	// 发送心跳，直接返回
	@PostMapping("/heartbeat")
	public HeartbeatResponse heartBeat(@RequestBody HeartbeatRequest request) {
		HeartbeatResponse response = new HeartbeatResponse();
		response.setSuc(true);
		response.setHeatbeatTime(soaConfig.getConsumerHeartBeatTime());
		response.setBakUrl(soaConfig.getMqBakUrl());
		try {
			if (request != null) {
				if (request.getAsyn() == 1) {
					if (request.getConsumerId() > 0) {
						mapAppPolling.put(request.getConsumerId(), true);
					}
					if (!CollectionUtils.isEmpty(request.getConsumerIds())) {
						request.getConsumerIds().forEach(t1 -> {
							mapAppPolling.put(t1, true);
						});
					}
				}else {
					response.setDeleted(consumerService.heartbeat(Arrays.asList(request.getConsumerId()))>0?0:1);
				}
			}

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return response;
	}

	@PreDestroy
	private void close() {
		try {
			executor.shutdown();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
