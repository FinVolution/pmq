package com.ppdai.infrastructure.mq.client.core;

import java.util.List;

public interface IMqTopicQueueRefreshService extends IMqClientService{
	List<Long> getTopicQueueIds(String topicName) ;
}
