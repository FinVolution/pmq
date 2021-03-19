package com.ppdai.infrastructure.mq.biz.common.util;

import java.util.concurrent.atomic.AtomicBoolean;

public interface IInstanceHealthChecker {
    void check(CheckData checkData, CheckCallback checkCallback);

    void checkSeq(CheckData checkData, CheckCallback checkCallback);

    void setConCheckCount(int count);

    public static interface CheckCallback {
        void onCheck(CheckResult checkResult);
    }

    public static class CheckData {
        private String url;
        private int count;
        private String expectData;

        private AtomicBoolean stopFlag = new AtomicBoolean(false);
        private Object arg;

        public CheckData(String url, int count, String expectData) {
            this.url = url;
            this.count = count;
            this.expectData = expectData;
        }

        public CheckData(String url, int count, Object arg, String expectData) {
            this.url = url;
            this.count = count;
            this.arg = arg;
            this.expectData = expectData;
        }

        public String getExpectData() {
            return expectData;
        }

        public void getExpectData(String expectData) {
            this.expectData = expectData;
        }

        public Object getArg() {
            return arg;
        }

        public void setArg(Object arg) {
            this.arg = arg;
        }

        public CheckData() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public AtomicBoolean getStopFlag() {
            return stopFlag;
        }

        public void setStopFlag(AtomicBoolean stopFlag) {
            this.stopFlag = stopFlag;
        }

    }

    public static class CheckResult {
        private String url;
        private int count;
        private boolean ok;
        private Object arg;

        public Object getArg() {
            return arg;
        }

        public void setArg(Object arg) {
            this.arg = arg;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

    }
}
