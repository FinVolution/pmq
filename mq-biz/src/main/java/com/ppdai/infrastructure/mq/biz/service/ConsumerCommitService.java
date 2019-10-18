package com.ppdai.infrastructure.mq.biz.service;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetResponse;

public interface ConsumerCommitService {
	CommitOffsetResponse commitOffset(CommitOffsetRequest request);
	Map<Long, ConsumerQueueVersionDto> getCache();
}
