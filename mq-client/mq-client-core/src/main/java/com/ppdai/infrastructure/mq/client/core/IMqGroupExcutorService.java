package com.ppdai.infrastructure.mq.client.core;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;

public interface IMqGroupExcutorService extends IMqClientService {
	void rbOrUpdate(ConsumerGroupOneDto consumerGroupOne, String serverIp);
	Map<Long, IMqQueueExcutorService> getQueueEx();
}
