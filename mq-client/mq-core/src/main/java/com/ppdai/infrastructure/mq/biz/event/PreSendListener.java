package com.ppdai.infrastructure.mq.biz.event;

import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;

//消息发送前事件
public interface PreSendListener {
	void onPreSend(ProducerDataDto producerDataDto);
}
