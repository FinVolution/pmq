package com.ppdai.infrastructure.mq.biz.polling;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageService;

/**
 * 内置消息定时清理服务
 *
 */
@Component
public class NotifyMessageCleanService extends AbstractTimerService {
	private Logger log = LoggerFactory.getLogger(NotifyMessageCleanService.class);

	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private NotifyMessageService notifyMessageService;

	@PostConstruct
	private void init() {
		super.init(Constants.NOTIFY_CLEAN_LOCK, soaConfig.getCleanInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getCleanInterval();
			@Override
			public void run() {
				if (soaConfig.getCleanInterval() != interval) {
					interval = soaConfig.getCleanInterval();
					updateInterval(interval);
				}
			}
		});
	}

	@Override
	public void doStart() {
		long minId = notifyMessageService.getMinId();
		if (minId > 0 && super.isMaster&&soaConfig.isEnbaleNotifyMessageClean()) {
			log.info("clear_old_data_minId_is_{}_and_maxId_is_{}", minId, minId + 500);
			int count = notifyMessageService.clearOld(soaConfig.getCleanInterval(), minId + 500);
			while (count > 0 && super.isMaster) {
				minId = notifyMessageService.getMinId();
				log.info("clear_old_data_minId_is_{}_and_maxId_is_{}", minId, minId + 500);
				count = notifyMessageService.clearOld(soaConfig.getCleanInterval(), minId + 500);
				Util.sleep(3000);
			}
		}
	}

	@PreDestroy
	public void stopPortal() {
		super.stopPortal();
	}
}
