package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class GetMetaGroupResponse extends BaseResponse {
	//重要ip分组集群数量
	private List<String> brokerIpG1;
	//非重要ip分组集群数量
	private List<String> brokerIpG2;
	// 1表示强制meta模式（meta模式表示由客户端进行lb负载均衡），-1表示类似强制nginx负载均衡，0表示由客户端决定
	private int brokerMetaMode;
	// 是否开启分区，只有当broker部署多余10个的时候才开启分组模式
	private int groupFlag;
	private String metricUrl;

	public String getMetricUrl() {
		return metricUrl;
	}

	public void setMetricUrl(String metricUrl) {
		this.metricUrl = metricUrl;
	}

	public List<String> getBrokerIpG1() {
		return brokerIpG1;
	}

	public void setBrokerIpG1(List<String> brokerIpG1) {
		this.brokerIpG1 = brokerIpG1;
	}

	public List<String> getBrokerIpG2() {
		return brokerIpG2;
	}

	public void setBrokerIpG2(List<String> brokerIpG2) {
		this.brokerIpG2 = brokerIpG2;
	}

	public int getBrokerMetaMode() {
		return brokerMetaMode;
	}

	public void setBrokerMetaMode(int brokerMetaMode) {
		this.brokerMetaMode = brokerMetaMode;
	}

	public int getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(int groupFlag) {
		this.groupFlag = groupFlag;
	}

}
