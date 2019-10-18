package com.ppdai.infrastructure.ui.spi;

import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.Organization;
import com.ppdai.infrastructure.mq.biz.dto.UserInfo;

//用户信息提供方
public interface UserProviderService {
	//获取部门
	Map<String, Organization> getOrgs() ;
	//获取所有用户信息
	Map<String, UserInfo> getUsers();
	boolean login(String username, String password);
}
