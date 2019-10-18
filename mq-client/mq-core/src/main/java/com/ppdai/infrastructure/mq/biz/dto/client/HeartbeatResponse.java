package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class HeartbeatResponse extends BaseResponse {
	private int heatbeatTime;

	public int getHeatbeatTime() {
		return heatbeatTime;
	}

	public void setHeatbeatTime(int heatbeatTime) {
		this.heatbeatTime = heatbeatTime;
	}
}
