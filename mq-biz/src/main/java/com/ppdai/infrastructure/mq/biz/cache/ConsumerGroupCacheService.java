package com.ppdai.infrastructure.mq.biz.cache;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.inf.BrokerTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.ConsumerGroupChangedListener;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupDto;

@Service
public interface ConsumerGroupCacheService extends BrokerTimerService {
	void addListener(ConsumerGroupChangedListener listener);

	Map<String, ConsumerGroupDto> getCache();
}
