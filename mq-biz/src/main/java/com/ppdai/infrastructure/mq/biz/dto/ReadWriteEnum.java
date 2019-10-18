package com.ppdai.infrastructure.mq.biz.dto;

public enum ReadWriteEnum {
    READ_WRITE(1, "读写"),
    READ_ONLY(2, "只读"),
    NO_READ_NO_WRITE(3, "不可读不可写");

    private int code;
    private String description;

    ReadWriteEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescByCode(int code) {
        for(ReadWriteEnum readWriteEnum: ReadWriteEnum.values()) {
            if (readWriteEnum.getCode() == code) {
                return readWriteEnum.getDescription();
            }
        }
        return "";
    }
}
