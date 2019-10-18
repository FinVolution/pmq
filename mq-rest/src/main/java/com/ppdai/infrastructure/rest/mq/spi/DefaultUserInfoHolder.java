package com.ppdai.infrastructure.rest.mq.spi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.dto.UserInfo;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;

/*
 * 全流程测试使用 
 */
@Service
public class DefaultUserInfoHolder implements UserInfoHolder{ 
	@Autowired
	private SoaConfig soaConfig;
    @Override
    public UserInfo getUser() {
    	UserInfo userInfo=new UserInfo();
    	userInfo.setAdmin(true);
    	userInfo.setUserId(soaConfig.getMqAdminUser());
    	userInfo.setName(soaConfig.getMqAdminUser());
    	return userInfo;
    }
    @Override
    public String getUserId() {
        return soaConfig.getMqAdminUser();
    }
	@Override
	public void setUserId(String userId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
