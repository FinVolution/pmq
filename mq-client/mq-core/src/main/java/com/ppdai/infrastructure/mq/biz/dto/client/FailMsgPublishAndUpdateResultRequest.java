package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;

//发送失败消息和更新重试执行结果
public class FailMsgPublishAndUpdateResultRequest extends BaseRequest {
	// 消息的id
	private List<Long> ids;

	// 对应的partionid
	private long queueId;

	private PublishMessageRequest failMsg;

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public long getQueueId() {
		return queueId;
	}

	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}

	public PublishMessageRequest getFailMsg() {
		return failMsg;
	}

	public void setFailMsg(PublishMessageRequest failMsg) {
		this.failMsg = failMsg;
	}

}
