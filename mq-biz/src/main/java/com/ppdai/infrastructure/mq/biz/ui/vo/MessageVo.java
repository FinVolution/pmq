package com.ppdai.infrastructure.mq.biz.ui.vo;

import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import org.springframework.beans.BeanUtils;

public class MessageVo extends Message01Entity{
    int type;
    String failMsgRetryStatus;
    public MessageVo(Message01Entity message01Entity){
        BeanUtils.copyProperties(message01Entity,this);
    }

    public String getFailMsgRetryStatus() {
        return failMsgRetryStatus;
    }

    public void setFailMsgRetryStatus(String failMsgRetryStatus) {
        this.failMsgRetryStatus = failMsgRetryStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
