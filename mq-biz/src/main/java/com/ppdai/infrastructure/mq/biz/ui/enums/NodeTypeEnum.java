package com.ppdai.infrastructure.mq.biz.ui.enums;

public enum NodeTypeEnum {
    SUCCESS_NODE_TYPE(1, "成功类型"),
    FAIL_NODE_TYPE(2, "失败类型");


    private int typeCode;
    private String description;

    NodeTypeEnum(int typeCode, String description) {
        this.typeCode = typeCode;
        this.description = description;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescByCode(int typeCode) {
        for (NodeTypeEnum nodeTypeEnum: NodeTypeEnum.values()) {
            if (nodeTypeEnum.getTypeCode() == typeCode) {
                return nodeTypeEnum.getDescription();
            }
        }
        return "";
    }
}
