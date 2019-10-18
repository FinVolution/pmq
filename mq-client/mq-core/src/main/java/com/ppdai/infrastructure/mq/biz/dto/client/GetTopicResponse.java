package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class GetTopicResponse extends BaseResponse{

	private List<String> topics;

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
}
