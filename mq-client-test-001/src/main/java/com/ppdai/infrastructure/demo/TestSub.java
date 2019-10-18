package com.ppdai.infrastructure.demo;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;

public class TestSub implements com.ppdai.infrastructure.mq.biz.event.ISubscriber {


	public TestSub() {
		
	}

	@Override
	public List<Long> onMessageReceived(List<MessageDto> messages) {
		// TODO Auto-generated method stub
		return null;
	}

}
