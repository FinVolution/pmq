package com.ppdai.infrastructure.ui.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.ServerGetListRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ServerGetListResponse;
import com.ppdai.infrastructure.ui.service.UiServerService;

import java.util.List;

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
	public ServerGetListResponse findBy(ServerGetListRequest serverGetListRequest) {
		return uiServerService.findBy(serverGetListRequest);
	}

	@RequestMapping("/changeStatusFlag")
	public BaseUiResponse changeStatusFlag(String serverId) {
		return uiServerService.changeStatusFlag(serverId);
	}

	@RequestMapping("/batchPull")
	@ResponseBody
	public BaseUiResponse batchPull(@RequestParam("servers") String servers) {
		List<ServerEntity> serverList = JSONObject.parseArray(servers, ServerEntity.class);

		return uiServerService.batchPull(serverList);
	}

	@RequestMapping("/batchPush")
	@ResponseBody
	public BaseUiResponse batchPush(@RequestParam("servers") String servers) {
		List<ServerEntity> serverList = JSONObject.parseArray(servers, ServerEntity.class);
		return uiServerService.batchPush(serverList);
	}

	@RequestMapping("/onLineServer")
	public BaseUiResponse<String> onLineServer(){
		return uiServerService.onLineServer();

	}

}
