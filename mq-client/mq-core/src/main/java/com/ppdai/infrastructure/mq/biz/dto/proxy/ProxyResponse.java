package com.ppdai.infrastructure.mq.biz.dto.proxy;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class ProxyResponse extends BaseResponse {
	private List<Long> failIds;

	public List<Long> getFailIds() {
		return failIds;
	}
	public void setFailIds(List<Long> failIds) {
		this.failIds = failIds;
	}
}
