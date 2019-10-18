package com.ppdai.infrastructure.mq.client.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;

public class ConsumerGroupTopicVo {
	private String name;
	@JsonIgnore
	private transient ISubscriber subscriber;
	public ConsumerGroupTopicVo() {

	}

	public ConsumerGroupTopicVo(String name, ISubscriber subscriber) {
		this.name = name;
		this.subscriber = subscriber;
	}

	public ISubscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(ISubscriber isubscriber) {
		this.subscriber = isubscriber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
