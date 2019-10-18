package com.ppdai.infrastructure.mq.client.core;

import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;

public interface IMqGroupExcutorService extends IMqClientService {
	void rbOrUpdate(ConsumerGroupOneDto consumerGroupOne, String serverIp);
}
