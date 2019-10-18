package com.ppdai.infrastructure.mq.biz.ui.dto.request;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;

public class TopicGetListRequest extends BaseUiRequst {
    private String name;
    private String id;
    private String ownerName;
    private String topicType;
    private String topicExceptionType;
    private String queueManagementType;



    public String getQueueManagementType() {
        return queueManagementType;
    }

    public void setQueueManagementType(String queueManagementType) {
        this.queueManagementType = queueManagementType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getTopicExceptionType() {
        return topicExceptionType;
    }

    public void setTopicExceptionType(String topicExceptionType) {
        this.topicExceptionType = topicExceptionType;
    }
}
