package com.ppdai.infrastructure.mq.biz.common.trace.internals.cat;

import java.lang.reflect.Method;

import com.ppdai.infrastructure.mq.biz.common.trace.spi.MessageProducer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

public class CatMessageProducer implements MessageProducer {
	private static Class CAT_CLASS;
	private static Method LOG_ERROR_WITH_CAUSE;
	private static Method LOG_ERROR_WITH_MESSAGE_AND_CAUSE;
	private static Method LOG_EVENT_WITH_TYPE_AND_NAME;
	private static Method LOG_EVENT_WITH_TYPE_AND_NAME_AND_STATUS_AND_NAME_VALUE_PAIRS;
	private static Method NEW_TRANSACTION_WITH_TYPE_AND_NAME;
	private static Method GET_MANAGER;
	private static String domain;
	static {
    try {
      CAT_CLASS = Class.forName(CatNames.CAT_CLASS);
      LOG_ERROR_WITH_CAUSE = CAT_CLASS.getMethod(CatNames.LOG_ERROR_METHOD, Throwable.class);
      GET_MANAGER= CAT_CLASS.getMethod(CatNames.GET_MANAGER_METHOD);
      Object manger= GET_MANAGER.invoke(CAT_CLASS);
      domain= manger.getClass().getMethod(CatNames.GET_DOMAIN_METHOD).invoke(manger).toString();
      LOG_ERROR_WITH_MESSAGE_AND_CAUSE = CAT_CLASS.getMethod(CatNames.LOG_ERROR_METHOD,
          String.class, Throwable.class);
      LOG_EVENT_WITH_TYPE_AND_NAME = CAT_CLASS.getMethod(CatNames.LOG_EVENT_METHOD,
          String.class, String.class);
      LOG_EVENT_WITH_TYPE_AND_NAME_AND_STATUS_AND_NAME_VALUE_PAIRS =
          CAT_CLASS.getMethod(CatNames.LOG_EVENT_METHOD, String.class, String.class,
              String.class, String.class);
      NEW_TRANSACTION_WITH_TYPE_AND_NAME = CAT_CLASS.getMethod(
          CatNames.NEW_TRANSACTION_METHOD, String.class, String.class);
      //eager init CatTransaction
      CatTransaction.init();
    } catch (Throwable ex) {
      throw new IllegalStateException("Initialize Cat message producer failed", ex);
    }
  }

	@Override
	public void logError(Throwable cause) {
		try {
			LOG_ERROR_WITH_CAUSE.invoke(null, cause);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void logError(String message, Throwable cause) {
		try {
			LOG_ERROR_WITH_MESSAGE_AND_CAUSE.invoke(null, message, cause);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void logEvent(String type, String name) {
		try {
			LOG_EVENT_WITH_TYPE_AND_NAME.invoke(null, type, name);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void logEvent(String type, String name, String status, String nameValuePairs) {
		try {
			LOG_EVENT_WITH_TYPE_AND_NAME_AND_STATUS_AND_NAME_VALUE_PAIRS.invoke(null, type, name, status,
					nameValuePairs);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public Transaction newTransaction(String type, String name) {
		try {
			return new CatTransaction(NEW_TRANSACTION_WITH_TYPE_AND_NAME.invoke(null, type, name));
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public String getDomain() {
		// TODO Auto-generated method stub
		return domain;
	}
}
