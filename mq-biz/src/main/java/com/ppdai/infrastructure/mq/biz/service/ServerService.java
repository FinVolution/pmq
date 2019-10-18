package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */
public interface ServerService extends BaseService<ServerEntity> {
	List<String> getBrokerUrlCache();

	List<String> getPortalCache();
}
