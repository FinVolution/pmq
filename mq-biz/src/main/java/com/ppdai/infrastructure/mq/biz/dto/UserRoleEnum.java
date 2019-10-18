package com.ppdai.infrastructure.mq.biz.dto;

/**
 * @author tongfeifan
 */

public enum UserRoleEnum {
    SUPER_USER(0, "超级管理员"),
    OWNER(1, "拥有者"),
    USER(2, "普通用户");

    private int roleCode;
    private String description;

    UserRoleEnum(int roleCode, String description) {
        this.roleCode = roleCode;
        this.description = description;
    }

    public int getRoleCode() {
        return this.roleCode;
    }

    public String getDescription() {
        return description;
    }
}
