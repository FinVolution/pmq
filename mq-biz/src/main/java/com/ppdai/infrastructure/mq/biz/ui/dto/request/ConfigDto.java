package com.ppdai.infrastructure.mq.biz.ui.dto.request;

public class ConfigDto {
    //配置项的可以
    private String key;
    //配置项的默认值
    private String defaultValue;
    //配置项的当前值
    private String currentValue;
    //配置项的描述说明
    private String description;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
