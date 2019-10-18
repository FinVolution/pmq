package com.ppdai.infrastructure.mq.biz.common.util;

public class TopicUtil {
	/*
	 * 获取失败队列名称
	 * */
	public static String getFailTopicName(String consumerGroupName, String topicName) {
		return String.format("%s_%s_fail", consumerGroupName, topicName);
	}
}
