package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class DbNodeEntity {
    
    /**
     * 
     */
     private long id;
    
    /**
     * 表示数据对应的ip
     */
     private String ip;
    
    /**
     * 
     */
     private int port;
    
    /**
     * 
     */
     private String dbName;
    
    /**
     * 
     */
     private String dbUserName;
    
    /**
     * 
     */
     private String dbPass;
    
    /**
     * 读写分离
     */
     private String ipBak;
    
    /**
     * 读写分离
     */
     private int portBak;
    
    /**
     * 读写分离
     */
     private String dbUserNameBak;
    
    /**
     * 读写分离
     */
     private String dbPassBak;
    
    /**
     * 数据库链接字符串
     */
     private String conStr;
    
    /**
     * 读写状态： 1读写 2只读 3不可读不可写
     */
     private int readOnly;
    
    /**
     * 1,表示存储正常队列消息，2，表示存储失败队列消息
     */
     private int nodeType;
    
    /**
     * 所有topic自动分配的节点 全部分配到普通节点上，只有手动分配的时候可以分配到特殊节点。1，表示普通节点，0，表示特殊节点
     */
     private int normalFlag;
    
    /**
     * 备注
     */
     private String remark;
    
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
    
    public String getDbName() {
        return dbName;
    }
    
    public String getDbUserName() {
        return dbUserName;
    }
    
    public String getDbPass() {
        return dbPass;
    }
    
    public String getIpBak() {
        return ipBak;
    }
    
    public int getPortBak() {
        return portBak;
    }
    
    public String getDbUserNameBak() {
        return dbUserNameBak;
    }
    
    public String getDbPassBak() {
        return dbPassBak;
    }
    
    public String getConStr() {
        return conStr;
    }
    
    public int getReadOnly() {
        return readOnly;
    }
    
    public int getNodeType() {
        return nodeType;
    }
    
    public int getNormalFlag() {
        return normalFlag;
    }
    
    public String getRemark() {
        return remark;
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
    
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    
    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }
    
    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }
    
    public void setIpBak(String ipBak) {
        this.ipBak = ipBak;
    }
    
    public void setPortBak(int portBak) {
        this.portBak = portBak;
    }
    
    public void setDbUserNameBak(String dbUserNameBak) {
        this.dbUserNameBak = dbUserNameBak;
    }
    
    public void setDbPassBak(String dbPassBak) {
        this.dbPassBak = dbPassBak;
    }
    
    public void setConStr(String conStr) {
        this.conStr = conStr;
    }
    
    public void setReadOnly(int readOnly) {
        this.readOnly = readOnly;
    }
    
    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }
    
    public void setNormalFlag(int normalFlag) {
        this.normalFlag = normalFlag;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
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
     * 如构造查询ID为121的查询map时，map.put(DbNodeEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "db_node";
    
    public static String FdId = "id";    
    
    public static String FdIp = "ip";    
    
    public static String FdPort = "port";    
    
    public static String FdDbName = "dbName";    
    
    public static String FdDbUserName = "dbUserName";    
    
    public static String FdDbPass = "dbPass";    
    
    public static String FdIpBak = "ipBak";    
    
    public static String FdPortBak = "portBak";    
    
    public static String FdDbUserNameBak = "dbUserNameBak";    
    
    public static String FdDbPassBak = "dbPassBak";    
    
    public static String FdConStr = "conStr";    
    
    public static String FdReadOnly = "readOnly";    
    
    public static String FdNodeType = "nodeType";    
    
    public static String FdNormalFlag = "normalFlag";    
    
    public static String FdRemark = "remark";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    
}
    