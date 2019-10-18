package com.ppdai.infrastructure.ui.boot;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.inf.PortalTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;

@Component
public class MqBootstrapListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {
	private static final Logger log = LoggerFactory.getLogger(MqBootstrapListener.class);
	private static boolean isInit = false;
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
				startPortalTimer();
				reportService.registerReport();
				isInit = true;
				log.info("mq初始化成功！");
			} catch (Exception e) {
				log.error("mq初始化异常", e);
				throw e;
			}
		}

	}

	

	private void startPortalTimer() {
		Map<String, PortalTimerService> startedServices = SpringUtil.getBeans(PortalTimerService.class);
		if (startedServices != null) {
			startedServices.entrySet().forEach(t1 -> {
				try {
					t1.getValue().startPortal();
					log.info(t1.getKey() + "启动完成！");
				} catch (Exception e) {
					log.error(t1.getKey() + "启动异常！", e);
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
