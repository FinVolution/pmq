package com.ppdai.infrastructure.mq.client.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.client.MqSpringUtil;
import com.ppdai.infrastructure.mq.client.stat.MqClientStatController;

@Component
public class MqClientBootstrapListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {
	private static final Logger logger = LoggerFactory.getLogger(MqClientBootstrapListener.class);

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (MqSpringUtil.getBean(MqClientStatController.class) == null) {
			logger.warn("当前项目是非spring web项目，建议引入spring web，方便更完善的监控。<groupId>org.springframework</groupId>\r\n"
					+ "			<artifactId>spring-web</artifactId>");
		}
		MqClientStartup.springInitComplete();
	}
}
