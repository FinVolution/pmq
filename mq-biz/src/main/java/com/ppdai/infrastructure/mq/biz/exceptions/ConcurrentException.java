package com.ppdai.infrastructure.mq.biz.exceptions;

/**
 * @author tongfeifan
 */
public class ConcurrentException extends MqException {
    public ConcurrentException(String msg) {
        super(msg);
    }
}
