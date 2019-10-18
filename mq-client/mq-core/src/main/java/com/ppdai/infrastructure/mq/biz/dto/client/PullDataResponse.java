package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;

public class PullDataResponse extends BaseResponse {
	private List<MessageDto> msgs;
	public List<MessageDto> getMsgs() {
		return msgs;
	}

	public void setMsgs(List<MessageDto> msgs) {
		this.msgs = msgs;
	}

}
