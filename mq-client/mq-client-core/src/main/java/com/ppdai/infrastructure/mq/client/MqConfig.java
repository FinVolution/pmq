package com.ppdai.infrastructure.mq.client;

import java.util.HashMap;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;

//此对象中的配置都是由用户提供的配置
public class MqConfig {
	private String url;
	private String ip = IPUtil.getLocalIP();
	private String serverPort;
	private volatile boolean metaMode = false;
	// 标记是否记录原始数据
	private volatile int logOrigData = 0;
	// 异步队列容量
	private volatile int asynCapacity = 2000;
	// 重平衡等待时间
	private volatile int rbTimes = 4;
	private Map<String, String> properties = new HashMap<>();
	// 发送失败重试次数
	private volatile int pbRetryTimes = 10;
	// 消息发送拉取超时时间
	private volatile long readTimeOut = 10000;
	// 数据拉取没有数据时，递增值
	private int pullDeltaTime = 150;

	public int getPullDeltaTime() {
		return pullDeltaTime;
	}

	public void setPullDeltaTime(int pullDeltaTime) {
		if (pullDeltaTime < 10) {
			pullDeltaTime = 10;
		}
		this.pullDeltaTime = pullDeltaTime;
	}

	public long getReadTimeOut() {
		if (readTimeOut < 10) {
			return 10;
		}
		return readTimeOut;
	}

	public void setReadTimeOut(long readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public int getPbRetryTimes() {
		return pbRetryTimes;
	}

	public void setPbRetryTimes(int pbRetryTimes) {
		this.pbRetryTimes = pbRetryTimes;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public int getRbTimes() {
		return rbTimes;
	}

	public void setRbTimes(int rbTimes) {
		this.rbTimes = rbTimes;
	}

	public int getAsynCapacity() {
		return asynCapacity;
	}

	public void setAsynCapacity(int asynCapacity) {
		this.asynCapacity = asynCapacity;
	}

	public int getLogOrigData() {
		return logOrigData;
	}

	public void setLogOrigData(int logOrigData) {
		this.logOrigData = logOrigData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public boolean isMetaMode() {
		return metaMode;
	}

	public void setMetaMode(boolean metaMode) {
		this.metaMode = metaMode;
	}
}
