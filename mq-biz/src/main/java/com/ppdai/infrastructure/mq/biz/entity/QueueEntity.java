package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class QueueEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * topic id
     */
     private long topicId;
    
    /**
     * topic名称
     */
     private String topicName;
    
    /**
     * 分区id
     */
     private long dbNodeId;
    
    /**
     * 1,表示存储正常队列消息，2，表示存储失败队列消息
     */
     private int nodeType;
    
    /**
     * 
     */
     private String ip;
    
    /**
     * 数据库名称
     */
     private String dbName;
    
    /**
     * 数据库表名
     */
     private String tbName;
    
    /**
     * 读写状态：1读写 2只读
     */
     private int readOnly;
    
    /**
     * 
     */
     private long minId;
    
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
     * 
     */
     private long lockVersion;
    
    /**
     * 元数据更新时间
     */
     private Date metaUpdateTime;
    
    
    public long getId() {
        return id;
    }
    
    public long getTopicId() {
        return topicId;
    }
    
    public String getTopicName() {
        return topicName;
    }
    
    public long getDbNodeId() {
        return dbNodeId;
    }
    
    public int getNodeType() {
        return nodeType;
    }
    
    public String getIp() {
        return ip;
    }
    
    public String getDbName() {
        return dbName;
    }
    
    public String getTbName() {
        return tbName;
    }
    
    public int getReadOnly() {
        return readOnly;
    }
    
    public long getMinId() {
        return minId;
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
    
    public long getLockVersion() {
        return lockVersion;
    }
    
    public Date getMetaUpdateTime() {
        return metaUpdateTime;
    }
    
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }
    
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    public void setDbNodeId(long dbNodeId) {
        this.dbNodeId = dbNodeId;
    }
    
    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public void setTbName(String tbName) {
        this.tbName = tbName;
    }
    
    public void setReadOnly(int readOnly) {
        this.readOnly = readOnly;
    }
    
    public void setMinId(long minId) {
        this.minId = minId;
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
    
    public void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
    }
    
    public void setMetaUpdateTime(Date metaUpdateTime) {
        this.metaUpdateTime = metaUpdateTime;
    }
    
    /**
     * 字段名常量值，在构造查询map时，key值就不需要hard code了。
     * 如构造查询ID为121的查询map时，map.put(QueueEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "queue";
    
    public static String FdId = "id";    
    
    public static String FdTopicId = "topicId";    
    
    public static String FdTopicName = "topicName";    
    
    public static String FdDbNodeId = "dbNodeId";    
    
    public static String FdNodeType = "nodeType";    
    
    public static String FdIp = "ip";    
    
    public static String FdDbName = "dbName";    
    
    public static String FdTbName = "tbName";    
    
    public static String FdReadOnly = "readOnly";    
    
    public static String FdMinId = "minId";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    public static String FdLockVersion = "lockVersion";    
    public static String FdDistributeType="distributeType";
    
    public static String FdMetaUpdateTime = "metaUpdateTime";    
    
    
}
    