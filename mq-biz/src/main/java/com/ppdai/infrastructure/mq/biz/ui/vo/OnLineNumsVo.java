package com.ppdai.infrastructure.mq.biz.ui.vo;

public class OnLineNumsVo {
    private String name;
    private Long number;
    public OnLineNumsVo(String name, Long number){
        this.name=name;
        this.number=number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
