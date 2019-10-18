package com.ppdai.infrastructure.mq.biz.common.trace;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 保留追踪信息 Created by liujianjun02 on 2017/12/15.
 */
public class TraceMessageItem {
	public String msg;
	public String status;
	public String startTime;
	public String endTime;

	public TraceMessageItem() {
		start();
	}

	private void start() {
		this.startTime = setDate();
	}

	private String setDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");
		return sdf.format(new Date());
	}

//	@Override
//	public String toString() {
//		return "开始时间:" + startTime + " 结束时间：" + endTime + " ,执行消息:" + this.msg;
//	}
}
