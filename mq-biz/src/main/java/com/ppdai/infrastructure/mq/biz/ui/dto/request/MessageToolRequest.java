package com.ppdai.infrastructure.mq.biz.ui.dto.request;

import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;

/**
 * @author: liujianjun02
 * @date: 2019/1/29
 */

public class MessageToolRequest extends BaseUiRequst {
	private String topicName;
	private int sendType;

	private MessageDto message;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public int getSendType() {
		return sendType;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
	}

	public MessageDto getMessage() {
		return message;
	}

	public void setMessage(MessageDto message) {
		this.message = message;
	}

}
