package com.ppdai.infrastructure.mq.biz.common.util;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

public class PropUtil {
	private static volatile String sdkVersion = null;
	private static volatile Integer processId = null;

	public static String getSdkVersion() {
		if (sdkVersion == null) {
			synchronized (PropUtil.class) {
				if (sdkVersion == null) {
					try {
						String path = "/mqversion.properties";
						InputStream stream = PropUtil.class.getResourceAsStream(path);
						Properties props = new Properties();
						props.load(stream);
						stream.close();
						sdkVersion = props.getProperty("version");
						return sdkVersion;
					} catch (Exception e) {
						throw new RuntimeException("获取skd version 异常", e);
					}
				}
			}
		}
		return sdkVersion;
	}

	public static Integer getProcessId() {
		if (processId == null) {
			synchronized (PropUtil.class) {
				if (processId == null) {
					RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
					String name = runtime.getName();
					processId = Integer.parseInt(name.substring(0, name.indexOf("@")));
				}
			}
		}
		return processId;
	}
}
