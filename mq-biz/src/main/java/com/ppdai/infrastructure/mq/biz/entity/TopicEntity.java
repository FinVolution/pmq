package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class TopicEntity{
    
    /**
     * 
     */
     private long id;
    
    /**
     * 名称
     */
     private String name;
    
    /**
     * 如果此topic是失败topic则origin_name为原始的topic，同时name为consumer_group_name+origin_name+"_fail"，否则name和origin_name一致
     */
     private String originName;
    
    /**
     * 部门名称
     */
     private String dptName;
    
    /**
     * 负责人ids
     */
     private String ownerIds;
    
    /**
     * 负责人名称
     */
     private String ownerNames;
    
    /**
     * 延迟告警邮件
     */
     private String emails;
    
    /**
     * 手机号码集合，后续可能通过这个发送钉钉消息或者邮件
     */
     private String tels;
    
    /**
     * 预期每天的数据量单位是万
     */
     private int expectDayCount;
    
    /**
     * 业务类型
     */
     private String businessType;
    
    /**
     * 消息保留天数
     */
     private int saveDayNum;
    
    /**
     * 备注
     */
     private String remark;
    
    /**
     * 如果为空表示不需要验证token，否则消息发送需要匹配token，token可以重新生成，直接简单用guid即可
     */
     private String token;
    
    /**
     * 所有topic自动分配的节点 全部分配到普通节点上，只有手动分配的时候可以分配到特殊节点。1，表示普通节点，0，表示特殊节点
     */
     private int normalFlag;
    
    /**
     * 1,表示正常队列，2，表示失败队列
     */
     private int topicType;
    
    /**
     * 默认topic 堆积告警条数
     */
     private int maxLag;
    
    /**
     * 是否允许所有人消费订阅，1是，0否。如果是0，则只能允许的消费者组才能订阅此topic消息
     */
     private int consumerFlag;
    
    /**
     * 允许订阅消费者组名称列表逗号隔开
     */
     private String consumerGroupNames;
    
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
     * 元数据更新时间
     */
     private Date metaUpdateTime;
    
    /**
     * 
     */
     private String appId;


    public long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getOriginName() {
        return originName;
    }
    
    public String getDptName() {
        return dptName;
    }
    
    public String getOwnerIds() {
        return ownerIds;
    }
    
    public String getOwnerNames() {
        return ownerNames;
    }
    
    public String getEmails() {
        return emails;
    }
    
    public String getTels() {
        return tels;
    }
    
    public int getExpectDayCount() {
        return expectDayCount;
    }
    
    public String getBusinessType() {
        return businessType;
    }
    
    public int getSaveDayNum() {
        return saveDayNum;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public String getToken() {
        return token;
    }
    
    public int getNormalFlag() {
        return normalFlag;
    }
    
    public int getTopicType() {
        return topicType;
    }
    
    public int getMaxLag() {
        return maxLag;
    }
    
    public int getConsumerFlag() {
        return consumerFlag;
    }
    
    public String getConsumerGroupNames() {
        return consumerGroupNames;
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
    
    public Date getMetaUpdateTime() {
        return metaUpdateTime;
    }
    
    public String getAppId() {
        return appId;
    }
    
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setOriginName(String originName) {
        this.originName = originName;
    }
    
    public void setDptName(String dptName) {
        this.dptName = dptName;
    }
    
    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }
    
    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
    }
    
    public void setEmails(String emails) {
        this.emails = emails;
    }
    
    public void setTels(String tels) {
        this.tels = tels;
    }
    
    public void setExpectDayCount(int expectDayCount) {
        this.expectDayCount = expectDayCount;
    }
    
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    public void setSaveDayNum(int saveDayNum) {
        this.saveDayNum = saveDayNum;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public void setNormalFlag(int normalFlag) {
        this.normalFlag = normalFlag;
    }
    
    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }
    
    public void setMaxLag(int maxLag) {
        this.maxLag = maxLag;
    }
    
    public void setConsumerFlag(int consumerFlag) {
        this.consumerFlag = consumerFlag;
    }
    
    public void setConsumerGroupNames(String consumerGroupNames) {
        this.consumerGroupNames = consumerGroupNames;
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
    
    public void setMetaUpdateTime(Date metaUpdateTime) {
        this.metaUpdateTime = metaUpdateTime;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    /**
     * 字段名常量值，在构造查询map时，key值就不需要hard code了。
     * 如构造查询ID为121的查询map时，map.put(TopicEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "topic";
    
    public static String FdId = "id";    
    
    public static String FdName = "name";    
    
    public static String FdOriginName = "originName";    
    
    public static String FdDptName = "dptName";    
    
    public static String FdOwnerIds = "ownerIds";    
    
    public static String FdOwnerNames = "ownerNames";    
    
    public static String FdEmails = "emails";    
    
    public static String FdTels = "tels";    
    
    public static String FdExpectDayCount = "expectDayCount";    
    
    public static String FdBusinessType = "businessType";    
    
    public static String FdSaveDayNum = "saveDayNum";    
    
    public static String FdRemark = "remark";    
    
    public static String FdToken = "token";    
    
    public static String FdNormalFlag = "normalFlag";    
    
    public static String FdTopicType = "topicType";    
    
    public static String FdMaxLag = "maxLag";    
    
    public static String FdConsumerFlag = "consumerFlag";    
    
    public static String FdConsumerGroupNames = "consumerGroupNames";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    public static String FdMetaUpdateTime = "metaUpdateTime";    
    
    public static String FdAppId = "appId";    
    
    
}
    