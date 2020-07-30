package com.ppdai.infrastructure.mq.client.resolver;

import com.ppdai.infrastructure.mq.biz.event.IAsynSubscriber;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;

/*
 * 次类与ISubscriberSelector的区别是,此类是根据配置的全路径类获取相关的类,ISubscriberSelector是在运行过程中动态决定的消费类,
 * 一个是在初始化时决定,一个是在运行过程中决定. 
 * */
public interface ISubscriberResolver {
	
	IAsynSubscriber getAsynSubscriber(String className) throws Exception;

	ISubscriber getSubscriber(String className) throws Exception;
		
}
