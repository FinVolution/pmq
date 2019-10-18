package com.ppdai.infrastructure.mq.biz.service.common;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;

@Component
public class MqCacheRebuildService {

	private volatile int cacheCount = 0;

	@Autowired
	private SoaConfig soaConfig;

	@PostConstruct
	private void init() {
		cacheCount = soaConfig.getCacheRebuild();
		soaConfig.registerChanged(new Runnable() {
			@Override
			public void run() {
				if (cacheCount != soaConfig.getCacheRebuild()) {
					cacheCount = soaConfig.getCacheRebuild();
					 Map<String, CacheUpdateService> cacheUpdateServices = SpringUtil.getBeans(CacheUpdateService.class);
			            if (cacheUpdateServices != null) {
			                cacheUpdateServices.values().forEach(t1 -> {
			                    t1.forceUpdateCache();
			                });
			            }
				}

			}
		});

	}
}
