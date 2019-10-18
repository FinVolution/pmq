package com.ppdai.infrastructure.mq.biz.dto;

public class AnalyseDto {

    private long topicId;
    private String topicName;
    private Integer quantity;
    private long dbNodeId;
    private String dbNodeIds;
    private String ip;
    private String dbName;
    private String dbStr;
    private Integer queueQuantity;
    private Integer writeableQueueQuantity;

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public long getDbNodeId() {
        return dbNodeId;
    }

    public void setDbNodeId(long dbNodeId) {
        this.dbNodeId = dbNodeId;
    }

    public String getDbNodeIds() {
        return dbNodeIds;
    }

    public void setDbNodeIds(String dbNodeIds) {
        this.dbNodeIds = dbNodeIds;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbStr() {
        return dbStr;
    }

    public void setDbStr(String dbStr) {
        this.dbStr = dbStr;
    }

    public Integer getQueueQuantity() {
        return queueQuantity;
    }

    public void setQueueQuantity(Integer queueQuantity) {
        this.queueQuantity = queueQuantity;
    }

    public Integer getWriteableQueueQuantity() {
        return writeableQueueQuantity;
    }

    public void setWriteableQueueQuantity(Integer writeableQueueQuantity) {
        this.writeableQueueQuantity = writeableQueueQuantity;
    }
}
