package com.ppdai.infrastructure.mq.biz.ui.vo;

import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import org.springframework.beans.BeanUtils;

public class QueueOffsetVo extends QueueOffsetEntity {
    private int role;
    private int nodeType;//节点类型
    private int readOnly;//队列是否只读
    private long messageNum;//消息总数
    private long pendingMessageNum;//待处理消息数
    private long maxId;//当前队列所在表的最大Id
    private long minId;//当前队列所在表的最小Id
    private long maxLag;//告警阈值
    private long minusMaxLag;//待处理消息数减去告警阈值

    private String consumerGroupOwners;

    private String consumerGroupOwnerIds;

    public String getConsumerGroupOwnerIds() {
        return consumerGroupOwnerIds;
    }

    public void setConsumerGroupOwnerIds(String consumerGroupOwnerIds) {
        this.consumerGroupOwnerIds = consumerGroupOwnerIds;
    }

    public String getConsumerGroupOwners() {
        return consumerGroupOwners;
    }

    public void setConsumerGroupOwners(String consumerGroupOwners) {
        this.consumerGroupOwners = consumerGroupOwners;
    }

    public QueueOffsetVo(QueueOffsetEntity queueOffsetEntity){
        BeanUtils.copyProperties(queueOffsetEntity,this);
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public int getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(int readOnly) {
        this.readOnly = readOnly;
    }

    public long getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(long messageNum) {
        this.messageNum = messageNum;
    }

    public long getPendingMessageNum() {
        return pendingMessageNum;
    }

    public void setPendingMessageNum(long pendingMessageNum) {
        this.pendingMessageNum = pendingMessageNum;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public long getMinId() {
        return minId;
    }

    public void setMinId(long minId) {
        this.minId = minId;
    }

    public long getMaxLag() {
        return maxLag;
    }

    public void setMaxLag(long maxLag) {
        this.maxLag = maxLag;
    }

    public long getMinusMaxLag() {
        return minusMaxLag;
    }

    public void setMinusMaxLag(long minusMaxLag) {
        this.minusMaxLag = minusMaxLag;
    }

}
