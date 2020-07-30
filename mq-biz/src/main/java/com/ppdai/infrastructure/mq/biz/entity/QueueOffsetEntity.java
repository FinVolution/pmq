package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class QueueOffsetEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * 订阅者组id
     */
     private long consumerGroupId;
    
    /**
     * 订阅者组主键
     */
     private String consumerGroupName;
    
    /**
     * 客户端消费者name
     */
     private String consumerName;
    
    /**
     * 
     */
     private long consumerId;
    
    /**
     * 主题id
     */
     private long topicId;
    
    /**
     * 主题名称,如果
     */
     private String topicName;
    
    /**
     * 如果是失败队列此字段名称表示原始的topic名称，topic_name为consumer_group_name+原始的topic_name+"_fail"，否则topic_name和origin_topic_name一致
     */
     private String originTopicName;
    
    /**
     * 1,表示正常队列，2，表示失败队列
     */
     private int topicType;
    
    /**
     * 分区id
     */
     private long queueId;
    
    /**
     * 消费者提交的偏移量
     */
     private long offset;
    
    /**
     * 订阅时的起始偏移量
     */
     private long startOffset;
    
    /**
     * 偏移版本号，当手动修改偏移时，会升级版本号，如果客户端提交更新便宜的时候，只能按照版本号相同，偏移量大的值更新
     */
     private long offsetVersion;
    
    /**
     * 1,表示客户端此queue停止消费，0，表示正常消费
     */
     private int stopFlag;
    
    /**
     * ip+db_name +tb_name
     */
     private String dbInfo;
    
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
     * 原始的消费者组名
     */
     private String originConsumerGroupName;
    
    /**
     * 1，为集群模式，2，为广播模式,3，为代理模式
     */
     private int consumerGroupMode;
    
    /**
     * 
     */
     private String subEnv;
    
    
    public long getId() {
        return id;
    }
    
    public long getConsumerGroupId() {
        return consumerGroupId;
    }
    
    public String getConsumerGroupName() {
        return consumerGroupName;
    }
    
    public String getConsumerName() {
        return consumerName;
    }
    
    public long getConsumerId() {
        return consumerId;
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
    
    public long getQueueId() {
        return queueId;
    }
    
    public long getOffset() {
        return offset;
    }
    
    public long getStartOffset() {
        return startOffset;
    }
    
    public long getOffsetVersion() {
        return offsetVersion;
    }
    
    public int getStopFlag() {
        return stopFlag;
    }
    
    public String getDbInfo() {
        return dbInfo;
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
    
    public String getOriginConsumerGroupName() {
        return originConsumerGroupName;
    }
    
    public int getConsumerGroupMode() {
        return consumerGroupMode;
    }
    
    public String getSubEnv() {
        return subEnv;
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
    
    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }
    
    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
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
    
    public void setQueueId(long queueId) {
        this.queueId = queueId;
    }
    
    public void setOffset(long offset) {
        this.offset = offset;
    }
    
    public void setStartOffset(long startOffset) {
        this.startOffset = startOffset;
    }
    
    public void setOffsetVersion(long offsetVersion) {
        this.offsetVersion = offsetVersion;
    }
    
    public void setStopFlag(int stopFlag) {
        this.stopFlag = stopFlag;
    }
    
    public void setDbInfo(String dbInfo) {
        this.dbInfo = dbInfo;
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
    
    public void setOriginConsumerGroupName(String originConsumerGroupName) {
        this.originConsumerGroupName = originConsumerGroupName;
    }
    
    public void setConsumerGroupMode(int consumerGroupMode) {
        this.consumerGroupMode = consumerGroupMode;
    }
    
    public void setSubEnv(String subEnv) {
        this.subEnv = subEnv;
    }
    
    /**
     * 字段名常量值，在构造查询map时，key值就不需要hard code了。
     * 如构造查询ID为121的查询map时，map.put(QueueOffsetEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "queue_offset";
    
    public static String FdId = "id";    
    
    public static String FdConsumerGroupId = "consumerGroupId";    
    
    public static String FdConsumerGroupName = "consumerGroupName";    
    
    public static String FdConsumerName = "consumerName";    
    
    public static String FdConsumerId = "consumerId";    
    
    public static String FdTopicId = "topicId";    
    
    public static String FdTopicName = "topicName";    
    
    public static String FdOriginTopicName = "originTopicName";    
    
    public static String FdTopicType = "topicType";    
    
    public static String FdQueueId = "queueId";    
    
    public static String FdOffset = "offset";    
    
    public static String FdStartOffset = "startOffset";    
    
    public static String FdOffsetVersion = "offsetVersion";    
    
    public static String FdStopFlag = "stopFlag";    
    
    public static String FdDbInfo = "dbInfo";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    public static String FdMetaUpdateTime = "metaUpdateTime";    
    
    public static String FdOriginConsumerGroupName = "originConsumerGroupName";    
    
    public static String FdConsumerGroupMode = "consumerGroupMode";    
    
    public static String FdSubEnv = "subEnv";
}
    