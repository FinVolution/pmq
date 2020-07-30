package com.ppdai.infrastructure.mq.biz.event;

import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
//如果返回true 表示 继续处理消息，返回false表示过滤此条消息
public interface IMsgFilter {
	boolean onMsgFilter(MessageDto messageDto);
}
