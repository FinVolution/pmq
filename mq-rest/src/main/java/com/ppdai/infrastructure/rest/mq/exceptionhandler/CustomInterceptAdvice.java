package com.ppdai.infrastructure.rest.mq.exceptionhandler;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

/**
 * @Author：wanghe02
 * @Date：2019/7/17 19:52
 */
@ControllerAdvice
public class CustomInterceptAdvice {

    private static final Logger logger = LoggerFactory.getLogger(CustomInterceptAdvice.class);

    /**
     * 拦截异常
     *
     * @param e
     * @param m
     * @return
     */
    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BaseResponse handle(Exception e, HandlerMethod m) {
        logger.info("CustomInterceptAdvice handle exception {}, method: {}", e.getMessage(), m.getMethod().getName());
        BaseResponse response = new BaseResponse();
        response.setCode(MqConstanst.NO);
        response.setSuc(false);
        response.setMsg(e.getMessage());
        return response;
    }
}