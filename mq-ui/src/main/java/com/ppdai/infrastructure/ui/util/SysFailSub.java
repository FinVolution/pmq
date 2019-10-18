package com.ppdai.infrastructure.ui.util;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.client.MqClient;

public class SysFailSub implements ISubscriber {	

	@Override
	public List<Long> onMessageReceived(List<MessageDto> messages) {
		messages.forEach(message->{
			MqClient.getContext().getMqResource().publish(JsonUtil.parseJson(message.getBody(), PublishMessageRequest.class));
		});		
		return null;
	}
}
