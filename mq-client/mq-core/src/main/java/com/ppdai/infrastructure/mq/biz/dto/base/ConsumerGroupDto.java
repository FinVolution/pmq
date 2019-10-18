package com.ppdai.infrastructure.mq.biz.dto.base;

import java.util.Map;

public class ConsumerGroupDto {

	private ConsumerGroupMetaDto meta;
	
	/*
	 * key 为consumerId，value 为 queueId
	 */
	private Map<Long, Map<Long,ConsumerQueueDto>> consumers;

	public ConsumerGroupMetaDto getMeta() {
		return meta;
	}

	public void setMeta(ConsumerGroupMetaDto meta) {
		this.meta = meta;
	}

	public Map<Long, Map<Long, ConsumerQueueDto>> getConsumers() {
		return consumers;
	}

	public void setConsumers(Map<Long, Map<Long, ConsumerQueueDto>> consumers) {
		this.consumers = consumers;
	}

	
}
