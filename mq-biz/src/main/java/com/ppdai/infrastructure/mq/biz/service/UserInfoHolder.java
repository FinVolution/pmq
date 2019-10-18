package com.ppdai.infrastructure.mq.biz.service;

import com.ppdai.infrastructure.mq.biz.dto.UserInfo;

/**
 * @author tongfeifan
 */
public interface UserInfoHolder {
	UserInfo getUser();

	String getUserId();

	void setUserId(String userId);

	void clear();
}
