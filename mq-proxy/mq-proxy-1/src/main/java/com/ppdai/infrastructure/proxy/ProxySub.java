package com.ppdai.infrastructure.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ppdai.infrastructure.mq.biz.common.util.BrokerException;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyDto;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyRequest;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyResponse;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;

public class ProxySub implements ISubscriber {
	private static HttpClient rtPostClient = new HttpClient(60000, 60000);
	private String exUrl = "";

	@Override
	public List<Long> onMessageReceived(List<MessageDto> messages) {
		initUrl();		
		if( SpringUtil.getApplicationContext().getEnvironment().getProperty("mq.client.pre","0").equals("1")){
			return null;
		}		
		ProxyRequest request = new ProxyRequest();
		List<ProxyDto> msgs = new ArrayList<>(messages.size());
		request.setMsgs(msgs);
		messages.forEach(t -> {
			msgs.add(convertProxyDto(t));
		});
		try {
			ProxyResponse response = remoteCall(messages.get(0).getConsumerGroupName(), request);
			return response.getFailIds();
		} catch (Exception e) {
			try {
				ProxyResponse response = remoteCall(messages.get(0).getConsumerGroupName(), request);
				return response.getFailIds();
			} catch (Exception e1) {
				
			}
		}
		List<Long> failIds = new ArrayList<>();
		messages.forEach(t1 -> {
			failIds.add(t1.getId());
		});
		return failIds;
	}

	private ProxyDto convertProxyDto(MessageDto t1) {
		ProxyDto proxyDto=new ProxyDto();
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

	private ProxyResponse remoteCall(String consumerGroupName, ProxyRequest request)
			throws IOException, BrokerException {
		long start = System.currentTimeMillis();
		ProxyResponse response = rtPostClient.post(exUrl, request, ProxyResponse.class);
		long end = System.currentTimeMillis();
		MetricSingleton.getMetricRegistry()
				.histogram("mq.client.proxy.network.time?consumerGroupName=" + consumerGroupName)
				.update(end - start - response.getTime());
		return response;
	}

	private void initUrl() {
		while (SpringUtil.getApplicationContext() == null) {
			Util.sleep(10);
		}		
		exUrl = SpringUtil.getApplicationContext().getEnvironment().getProperty("mq.client.proxy.exe.url");
	}
}
