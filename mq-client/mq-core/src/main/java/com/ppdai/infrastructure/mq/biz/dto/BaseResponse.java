package com.ppdai.infrastructure.mq.biz.dto;

import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;

public class BaseResponse {
	private boolean isSuc;
	private String code;
	private String msg;
	//客户端处理耗时
	private long time;	
	//处理的服务端ip
	private String serverIp=IPUtil.getLocalIP();
	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public boolean isSuc() {
		return isSuc;
	}

	public void setSuc(boolean isSuc) {
		this.isSuc = isSuc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
}
