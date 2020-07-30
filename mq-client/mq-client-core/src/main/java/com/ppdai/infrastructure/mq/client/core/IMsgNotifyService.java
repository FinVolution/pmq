package com.ppdai.infrastructure.mq.client.core;

import com.ppdai.infrastructure.mq.biz.dto.client.MsgNotifyRequest;

public interface IMsgNotifyService {
	void notify(MsgNotifyRequest request);
}
