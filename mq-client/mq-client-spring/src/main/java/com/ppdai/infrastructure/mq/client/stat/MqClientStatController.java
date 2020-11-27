package com.ppdai.infrastructure.mq.client.stat;

import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.client.MsgNotifyRequest;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyDto;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyRequest;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyResponse;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.core.IMsgNotifyService;
import com.ppdai.infrastructure.mq.client.core.impl.MsgNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class MqClientStatController {
	IMsgNotifyService msgNotifyService = new MsgNotifyService();
	private static final Logger logger = LoggerFactory.getLogger(MqClientStatController.class);

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

	@GetMapping("/mq/client/rs")
	public String rs() {
		MqClient.reStart();
		return "Ok";
	}

	@PostMapping("/mq/client/consume")
	public ProxyResponse proxy(@RequestBody ProxyRequest request) {
		ProxyResponse response = new ProxyResponse();
		response.setSuc(true);
		if (request != null && !CollectionUtils.isEmpty(request.getMsgs())) {
			Transaction transaction = Tracer.newTransaction("mq-queue-thread-handleMessage-proxy", request.getMsgs().get(0).getTopicName());
			try {
				ProxyDto proxyDto = request.getMsgs().get(0);
				ISubscriber iSubscriber1 = MqClient.getContext().getSubscriber(proxyDto.getConsumerGroupName(), proxyDto.getTopicName());
				if (iSubscriber1 != null) {
					List<MessageDto> messageDtos = convertDto(request.getMsgs());
					List<Long> failIds = iSubscriber1.onMessageReceived(messageDtos);
					response.setFailIds(failIds);
				}
				transaction.setStatus(Transaction.SUCCESS);
			} catch (Throwable e) {
				logger.error("proxy", e);
				response.setFailIds(request.getMsgs().stream().map(p->p.getId()).collect(Collectors.toList()));
				transaction.setStatus(e);
			}
			transaction.complete();
		}
		//MqClient.getContext().getMqSubscriber();

		return response;
	}

	private List<MessageDto> convertDto(List<ProxyDto> msgs) {
		List<MessageDto> rs = new ArrayList<>(msgs.size());
		msgs.forEach(t -> {
            /*private long id;
            private String topicName;
            private String consumerGroupName;
            //可能为空
            private String bizId;
            //可能为空
            private Map<String, String> head;
            private String body;
            //可能为空
            private String traceId;
            private String sendIp;
            // yyyy-MM-dd HH:mm:ss:SSS
            private Date insertTime;*/
			MessageDto messageDto = new MessageDto();
			messageDto.setId(t.getId());
			messageDto.setTopicName(t.getTopicName());
			messageDto.setConsumerGroupName(t.getConsumerGroupName());
			messageDto.setBizId(t.getBizId());
			messageDto.setHead(t.getHead());
			messageDto.setBody(t.getBody());
			messageDto.setTraceId(t.getTraceId());
			messageDto.setSendIp(t.getSendIp());
			messageDto.setSendTime(t.getInsertTime());
			rs.add(messageDto);
		});
		return rs;
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
