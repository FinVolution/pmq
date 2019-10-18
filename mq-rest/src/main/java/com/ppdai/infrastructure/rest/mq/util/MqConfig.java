package com.ppdai.infrastructure.rest.mq.util;

import java.text.SimpleDateFormat;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.support.http.StatViewServlet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class MqConfig {
	@Bean
	@Primary
	// @ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper jacksonObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"));
		// objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	@Bean
	public ServletRegistrationBean druidStatViewServle() {

		// org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.

		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),
				"/druid/*");

		// 添加初始化参数：initParams
		//
		// //白名单：
		//
		// servletRegistrationBean.addInitParameter("allow","127.0.0.1");
		//
		// //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not
		// permitted to view this page.
		//
		// servletRegistrationBean.addInitParameter("deny","127.0.0.1");

		// 登录查看信息的账号密码.

		servletRegistrationBean.addInitParameter("loginUsername", "admin");

		servletRegistrationBean.addInitParameter("loginPassword", "admin");

		// 是否能够重置数据.

		servletRegistrationBean.addInitParameter("resetEnable", "false");

		return servletRegistrationBean;

	}

}
