package com.ppdai.infrastructure.mq.biz.ui.dto.request;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;

public class MessageConditionRequest extends BaseUiRequst{
    private String queueId;

    private String bizId;

    private String startTime;

    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getQueueId() {

        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }
}
