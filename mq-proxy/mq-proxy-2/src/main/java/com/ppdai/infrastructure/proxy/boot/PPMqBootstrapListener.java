package com.ppdai.infrastructure.proxy.boot;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.biz.event.ISubscriberSelector;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.proxy.ProxyService;
import com.ppdai.infrastructure.proxy.ProxySub;
import com.ppdai.infrastructure.proxy.service.HsCheckService;

@Component
public class PPMqBootstrapListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {
	private static final Logger log = LoggerFactory.getLogger(PPMqBootstrapListener.class);
	private static AtomicBoolean isInit = new AtomicBoolean(false);
	@Autowired
	private ReportService reportService;
	@Autowired
	private HsCheckService hsCheckService;

	@Autowired
	private ProxyService proxyService;

	@Autowired
	private ProxySub proxySub;

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (isInit.compareAndSet(false, true)) {
			try {
				reportService.registerReport();
				MqClient.registerPreHandleEvent(hsCheckService);
				MqClient.registerISubscriberSelector(new ISubscriberSelector() {
					@Override
					public ISubscriber getSubscriber(String consumerGroupName, String topic) {
						// TODO Auto-generated method stub
						return proxySub;
					}

				});
				proxyService.start();				
				log.info("mq初始化成功！");
			} catch (Exception e) {
				log.error("mq初始化异常", e);
				throw e;
			}
		}

	}

}
