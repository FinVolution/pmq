package com.ppdai.infrastructure.mq.client.subEnv;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.MqEnv;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.client.MqClient;

@Component
public class SubEnvFilter implements Filter {
	@Autowired
	private Environment env;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	private String getSubRouteKey() {
		return env.getProperty("MQ-SUB-ENV-KEY", "X-PPD-ROUTER-TARGET");
	}

	private String getSubEnv(HttpServletRequest httpRequest) {
		String targetSubEnv = httpRequest.getHeader(getSubRouteKey());
		if (Util.isEmpty(targetSubEnv)) {
			targetSubEnv = httpRequest.getHeader(getSubRouteKey().toLowerCase());
			if (Util.isEmpty(targetSubEnv)) {
				targetSubEnv = httpRequest.getHeader("rd_sub_env");
				if (Util.isEmpty(targetSubEnv)) {
					targetSubEnv = httpRequest.getHeader("x-rd-sub-env");
				}
			}
		}
		return targetSubEnv;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			try {
				if (MqClient.getMqEnvironment() != null && MqClient.getMqEnvironment().getEnv() == MqEnv.FAT) {
					HttpServletRequest httpRequest = (HttpServletRequest) request;
					String subEnv = getSubEnv(httpRequest);
					if (!Util.isEmpty(subEnv) && MqClient.getMqEnvironment() != null) {
						MqClient.getMqEnvironment().setTargetSubEnv(subEnv);
					}
				}
			} catch (Throwable e) {
				// TODO: handle exception
			}

			chain.doFilter(request, response);
		} finally {
			if (MqClient.getMqEnvironment() != null) {
				MqClient.getMqEnvironment().clear();
			}
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
