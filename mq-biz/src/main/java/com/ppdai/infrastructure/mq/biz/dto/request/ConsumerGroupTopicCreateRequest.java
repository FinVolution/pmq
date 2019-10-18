package com.ppdai.infrastructure.mq.biz.dto.request;

public class ConsumerGroupTopicCreateRequest {
	private String consumerGroupName;
	private Long consumerGroupId;
	private Long topicId;
	private String topicName;
	private String originTopicName;
	private Integer topicType;
	private Integer retryCount;
	private Integer threadSize;
	private Integer maxLag;
	private String tag;
	private Integer delayProcessTime;
	private Integer pullBatchSize;
	private String alarmEmails;
	private Integer delayPullTime;
	private Integer timeOut;
    private int consumerBatchSize;//批量消费条数

    public ConsumerGroupTopicCreateRequest() {
    	
    }

    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public int getConsumerBatchSize() {
        return consumerBatchSize;
    }

    public void setConsumerBatchSize(int consumerBatchSize) {
        this.consumerBatchSize = consumerBatchSize;
    }

    public String getConsumerGroupName() {
        return consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName) {
        this.consumerGroupName = consumerGroupName;
    }

    public Long getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(Long consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getTopicType() {
        return topicType;
    }

    public void setTopicType(Integer topicType) {
        this.topicType = topicType;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(Integer threadSize) {
        this.threadSize = threadSize;
    }

    public Integer getMaxLag() {
        return maxLag;
    }

    public void setMaxLag(Integer maxLag) {
        this.maxLag = maxLag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getDelayProcessTime() {
        return delayProcessTime;
    }

    public void setDelayProcessTime(Integer delayProcessTime) {
        this.delayProcessTime = delayProcessTime;
    }

    public Integer getPullBatchSize() {
        return pullBatchSize;
    }

    public void setPullBatchSize(Integer pullBatchSize) {
        this.pullBatchSize = pullBatchSize;
    }

    public String getAlarmEmails() {
        return alarmEmails;
    }

    public void setAlarmEmails(String alarmEmails) {
        this.alarmEmails = alarmEmails;
    }

    public String getOriginTopicName() {
        return originTopicName;
    }

    public void setOriginTopicName(String originTopicName) {
        this.originTopicName = originTopicName;
    }

    public Integer getDelayPullTime() {
        return delayPullTime;
    }

    public void setDelayPullTime(Integer delayPullTime) {
        this.delayPullTime = delayPullTime;
    }
}
