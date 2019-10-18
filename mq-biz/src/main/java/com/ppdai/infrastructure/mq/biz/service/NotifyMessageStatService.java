package com.ppdai.infrastructure.mq.biz.service;

import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageStatEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */

public interface NotifyMessageStatService extends BaseService<NotifyMessageStatEntity> {	 
	 NotifyMessageStatEntity initNotifyMessageStat();
	 NotifyMessageStatEntity get();
	 void updateNotifyMessageId();
}
