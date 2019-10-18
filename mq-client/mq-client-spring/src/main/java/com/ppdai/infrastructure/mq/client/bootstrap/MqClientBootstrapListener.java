package com.ppdai.infrastructure.mq.client.bootstrap;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class MqClientBootstrapListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {	
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {		
		MqClientStartup.springInitComplete();		
	}
}
