package com.ppdai.infrastructure.proxy.vo;

public class ConsumerGroupIpUrlVo {
	private String ipLst;
	private String exeUrl;
	private String hsUrl;
	private int count = 1;

	public String getIpLst() {
		return ipLst;
	}

	public void setIpLst(String ipLst) {
		this.ipLst = ipLst;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getExeUrl() {
		return exeUrl;
	}

	public void setExeUrl(String exeUrl) {
		this.exeUrl = exeUrl;
	}

	public String getHsUrl() {
		return hsUrl;
	}

	public void setHsUrl(String hsUrl) {
		this.hsUrl = hsUrl;
	}
}
