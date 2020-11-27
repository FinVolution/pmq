package com.ppdai.infrastructure.mq.biz.dto.client;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class HeartbeatResponse extends BaseResponse {
	private int heatbeatTime;
	private int deleted = 0;
	private String bakUrl="";
	public String getBakUrl() {
		return bakUrl;
	}

	public void setBakUrl(String bakUrl) {
		this.bakUrl = bakUrl;
	}
	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}


	public int getHeatbeatTime() {
		return heatbeatTime;
	}

	public void setHeatbeatTime(int heatbeatTime) {
		this.heatbeatTime = heatbeatTime;
	}
}
