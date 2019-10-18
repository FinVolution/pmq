package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class GetTopicQueueIdsRequest extends BaseRequest{
	private List<String> topicNames;

	public List<String> getTopicNames() {
		return topicNames;
	}

	public void setTopicNames(List<String> topicNames) {
		this.topicNames = topicNames;
	}
}
