package com.ppdai.infrastructure.mq.biz.dto;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author：wanghe02
 * @Date：2019/12/4 11:28
 */
public class NotifyFailVo {

    //上次推送失败的时间
    private volatile long LastRetryTime;

    //当前状态（true表示可以调通，false表示无法调通）
    private volatile boolean status;

    //当前是否可以探测，true表示已经有请求在试探，其他请求直接屏蔽。false表示无请求在试探。
    private AtomicBoolean isRetrying = new AtomicBoolean(false);

    public long getLastRetryTime() {
        return LastRetryTime;
    }

    public void setLastRetryTime(long lastRetryTime) {
        LastRetryTime = lastRetryTime;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public AtomicBoolean getIsRetrying() {
        return isRetrying;
    }

    public void setIsRetrying(AtomicBoolean isRetrying) {
        this.isRetrying = isRetrying;
    }
}


