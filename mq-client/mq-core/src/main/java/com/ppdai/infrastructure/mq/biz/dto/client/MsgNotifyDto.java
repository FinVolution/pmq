package com.ppdai.infrastructure.mq.biz.dto.client;

public class MsgNotifyDto {
	private long queueId;
	private String consumerGroupName;

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}

	public long getQueueId() {
		return queueId;
	}

	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}
}
