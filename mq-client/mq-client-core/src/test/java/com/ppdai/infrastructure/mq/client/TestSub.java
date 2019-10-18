package com.ppdai.infrastructure.mq.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;

public class TestSub implements ISubscriber{

	@Override
	public List<Long> onMessageReceived(List<MessageDto> messages) {
		// TODO Auto-generated method stub
		return null;
	}

}
