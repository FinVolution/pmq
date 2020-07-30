package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class SendMailRequest extends BaseRequest {
	private String topicName;
	private String consumerGroupName;
	/*
	 * 0,表示info，1，表示warn，2，表示error
	 * */
	private int type;
	private String subject;
	private String content;
	//用来alter聚合
	private String key;
	//表示是否是服务端错误,服务端错误只有管理员才会收到
	private boolean server=false;
	public boolean isServer() {
		return server;
	}
	public void setServer(boolean server) {
		this.server = server;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
