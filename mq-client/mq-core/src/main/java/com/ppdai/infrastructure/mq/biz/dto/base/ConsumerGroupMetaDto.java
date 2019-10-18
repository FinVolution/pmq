package com.ppdai.infrastructure.mq.biz.dto.base;

public class ConsumerGroupMetaDto {
	private String name;
	private long rbVersion;
	private long metaVersion;
	private long version;
	/*
	 * 延迟消费毫秒数
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getRbVersion() {
		return rbVersion;
	}

	public void setRbVersion(long rbVersion) {
		this.rbVersion = rbVersion;
	}

	public long getMetaVersion() {
		return metaVersion;
	}

	public void setMetaVersion(long metaVersion) {
		this.metaVersion = metaVersion;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
