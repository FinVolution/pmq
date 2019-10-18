package com.ppdai.infrastructure.mq.client;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;

@RunWith(JUnit4.class)
public class MqConfigTest {

	@Test
	public void configTest() {
		MqConfig config=new MqConfig();
		config.setAsynCapacity(2);
		config.setIp(IPUtil.getLocalIP());
		config.setLogOrigData(1);
		config.setMetaMode(true);
		config.setPbRetryTimes(3);
		config.setProperties(new HashMap<String, String>());
		config.setReadTimeOut(1);
		config.setServerPort("800");
		config.setUrl("http://localhost");
	}
}
