package com.ppdai.infrastructure.mq.biz.dto.request;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;

public class ConsumerGroupTopicGetListRequest extends BaseUiRequst {
    private String consumerGroupName;
    private String topicName;
    private String consumerGroupId;

    public String getConsumerGroupName() {
        return consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName) {
        this.consumerGroupName = consumerGroupName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }
}
