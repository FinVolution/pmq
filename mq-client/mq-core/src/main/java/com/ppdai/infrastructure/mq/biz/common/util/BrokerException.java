package com.ppdai.infrastructure.mq.biz.common.util;

@SuppressWarnings("serial")
public class BrokerException extends Exception {
	public BrokerException(String message) {
		super(message);
	}

	public BrokerException(String message, Throwable cause) {
		super(message, cause);
	}
}
