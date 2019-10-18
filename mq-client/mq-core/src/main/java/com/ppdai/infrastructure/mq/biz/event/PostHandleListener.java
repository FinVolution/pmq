package com.ppdai.infrastructure.mq.biz.event;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;

public interface PostHandleListener {
	void postHandle(ConsumerQueueDto consumerQueue,Boolean isSuc);
}
