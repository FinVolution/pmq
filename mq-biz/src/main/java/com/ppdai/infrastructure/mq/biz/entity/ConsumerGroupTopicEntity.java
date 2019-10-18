package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class ConsumerGroupTopicEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * 
     */
     private long consumerGroupId;
    
    /**
     * 
     */
     private String consumerGroupName;
    
    /**
     * 
     */
     private long topicId;
    
    /**
     * topic name
     */
     private String topicName;
    
    /**
     * 与失败topic对应的topic
     */
     private String originTopicName;
    
    /**
     * 1,表示正常队列，2，表示失败队列
     */
     private int topicType;
    
    /**
     * 每个topic在某个consumergroup底下对应的失败队列尝试的次数
     */
     private int retryCount;
    
    /**
     * 
     */
     private int threadSize;
    
    /**
     * 可以自定义此topic下topic 告警条数
     */
     private int maxLag;
    
    /**
     * 用来做消息tag 过滤,规则是只要包含在消息体tag中就算符合
     */
     private String tag;
    
    /**
     * 默认为0，毫秒为单位。延迟处理时间，相对于发送时间的延迟，例如希望发送一条消息后10秒后被订阅，就需要设置该参数为10000。
     */
     private int delayProcessTime;
    
    /**
     * 批量拉取条数
     */
     private int pullBatchSize;
    
    /**
     * 批量消费条数
     */
     private int consumerBatchSize;
    
    /**
     * 最大拉取等待时间，单位是秒默认5秒，最小值1秒
     */
     private int maxPullTime;
    
    /**
     * 
     */
     private String alarmEmails;
    
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
     * 客户端消费超时熔断时间单位秒,0表示不熔断
     */
     private int timeOut;
    
    
    public long getId() {
        return id;
    }
    
    public long getConsumerGroupId() {
        return consumerGroupId;
    }
    
    public String getConsumerGroupName() {
        return consumerGroupName;
    }
    
    public long getTopicId() {
        return topicId;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public String getOriginTopicName() {
        return originTopicName;
    }
    
    public int getTopicType() {
        return topicType;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public int getThreadSize() {
        return threadSize;
    }
    
    public int getMaxLag() {
        return maxLag;
    }
    
    public String getTag() {
        return tag;
    }
    
    public int getDelayProcessTime() {
        return delayProcessTime;
    }
    
    public int getPullBatchSize() {
        return pullBatchSize;
    }
    
    public int getConsumerBatchSize() {
        return consumerBatchSize;
    }
    
    public int getMaxPullTime() {
        return maxPullTime;
    }
    
    public String getAlarmEmails() {
        return alarmEmails;
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
    
    public int getTimeOut() {
        return timeOut;
    }
    
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setConsumerGroupId(long consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }
    
    public void setConsumerGroupName(String consumerGroupName) {
        this.consumerGroupName = consumerGroupName;
    }
    
    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    public void setOriginTopicName(String originTopicName) {
        this.originTopicName = originTopicName;
    }
    
    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }
    
    public void setMaxLag(int maxLag) {
        this.maxLag = maxLag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public void setDelayProcessTime(int delayProcessTime) {
        this.delayProcessTime = delayProcessTime;
    }
    
    public void setPullBatchSize(int pullBatchSize) {
        this.pullBatchSize = pullBatchSize;
    }
    
    public void setConsumerBatchSize(int consumerBatchSize) {
        this.consumerBatchSize = consumerBatchSize;
    }
    
    public void setMaxPullTime(int maxPullTime) {
        this.maxPullTime = maxPullTime;
    }
    
    public void setAlarmEmails(String alarmEmails) {
        this.alarmEmails = alarmEmails;
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
    
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
    
    /**
     * 字段名常量值，在构造查询map时，key值就不需要hard code了。
     * 如构造查询ID为121的查询map时，map.put(ConsumerGroupTopicEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "consumer_group_topic";
    
    public static String FdId = "id";    
    
    public static String FdConsumerGroupId = "consumerGroupId";    
    
    public static String FdConsumerGroupName = "consumerGroupName";    
    
    public static String FdTopicId = "topicId";    
    
    public static String FdTopicName = "topicName";    
    
    public static String FdOriginTopicName = "originTopicName";    
    
    public static String FdTopicType = "topicType";    
    
    public static String FdRetryCount = "retryCount";    
    
    public static String FdThreadSize = "threadSize";    
    
    public static String FdMaxLag = "maxLag";    
    
    public static String FdTag = "tag";    
    
    public static String FdDelayProcessTime = "delayProcessTime";    
    
    public static String FdPullBatchSize = "pullBatchSize";    
    
    public static String FdConsumerBatchSize = "consumerBatchSize";    
    
    public static String FdMaxPullTime = "maxPullTime";    
    
    public static String FdAlarmEmails = "alarmEmails";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    public static String FdMetaUpdateTime = "metaUpdateTime";    
    
    public static String FdTimeOut = "timeOut";    
    
    
}
    