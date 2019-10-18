package com.ppdai.infrastructure.mq.biz.dto.proxy;

import java.util.Date;
import java.util.Map;

public class ProxyDto {
	private long id;
	private String topicName;
	private String consumerGroupName;
	//可能为空
	private String bizId;
	//可能为空
	private Map<String, String> head;
	private String body;
	//可能为空
	private String traceId;
	private String sendIp;
	// yyyy-MM-dd HH:mm:ss:SSS
	private Date insertTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public Map<String, String> getHead() {
		return head;
	}

	public void setHead(Map<String, String> head) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getSendIp() {
		return sendIp;
	}

	public void setSendIp(String sendIp) {
		this.sendIp = sendIp;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

}
