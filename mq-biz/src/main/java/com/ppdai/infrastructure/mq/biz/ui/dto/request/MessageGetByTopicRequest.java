package com.ppdai.infrastructure.mq.biz.ui.dto.request;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;

/**
 * @Author：wanghe02
 * @Date：2019/7/19 15:01
 */
public class MessageGetByTopicRequest extends BaseUiRequst {
    private String topicName;
    private String bizId;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
