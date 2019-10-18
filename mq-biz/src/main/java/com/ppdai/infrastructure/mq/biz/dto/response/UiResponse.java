package com.ppdai.infrastructure.mq.biz.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

import java.util.List;

public class UiResponse extends BaseResponse{
    private String code;
    private String msg;
    private Long count;
    private List data;
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
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

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public UiResponse buildMsg(String msg) {
        this.setMsg(msg);
        return this;
    }

    public UiResponse buildResult(Object result) {
        this.setResult(result);
        return this;
    }
}
