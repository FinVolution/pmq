package com.ppdai.infrastructure.mq.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;

import java.util.Date;

public class TraceMessageDto {
    private String inTime;
    private String startTime;
    @JsonIgnore
    public transient long start;
    private long id;
    @JsonIgnore
    public transient MessageDto message;
    private String topic;
    private String group;
    private long queueId;

    public TraceMessageDto(MessageDto messageDto, ConsumerQueueDto queueDto) {
        message = messageDto;
        topic = queueDto.getTopicName();
        group = queueDto.getConsumerGroupName();
        queueId = queueDto.getQueueId();
        inTime = Util.formateDate(new Date());
        id = messageDto.getId();
    }

    public long getStart() {
        return start;
    }

    public String getTopic() {
        return topic;
    }

    public String getGroup() {
        return group;
    }

    public long getQueueId() {
        return queueId;
    }

    public String getInTime() {
        return inTime;
    }

    public void start() {
        startTime = Util.formateDate(new Date());
        start = System.currentTimeMillis();
    }

    public String getStartTime() {
        return startTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
