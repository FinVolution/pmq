package com.ppdai.infrastructure.mq.biz.common.util;

import java.net.URL;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassLoaderUtil {
	private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtil.class);

	private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
	private static String classPath = "";
	static {
		if (loader == null) {
			logger.warn("Using system class loader");
			loader = ClassLoader.getSystemClassLoader();
		}		

		try {
			URL url = loader.getResource("");
			// get class path
			if (url != null) {
				classPath = url.getPath();
				classPath = URLDecoder.decode(classPath, "utf-8");
			}

			// 如果是jar包内的，则返回当前路径
			if (classPath == null || classPath.length() == 0 || classPath.trim().length() == 0
					|| classPath.contains(".jar!")) {
				classPath = System.getProperty("user.dir");
			}
		} catch (Throwable ex) {
			classPath = System.getProperty("user.dir");
			ex.printStackTrace();
		}
	}

	public static ClassLoader getLoader() {
		return loader;
	}

	public static String getClassPath() {
		return classPath;
	}	

	public static boolean isClassPresent(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}
}
