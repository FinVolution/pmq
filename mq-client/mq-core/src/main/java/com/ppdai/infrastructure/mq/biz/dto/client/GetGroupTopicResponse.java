package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class GetGroupTopicResponse extends BaseResponse {

	private List<GroupTopicDto> groupTopics;

	public List<GroupTopicDto> getGroupTopics() {
		return groupTopics;
	}

	public void setGroupTopics(List<GroupTopicDto> groupTopics) {
		this.groupTopics = groupTopics;
	}
}
