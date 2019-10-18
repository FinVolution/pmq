package com.ppdai.infrastructure.mq.biz.common.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

public class LogFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		MDC.put("guid", UUID.randomUUID().toString().replaceAll("-", "_"));
		Transaction catTransaction = null;
		try {
			HttpServletRequest rq = (HttpServletRequest) request;
			String url = rq.getRequestURI();
			if (url.indexOf(".thrift") != -1) {
				catTransaction = Tracer.newTransaction("Service-Filter", url);
			}
		} catch (Exception e) {
		}
		try {
			chain.doFilter(request, response);
			if (catTransaction != null) {
				catTransaction.setStatus(Transaction.SUCCESS);
			}
		} catch (Exception e) {
			if (catTransaction != null) {
				catTransaction.setStatus(e);
			}
			throw e;
		} finally {
			if (catTransaction != null) {
				catTransaction.complete();
			}
			MDC.remove("guid");
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
