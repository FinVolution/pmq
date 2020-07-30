package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class ConsumerGroupRegisterResponse extends BaseResponse {
	//key为原始的名称，value为虚拟的名称
	private Map<String,String> broadcastConsumerGroupName;
	//兼容broadcastConsumerGroupName,子环境会动态创建消费者组。
	private Map<String,String> consumerGroupNameNew;
	public Map<String, String> getConsumerGroupNameNew() {
		return consumerGroupNameNew;
	}

	public void setConsumerGroupNameNew(Map<String, String> consumerGroupNameNew) {
		this.consumerGroupNameNew = consumerGroupNameNew;
	}

	public Map<String, String> getBroadcastConsumerGroupName() {
		return broadcastConsumerGroupName;
	}

	public void setBroadcastConsumerGroupName(Map<String, String> broadcastConsumerGroupName) {
		this.broadcastConsumerGroupName = broadcastConsumerGroupName;
	}	
}
