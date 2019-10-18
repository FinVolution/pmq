package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;

public class CommitOffsetRequest extends BaseRequest {

	private List<ConsumerQueueVersionDto> queueOffsets;
	private int flag = 0;

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public List<ConsumerQueueVersionDto> getQueueOffsets() {
		return queueOffsets;
	}

	public void setQueueOffsets(List<ConsumerQueueVersionDto> queueOffsets) {
		this.queueOffsets = queueOffsets;
	}

}
