package com.ppdai.infrastructure.mq.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ppdai.infrastructure.mq.biz.common.util.PropUtil;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.event.IAsynSubscriber;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
import com.ppdai.infrastructure.mq.client.event.MqEvent;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public class MqContext {
	private volatile long consumerId;
	// 获取记录broker的随机ip，防止出现通过域名访问时，host和dns访问错误
	private String brokerIp;
	private String consumerName;
	private String sdkVersion;
	// 此参数表示broker端模式
	private volatile int brokerMetaMode = 0;
	// 记录所有的配置，key为consumergroupName
	private Map<String, ConsumerGroupVo> configConsumerGroup = new ConcurrentHashMap<>();
	// key为consumerGroupName,value 为版本号
	private Map<String, Long> consumerGroupVersion = new ConcurrentHashMap<>();
	private Map<String, ConsumerGroupOneDto> consumerGroupMap = new ConcurrentHashMap<>();

	private String configPath;
	// 子环境列表
	private volatile Set<String> appSubEnv = new HashSet<>();
	private transient IMqResource mqResource = null;

	private transient IMqResource mqHtResource = null;

	private transient IMqResource mqPollingResource = null;

	private List<String> lstGroup1 = null, lstGroup2 = null;

	// private volatile boolean stopFlag = true;
	// 标记是否记录原始数据
	// private int logOrigData = 0;

	private volatile String metricUrl;

	private MqConfig config = new MqConfig();

	private MqEvent mqEvent = new MqEvent();
	private MqEnvironment mqEnvironment = null;

	public MqContext() {
		this.sdkVersion = PropUtil.getSdkVersion();
	}

	public MqEnvironment getMqEnvironment() {
		return mqEnvironment;
	}

	public void setMqEnvironment(MqEnvironment mqEnvironment) {
		this.mqEnvironment = mqEnvironment;
	}

	public String getBrokerIp() {
		return brokerIp;
	}

	public void setBrokerIp(String brokerIp) {
		this.brokerIp = brokerIp;
	}

	public MqEvent getMqEvent() {
		return mqEvent;
	}

	public void setMqEvent(MqEvent mqEvent) {
		this.mqEvent = mqEvent;
	}

	public List<String> getLstGroup1() {
		return lstGroup1;
	}

	public List<String> getLstGroup2() {
		return lstGroup2;
	}

	public MqConfig getConfig() {
		return config;
	}

	public void setConfig(MqConfig config) {
		this.config = config;
	}

	public String getMetricUrl() {
		return metricUrl;
	}

	public void setMetricUrl(String metricUrl) {
		this.metricUrl = metricUrl;
	}

	// public int getLogOrigData() {
	// return logOrigData;
	// }
	//
	// public void setLogOrigData(int logOrigData) {
	// this.logOrigData = logOrigData;
	// }

	public long getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(long consumerId) {
		this.consumerId = consumerId;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public Set<String> getAppSubEnv() {
		return appSubEnv;
	}

	public void setAppSubEnvMap(Map<String, Set<String>> consumerGroupSubEnvMap) {
		Set<String> rs = new java.util.HashSet<>(5);
		if (consumerGroupSubEnvMap != null && consumerGroupSubEnvMap.size() > 0) {
			consumerGroupSubEnvMap.entrySet().forEach(t1 -> {
				rs.addAll(t1.getValue());
			});
		}
		this.appSubEnv = rs;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public Map<String, ConsumerGroupVo> getConfigConsumerGroup() {
		return configConsumerGroup;
	}

	public void setConfigConsumerGroup(Map<String, ConsumerGroupVo> configConsumerGroup) {
		this.configConsumerGroup = configConsumerGroup;
	}

	public Map<String, Long> getConsumerGroupVersion() {
		return consumerGroupVersion;
	}

	public void setConsumerGroupVersion(Map<String, Long> consumerGroupVersion) {
		this.consumerGroupVersion = consumerGroupVersion;
	}

	public Map<String, ConsumerGroupOneDto> getConsumerGroupMap() {
		return consumerGroupMap;
	}

	public void setConsumerGroupMap(Map<String, ConsumerGroupOneDto> consumerGroupMap) {
		this.consumerGroupMap = consumerGroupMap;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public IMqResource getMqResource() {
		return mqResource;
	}

	public void setMqResource(IMqResource mqResource) {
		this.mqResource = mqResource;
	}

	public int getBrokerMetaMode() {
		return brokerMetaMode;
	}

	public void setBrokerMetaMode(int brokerMetaMode) {
		this.brokerMetaMode = brokerMetaMode;
	}

	public IMqResource getMqHtResource() {
		return mqHtResource;
	}

	public void setMqHtResource(IMqResource mqHtResource) {
		this.mqHtResource = mqHtResource;
	}

	// brokerUrls1 重要地址分组，brokerUrls2为非重要地址分组
	public void setBrokerUrls(List<String> brokerUrls1, List<String> brokerUrls2) {
		// 如果非重要节点列表为空，则将重要节点数据赋值给非重要节点
		if (brokerUrls2 == null || brokerUrls2.size() == 0) {
			brokerUrls2 = brokerUrls1;
		}
		if (mqHtResource != null) {
			mqHtResource.setUrls(brokerUrls1, brokerUrls2);
		}
		if (mqResource != null) {
			mqResource.setUrls(brokerUrls1, brokerUrls2);
		}
		if (mqPollingResource != null) {
			mqPollingResource.setUrls(brokerUrls1, brokerUrls2);
		}
		lstGroup1 = brokerUrls1;
		lstGroup2 = brokerUrls2;
	}

	public IMqResource getMqPollingResource() {
		return mqPollingResource;
	}

	public void setMqPollingResource(IMqResource mqPollingResource) {
		this.mqPollingResource = mqPollingResource;
	}

	public ISubscriber getSubscriber(String consumerGroupName, String topic) {
		ISubscriber rs = null;
		if (mqEvent != null && mqEvent.getiSubscriberSelector() != null) {
			rs = mqEvent.getiSubscriberSelector().getSubscriber(consumerGroupName, topic);
			if (rs != null) {
				return rs;
			}
		}
		if (rs == null && configConsumerGroup != null && configConsumerGroup.containsKey(consumerGroupName)) {
			if (configConsumerGroup.get(consumerGroupName).getTopics() != null
					&& configConsumerGroup.get(consumerGroupName).getTopics().containsKey(topic)) {
				return configConsumerGroup.get(consumerGroupName).getTopics().get(topic).getSubscriber();
			}
		}
		return null;
	}

	public IAsynSubscriber getAsynSubscriber(String consumerGroupName, String topic) {
		IAsynSubscriber rs = null;
		if (mqEvent != null && mqEvent.getiAsynSubscriberSelector() != null) {
			rs = mqEvent.getiAsynSubscriberSelector().getSubscriber(consumerGroupName, topic);
			if (rs != null) {
				return rs;
			}
		}
		if (rs == null && configConsumerGroup != null && configConsumerGroup.containsKey(consumerGroupName)) {
			if (configConsumerGroup.get(consumerGroupName).getTopics() != null
					&& configConsumerGroup.get(consumerGroupName).getTopics().containsKey(topic)) {
				return configConsumerGroup.get(consumerGroupName).getTopics().get(topic).getAsynSubscriber();
			}
		}
		return null;
	}

	public void clear() {
		// configConsumerGroup = new ConcurrentHashMap<>();
		// key为consumerGroupName,value 为版本号
		consumerGroupVersion = new ConcurrentHashMap<>();
		// 此时是为了修正以备后用
		configConsumerGroup = new ConcurrentHashMap<>();
		consumerGroupMap = new ConcurrentHashMap<>();
	}

	// 获取原始的订阅组，因为广播消息会修改名称
	public Map<String, ConsumerGroupVo> getOrignConfig() {
		Map<String, ConsumerGroupVo> configConsumerGroup1 = new ConcurrentHashMap<>();
		getConfigConsumerGroup().values().forEach(t1 -> {
			t1.getMeta().setName(t1.getMeta().getOriginName());
			configConsumerGroup1.put(t1.getMeta().getName(), t1);
		});
		return configConsumerGroup1;
	}
}
