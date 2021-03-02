package com.ppdai.infrastructure.mq.client.core;

import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.client.dto.TraceMessageDto;

public interface IMqQueueExcutorService extends IMqClientService {
    void updateQueueMeta(ConsumerQueueDto consumerQueue);

    void notifyMsg();

    void commit(List<MessageDto> failMsgs, ConsumerQueueDto consumerQueue);

    Map<Long, TraceMessageDto> getSlowMsg();

    ConsumerQueueVersionDto getChangedCommit();

    ConsumerQueueVersionDto getLast();
    boolean hasFininshed();
    void stop();
}
