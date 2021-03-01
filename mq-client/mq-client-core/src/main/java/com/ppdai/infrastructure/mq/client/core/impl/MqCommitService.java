package com.ppdai.infrastructure.mq.client.core.impl;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqCommitService;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MqCommitService implements IMqCommitService {
    private Logger log = LoggerFactory.getLogger(MqCommitService.class);
    private MqContext mqContext;
    private IMqResource mqResource;
    private volatile boolean isStop = false;
    private volatile boolean runStatus = false;
    private AtomicBoolean startFlag = new AtomicBoolean(false);
    private ThreadPoolExecutor executor = null;

    public MqCommitService() {
        this(MqClient.getContext().getMqResource());
    }

    public MqCommitService(IMqResource mqResource) {
        this.mqContext = MqClient.getContext();
        this.mqResource = mqResource;
    }

    @Override
    public void start() {
        if (startFlag.compareAndSet(false, true)) {
            this.executor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("MqCommitService", true),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
            isStop = false;
            runStatus = false;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (!isStop) {
                        runStatus = true;
                        Transaction transaction = Tracer.newTransaction("mq-commit", "commit");
                        try {
                            commitData();
                            transaction.setStatus(Transaction.SUCCESS);
                        } catch (Throwable e) {
                            transaction.setStatus(e);
                        } finally {
                            transaction.complete();
                        }
                        runStatus = false;
                        Util.sleep(mqContext.getConfig().getAynCommitInterval());
                    }
                }
            });
        }
    }

    private void commitData() {
        IConsumerPollingService consumerPollingService = MqClient.getMqFactory().createConsumerPollingService();
        Map<String, IMqGroupExcutorService> groups = consumerPollingService.getMqExcutors();
        CommitOffsetRequest request = new CommitOffsetRequest();
        List<ConsumerQueueVersionDto> queueVersionDtos = new ArrayList<>();
        request.setQueueOffsets(queueVersionDtos);
        groups.entrySet().forEach(t -> {
            t.getValue().getQueueEx().entrySet().forEach(p -> {
                ConsumerQueueVersionDto changedQueue = p.getValue().getChangedCommit();
                if (changedQueue != null) {
                    queueVersionDtos.add(changedQueue);
                }
            });
        });
        if (queueVersionDtos.size() > 0) {
            mqResource.commitOffset(request);
        }
    }


    @Override
    public void close() {
        isStop = true;
        commitData();
        long start = System.currentTimeMillis();
        // 这是为了等待有未完成的任务
        while (runStatus) {
            Util.sleep(10);
            // System.out.println("closing...................."+isRunning);
            if (System.currentTimeMillis() - start > 5000) {
                break;
            }
        }
        try {
            if (executor != null) {
                executor.shutdownNow();

            }
        } catch (Exception e) {

        }
        startFlag.set(false);
        executor = null;
    }
}
