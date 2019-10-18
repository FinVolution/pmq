package com.ppdai.infrastructure.mq.biz.dto.base;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.event.PartitionInfo;

/*
 * 将messagedto 变成 producerdatadto的子类的原因是消息消费的时候，会将对象加上一些附加信息，为了让接口更加的易用，将发送消息与消费消息分离。
 * */
public class ProducerDataDto {
	private long id;
	private String tag;
	// 可能为空
	private String bizId;
	// 可能为空
	private Map<String, String> head;
	private String body;
	// 可能为空
	private String traceId;
	// 重试次数，不用自己赋值，sdk会自动赋值
	private int retryCount;
	// 当partitionInfo 为空时，表示不指定分区
	private PartitionInfo partitionInfo;

	public ProducerDataDto() {

	}

	public ProducerDataDto(String bizId, String body) {
		this(bizId, "", null, body);
	}

	public ProducerDataDto(String body) {
		this("", "", null, body);
	}

	public ProducerDataDto(String bizId, String body, String tag) {
		this(bizId, tag, null, body);
	}

	public ProducerDataDto(String bizId, String tag, Map<String, String> header, String body) {
		setBizId(bizId);
		setTag(tag);
		setHead(header);
		setBody(body);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PartitionInfo getPartitionInfo() {
		return partitionInfo;
	}

	public void setPartitionInfo(PartitionInfo partitionInfo) {
		this.partitionInfo = partitionInfo;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public Map<String, String> getHead() {
		return head;
	}

	public void setHead(Map<String, String> head) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
}
