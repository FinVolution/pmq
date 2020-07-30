package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

public class MsgNotifyRequest extends BaseRequest {
	private List<MsgNotifyDto> msgNotifyDtos;

	public List<MsgNotifyDto> getMsgNotifyDtos() {
		return msgNotifyDtos;
	}

	public void setMsgNotifyDtos(List<MsgNotifyDto> msgNotifyDtos) {
		this.msgNotifyDtos = msgNotifyDtos;
	}
}
