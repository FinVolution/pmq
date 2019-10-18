package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class ConsumerRegisterRequest extends BaseRequest {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
