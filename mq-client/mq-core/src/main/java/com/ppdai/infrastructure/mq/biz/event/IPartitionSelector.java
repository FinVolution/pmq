package com.ppdai.infrastructure.mq.biz.event;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;

public interface IPartitionSelector {
	//如果没有返回0
	PartitionInfo getPartitionId(String topic,ProducerDataDto message,List<Long> partitionIds);
}
