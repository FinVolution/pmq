package com.ppdai.infrastructure.mq.biz.ui.dto.request;

public class CatGetDataRequest {
    private String type;
    private String domain;
    private String date;
    private String ip;
    private String pageLevel;



    public String getPageLevel() {
        return pageLevel;
    }

    public void setPageLevel(String pageLevel) {
        this.pageLevel = pageLevel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
