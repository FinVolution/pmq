package com.ppdai.infrastructure.mq.biz.common.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ppdai.infrastructure.mq.biz.common.exception.SoaException;
import com.ppdai.infrastructure.mq.biz.common.trace.CatContext;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

@Aspect
@Component
public class ControllerAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);

	@Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void anyController() {
	}

	@Around("anyController()")
	public Object invokeWithCatTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			addCatChain(request);
			if (request == null) {
				return doExcute(joinPoint);
			} else {
				// MDC.put("guid", UUID.randomUUID().toString().replaceAll("-",
				// "_"));
				// 正常情况下过滤掉长连接
				String url = request.getRequestURI();

				// boolean flag = true;
				Transaction catTransaction = null;
				try {
					if (!url.equals("/hs")) {
						catTransaction = Tracer.newTransaction("Service", url);
					}
					Object result = doExcute(joinPoint);					
					if (catTransaction != null) {
						catTransaction.setStatus(Transaction.SUCCESS);
					}
					return result;
				} catch (SoaException ex) {
					if (catTransaction != null) {
						catTransaction.setStatus(ex.getException());
					}
					LOGGER.error(request.getRequestURL().toString(), ex.getException());
					return ex.getResponse();
				} catch (Throwable ex) {
					if (catTransaction != null) {
						catTransaction.setStatus(ex);
					}
					LOGGER.error(request.getRequestURL().toString(), ex);
					throw  ex;
				} finally {
					if (catTransaction != null) {
						catTransaction.complete();
					}
				}
			}
		} else {
			return doExcute(joinPoint);
		}
	}

	private Object doExcute(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.nanoTime();
		Object result = joinPoint.proceed();
		if (result instanceof BaseResponse) {
			((BaseResponse) result).setTime(System.nanoTime() - start);
		}
		return result;
	}

	private void addCatChain(HttpServletRequest request) {
		try {
			if (!StringUtils.isEmpty(request.getHeader(CatContext.ROOT))) {
				CatContext propertyContext = new CatContext();
				propertyContext.addProperty(CatContext.ROOT, request.getHeader(CatContext.ROOT));
				propertyContext.addProperty(CatContext.PARENT, request.getHeader(CatContext.PARENT));
				propertyContext.addProperty(CatContext.CHILD, request.getHeader(CatContext.CHILD));
				Tracer.logRemoteCallServer(propertyContext);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
