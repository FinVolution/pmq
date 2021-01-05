package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.dto.TraceMessageDto;
import com.ppdai.infrastructure.mq.client.server.StatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatRequest;
import com.ppdai.infrastructure.mq.client.MqClient;
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
	private long count = 0;

	public MqHeartbeatService() {
		this(MqClient.getMqFactory().createMqResource(MqClient.getContext().getConfig().getUrl(), 3500, 3500));
	}

	public MqHeartbeatService(IMqResource mqResource) {
		this.mqContext = MqClient.getContext();
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
						checkMsgTimeOut();
						count++;
					}
				}
			}, 1, 10, TimeUnit.SECONDS);
			StatService.start();
		}
	}
	private boolean checkTimeouting = false;

	private void checkMsgTimeOut() {
		if (checkTimeouting) {
			return;
		}
		checkTimeouting = true;

		try {
			long warnTimeout = MqClient.getContext().getConfig().getWarnTimeout() * 1000;
			IConsumerPollingService consumerPollingService = MqClient.getMqFactory().createConsumerPollingService();
			Map<String, IMqGroupExcutorService> groups = consumerPollingService.getMqExcutors();
			groups.entrySet().forEach(t1 -> {
				List<TraceMessageDto> rs = new ArrayList<>(100);
				Map<Long, IMqQueueExcutorService> queues = t1.getValue().getQueueEx();
				queues.entrySet().forEach(t2 -> {
					Collection<TraceMessageDto> slowMsg2 = t2.getValue().getSlowMsg().values();
					slowMsg2.forEach(t3 -> {
						if (t3.start > 0 && System.currentTimeMillis() - t3.start > warnTimeout) {
							rs.add(t3);
						}
					});
				});
				sendMsgWarn(warnTimeout,t1.getKey(),rs);
			});
		} catch (Throwable e) {

		}finally {
			checkTimeouting=false;
		}
	}

	private void sendMsgWarn(long warnTimeout,String consumerGroupName,List<TraceMessageDto> rs) {
		if(rs.size()==0)return;
		String subject = "消息消费耗时超长告警！";
		String content = "下列消息消费耗时超过设定时间了，请注意！"+ JsonUtil.toJsonNull(rs)+",超时时间阈值为"+(warnTimeout/1000)+"秒,当前检查时间为"+Util.formateDate(new Date());
		SendMailRequest request = new SendMailRequest();
		request.setType(2);
		request.setConsumerGroupName(consumerGroupName);
		//request.setTopicName(consumerQueueRef.get().getTopicName());
		request.setSubject(subject);
		request.setContent(content);
		//request.setKey(mqContext.getConsumerName() + "-" + consumerQueueRef.get().getTopicName() + "-消息处理失败");
		mqResource.sendMail(request);
	}
	private void doHeartbeat() {
		if (mqContext.getConsumerId() > 0) {
			if (request == null) {
				request = new HeartbeatRequest();
			}
			request.setConsumerId(mqContext.getConsumerId());
			TraceMessageItem traceMessageItem = new TraceMessageItem();
			try {
				request.setAsyn(count % 3==0 ? 0:1);
				HeartbeatResponse response = mqResource.heartbeat(request);
				if (response != null && response.getDeleted() == 1) {
					traceMessageItem.status = "restart";
					log.warn("client restart,"+mqContext.getConsumerId());
					MqClient.reStart();
					Util.sleep(5000);
				}
				if(response!=null){
					mqContext.setBakUrl(response.getBakUrl());
				}
				log.debug("consumer_client_{}_heartbeat_end", mqContext.getConsumerId());
				traceMessageItem.status = "suc";
				traceMessageItem.msg="心跳正常";
			} catch (Throwable e) {
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
		} catch (Throwable e) {
		}
		startFlag.set(false);
		request = null;
		executor = null;
	}
}
