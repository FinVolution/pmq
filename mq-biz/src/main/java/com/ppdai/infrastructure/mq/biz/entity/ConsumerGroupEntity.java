package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class ConsumerGroupEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * 订阅者集合名称，唯一
     */
     private String name;
    
    /**
     * 
     */
     private String dptName;
    
    /**
     * 指的是正常topic的集合
     */
     private String topicNames;
    
    /**
     * 负责人ids
     */
     private String ownerIds;
    
    /**
     * 负责人名称
     */
     private String ownerNames;
    
    /**
     * 延迟告警邮件，英文逗号隔开
     */
     private String alarmEmails;
    
    /**
     * 手机号码集合，后续可能通过这个发送钉钉消息或者邮件，英文逗号隔开
     */
     private String tels;
    
    /**
     * ip白名单，英文逗号隔开
     */
     private String ipWhiteList;
    
    /**
     * ip黑名单，英文逗号隔开
     */
     private String ipBlackList;
    
    /**
     * 是否消息堆积告警
     */
     private int alarmFlag;
    
    /**
     * 是否开启消息追踪功能，1开启，0不开启
     */
     private int traceFlag;
    
    /**
     * 备注
     */
     private String remark;
    
    /**
     * 重平衡版本号，每次发生重平衡的时候版本会进行升级，同时客户端提交的时候需要进行版本比对如果版本比对不成功说明发生重平衡了
     */
     private long rbVersion;
    
    /**
     * 元数据信息变更版本号
     */
     private long metaVersion;
    
    /**
     * 为了操作方便，引入一个总的version 版本，eb_version 和meta_version发送变更 都会引发version版本变更，拿到相关信息后自行判断是发生了什么类型版本变化
     */
     private long version;
    
    /**
     * 操作人
     */
     private String insertBy;
    
    /**
     * 创建时间
     */
     private Date insertTime;
    
    /**
     * 操作人
     */
     private String updateBy;
    
    /**
     * 更新时间
     */
     private Date updateTime;
    
    /**
     * 逻辑删除
     */
     private int isActive;
    
    /**
     * 应用id 一个消费者组只能属于一个appid，但是一个appid 可以有多个消费者组
     */
     private String appId;
    
    /**
     * 指定消费者数量，比如consumer_quality为2 但是有3个客户端，此时依然只能有2个能消费
     */
     private int consumerQuality;
    
    /**
     * 元数据更新时间
     */
     private Date metaUpdateTime;
    
    /**
     * 1，为集群模式，2，为广播模式,3，为代理模式
     */
     private int mode;
    
    /**
     * 原始的消费者组名
     */
     private String originName;
    
    
    public long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDptName() {
        return dptName;
    }
    
    public String getTopicNames() {
        return topicNames;
    }
    
    public String getOwnerIds() {
        return ownerIds;
    }
    
    public String getOwnerNames() {
        return ownerNames;
    }
    
    public String getAlarmEmails() {
        return alarmEmails;
    }
    
    public String getTels() {
        return tels;
    }
    
    public String getIpWhiteList() {
        return ipWhiteList;
    }
    
    public String getIpBlackList() {
        return ipBlackList;
    }
    
    public int getAlarmFlag() {
        return alarmFlag;
    }
    
    public int getTraceFlag() {
        return traceFlag;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public long getRbVersion() {
        return rbVersion;
    }
    
    public long getMetaVersion() {
        return metaVersion;
    }
    
    public long getVersion() {
        return version;
    }
    
    public String getInsertBy() {
        return insertBy;
    }
    
    public Date getInsertTime() {
        return insertTime;
    }
    
    public String getUpdateBy() {
        return updateBy;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public int getIsActive() {
        return isActive;
    }

    public String getAppId() {
        return appId;
    }
    
    public int getConsumerQuality() {
        return consumerQuality;
    }
    
    public Date getMetaUpdateTime() {
        return metaUpdateTime;
    }
    
    public int getMode() {
        return mode;
    }
    
    public String getOriginName() {
        return originName;
    }
    
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDptName(String dptName) {
        this.dptName = dptName;
    }
    
    public void setTopicNames(String topicNames) {
        this.topicNames = topicNames;
    }
    
    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }
    
    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
    }
    
    public void setAlarmEmails(String alarmEmails) {
        this.alarmEmails = alarmEmails;
    }
    
    public void setTels(String tels) {
        this.tels = tels;
    }
    
    public void setIpWhiteList(String ipWhiteList) {
        this.ipWhiteList = ipWhiteList;
    }
    
    public void setIpBlackList(String ipBlackList) {
        this.ipBlackList = ipBlackList;
    }
    
    public void setAlarmFlag(int alarmFlag) {
        this.alarmFlag = alarmFlag;
    }
    
    public void setTraceFlag(int traceFlag) {
        this.traceFlag = traceFlag;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public void setRbVersion(long rbVersion) {
        this.rbVersion = rbVersion;
    }
    
    public void setMetaVersion(long metaVersion) {
        this.metaVersion = metaVersion;
    }
    
    public void setVersion(long version) {
        this.version = version;
    }
    
    public void setInsertBy(String insertBy) {
        this.insertBy = insertBy;
    }
    
    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public void setConsumerQuality(int consumerQuality) {
        this.consumerQuality = consumerQuality;
    }
    
    public void setMetaUpdateTime(Date metaUpdateTime) {
        this.metaUpdateTime = metaUpdateTime;
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public void setOriginName(String originName) {
        this.originName = originName;
    }
    
    /**
     * 字段名常量值，在构造查询map时，key值就不需要hard code了。
     * 如构造查询ID为121的查询map时，map.put(ConsumerGroupEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "consumer_group";
    
    public static String FdId = "id";    
    
    public static String FdName = "name";    
    
    public static String FdDptName = "dptName";    
    
    public static String FdTopicNames = "topicNames";    
    
    public static String FdOwnerIds = "ownerIds";    
    
    public static String FdOwnerNames = "ownerNames";    
    
    public static String FdAlarmEmails = "alarmEmails";    
    
    public static String FdTels = "tels";    
    
    public static String FdIpWhiteList = "ipWhiteList";    
    
    public static String FdIpBlackList = "ipBlackList";    
    
    public static String FdAlarmFlag = "alarmFlag";    
    
    public static String FdTraceFlag = "traceFlag";    
    
    public static String FdRemark = "remark";    
    
    public static String FdRbVersion = "rbVersion";    
    
    public static String FdMetaVersion = "metaVersion";    
    
    public static String FdVersion = "version";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";
    
    public static String FdAppId = "appId";    
    
    public static String FdConsumerQuality = "consumerQuality";    
    
    public static String FdMetaUpdateTime = "metaUpdateTime";    
    
    public static String FdMode = "mode";    
    
    public static String FdOriginName = "originName";    
    
    
}
    