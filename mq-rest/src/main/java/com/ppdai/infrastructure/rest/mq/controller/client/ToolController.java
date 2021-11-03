package com.ppdai.infrastructure.rest.mq.controller.client;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.EmailService;
import com.ppdai.infrastructure.mq.biz.service.LogService;

@RestController
@RequestMapping(MqConstanst.TOOLPRE)
public class ToolController {
	@Autowired
	private Environment env;
	@Autowired
	private LogService logService;
	@Autowired
	private ConsumerGroupService consumerGroupService;

	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailUtil emailUtil;

	@PostMapping("/addLog")
	public void addLog(@RequestBody LogRequest request) {
		logService.addConsumerLog(request);
	}

	@GetMapping("/mtest")
	public void mtest() {
		emailUtil.sendErrorMail("test", "test");
	}

	@PostMapping("/addCat")
	public CatResponse addCat(@RequestBody CatRequest request) {
		if (request != null && "1".equals(env.getProperty("mq.cat.enable", "1"))) {
			Transaction transaction = Tracer.newTransaction("mq-client-error",
					request.getMethod() + "-" + request.getClientIp());
			transaction.setStatus(Transaction.SUCCESS);
			transaction.addData("msg", request.getMsg());
			transaction.addData("json", request.getJson());
			transaction.complete();
		}
		CatResponse response = new CatResponse();
		response.setSuc(true);
		return response;
	}

	@PostMapping("/addOpLog")
	public OpLogResponse addOpLog(@RequestBody OpLogRequest request) {
		logService.addOpLog(request);
		OpLogResponse response = new OpLogResponse();
		response.setSuc(true);
		return response;
	}

	@PostMapping("/sendMail")
	public SendMailResponse sendMail(@RequestBody SendMailRequest request) {
		emailService.sendConsumerMail(request);
		SendMailResponse response = new SendMailResponse();
		response.setSuc(true);
		return response;
	}

	@PostMapping("/rb")
	public UpdateMetaResponse rb(@RequestBody UpdateMetaRequest request) {
		if (request != null&&"1".equals(env.getProperty("mq.client.rb", "0"))) {
			consumerGroupService.notifyRbByNames(request.getConsumerGroupNames());
		}
		UpdateMetaResponse response = new UpdateMetaResponse();
		response.setSuc(true);
		return response;
	}

	@RequestMapping("/getIp")
	public String getIp() {		
		return IPUtil.getLocalIP();
	}

	@GetMapping("/th")
	public String th() {
		StringBuilder rs = new StringBuilder();
		Map<State, Integer> state = new HashMap<>();
		for (Map.Entry<Thread, StackTraceElement[]> t1 : Thread.getAllStackTraces().entrySet()) {
			Thread thread = t1.getKey();
			StackTraceElement[] stackTraceElements = t1.getValue();
			// if (thread.equals(Thread.currentThread())) {
			// continue;
			// }
			state.putIfAbsent(thread.getState(), 0);
			state.put(thread.getState(), state.get(thread.getState()) + 1);
			rs.append("\n<br/>线程名称：" + thread.getName() + ",线程id:" + thread.getId() + ",16进制为："
					+ Long.toHexString(thread.getId()) + "，线程状态：" + thread.getState() + "<br/>\n");
			for (StackTraceElement st : stackTraceElements) {
				rs.append(st.toString() + "<br/>\n");
			}
		}
		StringBuilder rs1 = new StringBuilder();
		for (Map.Entry<State, Integer> t1 : state.entrySet()) {
			rs1.append("线程状态：" + t1.getKey() + ",数量：" + t1.getValue() + "<br/>\n");
		}
		return rs1.toString() + rs.toString();
	}
}
