package com.ppdai.infrastructure.mq.biz.service;

import com.ppdai.infrastructure.mq.biz.dto.LogDto;
import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;

public interface LogService {
	void addConsumerLog(LogRequest request);
	void addBrokerLog(LogDto request);
	void addOpLog(OpLogRequest request);
}
