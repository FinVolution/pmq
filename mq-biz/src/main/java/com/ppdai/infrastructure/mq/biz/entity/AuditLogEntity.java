package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class AuditLogEntity {

    public AuditLogEntity() {
    }

    public AuditLogEntity(String tbName, long refId, String content, String insertBy, String updateBy) {
        this.tbName = tbName;
        this.refId = refId;
        this.content = content;
        this.insertBy = insertBy;
        this.updateBy = updateBy;
    }

    /**
     * 
     */
     private long id;
    
    /**
     * 名称
     */
     private String tbName;
    
    /**
     * 外键id
     */
     private long refId;
    
    /**
     * 内容
     */
     private String content;
    
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
    
    public String getTbName() {
        return tbName;
    }
    
    public long getRefId() {
        return refId;
    }
    
    public String getContent() {
        return content;
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
    
    public void setTbName(String tbName) {
        this.tbName = tbName;
    }
    
    public void setRefId(long refId) {
        this.refId = refId;
    }
    
    public void setContent(String content) {
        this.content = content;
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
     * 如构造查询ID为121的查询map时，map.put(AuditLogEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "audit_log";
    
    public static String FdId = "id";    
    
    public static String FdTbName = "tbName";    
    
    public static String FdRefId = "refId";    
    
    public static String FdContent = "content";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    
}
    