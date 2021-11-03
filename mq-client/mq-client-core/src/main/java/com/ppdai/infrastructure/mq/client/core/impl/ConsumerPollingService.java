package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.MqEnv;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupResponse;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public class ConsumerPollingService implements IConsumerPollingService {
	private Logger log = LoggerFactory.getLogger(ConsumerPollingService.class);
	private ThreadPoolExecutor executor = null;
	private TraceMessage traceMsg = TraceFactory.getInstance("ConsumerPollingService");
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private Map<String, IMqGroupExcutorService> mqExcutors = new ConcurrentHashMap<>();
	private MqContext mqContext = null;
	private IMqResource mqResource;
	private IMqFactory mqFactory;
	private volatile boolean isStop = false;
	private volatile boolean runStatus = false;

	public ConsumerPollingService() {
		this(MqClient.getMqFactory().createMqResource(MqClient.getContext().getConfig().getUrl(), 32000, 32000));
	}

	public ConsumerPollingService(IMqResource mqResource) {
		this.mqContext = MqClient.getContext();
		this.mqResource = mqResource;
		this.mqFactory = MqClient.getMqFactory();
		this.mqContext.setMqPollingResource(mqResource);
	}

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			isStop = false;
			runStatus = false;
			executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100),
					SoaThreadFactory.create("ConsumerPollingService", true),
					new ThreadPoolExecutor.DiscardOldestPolicy());
			executor.execute(new Runnable() {
				@Override
				public void run() {
					while (!isStop) {
						TraceMessageItem traceMessageItem = new TraceMessageItem();
						runStatus = true;
						try {
							traceMessageItem.status = "suc";
							longPolling();
						} catch (Throwable e) {
							// e.printStackTrace();
							traceMessageItem.status = "fail";
							Util.sleep(1000);
						}
						traceMsg.add(traceMessageItem);
						runStatus = false;
					}
				}
			});
		}
	}

	protected void longPolling() {
		if (mqContext.getConsumerId() > 0 && mqContext.getConsumerGroupVersion() != null
				&& mqContext.getConsumerGroupVersion().size() > 0) {
			Transaction transaction = Tracer.newTransaction("mq-group", "longPolling");
			try {
				GetConsumerGroupRequest request = new GetConsumerGroupRequest();
				request.setConsumerId(mqContext.getConsumerId());
				request.setConsumerGroupVersion(mqContext.getConsumerGroupVersion());
				GetConsumerGroupResponse response = mqResource.getConsumerGroup(request);
				if (response != null && response.getConsumerDeleted() == 1) {
					log.info("consumerid为" + request.getConsumerId());
				}
				handleGroup(response);
				transaction.setStatus(Transaction.SUCCESS);
			} catch (Exception e) {
				transaction.setStatus(e);
			} finally {
				transaction.complete();
			}
		} else {
			Util.sleep(1000);
		}
	}

	protected void handleGroup(GetConsumerGroupResponse response) {
		if (isStop) {
			return;
		}
		if (response != null) {
			mqContext.setBrokerMetaMode(response.getBrokerMetaMode());
			if (MqClient.getMqEnvironment() != null && MqClient.getMqEnvironment().getEnv() == MqEnv.FAT) {
				MqClient.getContext().setAppSubEnvMap(response.getConsumerGroupSubEnvMap());
			}
		}
	/*	if (response != null && response.getConsumerDeleted() == 1) {
			MqClient.reStart();
			Util.sleep(5000);
			return;
		} else*/
		if (response != null && response.getConsumerGroups() != null
				&& response.getConsumerGroups().size() > 0) {
			log.info("get_consumer_group_data,获取到的最新消费者组数据为：" + JsonUtil.toJson(response));
			TraceMessageItem item = new TraceMessageItem();
			item.status = "changed";
			item.msg = JsonUtil.toJson(response);
			response.getConsumerGroups().entrySet().forEach(t1 -> {
				if (!isStop) {
					if (!mqExcutors.containsKey(t1.getKey())) {
						mqExcutors.put(t1.getKey(), mqFactory.createMqGroupExcutorService());
					}
					log.info("consumer_group_data_change,消费者组" + t1.getKey() + "发生重平衡或者meta更新");
					// 进行重平衡操作或者更新元数据信息
					mqExcutors.get(t1.getKey()).rbOrUpdate(t1.getValue(), response.getServerIp());
					mqContext.getConsumerGroupVersion().put(t1.getKey(), t1.getValue().getMeta().getVersion());
				}
			});
			traceMsg.add(item);
		}
		// 然后启动
		mqExcutors.values().forEach(t1 -> {
			t1.start();
		});
	}

	@Override
	public void close() {
		isStop = true;
		try {
			mqExcutors.values().forEach(t1 -> {
				t1.close();
			});
			mqExcutors.clear();
		} catch (Exception e) {
			// TODO: handle exception
		}
		long start = System.currentTimeMillis();
		// 这是为了等待有未完成的任务
		while (runStatus) {
			Util.sleep(10);
			// System.out.println("closing...................."+isRunning);
			if (System.currentTimeMillis() - start > 10000) {
				break;
			}
		}
		try {
			executor.shutdown();
		} catch (Exception e) {
		}
		startFlag.set(false);
		isStop = true;
	}

	@Override
	public Map<String, IMqGroupExcutorService> getMqExcutors() {
		// TODO Auto-generated method stub
		return mqExcutors;
	}
}
