package com.ppdai.infrastructure.mq.biz.common.trace;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.trace.internals.NullMessageProducerManager;
import com.ppdai.infrastructure.mq.biz.common.trace.internals.cat.CatContextProxy;
import com.ppdai.infrastructure.mq.biz.common.trace.internals.cat.CatNames;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.MessageProducer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.MessageProducerManager;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

public abstract class Tracer {
	private static final Logger logger = LoggerFactory.getLogger(Tracer.class);
	private static final MessageProducerManager NULL_MESSAGE_PRODUCER_MANAGER = new NullMessageProducerManager();
	private static volatile MessageProducerManager producerManager;
	private static Object lock = new Object();
	private static Class CAT_CONTEXT_CLASS;
	private static Method LOGREMOTECALLCLIENT;
	private static Method LOGREMOTECALLSERVER;
	private static Class CAT_CLASS;
	private static ICatSkip catSkip;	

	static {
		getProducer();
		try {
			CAT_CLASS = Class.forName(CatNames.CAT_CLASS);
			Method[] methods = CAT_CLASS.getMethods();
			for (Method m : methods) {
				if (m.getName().equals(CatNames.LOG_REMOTE_CALL_CLIENT)) {
					LOGREMOTECALLCLIENT = m;
				} else if (m.getName().equals(CatNames.LOG_REMOTE_CALL_SERVER)) {
					LOGREMOTECALLSERVER = m;
				}
			}
			CAT_CONTEXT_CLASS = LOGREMOTECALLCLIENT.getParameterTypes()[0];
		} catch (Exception e) {

		}
	}

	private static MessageProducer getProducer() {
		try {
			if (producerManager == null) {
				synchronized (lock) {
					if (producerManager == null) {
						producerManager = ServiceBootstrap.loadFirst(MessageProducerManager.class);
					}
				}
			}
		} catch (Throwable ex) {
			logger.error("Failed to initialize message producer manager, use null message producer manager.", ex);
			producerManager = NULL_MESSAGE_PRODUCER_MANAGER;
		}
		return producerManager.getProducer();
	}

	public static void logError(String message, Throwable cause) {
		try {
			getProducer().logError(message, cause);
		} catch (Throwable ex) {
			logger.warn("Failed to log error for message: {}, cause: {}", message, cause, ex);
		}
	}

	public static void logError(Throwable cause) {
		try {
			getProducer().logError(cause);
		} catch (Throwable ex) {
			logger.warn("Failed to log error for cause: {}", cause, ex);
		}
	}

	public static void logEvent(String type, String name) {
		try {
			getProducer().logEvent(type, name);
		} catch (Throwable ex) {
			logger.warn("Failed to log event for type: {}, name: {}", type, name, ex);
		}
	}

	public static void logEvent(String type, String name, String status, String nameValuePairs) {
		try {
			getProducer().logEvent(type, name, status, nameValuePairs);
		} catch (Throwable ex) {
			logger.warn("Failed to log event for type: {}, name: {}, status: {}, nameValuePairs: {}", type, name,
					status, nameValuePairs, ex);
		}
	}

	public static Transaction newTransaction(String type, String name) {
		try {
			return getProducer().newTransaction(type, name);
		} catch (Throwable ex) {
			logger.warn("Failed to create transaction for type: {}, name: {}", type, name, ex);
			return NULL_MESSAGE_PRODUCER_MANAGER.getProducer().newTransaction(type, name);
		}
	}

	public static CatContext logRemoteCallClient() {
		if (CAT_CLASS != null) {
			CatContext catContext = new CatContext();
			CatContextProxy catContextProxy = new CatContextProxy(CAT_CONTEXT_CLASS, catContext);
			try {
				LOGREMOTECALLCLIENT.invoke(CAT_CLASS, catContextProxy.getProxyInstance());
			} catch (Exception e) {
				return null;
			}
			return catContext;
		} else {
			return null;
		}
	}

	public static void logRemoteCallServer(CatContext context) {
		if (CAT_CLASS != null) {
			CatContextProxy catContextProxy = new CatContextProxy(CAT_CONTEXT_CLASS, context);
			try {
				LOGREMOTECALLSERVER.invoke(CAT_CLASS, catContextProxy.getProxyInstance());
			} catch (Exception e) {

			}
		}
	}
	public static ICatSkip getCatSkip() {
		return catSkip;
	}

	public static void setCatSkip(ICatSkip catSkip) {
		Tracer.catSkip = catSkip;
	}
	public static String getDomain(){
		return getProducer().getDomain();
	}
}
