package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatRequest;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqHeartbeatService;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public class MqHeartbeatService implements IMqHeartbeatService {
	private Logger log = LoggerFactory.getLogger(MqHeartbeatService.class);
	private ScheduledExecutorService executor = null;
	private HeartbeatRequest request = null;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private TraceMessage traceMsg = TraceFactory.getInstance("MqHeartbeatService");
	private MqContext mqContext;
	private IMqResource mqResource;
	private volatile boolean isStop = false;

	public MqHeartbeatService(IMqClientBase mqClientBase) {	
		this(mqClientBase,mqClientBase.getMqFactory().createMqResource(mqClientBase.getContext().getConfig().getUrl(), 3500, 3500));
	}

	public MqHeartbeatService(IMqClientBase mqClientBase, IMqResource mqResource) {
		this.mqContext = mqClientBase.getContext();
		this.mqResource = mqResource;
		mqContext.setMqHtResource(this.mqResource);
	}

	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			// this.mqContext = mqContext;
			// this.mqResource = new
			// MqResource(mqContext.getConfig().getUrl(),3500,3500);
			isStop = false;
			executor = Executors.newScheduledThreadPool(1,
					SoaThreadFactory.create("mq-HeartbeatService-pool-%d", Thread.MAX_PRIORITY - 1, true));
			// heatBeatService = new
			// ScheduledThreadPoolExecutor(1,SoaThreadFactory.create("mq-HeartbeatService-pool-%d",true));
			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (!isStop) {
						doHeartbeat();
					}
				}
			}, 1, 10, TimeUnit.SECONDS);
		}
	}

	protected void doHeartbeat() { 
		if (mqContext.getConsumerId() > 0) {
			if (request == null) {
				request = new HeartbeatRequest();
				request.setConsumerId(mqContext.getConsumerId());
			}
			TraceMessageItem traceMessageItem = new TraceMessageItem();
			try {
				mqResource.heartbeat(request);
				log.debug("consumer_client_{}_heartbeat_end", mqContext.getConsumerId());
				traceMessageItem.status = "suc";
			} catch (Exception e) {
				traceMessageItem.status = "fail";
				traceMessageItem.msg = e.getMessage();
			}
			traceMsg.add(traceMessageItem);
		}
	}

	@Override
	public void close() {
		isStop = true;
		try {
			executor.shutdown();
		} catch (Exception e) {
		}
		startFlag.set(false);
		request = null;
		executor = null;
	}
}
