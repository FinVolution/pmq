package com.ppdai.infrastructure.mq.biz.ui.vo;

import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import org.springframework.beans.BeanUtils;

public class TopicVo extends TopicEntity {

    public TopicVo(TopicEntity topicEntity) {
        BeanUtils.copyProperties(topicEntity, this);
    }

    private int role;

    private int queueCount;

    private long msgCount=0;

    private long avgCount=0;//消息平均数，消息总数除以消息保留天数

    private String isReasonable;//判断队列分配是否合理

    private long avgCountOfQueue=0;

    private long manageQueueCount=0;//队列应该缩容或者扩容的数量，例如：-2表示应该减少两个队列，2表应该增加两个队列


    public long getManageQueueCount() {
        return manageQueueCount;
    }

    public void setManageQueueCount(long manageQueueCount) {
        this.manageQueueCount = manageQueueCount;
    }

    public long getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(long msgCount) {
        this.msgCount = msgCount;
    }

    public long getAvgCount() {
        return avgCount;
    }

    public void setAvgCount(long avgCount) {
        this.avgCount = avgCount;
    }

    public String getIsReasonable() {
        return isReasonable;
    }

    public void setIsReasonable(String isReasonable) {
        this.isReasonable = isReasonable;
    }

    public long getAvgCountOfQueue() {
        return avgCountOfQueue;
    }

    public void setAvgCountOfQueue(long avgCountOfQueue) {
        this.avgCountOfQueue = avgCountOfQueue;
    }

    public int getQueueCount() {
        return queueCount;
    }

    public void setQueueCount(int queueCount) {
        this.queueCount = queueCount;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
