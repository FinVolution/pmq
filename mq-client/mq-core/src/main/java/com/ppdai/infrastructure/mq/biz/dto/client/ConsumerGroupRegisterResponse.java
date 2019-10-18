package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class ConsumerGroupRegisterResponse extends BaseResponse {
	//key为原始的名称，value为虚拟的名称
	private Map<String,String> broadcastConsumerGroupName;

	public Map<String, String> getBroadcastConsumerGroupName() {
		return broadcastConsumerGroupName;
	}

	public void setBroadcastConsumerGroupName(Map<String, String> broadcastConsumerGroupName) {
		this.broadcastConsumerGroupName = broadcastConsumerGroupName;
	}

	
}
