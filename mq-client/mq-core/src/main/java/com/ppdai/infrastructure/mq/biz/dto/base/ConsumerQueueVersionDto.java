package com.ppdai.infrastructure.mq.biz.dto.base;

public class ConsumerQueueVersionDto {
	private long queueOffsetId;
	private volatile long offsetVersion;
	private volatile long offset;
	private String consumerGroupName;
	private String topicName;

	public long getQueueOffsetId() {
		return queueOffsetId;
	}

	public void setQueueOffsetId(long queueOffsetId) {
		this.queueOffsetId = queueOffsetId;
	}

	public long getOffsetVersion() {
		return offsetVersion;
	}

	public void setOffsetVersion(long offsetVersion) {
		this.offsetVersion = offsetVersion;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

}
