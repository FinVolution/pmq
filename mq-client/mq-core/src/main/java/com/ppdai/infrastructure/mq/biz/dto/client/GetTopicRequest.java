package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class GetTopicRequest extends BaseRequest{

	private String consumerGroupName;

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}
}
