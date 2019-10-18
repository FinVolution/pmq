package com.ppdai.infrastructure.mq.biz.ui.enums;

public enum  NormalFlagEnum {
    NORMAL_NODE(1, "普通节点"),
    SPECIAL_NODE(2, "特殊节点");

    private int flagCode;
    private String description;

    NormalFlagEnum(int flagCode, String description) {
        this.flagCode = flagCode;
        this.description = description;
    }

    public int getFlagCode() {
        return flagCode;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescByCode(int flagCode) {
        for (NormalFlagEnum normalFlagEnum: NormalFlagEnum.values()) {
            if (normalFlagEnum.getFlagCode() == flagCode) {
                return normalFlagEnum.getDescription();
            }
        }
        return "";
    }
}
