package com.ppdai.infrastructure.mq.biz.ui.vo;

/**
 * @Author：wanghe02
 * @Date：2019/9/3 10:19
 */
public class DepartmentVo {
    private String name;
    private long publishNum;//每周消息发送量
    private long consumerNum;//每周消息消费量

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPublishNum() {
        return publishNum;
    }

    public void setPublishNum(long publishNum) {
        this.publishNum = publishNum;
    }

    public long getConsumerNum() {
        return consumerNum;
    }

    public void setConsumerNum(long consumerNum) {
        this.consumerNum = consumerNum;
    }
}
