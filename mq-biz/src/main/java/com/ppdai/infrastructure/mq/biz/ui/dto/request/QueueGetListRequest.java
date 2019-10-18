package com.ppdai.infrastructure.mq.biz.ui.dto.request;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;

public class QueueGetListRequest extends BaseUiRequst {

    String id;
    String topicName;
    String dbNodeId;
    String nodeType;
    String readOnly;
    String distributeType;
    String sortTypeId;
    String ip;
    private int isException=0;//如果消息库中最早的一条消息的插入日期，加上消息的保存天数，比今天的日期大，则为异常。把isException设置为1，最小Id字段标红。


    public String getDistributeType() {
        return distributeType;
    }

    public void setDistributeType(String distributeType) {
        this.distributeType = distributeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDbNodeId() {
        return dbNodeId;
    }

    public void setDbNodeId(String dbNodeId) {
        this.dbNodeId = dbNodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getSortTypeId() {
        return sortTypeId;
    }

    public void setSortTypeId(String sortTypeId) {
        this.sortTypeId = sortTypeId;
    }

    public int getIsException() {
        return isException;
    }

    public void setIsException(int isException) {
        this.isException = isException;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
