package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.dto.UserRoleEnum;
import com.ppdai.infrastructure.mq.biz.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	private SoaConfig soaConfig;

	@Override
	public int getRole(String userId, String ownerIds) {
		if (StringUtils.isNotEmpty(userId) && isAdmin(userId)) {
			return UserRoleEnum.SUPER_USER.getRoleCode();
		} else if (StringUtils.isNotEmpty(userId) && StringUtils.isNotEmpty(ownerIds)
				&& Arrays.asList(ownerIds.split(",")).contains(userId)) {
			return UserRoleEnum.OWNER.getRoleCode();
		} else {
			return UserRoleEnum.USER.getRoleCode();
		}
	}

	@Override
	public int getRole(String userId) {
		if (StringUtils.isNotEmpty(userId) && isAdmin(userId)) {
			return UserRoleEnum.SUPER_USER.getRoleCode();
		} else {
			return UserRoleEnum.USER.getRoleCode();
		}
	}

	@Override
	public String getRoleName(String userId) {
		if (StringUtils.isNotEmpty(userId) && isAdmin(userId)) {
			return UserRoleEnum.SUPER_USER.getDescription();
		} else {
			return UserRoleEnum.USER.getDescription();
		}
	}

	@Override
	public boolean isAdmin(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return false;
		}
		if (soaConfig.getMqAdminUser().equals(userId)) {
			return true;
		}
		List<String> userIdList = soaConfig.getAdminUserIds();
		if (userIdList.contains(userId)) {
			return true;
		}
		return false;
	}
}
