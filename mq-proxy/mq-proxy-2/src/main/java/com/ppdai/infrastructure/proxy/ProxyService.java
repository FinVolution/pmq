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
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.client.GroupTopicDto;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupTopicVo;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
import com.ppdai.infrastructure.proxy.vo.ConsumerGroupIpUrlVo;

@Component
public class ProxyService {
	private static Logger log = LoggerFactory.getLogger(ProxyService.class);
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(1), SoaThreadFactory.create("HsCheckService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());
	@Autowired
	private Environment env;

	@Autowired
	private ProxySub proxySub;
	private static Map<String, ConsumerGroupIpUrlVo> config = new ConcurrentHashMap<>();
	// 当前ip需要注册的消费者组名列表
	private static AtomicReference<Map<String, String>> consumerGroupsRef = new AtomicReference<>(new HashMap<>());
	// 消费者组对应的topic列表,一级key为消费者组名，二级key为toipic名称，三级key为kong
	private static AtomicReference<Map<String, Map<String, String>>> consumerGroupTopicRef = new AtomicReference<>(
			new HashMap<>());
	private String ip = IPUtil.getLocalIP();
	private volatile boolean isRunning = true;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private Object lockObj = new Object();

	/*
	 * { "Test1Sub":{ "ipLst":"10.2.4.14,10.2.4.14,10.2.4.14",
	 * "exeUrl":"http://testproxy.******.com",
	 * "hsUrl":"http://testproxy.******.com/hs" }, "Test2Sub":{
	 * "ipLst":"1.1.1.1",
	 * "exeUrl":"http://testproxy.******.com",
	 * "hsUrl":"http://testproxy.******.com/hs" }
	 * 
	 * }
	 */
	// @PostConstruct
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
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

		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (isRunning) {
					Transaction transaction = Tracer.newTransaction("Proxy", "topic");
					try {
						synchronized (lockObj) {
							if (checkTopic()) {
								reStart();
							}
						}
						transaction.setStatus(Transaction.SUCCESS);
					} catch (Exception e) {
						transaction.setStatus(e);
					} finally {
						transaction.complete();
					}
					Util.sleep(10000L);
				}
			}
		});
	}

	private boolean checkTopic() {
		List<String> consumerGroups = new ArrayList<>(consumerGroupsRef.get().keySet());
		if (CollectionUtils.isEmpty(consumerGroups))
			return false;
		boolean flag = false;
		for (Map.Entry<String, Map<String, String>> t1 : consumerGroupTopicRef.get().entrySet()) {
			if (t1.getValue().size() == 0) {
				flag = true;
				break;
			}
		}
		if (flag) {
			return false;
		}
		List<GroupTopicDto> groupTopics = MqClient.getGroupTopic(consumerGroups);
		Map<String, Map<String, String>> consumerGroupTopicNew = new HashMap<>();
		for (GroupTopicDto t1 : groupTopics) {
			consumerGroupTopicNew.put(t1.getConsumerGroupName(), new HashMap<>());
			t1.getTopics().forEach(t2 -> {
				consumerGroupTopicNew.get(t1.getConsumerGroupName()).put(t2, "");
			});
		}
		if (!JsonUtil.toJsonNull(consumerGroupTopicNew).equals(JsonUtil.toJsonNull(consumerGroupTopicRef.get()))) {
			consumerGroupTopicRef.set(consumerGroupTopicNew);
			return true;
		} else {
			return false;
		}
	}

	private volatile String preJson = "";

	private void checkConfig() {
		String json = env.getProperty("mq.proxy.data", "");
		if (Util.isEmpty(json) || preJson.equals(json)) {
			return;
		}
		System.out.println(preJson.equals(json));
		synchronized (lockObj) {
			preJson = json;
			Boolean localChanged = false;
			Boolean atrrChanged = false;
			Map<String, ConsumerGroupIpUrlVo> config1 = JsonUtil.parseJson(json,
					new TypeReference<Map<String, ConsumerGroupIpUrlVo>>() {
					});
			for (Map.Entry<String, ConsumerGroupIpUrlVo> entry : config1.entrySet()) {
				if (!config.containsKey(entry.getKey())) {
					config.put(entry.getKey(), entry.getValue());
				} else {
					ConsumerGroupIpUrlVo temp = config.get(entry.getKey());
					if (!(temp.getExeUrl() + "").equals(entry.getValue().getExeUrl())
							|| !(temp.getHsUrl() + "").equals(entry.getValue().getHsUrl())
							|| !(temp.getIpLst() + "").equals(entry.getValue().getIpLst())) {
						config.put(entry.getKey(), entry.getValue());
						atrrChanged = true;
					}
				}
			}
			// 反向检查，检查配置是否有删除
			List<String> keys = new ArrayList<>(config.keySet());
			for (String consumerGroupName : keys) {
				// 之前有某个消费者组，现在新的没有需要删除，同时检查是否由当前ip代理，如果有则需要重启
				if (!config1.containsKey(consumerGroupName)) {
					if (!localChanged && (config.get(consumerGroupName).getIpLst() + "").indexOf(ip) != -1) {
						localChanged = true;
					}
					config.remove(consumerGroupName);
				}
			}
			// 检查当前ip分配的消费者组名
			// List<String> consumerGroupLst = new ArrayList<>(100);
			Map<String, String> consumerGroupMapOld = consumerGroupsRef.get();
			// 当前实例代理的消费者组
			Map<String, String> consumerGroupMap = new HashMap<>();
			for (Map.Entry<String, ConsumerGroupIpUrlVo> entry : config.entrySet()) {
				if ((entry.getValue().getIpLst() + "").indexOf(ip) != -1) {
					consumerGroupMap.put(entry.getKey(), "");
					// 如果存在新的消费者组不在老的map中表示发生变化了
					if (!consumerGroupMapOld.containsKey(entry.getKey())) {
						localChanged = true;
						atrrChanged = true;
					}
				}
			}
			// 如果数量不一致说明也发生变化了
			if (consumerGroupMapOld.size() != consumerGroupMap.size()) {
				localChanged = true;
				atrrChanged = true;
			}
			if (atrrChanged) {
				consumerGroupsRef.set(consumerGroupMap);
			}
			if (localChanged) {
				reStart();
			}
		}
	}

	private void reStart() {		
		//MqContext mqContext = MqClient.getContext();
		MqClient.stop();
		MqClient.start();		
		checkTopic();
		Map<String, Map<String, String>> consumerGroupTopic = consumerGroupTopicRef.get();
		consumerGroupTopic.keySet().forEach(t1 -> {
			final ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo();
			consumerGroupVo.setGroupName(t1);
			consumerGroupTopic.get(t1).keySet().forEach(t2 -> {
				ConsumerGroupTopicVo consumerGroupTopicVo = new ConsumerGroupTopicVo();
				consumerGroupTopicVo.setName(t2);
				consumerGroupTopicVo.setSubscriber(proxySub);
				consumerGroupVo.addTopic(consumerGroupTopicVo);
			});
			if (consumerGroupVo.getTopics().size() > 0) {
				if (!MqClient.registerConsumerGroup(consumerGroupVo)) {
					log.error("ConsuemrGroup_Regist_fail,json is " + JsonUtil.toJson(consumerGroupVo));
				}
			}
		});
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
		return config;
	}

	public String getInfo() {
		return "total config:" + JsonUtil.toJson(config) + ",\n local config:"
				+ JsonUtil.toJson(consumerGroupsRef.get()) + ",\n local consumer topic:"
				+ JsonUtil.toJson(consumerGroupTopicRef.get()) + ",local ip:" + ip;
	}
}
