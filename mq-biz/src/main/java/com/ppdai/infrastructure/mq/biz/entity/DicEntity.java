package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class DicEntity {
    
    /**
     * 主键
     */
     private long id;
    
    /**
     * 键
     */
     private String key1;
    
    /**
     * 值
     */
     private String value1;
    
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
    
    public String getKey1() {
        return key1;
    }
    
    public String getValue1() {
        return value1;
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
    
    public void setKey1(String key1) {
        this.key1 = key1;
    }
    
    public void setValue1(String value1) {
        this.value1 = value1;
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
     * 如构造查询ID为121的查询map时，map.put(DicEntity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "dic";
    
    public static String FdId = "id";    
    
    public static String FdKey1 = "key1";    
    
    public static String FdValue1 = "value1";    
    
    public static String FdRemark = "remark";    
    
    public static String FdInsertBy = "insertBy";    
    
    public static String FdInsertTime = "insertTime";    
    
    public static String FdUpdateBy = "updateBy";    
    
    public static String FdUpdateTime = "updateTime";    
    
    public static String FdIsActive = "isActive";    
    
    
}
    