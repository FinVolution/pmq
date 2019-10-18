package com.ppdai.infrastructure.mq.biz.common.trace.internals.cat;

import java.lang.reflect.Method;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

public class CatTransaction implements Transaction {
	private static Class CAT_TRANSACTION_CLASS;
	private static Class CAT_MESSAGEMANAGER_CLASS;
	private static Method SET_STATUS_WITH_STRING;
	private static Method SET_STATUS_WITH_THROWABLE;
	private static Method GET_STATUS;
	private static Method ADD_DATA_WITH_KEY_AND_VALUE;
	private static Method RESET;
	private static Method GET_TYPE;
	private static Method GET_NAME;
	private static Method COMPLETE;
	private static Method GET_MESSAGEMANAGER;
	private Object catTransaction;

	static {
		try {
			CAT_TRANSACTION_CLASS = Class.forName(CatNames.CAT_TRANSACTION_CLASS);

			GET_MESSAGEMANAGER = Class.forName(CatNames.DEFAULT_DEFAULTTRANSACTION)
					.getDeclaredMethod(CatNames.GET_MESSAGEMANAGER_METHOD);
			GET_MESSAGEMANAGER.setAccessible(true);
			SET_STATUS_WITH_STRING = CAT_TRANSACTION_CLASS.getMethod(CatNames.SET_STATUS_METHOD, String.class);
			SET_STATUS_WITH_THROWABLE = CAT_TRANSACTION_CLASS.getMethod(CatNames.SET_STATUS_METHOD, Throwable.class);
			try {
				CAT_MESSAGEMANAGER_CLASS = Class.forName(CatNames.CAT_MESSAGEMANAGER_CLASS);
				GET_STATUS = CAT_TRANSACTION_CLASS.getMethod(CatNames.GET_STATUS_METHOD);
				RESET = CAT_MESSAGEMANAGER_CLASS.getMethod(CatNames.RESET_METHOD);
				GET_TYPE = CAT_TRANSACTION_CLASS.getMethod(CatNames.GET_TYPE_METHOD);
				GET_NAME = CAT_TRANSACTION_CLASS.getMethod(CatNames.GET_NAME_METHOD);
			} catch (Exception e) {
				// TODO: handle exception
			}

			ADD_DATA_WITH_KEY_AND_VALUE = CAT_TRANSACTION_CLASS.getMethod(CatNames.ADD_DATA_METHOD, String.class,
					Object.class);
			COMPLETE = CAT_TRANSACTION_CLASS.getMethod(CatNames.COMPLETE_METHOD);
		} catch (Throwable ex) {
			throw new IllegalStateException("Initialize Cat transaction failed", ex);
		}
	}

	static void init() {
		// do nothing, just to initialize the static variables
	}

	public CatTransaction(Object catTransaction) {
		this.catTransaction = catTransaction;
	}

	@Override
	public void setStatus(String status) {
		try {
			SET_STATUS_WITH_STRING.invoke(catTransaction, status);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void setStatus(Throwable status) {
		try {
			SET_STATUS_WITH_THROWABLE.invoke(catTransaction, status);
			Tracer.logError(status);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void addData(String key, Object value) {
		try {
			ADD_DATA_WITH_KEY_AND_VALUE.invoke(catTransaction, key, value);
		} catch (Throwable ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public void complete() {
		try {
			if (GET_TYPE != null && GET_NAME != null && GET_STATUS != null) {
				String type = GET_TYPE.invoke(catTransaction).toString();
				String name = GET_NAME.invoke(catTransaction).toString();
				String status = GET_STATUS.invoke(catTransaction).toString();
				if (status.equals(CatTransaction.SUCCESS) && Tracer.getCatSkip() != null
						&& Tracer.getCatSkip().isSkip(type, name)) {
					RESET.invoke(GET_MESSAGEMANAGER.invoke(catTransaction));
				} else {
					COMPLETE.invoke(catTransaction);
				}
			} else {
				COMPLETE.invoke(catTransaction);
			}
		} catch (Throwable ex) {

		}
	}
}
