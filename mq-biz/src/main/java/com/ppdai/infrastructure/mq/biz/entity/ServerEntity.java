package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class ServerEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * 
     */
     private String ip;
    
    /**
     * 端口号
     */
     private int port;
    
    /**
     * 
     */
     private Date heartTime;
    
    /**
     * 1 表示broker，0 表示portal。当值为0时，这个是用在做批量清理时使用。
     */
     private int serverType;
    
    /**
     * 1 表示状态为up,0 表示状态为down，此状态在系统灰度平滑发布时使用。默认是1 表示up
     */
     private int statusFlag;
    
    /**
     * broker 版本号
     */
     private String serverVersion;
    
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
    
    public int getPort() {
        return port;
    }
    
    public Date getHeartTime() {
        return heartTime;
    }
    
    public int getServerType() {
        return serverType;
    }
    
    public int getStatusFlag() {
        return statusFlag;
    }
    
    public String getServerVersion() {
        return serverVersion;
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
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public void setHeartTime(Date heartTime) {
        this.heartTime = heartTime;
    }
    
    public void setServerType(int serverType) {
        this.serverType = serverType;
    }
    
    public void setStatusFlag(int statusFlag) {
        this.statusFlag = statusFlag;
    }
    
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
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
     * 如构造查询ID为121的查询map时，map.put(ServerEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "server";
    
    public static String FdId = "id";    
    
    public static String FdIp = "ip";    
    
    public static String FdPort = "port";    
    
    public static String FdHeartTime = "heartTime";    
    
    public static String FdServerType = "serverType";    
    
    public static String FdStatusFlag = "statusFlag";    
    
    public static String FdServerVersion = "serverVersion";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    
}
    