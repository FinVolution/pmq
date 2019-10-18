package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class OpLogRequest extends BaseRequest {
	private String consumerGroupName;
	private String content;
	private String consumerName;

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}
}
