package com.ppdai.infrastructure.ui.spi;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dto.UserInfo;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;

@Service
public class DefaultUserInfoHolder implements UserInfoHolder {
	@Autowired
	private UserProviderService userProviderService;
	private ThreadLocal<String> userIdLocal = new ThreadLocal<>();

	@Override
	public UserInfo getUser() {
		String userId = userIdLocal.get();
		Map<String, UserInfo> mapUser = userProviderService.getUsers();
		if (mapUser.containsKey(userId)) {
			return mapUser.get(userId);
		}
		return null;
	}

	@Override
	public String getUserId() {
		return userIdLocal.get();

	}
	
	@Override
	public void setUserId(String userId) {
		userIdLocal.set(userId);

	}
	@Override
	public void clear() {
		userIdLocal.remove();
	}
}
