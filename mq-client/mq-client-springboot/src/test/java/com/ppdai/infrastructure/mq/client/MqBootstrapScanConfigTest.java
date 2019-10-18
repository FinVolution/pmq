package com.ppdai.infrastructure.mq.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.ppdai.infrastructure.mq.client.stat.MqFilter;

@RunWith(JUnit4.class)
public class MqBootstrapScanConfigTest {
	@Test
	public void test() {
		MqBootstrapScanConfig mqBootstrapScanConfig=new MqBootstrapScanConfig();
		FilterRegistrationBean filterRegistrationBean=mqBootstrapScanConfig.ppdfilter(new MqFilter());
		assertEquals(1, filterRegistrationBean.getUrlPatterns().size());
	}
}
