package com.ppdai.infrastructure.mq.biz.ui.vo;

public class PanelNodeVo {
    private Long id;
    private String nodeType;
    private String normalFlag;
    private String readOnly;
    private Long distributedCount;
    private Long undistributedCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNormalFlag() {
        return normalFlag;
    }

    public void setNormalFlag(String normalFlag) {
        this.normalFlag = normalFlag;
    }

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public Long getDistributedCount() {
        return distributedCount;
    }

    public void setDistributedCount(Long distributedCount) {
        this.distributedCount = distributedCount;
    }

    public Long getUndistributedCount() {
        return undistributedCount;
    }

    public void setUndistributedCount(Long undistributedCount) {
        this.undistributedCount = undistributedCount;
    }
}
