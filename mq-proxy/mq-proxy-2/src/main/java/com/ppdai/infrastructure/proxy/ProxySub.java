package com.ppdai.infrastructure.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.util.BrokerException;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyDto;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyRequest;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyResponse;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.proxy.service.HsCheckService;
import com.ppdai.infrastructure.proxy.util.MetricSingleton;

@Component
public class ProxySub implements ISubscriber {
	private static Logger log = LoggerFactory.getLogger(ProxySub.class);
	private static HttpClient rtPostClient = new HttpClient(60000, 60000);
	@Autowired
	private HsCheckService hsCheckService;

	@Override
	public List<Long> onMessageReceived(List<MessageDto> messages) {		
		String exUrl = ProxyService.getExeUrl(messages.get(0).getConsumerGroupName());
		ProxyRequest request = new ProxyRequest();
		List<ProxyDto> msgs = new ArrayList<>(messages.size());
		request.setMsgs(msgs);
		List<Long> failIds = new ArrayList<>();
		messages.forEach(t -> {
			msgs.add(convertProxyDto(t));
			failIds.add(t.getId());
		});
		try {
			ProxyResponse response = remoteCall(exUrl, messages.get(0).getConsumerGroupName(), request);
			return response.getFailIds();
		} catch (Exception e) {
			try {
				ProxyResponse response = remoteCall(exUrl, messages.get(0).getConsumerGroupName(), request);
				return response.getFailIds();
			} catch (Exception e1) {
				hsCheckService.updateHs(ProxyService.getHsUrl(messages.get(0).getConsumerGroupName()), false);
				log.warn(exUrl+"调用异常", e1);	
			}
		}		
		return failIds;
	}

	private ProxyDto convertProxyDto(MessageDto t1) {
		ProxyDto proxyDto = new ProxyDto();
		proxyDto.setBizId(t1.getBizId());
		proxyDto.setBody(t1.getBody());
		proxyDto.setConsumerGroupName(t1.getConsumerGroupName());
		proxyDto.setHead(t1.getHead());
		proxyDto.setId(t1.getId());
		proxyDto.setInsertTime(t1.getSendTime());
		proxyDto.setSendIp(t1.getSendIp());
		proxyDto.setTopicName(t1.getTopicName());
		proxyDto.setTraceId(t1.getTraceId());
		return proxyDto;
	}

	private ProxyResponse remoteCall(String exUrl, String consumerGroupName, ProxyRequest request)
			throws IOException, BrokerException {
		long start = System.currentTimeMillis();
		ProxyResponse response = rtPostClient.post(exUrl, request, ProxyResponse.class);
		long end = System.currentTimeMillis();
		MetricSingleton.getMetricRegistry()
				.histogram("mq.client.proxy.network.time?consumerGroupName=" + consumerGroupName)
				.update(end - start - response.getTime());
		return response;
	}
}
