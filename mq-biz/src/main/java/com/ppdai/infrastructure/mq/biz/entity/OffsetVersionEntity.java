package com.ppdai.infrastructure.mq.biz.entity;

public class OffsetVersionEntity {
	private long id;
	private long offset;
	private long offsetVersion;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getOffsetVersion() {
		return offsetVersion;
	}

	public void setOffsetVersion(long offsetVersion) {
		this.offsetVersion = offsetVersion;
	}
}
