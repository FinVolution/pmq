package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

public class GroupTopicDto {

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
