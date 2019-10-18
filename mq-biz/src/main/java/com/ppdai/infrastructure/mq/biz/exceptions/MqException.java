package com.ppdai.infrastructure.mq.biz.exceptions;

/**
 * @author tongfeifan
 */
public class MqException extends RuntimeException {
    public MqException(String msg) {
        super(msg);
    }
}
