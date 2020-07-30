package com.ppdai.infrastructure.mq.biz.event;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;

public interface IAsynSubscriber {
	void onMessageReceived(List<MessageDto> messages,ConsumerQueueDto consumerQueue);
}
