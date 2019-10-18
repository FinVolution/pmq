package com.ppdai.infrastructure.mq.biz.dto.base;

public class ConsumerQueueDto extends ConsumerQueueVersionDto {
	private long queueId;
	private String originTopicName;
	// 1,表示正常队列，2，表示失败队列
	private int topicType;
	private long topicId;
	/*
	 * 延迟消费毫秒数
	 */
	private int delayProcessTime = 0;
	private int threadSize = 10;
	private int pullBatchSize = 50;
	private int consumerBatchSize = 1;
	/*
	 * 失败重试次数默认100
	 */
	private int retryCount = 100;
	// 是否停止
	private int stopFlag = 0;
	// 给topic 设置tag 过滤
	private String tag;

	private int traceFlag = 0;
	//为了方便缓存查看
	private volatile long lastId = 0;
	// 无消息最大等待时间
	private int maxPullTime = 5;
	// 调用本地方法熔断时间，0表示不熔断,单位秒
	private int timeout = 0;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxPullTime() {
		return maxPullTime;
	}

	public void setMaxPullTime(int maxPullTime) {
		this.maxPullTime = maxPullTime;
	}

	public long getLastId() {
		return lastId;
	}

	public void setLastId(long lastId) {
		this.lastId = lastId;
	}

	public long getQueueId() {
		return queueId;
	}

	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}

	public String getOriginTopicName() {
		return originTopicName;
	}

	public void setOriginTopicName(String originTopicName) {
		this.originTopicName = originTopicName;
	}

	public int getTopicType() {
		return topicType;
	}

	public void setTopicType(int topicType) {
		this.topicType = topicType;
	}

	public long getTopicId() {
		return topicId;
	}

	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}

	public int getDelayProcessTime() {
		return delayProcessTime;
	}

	public void setDelayProcessTime(int delayProcessTime) {
		this.delayProcessTime = delayProcessTime;
	}

	public int getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
	}

	public int getPullBatchSize() {
		return pullBatchSize;
	}

	public void setPullBatchSize(int pullBatchSize) {
		this.pullBatchSize = pullBatchSize;
	}

	public int getConsumerBatchSize() {
		return consumerBatchSize;
	}

	public void setConsumerBatchSize(int consumerBatchSize) {
		this.consumerBatchSize = consumerBatchSize;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(int stopFlag) {
		this.stopFlag = stopFlag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getTraceFlag() {
		return traceFlag;
	}

	public void setTraceFlag(int traceFlag) {
		this.traceFlag = traceFlag;
	}

}
