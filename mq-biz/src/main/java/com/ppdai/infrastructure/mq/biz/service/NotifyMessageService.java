package com.ppdai.infrastructure.mq.biz.service;

import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */

public interface NotifyMessageService extends BaseService<NotifyMessageEntity> {

	long getDataMaxId(long maxId1);

	long getDataMaxId();

	long getDataMinId();

	long getRbMaxId(long maxId1);	

	long getRbMaxId();

	long getRbMinId();

	int clearOld(long clearOldTime, long maxId);

	long getMinId();
}
