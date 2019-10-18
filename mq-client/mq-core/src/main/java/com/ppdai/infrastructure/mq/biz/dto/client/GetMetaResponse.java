package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class GetMetaResponse extends BaseResponse {
	private List<String> brokerIp;
	private int brokerMetaMode;

	public int getBrokerMetaMode() {
		return brokerMetaMode;
	}

	public void setBrokerMetaMode(int brokerMetaMode) {
		this.brokerMetaMode = brokerMetaMode;
	}

	public List<String> getBrokerIp() {
		return brokerIp;
	}

	public void setBrokerIp(List<String> brokerIp) {
		this.brokerIp = brokerIp;
	}
}
