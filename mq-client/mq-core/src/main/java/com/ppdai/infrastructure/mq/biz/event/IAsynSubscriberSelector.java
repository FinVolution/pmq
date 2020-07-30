package com.ppdai.infrastructure.mq.biz.event;

public interface IAsynSubscriberSelector {
	IAsynSubscriber getSubscriber(String consumerGroupName, String topic);
}
