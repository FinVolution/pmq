package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class GetConsumerGroupResponse extends BaseResponse {

	private long sleepTime;
	//key 为消费者组名称
	private Map<String, ConsumerGroupOneDto> consumerGroups;
	
	private int consumerDeleted;

	private int brokerMetaMode;	

	public int getBrokerMetaMode() {
		return brokerMetaMode;
	}

	public void setBrokerMetaMode(int brokerMetaMode) {
		this.brokerMetaMode = brokerMetaMode;
	}

	public int getConsumerDeleted() {
		return consumerDeleted;
	}

	public void setConsumerDeleted(int consumerDeleted) {
		this.consumerDeleted = consumerDeleted;
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public Map<String, ConsumerGroupOneDto> getConsumerGroups() {
		return consumerGroups;
	}

	public void setConsumerGroups(Map<String, ConsumerGroupOneDto> consumerGroups) {
		this.consumerGroups = consumerGroups;
	}
}
