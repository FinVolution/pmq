package com.ppdai.infrastructure.mq.biz.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class BaseUiResponse<T> extends BaseResponse{
    private String code;
    private String msg;
    private Long count;
    private T data;

    public BaseUiResponse() {
        this.code = "0";
        this.msg = "操作成功";
    }

    public BaseUiResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseUiResponse(Long count, T data) {
        this.code = "0";
        this.msg = "操作成功";
        this.count = count;
        this.data = data;
    }

    public BaseUiResponse(T data) {
        this.code = "0";
        this.msg = "操作成功";
        this.data = data;
    }

    public BaseUiResponse<T> buildMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
