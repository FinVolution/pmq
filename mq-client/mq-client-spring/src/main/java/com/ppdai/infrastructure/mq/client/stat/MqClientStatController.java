package com.ppdai.infrastructure.mq.client.stat;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.client.MqClient;

@RestController
public class MqClientStatController {
	@GetMapping("/mq/client/cache")
	public String data() {
		MqClient.getContext().setBrokerIp(MqClient.getContext().getMqResource().getBrokerIp());
		return JsonUtil.toJsonNull(MqClient.getContext());
	}

	@GetMapping("/mq/client/trace")
	public String trace() {
		return JsonUtil.toJsonNull(TraceFactory.getTraces());
	}

	@GetMapping("/mq/client/traceItem")
	public String traceItem(String consumerGroupName, long queueId) {
		Map<String, TraceMessage> rsMap = new HashMap<>();
		rsMap.put("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId,
				TraceFactory.getInstance("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId));
		rsMap.put("MqQueueExcutorService-处理-" + consumerGroupName + "-queueId-" + queueId,
				TraceFactory.getInstance("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId));

		rsMap.put("MqQueueExcutorService-拉取状态-" + consumerGroupName + "-queueId-" + queueId,
				TraceFactory.getInstance("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId));
		rsMap.put("MqQueueExcutorService-提交偏移-" + consumerGroupName + "-queueId-" + queueId,
				TraceFactory.getInstance("MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + queueId));
		return JsonUtil.toJsonNull(rsMap);
	}

	@GetMapping("/mq/client/dm")
	public String dm() {
		return Tracer.getDomain();
	}

	@GetMapping("/mq/client/hs")
	public String hs() {
		return "Ok";
	}

	@GetMapping("/mq/client/th")
	public String th() {
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
}
