package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class ConsumerEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * 
     */
     private String ip;
    
    /**
     * 客户端唯一标识采用ip+进程号
     */
     private String name;
    
    /**
     * 组名称
     */
     private String consumerGroupNames;
    
    /**
     * 客户端版本号
     */
     private String sdkVersion;
    
    /**
     * 客户端编程语言
     */
     private String lan;
    
    /**
     * 
     */
     private Date heartTime;
    
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
    
    public String getIp() {
        return ip;
    }
    
    public String getName() {
        return name;
    }
    
    public String getConsumerGroupNames() {
        return consumerGroupNames;
    }
    
    public String getSdkVersion() {
        return sdkVersion;
    }
    
    public String getLan() {
        return lan;
    }
    
    public Date getHeartTime() {
        return heartTime;
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
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setConsumerGroupNames(String consumerGroupNames) {
        this.consumerGroupNames = consumerGroupNames;
    }
    
    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
    
    public void setLan(String lan) {
        this.lan = lan;
    }
    
    public void setHeartTime(Date heartTime) {
        this.heartTime = heartTime;
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
     * 如构造查询ID为121的查询map时，map.put(ConsumerEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "consumer";
    
    public static String FdId = "id";    
    
    public static String FdIp = "ip";    
    
    public static String FdName = "name";    
    
    public static String FdConsumerGroupNames = "consumerGroupNames";    
    
    public static String FdSdkVersion = "sdkVersion";    
    
    public static String FdLan = "lan";    
    
    public static String FdHeartTime = "heartTime";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    
}
    