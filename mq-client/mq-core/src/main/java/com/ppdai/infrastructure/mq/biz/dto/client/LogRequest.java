package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class LogRequest extends BaseRequest {
	private long queueOffsetId;
	private String consumerGroupName;
	private long consumerGroupId;
	private String consumerName;
	private String topicName;
	private long queueId;
	private String traceId;
	private String bizId;
	private String action;
	private String msg;
	/*
	 * 日志类型，1 表示error，2，warn，3，info，4，debug
	 */
	private int type;

	public long getQueueOffsetId() {
		return queueOffsetId;
	}

	public void setQueueOffsetId(long queueOffsetId) {
		this.queueOffsetId = queueOffsetId;
	}

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}

	public long getConsumerGroupId() {
		return consumerGroupId;
	}

	public void setConsumerGroupId(long consumerGroupId) {
		this.consumerGroupId = consumerGroupId;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public long getQueueId() {
		return queueId;
	}

	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
