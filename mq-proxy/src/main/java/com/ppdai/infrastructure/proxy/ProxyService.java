package com.ppdai.infrastructure.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.biz.event.ISubscriberSelector;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqConfig;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
import com.ppdai.infrastructure.proxy.service.HsCheckService;
import com.ppdai.infrastructure.proxy.vo.ConsumerGroupIpUrlVo;

@Component
public class ProxyService {
	private static Logger log = LoggerFactory.getLogger(ProxyService.class);
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(1), SoaThreadFactory.create("ProxyService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	@Autowired
	private Environment env;

	@Autowired
	private ProxySub proxySub;
	private static AtomicReference<Map<String, ConsumerGroupIpUrlVo>> config = new AtomicReference<>(
			new ConcurrentHashMap<>());
	// 当前ip需要注册的消费者组名列表
	private static AtomicReference<List<String>> consumerGroupsRef = new AtomicReference<>(new ArrayList<>());

	private String ip = IPUtil.getLocalIP();
	private volatile boolean isRunning = true;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private Object lockObj = new Object();

	/*
	 * { "Test1Sub":{ "ipLst":"10.2.4.14,10.2.4.14,10.2.4.14",
	 * "exeUrl":"http://testproxy.ppdaicorp.com",
	 * "hsUrl":"http://testproxy.ppdaicorp.com/hs" }, "Test2Sub":{
	 * "ipLst":"10.2.4.14,10.2.4.14,10.2.4.14",
	 * "exeUrl":"http://testproxy.ppdaicorp.com",
	 * "hsUrl":"http://testproxy.ppdaicorp.com/hs" }
	 * 
	 * }
	 */
	// @PostConstruct
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			MqClient.registerISubscriberSelector(new ISubscriberSelector() {
				@Override
				public ISubscriber getSubscriber(String consumerGroupName, String topic) {
					// TODO Auto-generated method stub
					return proxySub;
				}
			});
			// System.out.println("开启proxy");
			executor.execute(new Runnable() {
				@Override
				public void run() {
					while (isRunning) {
						Transaction transaction = Tracer.newTransaction("Proxy", "config");
						try {
							checkConfig();
							transaction.setStatus(Transaction.SUCCESS);
						} catch (Exception e) {
							transaction.setStatus(e);
						} finally {
							transaction.complete();
						}
						Util.sleep(5000L);
					}
				}
			});
		}
	}

	private volatile String preJson = "";

	private void checkConfig() {
		String json = env.getProperty("mq.proxy.data", "");
		if (Util.isEmpty(json) || preJson.equals(json)) {
			return;
		}
		// System.out.println(preJson.equals(json));
		synchronized (lockObj) {
			Map<String, ConsumerGroupIpUrlVo> config1 = null;
			preJson = json;
			try {
				config1 = JsonUtil.parseJson(json, new TypeReference<Map<String, ConsumerGroupIpUrlVo>>() {
				});
			} catch (Exception e) {
				log.error("parer error", e);
				return;
			}
			config.set(config1);
			// 反向检查，检查配置是否有删除
			List<String> keys = new ArrayList<>(config1.keySet());
			List<String> consumerGroups = new ArrayList<>();
			for (String consumerGroupName : keys) {
				if ((config1.get(consumerGroupName).getIpLst() + "").indexOf(ip) != -1) {
					consumerGroups.add(consumerGroupName);
				}else if(Util.isEmpty(config1.get(consumerGroupName).getIpLst())){
					consumerGroups.add(consumerGroupName);
				}
			}
			if (!JsonUtil.toJsonNull(consumerGroups).equalsIgnoreCase(JsonUtil.toJsonNull(consumerGroupsRef.get()))) {
				consumerGroupsRef.set(consumerGroups);
				reStart();
			}

		}
	}

	private void reStart() {
		MqConfig config = MqClient.getContext().getConfig();
		MqClient.close();
		MqClient.start(config);
		Map<String, ConsumerGroupVo> consumerGroupMap = new HashMap<String, ConsumerGroupVo>();
		List<String> consumerGroups = consumerGroupsRef.get();
		Map<String, String> urlMap = new ConcurrentHashMap<>(consumerGroupMap.size());
		Map<String, Boolean> statusMap = new ConcurrentHashMap<>(consumerGroupMap.size());
		Map<String, ConsumerGroupIpUrlVo> data = getConfig();
		for (String t1 : consumerGroups) {
			if (data.containsKey(t1)) {
				urlMap.put(t1, data.get(t1).getHsUrl());
				statusMap.put(t1, true);
			}
			final ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo();
			consumerGroupVo.setGroupName(t1);
			consumerGroupMap.put(t1, consumerGroupVo);
		}
		HsCheckService.updateHsUrl(urlMap);
		HsCheckService.updateStatus(statusMap);
		if (consumerGroupMap.size() > 0) {
			if (!MqClient.registerConsumerGroup(consumerGroupMap)) {
				log.error("ConsuemrGroup_Regist_fail,json is " + JsonUtil.toJson(consumerGroupMap));
			}
		}
	}

	@PreDestroy
	private void close() {
		isRunning = false;
		try {
			executor.shutdown();
		} catch (Exception e) {
		}
	}

	public static String getExeUrl(String consumerGroupName) {
		Map<String, ConsumerGroupIpUrlVo> data = getConfig();
		if (data.containsKey(consumerGroupName)) {
			return data.get(consumerGroupName).getExeUrl();
		}
		return null;
	}

	public static String getHsUrl(String consumerGroupName) {
		Map<String, ConsumerGroupIpUrlVo> data = getConfig();
		if (data.containsKey(consumerGroupName)) {
			return data.get(consumerGroupName).getHsUrl();
		}
		return null;
	}

	public static Map<String, ConsumerGroupIpUrlVo> getConfig() {
		return config.get();
	}

	public String getInfo() {
		return "total config:" + JsonUtil.toJson(config) + ",\n local config:"
				+ JsonUtil.toJson(consumerGroupsRef.get());
	}
}
