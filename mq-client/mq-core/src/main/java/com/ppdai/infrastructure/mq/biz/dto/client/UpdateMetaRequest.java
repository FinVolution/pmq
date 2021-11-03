package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class UpdateMetaRequest extends BaseRequest{
	private List<String> consumerGroupNames;

	public List<String> getConsumerGroupNames() {
		return consumerGroupNames;
	}

	public void setConsumerGroupNames(List<String> consumerGroupNames) {
		this.consumerGroupNames = consumerGroupNames;
	}

}
