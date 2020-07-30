package com.ppdai.infrastructure.mq.client.core;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;

public interface IMqQueueExcutorService extends IMqClientService {
	void updateQueueMeta(ConsumerQueueDto consumerQueue);
	void notifyMsg();
	void commit(List<MessageDto> failMsgs,ConsumerQueueDto consumerQueue);
}
