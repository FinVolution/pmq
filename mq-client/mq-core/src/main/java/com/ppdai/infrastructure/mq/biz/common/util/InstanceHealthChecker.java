package com.ppdai.infrastructure.mq.biz.common.util;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InstanceHealthChecker implements IInstanceHealthChecker {
    private static final Logger logger = LoggerFactory.getLogger(InstanceHealthChecker.class);
    private Semaphore checkingSemaphore;
    private IHttpClient iHttpClient;
    private boolean debug = false;
    private int conSeqCount = 2;

    public InstanceHealthChecker(IHttpClient iHttpClient, int maxCheck) {
        checkingSemaphore = new Semaphore(maxCheck);
        // System.out.println("total"+checkingSemaphore.availablePermits());
        this.iHttpClient = iHttpClient;
    }

    public InstanceHealthChecker(IHttpClient iHttpClient, int maxCheck, int conSeq) {
        checkingSemaphore = new Semaphore(maxCheck);
        // System.out.println("total"+checkingSemaphore.availablePermits());
        this.iHttpClient = iHttpClient;
        this.conSeqCount = conSeq;
    }

    @Override
    public void check(CheckData checkData, CheckCallback checkCallback) {
        checkingSemaphore.acquireUninterruptibly();
        try {
            doCheck(checkData, new AtomicInteger(0), new AtomicInteger(0), new AtomicBoolean(false), checkCallback);
        } catch (Exception e) {
            checkingSemaphore.release();
        }
    }

    private void doCheck(CheckData checkData, AtomicInteger count, AtomicInteger faildCount, AtomicBoolean flag,
                         CheckCallback checkCallback) {
        if (flag.get() || faildCount.get() >= checkData.getCount())
            return;
        if (debug) {
            System.out.println("time-" + count.get());
        }
        iHttpClient.getAsyn(checkData.getUrl(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.warn("hs_check "+checkData.getUrl()+",result "+e.getMessage());
                faildCount.incrementAndGet();
                if (!flag.get() && faildCount.get() >= checkData.getCount()) {
                    excuFailTImes(checkData, flag, checkCallback);
                } else if (!flag.get()) {
                    if (!checkData.getStopFlag().get()) {
                        retryCheck(checkData, count, faildCount, flag, checkCallback);
                    } else {
                        checkingSemaphore.release();
                        flag.set(true);
                        if (debug) {
                            System.out.println("smq-" + checkingSemaphore.availablePermits());
                        }
                    }
                }
                Transaction transaction = Tracer.newTransaction("rd-check-fail", checkData.getUrl());
                transaction.setStatus(Transaction.SUCCESS);
                transaction.addData("exception",e.getMessage());
                transaction.complete();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = (response.body().string()+"").toLowerCase();
                logger.info("hs_check "+checkData.getUrl()+",result "+body);
                // if (("" + checkData.getExpectData()).equalsIgnoreCase(body)) {
                if (body.indexOf(("" + checkData.getExpectData().toLowerCase()))!=-1) {
                    Transaction transaction = Tracer.newTransaction("rd-check-suc", checkData.getUrl());
                    CheckResult checkResult = new CheckResult();
                    checkResult.setOk(true);
                    checkResult.setCount(checkData.getCount());
                    checkResult.setUrl(checkData.getUrl());
                    checkResult.setArg(checkData.getArg());
                    transaction.setStatus(Transaction.SUCCESS);
                    transaction.addData("Health-Check-Host",checkData.getArg());
                    close(response);
                    if (flag.compareAndSet(false, true)) {
                        checkingSemaphore.release();
                        if (debug) {
                            System.out.println("smq-" + checkingSemaphore.availablePermits());
                        }
                        try {
                            checkCallback.onCheck(checkResult);
                        } catch (Exception e) {
                            logger.error("check error", e);
                        }
                    }
                    transaction.complete();
                } else if (!checkData.getStopFlag().get()) {
                    Transaction transaction = Tracer.newTransaction("rd-check-fail2", checkData.getUrl());
                    close(response);
                    if (!flag.get()) {
                        faildCount.incrementAndGet();
                        if (!flag.get() && faildCount.get() >= checkData.getCount()) {
                            excuFailTImes(checkData, flag, checkCallback);
                        } else if (!flag.get()) {
                            retryCheck(checkData, count, faildCount, flag, checkCallback);
                        }
                    }
                    transaction.addData("Health-Check-Host",checkData.getArg());
                    transaction.addData("Health-Check-Result",body);
                    transaction.setStatus(Transaction.SUCCESS);
                    transaction.complete();
                } else {
                    Transaction transaction = Tracer.newTransaction("rd-check-fail1", checkData.getUrl());
                    close(response);
                    if (flag.compareAndSet(false, true)) {
                        checkingSemaphore.release();
                        if (debug) {
                            System.out.println("smq-" + checkingSemaphore.availablePermits());
                        }
                        // checkingSemaphore.release();
                    }
                    transaction.addData("Health-Check-Host",checkData.getArg());
                    transaction.addData("Health-Check-Result",body);
                    transaction.setStatus(Transaction.SUCCESS);
                    transaction.complete();
                }

                //transaction.addData("error", e.getMessage());

            }
        });
    }

    private void retryCheck(CheckData checkData, AtomicInteger count, AtomicInteger faildCount, AtomicBoolean flag,
                            CheckCallback checkCallback) {
        int count1 = 0;
        for (int i = 0; i < conSeqCount; i++) {
            count1 = count.incrementAndGet();
            if (count1 < checkData.getCount()) {
                doCheck(checkData, count, faildCount, flag, checkCallback);
                if (i != conSeqCount - 1) {
                    Util.sleep(5);
                }
            }
        }
    }

    private void excuFailTImes(CheckData checkData, AtomicBoolean flag, CheckCallback checkCallback) {
        // synchronized (flag) {
        // if (!flag.get()) {
        if (flag.compareAndSet(false, true)) {
            CheckResult checkResult = new CheckResult();
            checkResult.setOk(false);
            checkResult.setCount(checkData.getCount());
            checkResult.setUrl(checkData.getUrl());
            checkResult.setArg(checkData.getArg());
            checkingSemaphore.release();
            if (debug) {
                System.out.println("smq-" + checkingSemaphore.availablePermits());
            }
            try {
                checkCallback.onCheck(checkResult);
            } catch (Exception e) {
                logger.error("check error", e);
            }
        }
    }
    // }

    private void close(Response response) {
        try {
            response.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void checkSeq(CheckData checkData, CheckCallback checkCallback) {
        checkingSemaphore.acquireUninterruptibly();
        try {
            doCheck(checkData, 0, checkCallback);
        } catch (Exception e) {
            checkingSemaphore.release();
        }
    }

    private void doCheck(CheckData checkData, int currentCount, CheckCallback checkCallback) {
        if (currentCount >= checkData.getCount()) {
            CheckResult checkResult = new CheckResult();
            checkResult.setOk(false);
            checkResult.setCount(checkData.getCount());
            checkResult.setUrl(checkData.getUrl());
            checkResult.setArg(checkData.getArg());
            checkingSemaphore.release();
            if (debug) {
                System.out.println("smq-" + checkingSemaphore.availablePermits());
            }
            try {
                checkCallback.onCheck(checkResult);
            } catch (Exception e) {
                logger.error("check error", e);
            }

        } else {

            iHttpClient.getAsyn(checkData.getUrl(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (!checkData.getStopFlag().get()) {
                        doCheck(checkData, currentCount + 1, checkCallback);
                    } else {
                        checkingSemaphore.release();
                        if (debug) {
                            System.out.println("smq-" + checkingSemaphore.availablePermits());
                        }
                    }
                    //Tracer.logEvent("rd", name, e.getMessage(), "");
                    Transaction transaction = Tracer.newTransaction("rd-check-fail", checkData.getUrl());
                    transaction.setStatus(e);
                    transaction.complete();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = (response.body().string()+"").toLowerCase();
                    if (body.indexOf(("" + checkData.getExpectData()).toLowerCase())!=-1) {
                        Transaction transaction = Tracer.newTransaction("rd-check-suc", checkData.getUrl());
                        CheckResult checkResult = new CheckResult();
                        checkResult.setOk(true);
                        checkResult.setCount(checkData.getCount());
                        checkResult.setUrl(checkData.getUrl());
                        checkResult.setArg(checkData.getArg());
                        close(response);
                        checkingSemaphore.release();
                        if (debug) {
                            System.out.println("smq-" + checkingSemaphore.availablePermits());
                        }
                        try {
                            checkCallback.onCheck(checkResult);
                        } catch (Exception e) {
                            logger.error("check error", e);
                        }
                        transaction.setStatus(Transaction.SUCCESS);
                        transaction.complete();
                    } else if (!checkData.getStopFlag().get()) {
                        Transaction transaction = Tracer.newTransaction("rd-check-fail2", checkData.getUrl());
                        close(response);
                        doCheck(checkData, currentCount + 1, checkCallback);
                        transaction.setStatus(body);
                        transaction.complete();
                    } else {
                        Transaction transaction = Tracer.newTransaction("rd-check-fail1", checkData.getUrl());
                        close(response);
                        checkingSemaphore.release();
                        if (debug) {
                            System.out.println("smq-" + checkingSemaphore.availablePermits());
                        }
                        transaction.setStatus(body);
                        transaction.complete();
                    }


                }
            });
        }
    }

    @Override
    public void setConCheckCount(int count) {
        // TODO Auto-generated method stub
        conSeqCount = count;
    }
}
