package com.ppdai.infrastructure.mq.client.config;

public class ConsumerGroupMetaVo {
	private String name;
	private String originName;	

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
