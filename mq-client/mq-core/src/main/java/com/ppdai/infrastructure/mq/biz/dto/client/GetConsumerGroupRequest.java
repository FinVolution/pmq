package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class GetConsumerGroupRequest extends BaseRequest {
	private long consumerId;

	private Map<String, Long> consumerGroupVersion;

	public long getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(long consumerId) {
		this.consumerId = consumerId;
	}

	public Map<String, Long> getConsumerGroupVersion() {
		return consumerGroupVersion;
	}

	public void setConsumerGroupVersion(Map<String, Long> consumerGroupVersion) {
		this.consumerGroupVersion = consumerGroupVersion;
	}
}
