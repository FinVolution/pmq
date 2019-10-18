package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

/**
 * 获取topic或者consumergroup剩余的消息的数量
 * 
 */
public class GetMessageCountRequest extends BaseRequest {

	private String consumerGroupName;	

	private List<String> topics;

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	
}
