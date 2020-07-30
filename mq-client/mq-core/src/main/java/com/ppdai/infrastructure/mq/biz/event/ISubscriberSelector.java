package com.ppdai.infrastructure.mq.biz.event;

/*
 * 次类与ISubscriberResolver的区别是,此类是运行时获取相关的类, ISubscriberResolver是在初始化时决定的消费类,
 * 一个是在初始化时决定,一个是在运行过程中决定. 
 * */
public interface ISubscriberSelector {
	ISubscriber getSubscriber(String consumerGroupName, String topic);
}
