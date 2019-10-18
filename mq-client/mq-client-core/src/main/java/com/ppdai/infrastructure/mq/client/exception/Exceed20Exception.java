package com.ppdai.infrastructure.mq.client.exception;

@SuppressWarnings("serial")
public class Exceed20Exception extends Exception {
	public Exceed20Exception(String msg) {
		super(msg);
	}
}
