package com.ppdai.infrastructure.mq.client;

import java.util.List;
import java.util.Set;

import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.MqEnv;

public interface MqEnvironment {
	public static final String DEFAULT_SUBENV = MqConst.DEFAULT_SUBENV;
	
	boolean isPro();

	String getAppId();

	//获取环境名称，默认环境名称是fat,uat,pre,pro，注意只有fat才支持多环境
	MqEnv getEnv();
	//获取子环境名称
	String getSubEnv();
	//获取目标环境名称
	String getTargetSubEnv();
	//设置目标环境名称
	void setTargetSubEnv(String targetSubEnv1);
	//清理目标环境名称
	void clear();
	//获取当前应用部署的子环境列表
	Set<String> getAppSubEnvs();
	//设置当前应用部署的子环境列表
	void setAppSubEnvs(List<String> appSubEnvs);
}
