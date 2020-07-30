package com.ppdai.infrastructure.mq.biz.service;

import com.ppdai.infrastructure.mq.biz.entity.MqLockEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */

public interface MqLockService extends BaseService<MqLockEntity> {
	boolean isMaster();
	boolean updateHeatTime();
	boolean isInLock();
}
