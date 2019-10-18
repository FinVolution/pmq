package com.ppdai.infrastructure.mq.biz.event;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;

public interface ISubscriber {
	List<Long> onMessageReceived(List<MessageDto> messages);
}
