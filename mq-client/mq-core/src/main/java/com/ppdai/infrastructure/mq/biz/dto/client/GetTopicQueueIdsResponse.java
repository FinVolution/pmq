package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class GetTopicQueueIdsResponse extends BaseResponse {
	private Map<String, List<Long>> topicQueues;

	public Map<String, List<Long>> getTopicQueues() {
		return topicQueues;
	}

	public void setTopicQueues(Map<String, List<Long>> topicQueues) {
		this.topicQueues = topicQueues;
	}
}
