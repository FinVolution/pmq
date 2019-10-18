package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class ConsumerDeRegisterRequest extends BaseRequest {
	private long id;

	private List<String> consumerGroupNames;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<String> getConsumerGroupNames() {
		return consumerGroupNames;
	}

	public void setConsumerGroupNames(List<String> consumerGroupNames) {
		this.consumerGroupNames = consumerGroupNames;
	}
}
