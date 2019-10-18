package com.ppdai.infrastructure.mq.client.core;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;

public interface IMqQueueExcutorService extends IMqClientService {
	void updateQueueMeta(ConsumerQueueDto consumerQueue);
}
