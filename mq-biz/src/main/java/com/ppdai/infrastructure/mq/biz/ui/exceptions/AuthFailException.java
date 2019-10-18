package com.ppdai.infrastructure.mq.biz.ui.exceptions;

public class AuthFailException extends RuntimeException {
    public AuthFailException(String msg) {
        super(msg);
    }
}
