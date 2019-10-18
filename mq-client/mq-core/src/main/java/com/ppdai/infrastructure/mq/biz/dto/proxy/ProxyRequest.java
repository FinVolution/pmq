package com.ppdai.infrastructure.mq.biz.dto.proxy;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class ProxyRequest extends BaseRequest {
	private List<ProxyDto> msgs;

	public List<ProxyDto> getMsgs() {
		return msgs;
	}

	public void setMsgs(List<ProxyDto> msgs) {
		this.msgs = msgs;
	}
}
