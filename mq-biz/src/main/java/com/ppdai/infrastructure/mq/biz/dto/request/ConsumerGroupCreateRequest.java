package com.ppdai.infrastructure.mq.biz.dto.request;

import com.ppdai.infrastructure.mq.biz.MqConst;

public class ConsumerGroupCreateRequest {
    private String id;
    private String name;
    private String ownerIds;
    private String ownerNames;
    private Integer ipFlag;
    private Integer alarmFlag;
    private Integer traceFlag;
    private String alarmEmails;
    private String tels;
    private String dptName;
    private String remark;
    private String ipList;
    private String appId;
    private Integer mode;//消费者组的消费模式
    private int consumerQuality;
    private String subEnv=MqConst.DEFAULT_SUBENV;
    //实时消息
    private int pushFlag;


    public String getSubEnv() {
		return subEnv;
	}

	public void setSubEnv(String subEnv) {
		this.subEnv = subEnv;
	}


    public int getConsumerQuality() {
        return consumerQuality;
    }

    public void setConsumerQuality(int consumerQuality) {
        this.consumerQuality = consumerQuality;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getAlarmEmails() {
        return alarmEmails;
    }

    public void setAlarmEmails(String alarmEmails) {
        this.alarmEmails = alarmEmails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAlarmFlag(Integer alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    public void setTraceFlag(Integer traceFlag) {
        this.traceFlag = traceFlag;
    }

    public String getIpList() {
        return ipList;
    }

    public void setIpList(String ipList) {
        this.ipList = ipList;
    }

    public Integer getIpFlag() {
        return ipFlag;
    }

    public void setIpFlag(Integer ipFlag) {
        this.ipFlag = ipFlag;
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

    public int getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(int alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    public int getTraceFlag() {
        return traceFlag;
    }

    public void setTraceFlag(int traceFlag) {
        this.traceFlag = traceFlag;
    }

    public String getTels() {
        return tels;
    }

    public void setTels(String tels) {
        this.tels = tels;
    }

    public String getDptName() {
        return dptName;
    }

    public void setDptName(String dptName) {
        this.dptName = dptName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getPushFlag() {
        return pushFlag;
    }

    public void setPushFlag(int pushFlag) {
        this.pushFlag = pushFlag;
    }
}
