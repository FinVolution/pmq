package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

/**
 * @author dal-generator
 */
public class Message01Entity {
    
    /**
     * 主键
     */
     private long id;
    
    /**
     * 业务id
     */
     private String bizId;
    
    /**
     * 标记
     */
     private String tag;
    
    /**
     * 消息头
     */
     private String head;
    
    /**
     * 消息体
     */
     private String body;
    
    /**
     * 发送的ip
     */
     private String traceId;
    
    /**
     * 失败重试次数
     */
     private int retryCount;
    
    /**
     * 发送的ip
     */
     private String sendIp;
    
    /**
     * 创建时间
     */
     private Date sendTime;
    
   
    
    
    public long getId() {
        return id;
    }
    
    public String getBizId() {
        return bizId;
    }
    
    public String getTag() {
        return tag;
    }
    
    public String getHead() {
        return head;
    }
    
    public String getBody() {
        return body;
    }
    
    public String getTraceId() {
        return traceId;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public String getSendIp() {
        return sendIp;
    }
    
    public Date getSendTime() {
        return sendTime;
    }   
   
    
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public void setHead(String head) {
        this.head = head;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public void setSendIp(String sendIp) {
        this.sendIp = sendIp;
    }
    
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }   
   
    
    /**
     * 字段名常量值，在构造查询map时，key值就不需要hard code了。
     * 如构造查询ID为121的查询map时，map.put(Message01Entity.FdId， "121");
     */
    
    public final static String TABLE_NAME = "message_01";
    
    public static String FdId = "id";    
    
    public static String FdBizId = "bizId";    
    
    public static String FdTag = "tag";    
    
    public static String FdHead = "head";    
    
    public static String FdBody = "body";    
    
    public static String FdTraceId = "traceId";    
    
    public static String FdRetryCount = "retryCount";    
    
    public static String FdSendIp = "sendIp";    
    
    public static String FdSendTime = "sendTime";    
   
}
    