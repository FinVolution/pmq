package com.ppdai.infrastructure.mq.biz.ui.vo;

import org.springframework.beans.BeanUtils;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;

public class ConsumerGroupTopicVo extends ConsumerGroupTopicEntity {
    private int role;
    public ConsumerGroupTopicVo(ConsumerGroupTopicEntity consumerGroupTopicEntity){
        BeanUtils.copyProperties(consumerGroupTopicEntity,this);
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
