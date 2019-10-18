package com.ppdai.infrastructure.mq.biz.event;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;

public interface PreHandleListener {
	//如返回false则表示暂停当前消费
    boolean preHandle(ConsumerQueueDto consumerQueue);
}
