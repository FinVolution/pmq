package com.ppdai.infrastructure.mq.client.core;

import java.util.Map;

public interface IConsumerPollingService extends IMqClientService{
	Map<String, IMqGroupExcutorService> getMqExcutors();
}
