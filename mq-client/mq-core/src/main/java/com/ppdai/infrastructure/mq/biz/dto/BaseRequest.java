package com.ppdai.infrastructure.mq.biz.dto;

import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.PropUtil;

public class BaseRequest {	
	private String lan;
	private String sdkVersion;
	private String clientIp = IPUtil.getLocalIP();

	public BaseRequest(){
		lan="java";
		sdkVersion=PropUtil.getSdkVersion();
	}
	public String getLan() {
		return lan;
	}

	public void setLan(String lan) {
		this.lan = lan;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
}
