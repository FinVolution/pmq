package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class ConsumerGroupConsumerEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * 消费者Id
     */
     private long consumerId;
    
    /**
     * 
     */
     private String consumerName;
    
    /**
     * 
     */
     private String ip;
    
    /**
     * 组id
     */
     private long consumerGroupId;
    
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
    
    
    public long getId() {
        return id;
    }
    
    public long getConsumerId() {
        return consumerId;
    }
    
    public String getConsumerName() {
        return consumerName;
    }
    
    public String getIp() {
        return ip;
    }
    
    public long getConsumerGroupId() {
        return consumerGroupId;
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
    
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }
    
    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public void setConsumerGroupId(long consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
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
    
    /**
     * 字段名常量值，在构造查询map时，key值就不需要hard code了。
     * 如构造查询ID为121的查询map时，map.put(ConsumerGroupConsumerEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "consumer_group_consumer";
    
    public static String FdId = "id";    
    
    public static String FdConsumerId = "consumerId";    
    
    public static String FdConsumerName = "consumerName";    
    
    public static String FdIp = "ip";    
    
    public static String FdConsumerGroupId = "consumerGroupId";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    
}
    