package com.ppdai.infrastructure.mq.client.stat;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.client.MsgNotifyRequest;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.core.IMsgNotifyService;
import com.ppdai.infrastructure.mq.client.core.impl.MsgNotifyService;

@RestController
public class MqClientStatController {
	IMsgNotifyService msgNotifyService = new MsgNotifyService();

	final String MQ_CLINET_STAT_OPEN = "mq.client.stat.open";
	@Autowired
	private Environment env;

	private boolean isOpenFlag() {
		return "true".equalsIgnoreCase(env.getProperty(MQ_CLINET_STAT_OPEN, "true"));
	}

	@GetMapping("/mq/client/cache")
	public String data() {
		if (isOpenFlag()) {
			MqClient.getContext().setBrokerIp(MqClient.getContext().getMqResource().getBrokerIp());
			return JsonUtil.toJsonNull(MqClient.getContext());
		}
		return "";
	}

	@GetMapping("/mq/client/trace")
	public String trace() {
		if (isOpenFlag()) {
			return JsonUtil.toJsonNull(TraceFactory.getTraces());
		}
		return "";
	}

	@RequestMapping("/mq/client/notify")
	public void notify(@RequestBody MsgNotifyRequest request) {
		if (isOpenFlag()) {
			Transaction transaction = Tracer.newTransaction("mq-client", "/mq/client/notify");
			try {
				msgNotifyService.notify(request);
				transaction.setStatus(Transaction.SUCCESS);
			} catch (Exception e) {
				transaction.setStatus(e);
			}
			transaction.complete();
		}
	}

	@GetMapping("/mq/client/traceItem")
	public String traceItem(String consumerGroupName, long queueId) {
		if (isOpenFlag()) {
			Map<String, TraceMessage> rsMap = new HashMap<>();
			rsMap.put("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId, TraceFactory
					.getInstance("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId));
			rsMap.put("MqQueueExcutorService-处理-" + consumerGroupName + "-queueId-" + queueId, TraceFactory
					.getInstance("MqQueueExcutorService-处理-" + consumerGroupName + "-queueId-" + queueId));

			rsMap.put("MqQueueExcutorService-拉取状态-" + consumerGroupName + "-queueId-" + queueId, TraceFactory
					.getInstance("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId));
			rsMap.put("MqQueueExcutorService-提交偏移-" + consumerGroupName + "-queueId-" + queueId, TraceFactory
					.getInstance("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId));
			return JsonUtil.toJsonNull(rsMap);
		}
		return "";
	}	

	@GetMapping("/mq/client/dm")
	public String dm() {
		if (isOpenFlag()) {
			return JsonUtil.toJsonNull(Tracer.getDomain());
		}
		return "";
	}

	@GetMapping("/mq/client/hs")
	public String hs() {
		return "Ok";
	}

	@GetMapping("/mq/client/th")
	public String th() {
		if (isOpenFlag()) {
			StringBuilder rs = new StringBuilder();
			Map<State, Integer> state = new HashMap<>();
			for (Map.Entry<Thread, StackTraceElement[]> t1 : Thread.getAllStackTraces().entrySet()) {
				Thread thread = t1.getKey();
				StackTraceElement[] stackTraceElements = t1.getValue();
				state.putIfAbsent(thread.getState(), 0);
				state.put(thread.getState(), state.get(thread.getState()) + 1);
				rs.append("\n<br/>线程名称：" + thread.getName() + ",线程id:" + thread.getId() + ",16进制为："
						+ Long.toHexString(thread.getId()) + ",线程优先级为：" + thread.getPriority() + "，线程状态："
						+ thread.getState() + "<br/>\n");
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
		return "";
	}
}
