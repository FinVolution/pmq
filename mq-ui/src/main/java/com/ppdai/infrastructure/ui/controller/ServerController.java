package com.ppdai.infrastructure.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ServerGetListResponse;
import com.ppdai.infrastructure.ui.service.UiServerService;

/**
 * @Author：wanghe02 @Date：2018/11/27 17:31
 */
@RestController
@RequestMapping("/server")
public class ServerController {
	@Autowired
	private UiServerService uiServerService;
	Logger log = LoggerFactory.getLogger(ServerController.class);

	@RequestMapping("/list/data")
	public ServerGetListResponse findBy(BaseUiRequst baseUiRequst) {
		return uiServerService.findBy(baseUiRequst);
	}

	@RequestMapping("/changeStatusFlag")
	public BaseUiResponse changeStatusFlag(String serverId) {
		return uiServerService.changeStatusFlag(serverId);
	}

}
