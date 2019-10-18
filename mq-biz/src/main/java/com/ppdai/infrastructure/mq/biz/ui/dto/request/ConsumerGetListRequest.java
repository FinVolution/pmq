package com.ppdai.infrastructure.mq.biz.ui.dto.request;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;

public class ConsumerGetListRequest extends BaseUiRequst {
    private Long id;

    private String ip;

    private String consumerGroupNames;

    private String sdkVersion;

    private String compareType;

    public String getCompareType() {
        return compareType;
    }

    public void setCompareType(String compareType) {
        this.compareType = compareType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getConsumerGroupNames() {
        return consumerGroupNames;
    }

    public void setConsumerGroupNames(String consumerGroupNames) {
        this.consumerGroupNames = consumerGroupNames;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
}
