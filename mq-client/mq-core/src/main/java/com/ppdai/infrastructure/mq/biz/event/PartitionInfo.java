package com.ppdai.infrastructure.mq.biz.event;

public class PartitionInfo {
	private long queueId;
	//此字段表示是否是严格模式，当为严格模式时，如果出现partitionId 不存在时，抛弃消息，否则会保存到此topic下的其他队列。在topic对应的queue发生变化时会出现
	private int strictMode=1;
	public long getQueueId() {
		return queueId;
	}
	public void setQueueId(long queueId) {
		this.queueId = queueId;
	}
	public int getStrictMode() {
		return strictMode;
	}
	public void setStrictMode(int strictMode) {
		this.strictMode = strictMode;
	}
}
