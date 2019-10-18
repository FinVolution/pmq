package com.ppdai.infrastructure.rest.mq.boot;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.cache.ConsumerGroupCacheService;
import com.ppdai.infrastructure.mq.biz.common.inf.BrokerTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.ConsumerGroupChangedListener;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;


@Component
public class MqBootstrapListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {
	private static final Logger log = LoggerFactory.getLogger(MqBootstrapListener.class);
	private static boolean isInit = false;
	@Autowired
	private ConsumerGroupCacheService consumerGroupCacheService;	
	@Autowired
	private ReportService reportService;
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!isInit) {
			try {
				startTimer();
				startBrokeTimer();
				registerEvent();
				reportService.registerReport();
				isInit = true;
				log.info("mq初始化成功！");
			} catch (Exception e) {
				log.error("mq初始化异常", e);
				throw e;
			}
		}

	}

	private void startBrokeTimer() {
		Map<String, BrokerTimerService> startedServices = SpringUtil.getBeans(BrokerTimerService.class);
		if (startedServices != null) {
			startedServices.entrySet().forEach(t1 -> {
				try {
					t1.getValue().startBroker();
					log.info(t1.getKey() + "启动完成！");
				} catch (Exception e) {
					log.error(t1.getKey() + "启动异常！", e);
				}
			});
		}
		
	}

	private void registerEvent() {
		Map<String, ConsumerGroupChangedListener> dataMap = SpringUtil.getBeans(ConsumerGroupChangedListener.class);
		if (dataMap != null) {
			dataMap.entrySet().forEach(t1 -> {
				try {
					consumerGroupCacheService.addListener(t1.getValue());
					log.info(t1.getKey() + "注册成功！");
				} catch (Exception e) {
					log.error(t1.getKey() + "注册异常！", e);
				}
			});
		}
	}

	private void startTimer() {
		Map<String, TimerService> startedServices = SpringUtil.getBeans(TimerService.class);
		if (startedServices != null) {
			startedServices.entrySet().forEach(t1 -> {
				try {
					t1.getValue().start();
					log.info(t1.getKey() + "启动完成！");
				} catch (Exception e) {
					log.error(t1.getKey() + "启动异常！", e);
				}
			});
		}
	}

}
