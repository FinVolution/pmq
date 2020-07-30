package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupResponse;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqBrokerUrlRefreshService;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public class MqBrokerUrlRefreshService implements IMqBrokerUrlRefreshService {
	private Logger log = LoggerFactory.getLogger(MqBrokerUrlRefreshService.class);
	private ScheduledExecutorService executor = null;
	private GetMetaGroupRequest request = new GetMetaGroupRequest(); 
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private MqContext mqContext;
	private IMqResource mqResource;
	private volatile boolean isStop = false;
	private volatile boolean runStatus = false;

	public MqBrokerUrlRefreshService() {
		this(MqClient.getContext().getMqResource());
	}
	public MqBrokerUrlRefreshService(IMqResource mqResource) { 
		this.mqContext = MqClient.getContext();
		this.mqResource = mqResource;
	}
	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {			
			isStop = false;
			runStatus = false;
			doUpdateBrokerUrls();
			executor = Executors.newScheduledThreadPool(1,
					SoaThreadFactory.create("mq-brokerFreshService-pool-%d", Thread.MAX_PRIORITY - 1, true));
			executor.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					if (!isStop) {
						runStatus = true;
						doUpdateBrokerUrls();
						runStatus = false;
					}
				}
			}, 1, 20, TimeUnit.SECONDS);
		}
	}

	protected void doUpdateBrokerUrls() {
		try {
			GetMetaGroupResponse response = this.mqResource.getMetaGroup(request);
			if(response==null){
				return;
			}
			if (response != null && response.isSuc()) {
				mqContext.setBrokerMetaMode(response.getBrokerMetaMode());
				mqContext.setMetricUrl(response.getMetricUrl());
				if(Util.isEmpty(mqContext.getMetricUrl())){
					//MqMeticReporterService.getInstance(mqClientBase).close();
					MqClient.getMqFactory().createMqMeticReporterService().close();
				}else{
					MqClient.getMqFactory().createMqMeticReporterService().start();
				}
			}			
			if (mqContext.getBrokerMetaMode() == 1 || (mqContext.getBrokerMetaMode() == 0 && mqContext.getConfig().isMetaMode())) {
				//List<String> brokerUrls = response.getBrokerIp();
				if (response.getBrokerIpG1()!=null) {					
					mqContext.setBrokerUrls(response.getBrokerIpG1(),response.getBrokerIpG2());
				}
			} else if (mqContext.getBrokerMetaMode() == -1 || !mqContext.getConfig().isMetaMode()) {
				mqContext.setBrokerUrls(new ArrayList<>(),new ArrayList<>());
			}
		} catch (Exception e) {
			log.error("updateBrokerError", e);
		}

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
	}

}
