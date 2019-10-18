package com.ppdai.infrastructure.mq.client.event;

import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;

public  interface RegisterConsumerGroupListener {
	void complete(ConsumerGroupVo consumerGroupVo);
}