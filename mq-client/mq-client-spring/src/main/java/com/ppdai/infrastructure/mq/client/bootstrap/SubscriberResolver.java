package com.ppdai.infrastructure.mq.client.bootstrap;

import com.ppdai.infrastructure.mq.biz.event.IAsynSubscriber;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.client.MqSpringUtil;
import com.ppdai.infrastructure.mq.client.resolver.ISubscriberResolver;

public class SubscriberResolver implements ISubscriberResolver {

	@Override
	public IAsynSubscriber getAsynSubscriber(String className) throws Exception {
		Class<IAsynSubscriber> onwClass = (Class<IAsynSubscriber>) Class.forName(className);
		IAsynSubscriber iAsynSubscriber = (IAsynSubscriber) MqSpringUtil.getBean(className);
		if (iAsynSubscriber == null) {
			if (IAsynSubscriber.class.isAssignableFrom(onwClass)) {
				iAsynSubscriber = (IAsynSubscriber) onwClass.newInstance();
			}
		}
		if (iAsynSubscriber == null) {
			throw new Exception(className + " 不存在!");
		}
		return iAsynSubscriber;
	}

	@Override
	public ISubscriber getSubscriber(String className) throws Exception {
		Class<ISubscriber> onwClass = (Class<ISubscriber>) Class.forName(className);
		ISubscriber iSubscriber = (ISubscriber) (MqSpringUtil.getBean(onwClass));
		if (iSubscriber == null) {
			if (ISubscriber.class.isAssignableFrom(onwClass)) {
				iSubscriber = (ISubscriber) onwClass.newInstance();
			}
		}
		if (iSubscriber == null) {
			throw new Exception(className + " 不存在!");
		}
		return iSubscriber;
	}
}
