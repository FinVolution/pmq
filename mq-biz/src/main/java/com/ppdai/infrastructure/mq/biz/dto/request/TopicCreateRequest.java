package com.ppdai.infrastructure.mq.biz.dto.request;

public class TopicCreateRequest {
    private String id;
    private String name;
    private String ownerIds;
    private String ownerNames;
    private Integer expectDayCount;
    private String emails;
    private String businessType;
    private Integer maxLag;
    private String remark;
    private String dptName;
    private Integer normalFlag;
    private Integer saveDayNum;
    private String tels;
    private String insertBy;
    private int topicType;
    private Integer consumerFlag;
    private String consumerGroupList;
    String appId;

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    public String getInsertBy() {
        return insertBy;
    }

    public void setInsertBy(String insertBy) {
        this.insertBy = insertBy;
    }

    public String getTels() {
        return tels;
    }

    public void setTels(String tels) {
        this.tels = tels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }

    public String getOwnerNames() {
        return ownerNames;
    }

    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
    }

    public Integer getExpectDayCount() {
        return expectDayCount;
    }

    public void setExpectDayCount(Integer expectDayCount) {
        this.expectDayCount = expectDayCount;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getMaxLag() {
        return maxLag;
    }

    public void setMaxLag(Integer maxLag) {
        this.maxLag = maxLag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDptName() {
        return dptName;
    }

    public void setDptName(String dptName) {
        this.dptName = dptName;
    }

    public Integer getNormalFlag() {
        return normalFlag;
    }

    public void setNormalFlag(Integer normalFlag) {
        this.normalFlag = normalFlag;
    }

    public Integer getSaveDayNum() {
        return saveDayNum;
    }

    public void setSaveDayNum(Integer saveDayNum) {
        this.saveDayNum = saveDayNum;
    }

    public Integer getConsumerFlag() {
        return consumerFlag;
    }

    public void setConsumerFlag(Integer consumerFlag) {
        this.consumerFlag = consumerFlag;
    }

    public String getConsumerGroupList() {
        return consumerGroupList;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setConsumerGroupList(String consumerGroupList) {
        this.consumerGroupList = consumerGroupList;
    }
}
