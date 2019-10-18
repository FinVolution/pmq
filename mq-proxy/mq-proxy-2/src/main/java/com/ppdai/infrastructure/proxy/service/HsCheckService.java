package com.ppdai.infrastructure.proxy.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;
import com.ppdai.infrastructure.proxy.ProxyService;
import com.ppdai.infrastructure.proxy.util.SpringUtil;

@Component
public class HsCheckService implements PreHandleListener {
	private static Map<String, Boolean> hsUrlMap = new ConcurrentHashMap<>(400);
	private static HttpClient htCheckClient = new HttpClient(1500, 1500);
	@Autowired
	private Environment env;

	@Override
	public boolean preHandle(ConsumerQueueDto consumerQueue) {
		while (SpringUtil.getApplicationContext() == null) {
			Util.sleep(10);
		}
		if (SpringUtil.getApplicationContext().getEnvironment().getProperty("mq.client.pre", "0").equals("1")) {
			return true;
		}
		String consumerGroupName = consumerQueue.getConsumerGroupName();
		String hsUrl = ProxyService.getHsUrl(consumerGroupName);
		String exeUrl = ProxyService.getExeUrl(consumerGroupName);
		if (Util.isEmpty(hsUrl) || Util.isEmpty(exeUrl)) {
			return false;
		}
		if (!hsUrlMap.containsKey(hsUrl)) {
			hsUrlMap.put(hsUrl, htCheckClient.check(hsUrl));
		}
		//当远程方法调用发生异常时，会将健康检查设置为false，然后下次调用时，开启健康检查
		while (!hsUrlMap.get(hsUrl)) {
			doCheckHsUrl(hsUrl);
			hsUrl = ProxyService.getHsUrl(consumerGroupName);
			if (!hsUrlMap.containsKey(hsUrl)) {
				hsUrlMap.put(hsUrl, htCheckClient.check(hsUrl));
			}
			Util.sleep(Long.parseLong(env.getProperty("mq.proxy.sleep", "10000")));
		}
		return true;
	}

	private boolean doCheckHsUrl(String hsUrl) {
		if (!htCheckClient.check(hsUrl)) {
			hsUrlMap.put(hsUrl, false);
			return false;
		} else {
			hsUrlMap.put(hsUrl, true);
			return true;
		}
	}

	public void updateHs(String hsUrl, boolean value) {
		hsUrlMap.put(hsUrl, value);
	}

	public String getInfo() {
		return "local hs is:" + JsonUtil.toJsonNull(hsUrlMap);
	}

}
