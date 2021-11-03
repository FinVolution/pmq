package com.ppdai.infrastructure.mq.client;

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
import com.ppdai.infrastructure.mq.client.core.IMsgNotifyService;
import com.ppdai.infrastructure.mq.client.core.impl.MsgNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MqClientTool {
    static IMsgNotifyService msgNotifyService = new MsgNotifyService();
    private static final Logger logger = LoggerFactory.getLogger(MqClientTool.class);

    public static String data() {
        if (MqClient.getContext().getConfig().isMqclientopen()) {
            MqClient.getContext().setBrokerIp(MqClient.getContext().getMqResource().getBrokerIp());
            return JsonUtil.toJsonNull(MqClient.getContext());
        }
        return "";
    }

    public static String trace() {
        if (MqClient.getContext().getConfig().isMqclientopen()) {
            return JsonUtil.toJsonNull(TraceFactory.getTraces());
        }
        return "";
    }


    public static String rs() {
        MqClient.reStart();
        return "Ok";
    }

    public static ProxyResponse proxy(ProxyRequest request) {
        ProxyResponse response = new ProxyResponse();
        response.setSuc(true);
        if (request != null && request.getMsgs() != null && request.getMsgs().size() > 0) {
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
                response.setFailIds(request.getMsgs().stream().map(p -> p.getId()).collect(Collectors.toList()));
                transaction.setStatus(e);
            }
            transaction.complete();
        }
        //MqClient.getContext().getMqSubscriber();

        return response;
    }

    private static List<MessageDto> convertDto(List<ProxyDto> msgs) {
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

    public static void notify(MsgNotifyRequest request) {
        Transaction transaction = Tracer.newTransaction("mq-client", "/mq/client/notify");
        try {
            msgNotifyService.notify(request);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        }
        transaction.complete();

    }


    public static String traceItem(String consumerGroupName, long queueId) {
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


    public static String dm() {
        if (MqClient.getContext().getConfig().isMqclientopen()) {
            return JsonUtil.toJsonNull(Tracer.getDomain());
        }
        return "";
    }

    public static String hs() {
        return "Ok";
    }


    public static String th() {
        if (MqClient.getContext().getConfig().isMqclientopen()) {
            StringBuilder rs = new StringBuilder();
            Map<Thread.State, Integer> state = new HashMap<>();
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
            for (Map.Entry<Thread.State, Integer> t1 : state.entrySet()) {
                rs1.append("线程状态：" + t1.getKey() + ",数量：" + t1.getValue() + "<br/>\n");
            }
            return rs1.toString() + rs.toString();
        }
        return "";

    }
}
