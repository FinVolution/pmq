package com.ppdai.infrastructure.rest.mq.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetResponse;
import com.ppdai.infrastructure.mq.biz.service.ConsumerCommitService;

@RestController
@RequestMapping(MqConstanst.CONSUMERPRE)
public class ConsumerCommitController {
	@Autowired
	private ConsumerCommitService consumerCommitService;

	// 发送心跳，直接返回
	@PostMapping("/commitOffset")
	public CommitOffsetResponse commitOffset(@RequestBody CommitOffsetRequest request) {
		return consumerCommitService.commitOffset(request);
	}

	@GetMapping("/getCommitOffsetCache")
	public Object getCommitOffsetCache() {
		return consumerCommitService.getCache();
	}

}
