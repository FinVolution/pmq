package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupMetaDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;

public class ConsumerGroupOneDto {
	private ConsumerGroupMetaDto meta;
	//key为queueid
	private Map<Long, ConsumerQueueDto> queues;

	public ConsumerGroupMetaDto getMeta() {
		return meta;
	}

	public void setMeta(ConsumerGroupMetaDto meta) {
		this.meta = meta;
	}

	public Map<Long, ConsumerQueueDto> getQueues() {
		return queues;
	}

	public void setQueues(Map<Long, ConsumerQueueDto> queues) {
		this.queues = queues;
	}

}
