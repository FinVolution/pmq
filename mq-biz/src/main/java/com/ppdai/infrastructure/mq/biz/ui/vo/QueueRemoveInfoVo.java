package com.ppdai.infrastructure.mq.biz.ui.vo;

/**
 * @author tongfeifan
 */
public class QueueRemoveInfoVo {
    private Long id;
    private Long topicId;
    private int queueReadOnly;
    private int dbReadOnly;
    private Long leftMessage;
    private String consumerGroups;
    private int isBestRemove;
    private Long dbNodeId;
    private int readStatus;

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public Long getDbNodeId() {
        return dbNodeId;
    }

    public void setDbNodeId(Long dbNodeId) {
        this.dbNodeId = dbNodeId;
    }

    public int getIsBestRemove() {
        return isBestRemove;
    }

    public void setIsBestRemove(int isBestRemove) {
        this.isBestRemove = isBestRemove;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public int getQueueReadOnly() {
        return queueReadOnly;
    }

    public void setQueueReadOnly(int queueReadOnly) {
        this.queueReadOnly = queueReadOnly;
    }

    public int getDbReadOnly() {
        return dbReadOnly;
    }

    public void setDbReadOnly(int dbReadOnly) {
        this.dbReadOnly = dbReadOnly;
    }

    public Long getLeftMessage() {
        return leftMessage;
    }

    public void setLeftMessage(Long leftMessage) {
        this.leftMessage = leftMessage;
    }

    public String getConsumerGroups() {
        return consumerGroups;
    }

    public void setConsumerGroups(String consumerGroups) {
        this.consumerGroups = consumerGroups;
    }
}
