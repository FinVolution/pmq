package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class PullDataRequest extends BaseRequest {
	private long queueId;
	private long offsetStart;
	private long offsetEnd;
	private String topicName;
	private String consumerGroupName;

	public long getQueueId() {
		return queueId;
	}

	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}

	public long getOffsetStart() {
		return offsetStart;
	}

	public void setOffsetStart(long offsetStart) {
		this.offsetStart = offsetStart;
	}

	public long getOffsetEnd() {
		return offsetEnd;
	}

	public void setOffsetEnd(long offsetEnd) {
		this.offsetEnd = offsetEnd;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}
}
