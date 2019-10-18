package com.ppdai.infrastructure.ui.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;

import java.util.List;

/**
 * TraceController
 *
 * @author wanghe
 * @date 2018/06/07
 */
@Controller
@RequestMapping("/trace")
public class TraceController {
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserInfoHolder userInfoHolder;
	@Autowired
	private SoaConfig soaConfig;

	@RequestMapping("/hide")
	@ResponseBody
	public int hide(HttpServletRequest request) {
		return roleService.getRole(userInfoHolder.getUserId(), null);
	}

	@RequestMapping("/hideMessageTool")
	@ResponseBody
	public int hideMessageTool(HttpServletRequest request) {
		boolean isPro = soaConfig.isPro();
		//获取界面发送工具的授权用户
		List<String> authorizedList=soaConfig.getAuthorizedUsers();

		if(isPro){
			if(roleService.getRole(userInfoHolder.getUserId(), null)==0){
				//当生产环境时，只有超级管理员可以展示发送消息的"管理工具"
				return 0;
			}else if(authorizedList.contains(userInfoHolder.getUserId())){
				//如果用户已经授权，则展示发送工具
				return 0;
			}else{
				return 1;
			}

		}else {
			//非生产环境时，可以展示发送消息的"管理工具"
			return 0;
		}
	}

}
