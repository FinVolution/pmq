package com.ppdai.infrastructure.mq.biz.ui.vo;

import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import org.springframework.beans.BeanUtils;

import java.util.Date;

public class QueueVo extends QueueEntity {

    public QueueVo(QueueEntity queueEntity) {
        BeanUtils.copyProperties(queueEntity, this);
    }

    private int role=0;

    private int dbReadOnly;

    private long msgCount=0;

    private long avgCount=0;//消息平均数，消息总数除以消息保留天数

    private int isException=0;//如果消息库中最早的一条消息的插入日期，加上消息的保存天数，比今天的日期大，则为异常。把isException设置为1，最小Id字段标红。

    private long maxId=0;//队列的最大Id

    /**
     * 消息表中，id最小的消息的插入时间
     */
    private Date minTime;

    public long getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(long msgCount) {
        this.msgCount = msgCount;
    }

    public int getDbReadOnly() {
        return dbReadOnly;
    }

    public void setDbReadOnly(int dbReadOnly) {
        this.dbReadOnly = dbReadOnly;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public long getAvgCount() {
        return avgCount;
    }

    public void setAvgCount(long avgCount) {
        this.avgCount = avgCount;
    }

    public int getIsException() {
        return isException;
    }

    public void setIsException(int isException) {
        this.isException = isException;
    }

    public Date getMinTime() {
        return minTime;
    }

    public void setMinTime(Date minTime) {
        this.minTime = minTime;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }
}
