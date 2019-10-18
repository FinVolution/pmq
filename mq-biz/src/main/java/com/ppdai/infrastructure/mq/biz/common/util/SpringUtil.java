package com.ppdai.infrastructure.mq.biz.common.util;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (SpringUtil.applicationContext == null) {
			SpringUtil.applicationContext = applicationContext;
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

	public static Object getValue(String beanName, String fieldName) {
		Object object = getBean(beanName);
		if (object != null) {
			return getValue(fieldName, object);
		}
		return null;
	}

	private static Object getValue(String fieldName, Object object) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Object rs = field.get(object);
			field.setAccessible(false);
			return rs;
		} catch (Exception e) {
			return null;
		}
	}
}