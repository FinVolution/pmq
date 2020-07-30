package com.ppdai.infrastructure.mq.biz.common.util;

public class DbUtil {
	public static String getDbIp(String url) {
		if (Util.isEmpty(url))
			return "unknown";
		else {
			String[] arr = url.split("/");
			return arr[2];
		}
	}

}
