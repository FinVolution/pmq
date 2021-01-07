package com.ppdai.infrastructure.mq.client.dto;

public class MqHandleSlowException extends Exception {
    public MqHandleSlowException(String msg) {
        super(msg);
    }
}
