package com.ppdai.infrastructure.mq.client.bootstrap;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.util.ConfigUtil;

public class ApolloEnvironment {
	public static final String DEFAULT_SUBENV = ConfigConsts.CLUSTER_NAME_DEFAULT;

	private static final ConfigUtil m_configUtil;
	private static Boolean isPro1 = null;
	// 当前应用appid，由apollo启动时赋值,只能赋值一次
	private static String appId;
	// 当前应用appid，由apollo启动时赋值,只能赋值一次， 如fat dev pro等
	private static Env env;
	// 当前子环境名称，由apollo启动时赋值,只能赋值一次
	private static String subEnv;
	// 指定目标子环境名称
	private static ThreadLocal<String> targetSubEnv = new InheritableThreadLocal<>();
	// key为appid，value为对应的环境列表，由注册中心负责刷新
	private static AtomicReference<List<String>> appSubEnvsRef = new AtomicReference<>();

	public static List<String> getAppSubEnvs() {
		return appSubEnvsRef.get();
	}

	public static void setAppSubEnvs(List<String> appSubEnvs) {
		appSubEnvsRef.set(appSubEnvs);
	}

	static {
		m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
		appId = m_configUtil.getAppId();
		env = m_configUtil.getApolloEnv();
		subEnv = m_configUtil.getCluster();
		isPro1 = (Env.PRO == env);
	}

	public static boolean isPro() {
		return isPro1;
	};

	public static String getAppId() {
		return appId;
	}

	public static String getEnv() {
		return env.toString();
	}

	public static String getSubEnv() {
		return subEnv;
	}

	public static String getTargetSubEnv() {
		return targetSubEnv.get();
	}

	public static void setTargetSubEnv(String targetSubEnv1) {
		targetSubEnv.set(targetSubEnv1);
	}

	public static void clear() {
		targetSubEnv.remove();
	}	
	
}
