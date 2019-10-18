package com.ppdai.infrastructure.mq.biz.event;

public interface ISubscriberSelector {
	ISubscriber getSubscriber(String consumerGroupName, String topic);
}
