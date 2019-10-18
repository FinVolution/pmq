package com.ppdai.infrastructure.mq.biz.ui.vo;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerEntity;
import org.springframework.beans.BeanUtils;

/**
 * @Author：wanghe02
 * @Date：2018/9/17 15:58
 */
public class ConsumerVo extends ConsumerEntity {
    private int role;
    private String ownerIds;
    private String ownerNames;

    public ConsumerVo(ConsumerEntity consumerEntity) {
        BeanUtils.copyProperties(consumerEntity, this);
    }

    public String getOwnerNames() {
        return ownerNames;
    }

    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
    }

    public String getOwnerIds() {
        return ownerIds;
    }

    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
