package com.ppdai.infrastructure.mq.client;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

@Component
@Qualifier("MqClientSpringUtil")
public class MqSpringUtil implements BeanFactoryPostProcessor, PriorityOrdered, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (MqSpringUtil.applicationContext == null) {
			MqSpringUtil.applicationContext = applicationContext;
		}
	}

	// 获取applicationContext
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	// 通过name获取 Bean.
	public static Object getBean(String name) {
		try {
			if (applicationContext != null) {
				return getApplicationContext().getBean(name);
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	// 通过class获取Bean.
	public static <T> T getBean(Class<T> clazz) {
		try {
			if (applicationContext != null) {
				return getApplicationContext().getBean(clazz);
			}
		} catch (Exception e) {

		}
		return null;

	}

	// 通过class获取Bean.
	public static <T> Map<String, T> getBeans(Class<T> clazz) {
		try {
			if (applicationContext != null) {
				return getApplicationContext().getBeansOfType(clazz);
			}
		} catch (Exception e) {

		}
		return null;

	}

	// 通过name,以及Clazz返回指定的Bean
	public static <T> T getBean(String name, Class<T> clazz) {
		try {
			if (applicationContext != null) {
				return getApplicationContext().getBean(name, clazz);
			}
		} catch (Exception e) {

		}
		return null;

	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub

	}

}