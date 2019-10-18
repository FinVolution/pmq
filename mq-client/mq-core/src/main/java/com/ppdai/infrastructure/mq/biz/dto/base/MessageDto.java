package com.ppdai.infrastructure.mq.biz.dto.base;

import java.util.Date;
import java.util.Map;

public class MessageDto extends ProducerDataDto {	
	private long id;
	//注意此对象是消费的时候赋值的，在broker服务端没有用
	private String topicName;
	//注意此对象是消费的时候赋值的，在broker服务端没有用
	private String consumerGroupName;	
	// 失败尝试次数，默认是0
	private int retryCount;
	private String sendIp;	
	// yyyy-MM-dd HH:mm:ss:SSS
	private Date sendTime;
	public MessageDto() {

	}

	public MessageDto(String bizId, String body) {
		this(bizId, "", null, body);
	}

	public MessageDto(String body) {
		this("", "", null, body);
	}

	public MessageDto(String bizId, String body, String tag) {
		this(bizId, tag, null, body);
	}

	public MessageDto(String bizId, String tag, Map<String, String> header, String body) {
		super.setBizId(bizId);
		super.setTag(tag);
		super.setHead(header);
		super.setBody(body);
	}

	public MessageDto(String bizId, String tag, Map<String, String> header, String body, String sendIp) {
		super.setBizId(bizId);
		super.setTag(tag);
		super.setHead(header);
		super.setBody(body);
		setSendIp(sendIp);
	}

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
	
	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getSendIp() {
		return sendIp;
	}

	public void setSendIp(String sendIp) {
		this.sendIp = sendIp;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
}
