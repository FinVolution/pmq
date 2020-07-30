package com.ppdai.infrastructure.mq.client.bootstrap;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.client.MqClient;

@Component
public class MqStartProcessor implements BeanFactoryPostProcessor, PriorityOrdered, EnvironmentAware {
	private static final Logger logger = LoggerFactory.getLogger(MqStartProcessor.class);
	protected static AtomicBoolean initFlag = new AtomicBoolean(false);
	private Environment environment;

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return Ordered.HIGHEST_PRECEDENCE+2;
	}	

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;

	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (environment != null) { 
			if (initFlag.compareAndSet(false, true)) {	
				logger.info("消息客户端开始初始化！");
				MqClient.setSubscriberResolver(new SubscriberResolver());
				MqClientStartup.init(environment);
				//MqClientStartup.start();
				// statService.start();
				logger.info("消息客户端初始化完成！");
			}
		}
	}

}
