package com.ppdai.infrastructure.mq.client;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupMetaDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;

public abstract class AbstractTest {
	public static String consumerGroupName = "TestSub";
	public static String topicName = "test";

	public static ConsumerGroupOneDto buildModifyConsumerGroupOne() {
		ConsumerGroupOneDto consumerGroupOneDto = new ConsumerGroupOneDto();
		ConsumerGroupMetaDto meta = new ConsumerGroupMetaDto();
		meta.setMetaVersion(11L);
		meta.setRbVersion(11L);
		meta.setVersion(1L);
		meta.setName(consumerGroupName);
		consumerGroupOneDto.setMeta(meta);
		Map<Long, ConsumerQueueDto> queues = new HashMap<Long, ConsumerQueueDto>();
		ConsumerQueueDto consumerQueueDto = buildModifyConsumerQueueDto();
		queues.put(1L, consumerQueueDto);
		consumerGroupOneDto.setQueues(queues);
		return consumerGroupOneDto;
	}

	public static ConsumerQueueDto buildModifyConsumerQueueDto() {
		ConsumerQueueDto consumerQueueDto = new ConsumerQueueDto();
		consumerQueueDto.setConsumerBatchSize(11);
		consumerQueueDto.setConsumerGroupName(consumerGroupName);
		consumerQueueDto.setDelayProcessTime(11);
		consumerQueueDto.setLastId(0L);
		consumerQueueDto.setMaxPullTime(11);
		consumerQueueDto.setOffset(11L);
		consumerQueueDto.setOffsetVersion(11L);
		consumerQueueDto.setOriginTopicName(topicName);
		consumerQueueDto.setPullBatchSize(11);
		consumerQueueDto.setQueueId(1L);
		consumerQueueDto.setQueueOffsetId(11L);
		consumerQueueDto.setRetryCount(11);
		consumerQueueDto.setStopFlag(1);
		consumerQueueDto.setTag("11");
		consumerQueueDto.setThreadSize(110);
		consumerQueueDto.setTimeout(11);
		consumerQueueDto.setTopicId(11);
		consumerQueueDto.setTopicName(topicName);
		consumerQueueDto.setTopicType(11);
		consumerQueueDto.setTraceFlag(11);
		return consumerQueueDto;
	}

	public static ConsumerGroupOneDto buildConsumerGroupOne() {
		ConsumerGroupOneDto consumerGroupOneDto = new ConsumerGroupOneDto();
		ConsumerGroupMetaDto meta = new ConsumerGroupMetaDto();
		meta.setMetaVersion(1L);
		meta.setRbVersion(1L);
		meta.setVersion(1L);
		meta.setName(consumerGroupName);
		consumerGroupOneDto.setMeta(meta);
		Map<Long, ConsumerQueueDto> queues = new HashMap<Long, ConsumerQueueDto>();
		ConsumerQueueDto consumerQueueDto = buildDefaultConsumerQueueDto();
		queues.put(1L, consumerQueueDto);
		consumerGroupOneDto.setQueues(queues);
		return consumerGroupOneDto;
	}

	public static ConsumerQueueDto buildDefaultConsumerQueueDto() {
		ConsumerQueueDto consumerQueueDto = new ConsumerQueueDto();
		consumerQueueDto.setConsumerBatchSize(1);
		consumerQueueDto.setConsumerGroupName(consumerGroupName);
		consumerQueueDto.setDelayProcessTime(1);
		consumerQueueDto.setLastId(1L);
		consumerQueueDto.setMaxPullTime(1);
		consumerQueueDto.setOffset(10L);
		consumerQueueDto.setOffsetVersion(1L);
		consumerQueueDto.setOriginTopicName(topicName);
		consumerQueueDto.setPullBatchSize(1);
		consumerQueueDto.setQueueId(1L);
		consumerQueueDto.setQueueOffsetId(1L);
		consumerQueueDto.setRetryCount(1);
		consumerQueueDto.setStopFlag(0);
		consumerQueueDto.setTag("1");
		consumerQueueDto.setThreadSize(10);
		consumerQueueDto.setTimeout(1);
		consumerQueueDto.setTopicId(1);
		consumerQueueDto.setTopicName(topicName);
		consumerQueueDto.setTopicType(1);
		consumerQueueDto.setTraceFlag(1);
		return consumerQueueDto;
	}
	
	public static <T> T getValue(String fieldName, Object object) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Object rs = field.get(object);
			//field.setAccessible(false);
			return (T)rs;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void setValue(String fieldName,Object value, Object object) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
			//field.setAccessible(false);
			
		} catch (Exception e) {
			
		}
	}
}
