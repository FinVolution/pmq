package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class HeartbeatRequest extends BaseRequest {
	private long consumerId;
	private List<Long> consumerIds;
	private int asyn = 1;

	public int getAsyn() {
		return asyn;
	}

	public void setAsyn(int asyn) {
		this.asyn = asyn;
	}


	public List<Long> getConsumerIds() {
		return consumerIds;
	}

	public void setConsumerIds(List<Long> consumerIds) {
		this.consumerIds = consumerIds;
	}

	public long getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(long consumerId) {
		this.consumerId = consumerId;
	}
}
