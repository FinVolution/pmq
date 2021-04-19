package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.ppdai.infrastructure.mq.client.dto.BatchRecorderItem;
import com.ppdai.infrastructure.mq.client.dto.MqHandleSlowException;
import com.ppdai.infrastructure.mq.client.dto.TraceMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.ConfigurationManager;
import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.MqEnv;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.TopicUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.event.IAsynSubscriber;
import com.ppdai.infrastructure.mq.biz.event.IMsgFilter;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.client.MessageUtil;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.hystrix.MessageInvokeCommandForThreadIsolation;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public class MqQueueExcutorService implements IMqQueueExcutorService {
    public static final int COMMIT_TIME_DELTA = 20_000;
    private Logger log = LoggerFactory.getLogger(MqQueueExcutorService.class);
    private AtomicReference<ConsumerQueueDto> consumerQueueRef = new AtomicReference<ConsumerQueueDto>();
    private ThreadPoolExecutor executor = null;
    private volatile ThreadPoolExecutor executorNotify = null;
    private String consumerGroupName;
    private volatile boolean isRunning = false;
    private volatile long lastId = 0;
    private BlockingQueue<TraceMessageDto> messages = new ArrayBlockingQueue<>(300);
    private Map<Long, TraceMessageDto> slowMsgMap = new ConcurrentHashMap<>(350);
    private PullDataRequest request = new PullDataRequest();
    private ISubscriber iSubscriber = null;
    private IAsynSubscriber iAsynSubscriber = null;
    private AtomicInteger failCount = new AtomicInteger(0);// 处理失败次数
    private volatile long failBeginTime = 0;// 处理失败开始时间
    private TraceMessage traceMsgPull = null;
    private TraceMessage traceMsgDeal = null;
    private TraceMessage traceMsgCommit = null;
    private TraceMessage traceMsg = null;
    private MqContext mqContext;
    private IMqResource mqResource;
    private volatile boolean isStop = false;
    private AtomicBoolean isStart = new AtomicBoolean(false);
    private volatile boolean runStatus = false;
    // private final Object lockOffsetObj = new Object();
    private final Object lockMetaObj = new Object();

    private BatchRecorder batchRecorder = new BatchRecorder();
    // public volatile boolean timeOutFlag = true;
    private AtomicInteger timeOutCount = new AtomicInteger(0);
    private AtomicInteger taskCounter = new AtomicInteger(0);

    public MqQueueExcutorService(String consumerGroupName, ConsumerQueueDto consumerQueue) {
        this.mqContext = MqClient.getContext();
        this.mqResource = mqContext.getMqResource();
        this.consumerGroupName = consumerGroupName;
        initTraceAndSubscriber(consumerGroupName, consumerQueue);
        // resetConsumerBatchSize(consumerQueue);
        consumerQueueRef.set(consumerQueue);
        createExecutor(consumerQueue);
        this.lastId = consumerQueue.getOffset();
        consumerQueue.setLastId(lastId);
        isRunning = consumerQueueRef.get().getStopFlag() == 0;
        updateTimeout(consumerQueue);
    }

    private void updateTimeout(ConsumerQueueDto consumerQueue) {
        if (consumerQueue.getTimeout() > 0) {
            String key = "hystrix.command." + consumerGroupName + "." + consumerQueue.getOriginTopicName()
                    + ".execution.isolation.thread.timeoutInMilliseconds";
            ConfigurationManager.getConfigInstance().setProperty(key, consumerQueue.getTimeout() * 1000);
        }
    }

    // 注意在fat多环境下只能单个批量消费
    private void resetConsumerBatchSize(ConsumerQueueDto consumerQueue) {
        if (MqClient.getMqEnvironment() != null) {
            if (MqEnv.FAT == MqClient.getMqEnvironment().getEnv()) {
                consumerQueue.setConsumerBatchSize(1);
            }
        }
        if (consumerQueue.getConsumerBatchSize() < 0) {
            consumerQueue.setConsumerBatchSize(1);
            log.warn("ConsumerBatchSize error!");
        }
    }

    protected void initTraceAndSubscriber(String consumerGroupName, ConsumerQueueDto consumerQueue) {
        traceMsgPull = TraceFactory.getInstance(
                "MqQueueExcutorService-拉取过程-" + consumerGroupName + "-queueId-" + consumerQueue.getQueueId());
        traceMsgDeal = TraceFactory.getInstance(
                "MqQueueExcutorService-处理-" + consumerGroupName + "-queueId-" + consumerQueue.getQueueId());
        traceMsg = TraceFactory.getInstance(
                "MqQueueExcutorService-拉取状态-" + consumerGroupName + "-queueId-" + consumerQueue.getQueueId());
        traceMsgCommit = TraceFactory.getInstance(
                "MqQueueExcutorService-提交偏移-" + consumerGroupName + "-queueId-" + consumerQueue.getQueueId());
        // 失败队列和正常队列都是用同一个接口消费
        this.iSubscriber = mqContext.getSubscriber(consumerGroupName, consumerQueue.getOriginTopicName());
        if (this.iSubscriber == null) {
            this.iAsynSubscriber = mqContext.getAsynSubscriber(consumerGroupName, consumerQueue.getOriginTopicName());
            if (this.iAsynSubscriber == null) {
                if (consumerQueue.getTopicName().equals(consumerQueue.getOriginTopicName())) {
                    String log1 = "consumerGroupName_" + consumerGroupName + "_"
                            + consumerQueue.getOriginTopicName() + "没有订阅的接口";
                    log.info(log1);
                    addOpLog(consumerQueue, consumerQueue.getConsumerGroupName() + "下没有"
                            + consumerQueue.getOriginTopicName() + "订阅的接口");
                }
            }
        }
    }

    protected void createExecutor(ConsumerQueueDto consumerQueue) {
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            executor = new ThreadPoolExecutor(consumerQueue.getThreadSize() + 2, consumerQueue.getThreadSize() + 2, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100),
                    SoaThreadFactory.create(
                            "MqQueueExcutorServiceNew-" + consumerGroupName + "-" + consumerQueue.getQueueId(), true),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
            log.info("创建Queue," + consumerQueue.getQueueId() + "," + consumerQueue.getQueueOffsetId());
        }
    }

    @Override
    public void updateQueueMeta(ConsumerQueueDto consumerQueue) {
        synchronized (lockMetaObj) {
            doUpdateQueueMeta(consumerQueue);
        }
    }

    protected void doUpdateQueueMeta(ConsumerQueueDto consumerQueue) {
        Transaction transaction = Tracer.newTransaction("mq-group", "updateQueueMeta-" + consumerQueue.getTopicName());
        try {
            if (consumerQueue.getTimeout() == 0) {
                timeOutCount.set(0);
            }
            ConsumerQueueDto temp = consumerQueueRef.get();
            boolean flag = consumerQueue.getThreadSize() != temp.getThreadSize();
            if (flag) {
                log.info("update_thread_size,更新线程数" + consumerQueue.getTopicName());
                executor.setCorePoolSize(consumerQueue.getThreadSize() + 2);
                executor.setMaximumPoolSize(consumerQueue.getThreadSize() + 2);
            }
            // 此时是元数据发生更新
            if (consumerQueue.getOffsetVersion() == temp.getOffsetVersion()) {
                log.info("update meta with topic:" + consumerQueue.getTopicName());
                // 更新元数据
                updateQueueMetaWithOutOffset(consumerQueue);

            } else {
                // 此时需要进行偏移更新
                log.info("queue offset changed,发生队列重新消费" + consumerQueue.getTopicName());
                consumerQueueRef.set(consumerQueue);
                // 此时是为了防止拉取到数据后，会有阻塞，清除掉后，会消除阻塞
                messages.clear();
                // 防止清除后，messages 里面有数据
                Util.sleep(100);
                // 再次清理，确保message 里面为空，同时使得拉取消息释放锁。
                messages.clear();
                slowMsgMap.clear();
                // 确保更新拉取消息的起始值，为偏移重置的值，加锁是防止拉取与重置同时操作
                consumerQueue.setLastId(consumerQueue.getOffset());
                // 说明修改偏移了需要重新，拉取
                this.lastId = consumerQueue.getOffset();
            }
            if (isRunning && consumerQueue.getStopFlag() == 1) {
                log.info("stop deal,停止消费" + consumerQueue.getTopicName());
                TraceMessageItem item = new TraceMessageItem();
                isRunning = consumerQueue.getStopFlag() == 0;
                doCommit(temp);
                item.status = "停止消费";
                item.msg = temp.getOffset() + "-" + temp.getOffsetVersion();
                traceMsgCommit.add(item);
            }
            isRunning = consumerQueue.getStopFlag() == 0;
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    protected void updateQueueMetaWithOutOffset(ConsumerQueueDto consumerQueue) {
        resetConsumerBatchSize(consumerQueue);
        consumerQueueRef.get().setConsumerBatchSize(consumerQueue.getConsumerBatchSize());
        consumerQueueRef.get().setDelayProcessTime(consumerQueue.getDelayProcessTime());
        consumerQueueRef.get().setPullBatchSize(consumerQueue.getPullBatchSize());
        consumerQueueRef.get().setRetryCount(consumerQueue.getRetryCount());
        consumerQueueRef.get().setStopFlag(consumerQueue.getStopFlag());
        consumerQueueRef.get().setTag(consumerQueue.getTag());
        consumerQueueRef.get().setThreadSize(consumerQueue.getThreadSize());
        consumerQueueRef.get().setTraceFlag(consumerQueue.getTraceFlag());
        consumerQueueRef.get().setMaxPullTime(consumerQueue.getMaxPullTime());
        if (consumerQueueRef.get().getTimeout() != consumerQueue.getTimeout()) {
            updateTimeout(consumerQueue);
        }
        consumerQueueRef.get().setTimeout(consumerQueue.getTimeout());
    }

    // 停止拉取数据
    @Override
    public void close() {
        stop();
        messages.clear();
        try {
            if (executor != null) {
                executor.shutdownNow();
                executor = null;
            }
        } catch (Throwable e) {

        }
        clearTrace();
        slowMsgMap.clear();
        isStart.set(false);
    }

    protected void pullingData() {
        boolean flag = false;
        int sleepTime = 500;
        // log.info("queue1_{}_started", consumerQueueRef.get().getQueueId());
        while (!isStop) {
            TraceMessageItem traceMessageItem = new TraceMessageItem();
            traceMessageItem.status = "当前拉取状态：" + isRunning;
            // System.out.println("topic_name_"+consumerQueueRef.get().getTopicName()+"_queueid_"+consumerQueueRef.get().getQueueId()+"_isruning_"+isRunning);
            if (isRunning) {
                flag = doPullingData();
            }
            if (flag) {
                sleepTime = 0;
            } else {
                sleepTime = sleepTime + mqContext.getConfig().getPullDeltaTime();
                // System.out.println(consumerQueueRef.get().getTopicName()+"--"+consumerQueueRef.get().getMaxPullTime());
                if (sleepTime >= consumerQueueRef.get().getMaxPullTime() * 1000)
                    sleepTime = 50;
            }
            traceMsg.add(traceMessageItem);
            if (sleepTime > 0) {
                Util.sleep(sleepTime);
            }
        }
    }

    private volatile long lastPullTime = System.currentTimeMillis();
    private AtomicBoolean pullFlag = new AtomicBoolean(false);

    protected boolean doPullingData() {
        if (pullFlag.compareAndSet(false, true)) {
            lastPullTime = System.currentTimeMillis();
            ConsumerQueueDto consumerQueueDto = consumerQueueRef.get();
            if (consumerQueueDto != null) {
                Transaction transaction = Tracer.newTransaction("mq-queue-pull",
                        consumerQueueDto.getTopicName() + "-" + consumerQueueDto.getQueueId());
                TraceMessageItem traceMessageItem = new TraceMessageItem();
                try {
                    request.setQueueId(consumerQueueDto.getQueueId());
                    if (checkOffsetVersion(consumerQueueDto)) {
                        consumerQueueDto.setLastId(lastId);
                        request.setOffsetStart(lastId);
                        request.setOffsetEnd(lastId + consumerQueueDto.getPullBatchSize());
                        request.setConsumerGroupName(consumerQueueDto.getConsumerGroupName());
                        request.setTopicName(consumerQueueDto.getTopicName());
                        PullDataResponse response = null;
                        if (checkOffsetVersion(consumerQueueDto)) {
                            response = mqResource.pullData(request);
                        }
                        // PullDataResponse response = null;
                        traceMessageItem.status = "拉取消息正常lastid-" + lastId;
                        traceMessageItem.msg = "当前拉取lastid为:" + lastId + ",end:"
                                + (lastId + consumerQueueDto.getPullBatchSize()) + ",consumerGroupName:"
                                + consumerQueueDto.getConsumerGroupName() + ",topicName:"
                                + consumerQueueDto.getTopicName();
                        if (response != null && response.getMsgs() != null && response.getMsgs().size() > 0) {
                            cacheData(response, consumerQueueDto);
                            transaction.setStatus(Transaction.SUCCESS);
                            return true;
                        }
                    }
                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Throwable e) {
                    traceMessageItem.status = "拉取消息失败";
                    traceMessageItem.msg = e.getMessage();
                    transaction.setStatus(e);
                } finally {
                    traceMsgPull.add(traceMessageItem);
                    transaction.complete();
                    pullFlag.set(false);
                }
            }
            return false;
        } else {
            return true;
        }

    }

    public void start() {
        if (this.iSubscriber != null || this.iAsynSubscriber != null) {
            // 确保只启动一次
            if (isStart.compareAndSet(false, true)) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pullingData();
                        } catch (Throwable e) {
                        }
                    }

                });
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (!isStop) {
                            if (isRunning) {
                                try {
                                    // 注意此处不能加锁，因为有些会出现延消费，然后出现阻塞
                                    handleData();
                                } catch (Throwable e) {

                                }

                            } else {
                                Util.sleep(50);
                            }
                        }
                    }
                });
            }
        }
    }

    protected void handleData() {
        ConsumerQueueDto temp = consumerQueueRef.get();
        runStatus = false;
        int msgSize = messages.size();
        refreshSubscriber();
        if (temp != null && msgSize > 0 && temp.getThreadSize() - taskCounter.get() > 0
                && (iSubscriber != null || iAsynSubscriber != null)
                && (temp.getTimeout() == 0 || (temp.getTimeout() > 0 && timeOutCount.get() == 0))) {
            if (!checkPreHand(temp)) {
                return;
            }
            runStatus = true;
            doHandleData(temp, msgSize);
            runStatus = false;
        } else {
            Util.sleep(10);
        }
    }

    private long lastRefresh = System.currentTimeMillis();
    private long lastSend = System.currentTimeMillis() - 370000;

    private void refreshSubscriber() {
        try {
            if (mqContext.getMqEvent().getiSubscriberSelector() != null
                    || mqContext.getMqEvent().getiAsynSubscriberSelector() != null) {
                if (System.currentTimeMillis() - lastRefresh > 5000) {
                    if (mqContext.getMqEvent().getiSubscriberSelector() != null) {
                        iSubscriber = mqContext.getMqEvent().getiSubscriberSelector().getSubscriber(consumerGroupName,
                                consumerQueueRef.get().getOriginTopicName());
                    }
                    if (iSubscriber == null && mqContext.getMqEvent().getiAsynSubscriberSelector() != null) {
                        iAsynSubscriber = mqContext.getMqEvent().getiAsynSubscriberSelector()
                                .getSubscriber(consumerGroupName, consumerQueueRef.get().getOriginTopicName());
                    }
                    if (iSubscriber == null && iAsynSubscriber == null
                            && System.currentTimeMillis() - lastSend > 360000) {
                        addOpLog(consumerQueueRef.get(), "此消费者组下" + consumerQueueRef.get().getTopicName() + "没有消费处理类！");
                        lastSend = System.currentTimeMillis();
                    }
                    lastRefresh = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            log.error("getSubscriber_error", e);
        }
    }

    private void addOpLog(ConsumerQueueDto consumerQueue, String content) {
        OpLogRequest opLogRequest = new OpLogRequest();
        opLogRequest.setConsumerGroupName(consumerQueue.getConsumerGroupName());
        opLogRequest.setConsumerName(mqContext.getConsumerName());
        opLogRequest.setContent("消费端,consumer_" + mqContext.getConsumerName() + "," + content + "__offsetVersion_is_"
                + consumerQueue.getOffsetVersion());
        mqResource.addOpLog(opLogRequest);
    }

    protected boolean checkPreHand(ConsumerQueueDto temp) {
        if (MqClient.getContext().getMqEvent().getPreHandleListener() != null) {
            try {
                if (!MqClient.getContext().getMqEvent().getPreHandleListener().preHandle(temp)) {
                    return false;
                }
            } catch (Exception e) {
                log.error("PreHandle_error", e);
                return false;
            }
        }
        return true;
    }

    private void doHandleData(ConsumerQueueDto pre, int msgSize) {
        // int threadSize =threadRemain.get();
        int threadSize = pre.getThreadSize() - taskCounter.get();
        int startThread = (int) ((msgSize + pre.getConsumerBatchSize() - 1) / pre.getConsumerBatchSize());
        if (startThread >= threadSize) {
            startThread = threadSize;
        }
        if (startThread > pre.getThreadSize()) {
            startThread = pre.getThreadSize();
        }
        if (batchRecorder.recordMap.size() > 200) {
            MqHandleSlowException runtimeException = new MqHandleSlowException("出现消费堆积了：" + com.ppdai.infrastructure.mq.biz.common.util.JsonUtil.toJsonNull(slowMsgMap));
            log.error("消息堆积", runtimeException);
            Transaction transaction = Tracer.newTransaction("mq-handle", "slow");
            transaction.setStatus(runtimeException);
            transaction.complete();
        }
        long batchRecorderId = batchRecorder.begin(startThread);
        CountDownLatch countDownLatch = new CountDownLatch(startThread);
        batchExcute(pre, startThread, batchRecorderId, countDownLatch);
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            // TODO Auto-generated catch block

        }
    }

    private void batchExcute(ConsumerQueueDto pre, int startThread, long batchRecorderId,
                             CountDownLatch countDownLatch) {
        for (int i = 0; i < startThread; i++) {
            if (executor != null) {
                executor.execute(new MsgThread(pre, batchRecorderId, countDownLatch, timeOutCount));
            }
        }
    }

    protected void updateOffset(ConsumerQueueDto pre, long id) {
        if (pre.getOffset() < id && checkOffsetVersion(pre)) {
            pre.setOffset(id);
        }
    }

    protected void updateLastId(ConsumerQueueDto pre, MessageDto t1) {
        synchronized (lockMetaObj) {
            if (lastId < t1.getId() && checkOffsetVersion(pre)) {
                lastId = t1.getId();
                pre.setLastId(lastId);
            }
        }
    }

    private void doCommit(ConsumerQueueDto temp) {
        BatchRecorderItem item = batchRecorder.getLastestItem();
        if (item != null) {
            doCommit(temp, item, true);
        }
    }

    private void doCommit(ConsumerQueueDto temp, BatchRecorderItem batchRecorderItem, boolean flag) {
        if (batchRecorderItem == null) {
            return;
        }
        if (iAsynSubscriber == null) {
            if (checkOffsetVersion(temp)) {
                // consumerQueueVersionDto.setOffset(temp.getOffset());
                consumerQueueVersionDto.setOffset(batchRecorderItem.getMaxId());
                consumerQueueVersionDto.setOffsetVersion(temp.getOffsetVersion());
                consumerQueueVersionDto.setQueueOffsetId(temp.getQueueOffsetId());
                consumerQueueVersionDto.setConsumerGroupName(temp.getConsumerGroupName());
                consumerQueueVersionDto.setTopicName(temp.getTopicName());
                commitVersion.incrementAndGet();
                // request.setFailIds(preFailIds);
                if (mqContext.getConfig().isSynCommit() || flag) {
                    TraceMessageItem item = commit();
                    item.status = "提交偏移";
                    item.msg = temp.getOffset() + "-" + temp.getOffsetVersion();
                    traceMsgCommit.add(item);
                }
            } else {
                TraceMessageItem item = new TraceMessageItem();
                // mqResource.commitOffset(request);
                item.status = "提交偏移失败";
                item.msg = temp.getOffsetVersion() + "-" + consumerQueueRef.get().getOffsetVersion();
                traceMsgCommit.add(item);
            }
        }
        batchRecorder.delete(batchRecorderItem.getBatchReacorderId());
    }

    private void doCommit(ConsumerQueueDto temp, BatchRecorderItem batchRecorderItem) {
        doCommit(temp, batchRecorderItem, false);
    }

    private ConsumerQueueVersionDto consumerQueueVersionDto = new ConsumerQueueVersionDto();
    private AtomicLong commitVersion = new AtomicLong(0);
    private volatile long hasCommitVersion = 0;
    private long lastCommitTime = System.currentTimeMillis();

    @Override
    public ConsumerQueueVersionDto getLast() {
        if (hasCommitVersion > 0) {
            return consumerQueueVersionDto;
        }
        return null;
    }

    @Override
    public boolean hasFininshed() {
        return taskCounter.get() == 0;
    }

    @Override
    public void stop() {
        isRunning = false;
        isStop = true;
    }

    @Override
    public ConsumerQueueVersionDto getChangedCommit() {
        long commitV = commitVersion.get();
        if (hasCommitVersion < commitV) {
            hasCommitVersion = commitV;
            return consumerQueueVersionDto;
        }
        if (hasCommitVersion > 0 && System.currentTimeMillis() - lastCommitTime > COMMIT_TIME_DELTA) {
            TraceMessageItem item = new TraceMessageItem();
            item.status = "提交偏移-" + consumerQueueVersionDto.getOffset();
            item.msg = consumerQueueVersionDto.getOffsetVersion() + "-" + consumerQueueRef.get().getOffsetVersion();
            traceMsgCommit.add(item);
            return consumerQueueVersionDto;
        }
        return null;
    }

    private TraceMessageItem commit() {
        if (commitVersion.get() > 0) {
            CommitOffsetRequest request = new CommitOffsetRequest();
            List<ConsumerQueueVersionDto> queueVersionDtos = new ArrayList<>();
            request.setQueueOffsets(queueVersionDtos);
            queueVersionDtos.add(consumerQueueVersionDto);
            TraceMessageItem item = new TraceMessageItem();
            mqResource.commitOffset(request);
            return item;
        }
        return null;
    }

    protected long threadExcute(ConsumerQueueDto pre, CountDownLatch countDownLatch) {
        if (isRunning && (iSubscriber != null || iAsynSubscriber != null)) {
            TraceMessageItem traceMessageItem = new TraceMessageItem();
            Map<Long, MessageDto> messageMap = new LinkedHashMap<>();
            Pair<Long, Boolean> pair = prepareValue(pre, messageMap);
            countDownLatch.countDown();
            long maxId = pair.item1;
            boolean flag = pair.item2;
            if (messageMap.size() > 0) {
                traceMessageItem.status = "maxId：" + maxId;
                traceMessageItem.msg = "开始消费,起始时间为" + Util.formateDate(new Date());
                List<Long> failIds = invokeMessage(pre, messageMap);
                traceMessageItem.msg = traceMessageItem.msg + ",消费结束,结束时间为" + Util.formateDate(new Date());
                traceMsgDeal.add(traceMessageItem);
                List<Long> sucIds = new ArrayList<>();
                Map<Long, MessageDto> failMsg = getFailMsg(pre, failIds, sucIds, messageMap);
                addExcuteLog(failMsg, pre, messageMap);
                // 发送失败告警
                failAlarm(failMsg, pre);
                // 发送失败队列消息
                PublishMessageRequest failRequest = getFailMsgRequest(pre, new ArrayList<>(failMsg.values()));
                // 如果是失败消息更新失败消息执行成功结果
                publishAndUpdateResultFailMsg(failRequest, pre, sucIds);
                return maxId;
            } else {
                if (flag) {
                    return maxId;
                } else {
                    traceMessageItem.status = "当前数据缓存为空";
                    traceMsgDeal.add(traceMessageItem);
                }
            }

        } else {
            countDownLatch.countDown();
        }
        return 0;

    }

    protected void publishAndUpdateResultFailMsg(PublishMessageRequest failRequest, ConsumerQueueDto pre,
                                                 List<Long> sucIds) {
        FailMsgPublishAndUpdateResultRequest request = new FailMsgPublishAndUpdateResultRequest();
        if (sucIds != null && sucIds.size() > 0) {
            request.setIds(sucIds);
        }
        request.setQueueId(pre.getQueueId());
        if (failRequest != null) {
            request.setFailMsg(failRequest);
        }
        if ((sucIds != null && sucIds.size() > 0) || failRequest != null) {
            mqResource.publishAndUpdateResultFailMsg(request);
        }
    }

    protected void failAlarm(Map<Long, MessageDto> failMsg, ConsumerQueueDto pre) {
        if (failMsg.size() > 0) {
            failMsg.values().forEach(t1 -> {
                log.error("groupname_{}_topic_{}_bizId_{}_fail,消费失败", consumerGroupName, pre.getTopicName(),
                        t1.getBizId());
                failCount.incrementAndGet();
            });
            sendFailMail();
        } else {
            restetFailAlarm();
        }

    }

    protected Map<Long, MessageDto> getFailMsg(ConsumerQueueDto pre, List<Long> failIds, List<Long> sucIds,
                                               Map<Long, MessageDto> messageMap) {
        Map<Long, MessageDto> messageMap1 = new HashMap<>();
        failIds.forEach(t1 -> {
            messageMap1.put(t1, messageMap.get(t1));
        });
        if (pre.getTopicType() == 2) {
            messageMap.entrySet().forEach(t1 -> {
                if (!messageMap1.containsKey(t1.getKey())) {
                    sucIds.add(t1.getKey());
                }
            });
        }
        return messageMap1;
    }

    protected void addExcuteLog(Map<Long, MessageDto> failMsg, ConsumerQueueDto temp,
                                Map<Long, MessageDto> messageMap) {
        if (temp.getTraceFlag() == 1) {
            for (MessageDto t1 : messageMap.values()) {
                if (failMsg.containsKey(t1.getId())) {
                    addHandleLog(t1, temp, "end_call_fail", MqConst.DEBUG);
                } else {
                    addHandleLog(t1, temp, "end_call_suc", MqConst.DEBUG);
                }
            }
        }
    }

    protected Pair<Long, Boolean> prepareValue(ConsumerQueueDto pre, Map<Long, MessageDto> messageMap) {
        Pair<Long, Boolean> pair = new Pair<>();
        long maxId = 0;
        // boolean flag = false;
        pair.item1 = 0L;
        pair.item2 = false;
        int count = 0;
        while (count < pre.getConsumerBatchSize()) {
            TraceMessageDto traceMessageDto = messages.poll();
            if (isRunning && traceMessageDto != null && checkOffsetVersion(pre)) {
                slowMsgMap.put(traceMessageDto.getId(), traceMessageDto);
                MessageDto messageDto = traceMessageDto.message;
                if (onMsgFilter(messageDto)) {
                    if (checkTag(pre, messageDto)) {
                        if (checkDelay(messageDto, pre))
                            if (checkRetryCount(messageDto, pre)) {
                                messageDto.setTopicName(pre.getOriginTopicName());
                                messageDto.setConsumerGroupName(pre.getConsumerGroupName());
                                messageMap.put(messageDto.getId(), messageDto);
                                traceMessageDto.start();
                            }
                    }
                }
                maxId = maxId < messageDto.getId() ? messageDto.getId() : maxId;
                // flag = true;
                pair.item1 = maxId;
                pair.item2 = true;
            }
            count++;
        }
        return pair;
    }

    private boolean onMsgFilter(MessageDto messageDto) {
        List<IMsgFilter> msgFilters = mqContext.getMqEvent().getMsgFilters();
        boolean flag = true;
        for (IMsgFilter msgFilter : msgFilters) {
            try {
                flag = msgFilter.onMsgFilter(messageDto);
                if (!flag) {
                    return false;
                }
            } catch (Exception e) {

            }
        }
        return true;
    }

    protected boolean checkRetryCount(MessageDto messageDto, ConsumerQueueDto pre) {
        // TODO Auto-generated method stub
        return pre.getTopicType() == 1 || (pre.getTopicType() == 2 && pre.getRetryCount() > messageDto.getRetryCount());
    }

    // 调用本地方法
    protected List<Long> invokeMessage(ConsumerQueueDto temp, Map<Long, MessageDto> messageMap) {
        List<MessageDto> dtos = new ArrayList<>(messageMap.values());
        List<Long> failIds = null;
        Transaction transaction = Tracer.newTransaction("mq-queue-thread-handleMessage", temp.getTopicName());
        try {
            // 默认先获取所有id为失败ids
            failIds = doUncompressOrAddLog(temp, dtos);
            // 如果发送异常则当前批次消息算作全部失败
            failIds = doMessageReceived(dtos);
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            transaction.setStatus(e);
            log.error("消息消费失败,参数为：" + com.ppdai.infrastructure.mq.biz.common.util.JsonUtil.toJson(messageMap.values()),
                    e);
        } finally {
            if (failIds == null) {
                failIds = new ArrayList<>();
            }
            transaction.complete();
        }
        try {
            if (MqClient.getContext().getMqEvent().getPostHandleListener() != null) {
                MqClient.getContext().getMqEvent().getPostHandleListener().postHandle(temp,
                        failIds == null || failIds.isEmpty());
            }
        } catch (Exception e) {
            log.error("postHandle_error", e);
        }
        return failIds;
    }

    protected List<Long> doMessageReceived(List<MessageDto> dtos) throws Exception {
        if (consumerQueueRef.get().getTimeout() > 0) {
            return new MessageInvokeCommandForThreadIsolation(consumerGroupName, consumerQueueRef.get(), dtos,
                    iSubscriber, iAsynSubscriber).execute();
        } else {
            return MessageInvokeCommandForThreadIsolation.invoke(dtos, iSubscriber, iAsynSubscriber,
                    consumerQueueRef.get());
        }
    }

    protected List<Long> doUncompressOrAddLog(ConsumerQueueDto temp, List<MessageDto> dtos) {
        List<Long> allIds = new ArrayList<Long>(dtos.size());
        dtos.forEach(t1 -> {
            allIds.add(t1.getId());
            if (temp.getTraceFlag() == 1) {
                addHandleLog(t1, temp, "bengin_call", MqConst.DEBUG);
            }
        });
        return allIds;
    }

    protected void addHandleLog(MessageDto messageDto, ConsumerQueueDto temp, String action, int type) {
        if (temp.getTraceFlag() == 1) {
            LogRequest request = new LogRequest();
            request.setType(type);
            //request.setAction("consumer_handle_" + ((type == MqConst.ERROR) ? "error" : "suc"));
            request.setAction(action);
            request.setTopicName(temp.getTopicName());
            request.setBizId(messageDto.getBizId());
            request.setConsumerGroupName(consumerGroupName);
            request.setConsumerName(mqContext.getConsumerName());
            request.setTraceId(messageDto.getTraceId());
            request.setQueueId(temp.getQueueId());
            request.setQueueOffsetId(temp.getQueueOffsetId());
            request.setTraceId(messageDto.getTraceId());
            //System.out.println(request.getAction());
            mqResource.addLog(request);
        }

    }

    protected void sendFailMail() {
        if (failBeginTime == 0L) {
            failBeginTime = System.currentTimeMillis();
        } else {
            // 超过一分钟，一次都没有成功，则告警
            if ((System.currentTimeMillis() - failBeginTime) >= 60 * 1000) {
                String subject = "消息处理失败！";
                String content = String.format(
                        "ConsumerGroup:[%s]下的Consumer:[%s]处理的消息(topic:[%s],queue:[%s])从[%s]到[%s]这段时间一直处理失败，失败总数已达到%s条，请尽快处理!",
                        this.consumerGroupName, mqContext.getConsumerName(), consumerQueueRef.get().getTopicName(),
                        consumerQueueRef.get().getQueueId(), Util.formateDate(new Date(failBeginTime)),
                        Util.formateDate(new Date()), this.failCount.get());

                SendMailRequest request = new SendMailRequest();
                request.setType(2);
                request.setConsumerGroupName(consumerGroupName);
                request.setTopicName(consumerQueueRef.get().getTopicName());
                request.setSubject(subject);
                request.setContent(content);
                request.setKey(mqContext.getConsumerName() + "-" + consumerQueueRef.get().getTopicName() + "-消息处理失败");
                mqResource.sendMail(request);
                restetFailAlarm();
            }
        }
    }

    protected void restetFailAlarm() {
        failCount.set(0);
        failBeginTime = 0L;
    }

    protected boolean checkTag(ConsumerQueueDto pre, MessageDto messageDto) {
        if (Util.isEmpty(pre.getTag())) {
            return true;
        } else {
            boolean rs = ("," + pre.getTag() + ",").replaceAll(",,", ",")
                    .indexOf("," + messageDto.getTag() + ",") != -1;
            if (!rs)
                log.debug("当前消息的tag:" + messageDto.getTag() + ",不在订阅管理里的tag:" + pre.getTag() + "中！");
            return rs;
        }
    }

    protected PublishMessageRequest getFailMsgRequest(ConsumerQueueDto temp, List<MessageDto> messageDtos) {
        try {
            String failTopicName = TopicUtil.getFailTopicName(this.consumerGroupName, temp.getOriginTopicName());
            List<MessageDto> messageDtos1 = new ArrayList<>(messageDtos.size());
            messageDtos.forEach(messageDto -> {
                messageDto.setRetryCount(messageDto.getRetryCount() + 1);
                if (temp.getRetryCount() >= messageDto.getRetryCount()) {
                    MqClient.checkBody(messageDto);
                    messageDtos1.add(messageDto);
                } else {
                    log.warn("当前消息达到最大重试次数" + temp.getRetryCount() + "了,此条失败消息会丢失。"
                            + com.ppdai.infrastructure.mq.biz.common.util.JsonUtil.toJsonNull(messageDto));
                }
            });
            if (messageDtos1.size() > 0) {
                PublishMessageRequest request = new PublishMessageRequest();
                request.setTopicName(failTopicName);
                request.setMsgs(MessageUtil.getData(messageDtos1));
                if (!Util.isEmpty(mqContext.getConfig().getIp())) {
                    request.setClientIp(mqContext.getConfig().getIp());
                }
                return request;
            }

        } catch (Exception e) {
            log.error("publish fail message error", e);
        }
        return null;
    }

    // 检查是否需要延迟执行，注意需要保证服务器时间与消费机器本地时间不一致的问题,为了减轻服务器数据库的压力，需要假定数据库时间是同步的
    protected boolean checkDelay(MessageDto messageDto, ConsumerQueueDto temp) {
        if (temp.getDelayProcessTime() > 0) {
            long delta = messageDto.getSendTime().getTime() + temp.getDelayProcessTime() * 1000
                    - System.currentTimeMillis();
            if (delta > 0) {
                Util.sleep(delta);
                log.info("topic:" + temp.getTopicName() + "延迟" + delta + "毫秒");
            }
        }
        return true;
    }

    protected boolean checkOffsetVersion(ConsumerQueueDto pre) {
        return pre.getOffsetVersion() == consumerQueueRef.get().getOffsetVersion();
    }

    protected void cacheData(PullDataResponse response, ConsumerQueueDto pre) {
        if (checkOffsetVersion(pre)) {
            for (MessageDto t1 : response.getMsgs()) {
                if (!checkOffsetVersion(pre)) {
                    messages.clear();
                    break;
                }
                // 防止messaes 满了
                while (true && checkOffsetVersion(pre)) {
                    try {
                        TraceMessageDto traceMessageDto = new TraceMessageDto(t1, pre);
                        if (slowMsgMap.size() > 350) {
                            log.error("slownMsg is " + com.ppdai.infrastructure.mq.biz.common.util.JsonUtil.toJsonNull(slowMsgMap));
                            slowMsgMap.clear();
                        }
                        messages.put(traceMessageDto);
                        addPullLog(t1);
                        break;
                    } catch (Exception e) {
                    }
                    Util.sleep(100);
                }
                updateLastId(pre, t1);

            }
        }
    }

    protected void addPullLog(MessageDto t1) {
        ConsumerQueueDto temp = consumerQueueRef.get();
        addHandleLog(t1, temp, "pull_data_suc", MqConst.DEBUG);
    }

    protected void clearTrace() {
        try {
            TraceFactory.remove(traceMsgPull.getName());
            TraceFactory.remove(traceMsgDeal.getName());
            TraceFactory.remove(traceMsgCommit.getName());
            TraceFactory.remove(traceMsg.getName());
        } catch (Exception e) {
        }
    }

    public class Pair<T1, T2> {
        public T1 item1;
        public T2 item2;
    }

    private Object lockObject = new Object();

    public class BatchRecorder {
        public Map<Long, BatchRecorderItem> recordMap = new ConcurrentHashMap<Long, BatchRecorderItem>();
        // 记录最小的线程批次编号
        private volatile long start = 0L;
        // 记录当前开启的线程批次编号
        private volatile AtomicLong current = new AtomicLong(0);

        // 开启新的线程批次
        public long begin(int threadCount) {
            long current1 = current.incrementAndGet();
            BatchRecorderItem batchRecorderItem = new BatchRecorderItem();
            batchRecorderItem.setBatchReacorderId(current1);
            batchRecorderItem.setThreadCount(threadCount);
            batchRecorderItem.setBatchFinished(false);
            recordMap.put(current1, batchRecorderItem);
            return current1;
        }

        // 结束某个批次的线程，同时返回最大的连续执行完毕的线程批次id，如果没有返回null
        public BatchRecorderItem end(long batchReacorderId, long maxId) {
            BatchRecorderItem finishedItem = recordMap.get(batchReacorderId);
            if (finishedItem == null) {
                return null;
            }
            int count = finishedItem.getCounter().incrementAndGet();
            if (finishedItem.getMaxId() < maxId) {
                finishedItem.setMaxId(maxId);
            }
            if (!finishedItem.isBatchFinished()) {
                finishedItem.setBatchFinished(count == finishedItem.getThreadCount());
            }
            if (finishedItem.isBatchFinished()) {
                BatchRecorderItem rs = getLastestItem();
                return rs;
            }

            return null;
        }

        // 删除之前执行成功的线程批次
        public void delete(long batchReacorderId) {
            long temp = start;
            start = batchReacorderId;
            for (long i = temp + 1; i <= batchReacorderId; i++) {
                recordMap.remove(i);
            }
        }

        // 获取最大的连续执行线程批次
        public BatchRecorderItem getLastestItem() {
            BatchRecorderItem finishedItemPre = null;
            long current1 = current.get();
            for (long i = start; i <= current1; i++) {
                BatchRecorderItem finishedItem = recordMap.get(i);
                if (finishedItem == null) {
                    continue;
                }
                if (!finishedItem.isBatchFinished()) {
                    break;
                } else {
                    finishedItemPre = finishedItem;
                }
            }
            return finishedItemPre;
        }
    }

    public class MsgThread implements Runnable {
        private long batchRecorderId;
        private ConsumerQueueDto pre;
        private CountDownLatch countDownLatch;
        private AtomicInteger timeOutCount;

        public MsgThread(ConsumerQueueDto pre, long batchRecorderId, CountDownLatch countDownLatch,
                         AtomicInteger timeOutCount) {
            this.batchRecorderId = batchRecorderId;
            this.pre = pre;
            this.countDownLatch = countDownLatch;
            this.timeOutCount = timeOutCount;
        }

        @Override
        public void run() {
            this.timeOutCount.incrementAndGet();
            taskCounter.incrementAndGet();
            BatchRecorderItem batchRecorderItem = null;
            long maxId = 0;
            try {
                if (isRunning && checkOffsetVersion(pre)) {
                    maxId = threadExcute(pre, countDownLatch);
                    updateOffset(pre, maxId);
                } else {
                    countDownLatch.countDown();
                }
            } catch (Throwable e) {
                log.error("", e);
            }
            synchronized (lockObject) {
                batchRecorderItem = batchRecorder.end(batchRecorderId, maxId);
                if (batchRecorderItem != null) {
                    doCommit(pre, batchRecorderItem);
                }
            }
            this.timeOutCount.decrementAndGet();
            taskCounter.decrementAndGet();
        }
    }

    @Override
    public void notifyMsg() {
        createExecutorNotify();
        if (System.currentTimeMillis() > lastPullTime) {
            executorNotify.submit(new Runnable() {
                @Override
                public void run() {
                    doPullingData();
                }
            });
        }
    }

    private void createExecutorNotify() {
        if (executorNotify == null) {
            synchronized (this) {
                if (executorNotify == null) {
                    if (executorNotify == null || executorNotify.isShutdown() || executorNotify.isTerminated()) {
                        executorNotify = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                                new LinkedBlockingQueue<Runnable>(100),
                                SoaThreadFactory.create("MqQueueExcutorService-notify-" + consumerGroupName + "-"
                                        + consumerQueueRef.get().getQueueId(), true),
                                new ThreadPoolExecutor.DiscardOldestPolicy());
                    }
                }
            }
        }

    }

    @Override
    public void commit(List<MessageDto> messageDtos, ConsumerQueueDto consumerQueue) {
        if (checkOffsetVersion(consumerQueue)) {
            if (messageDtos != null && messageDtos.size() > 0) {
                Map<Long, MessageDto> failMsg = new HashMap<>(messageDtos.size());
                addExcuteLog(failMsg, consumerQueue, failMsg);
                // 发送失败告警
                failAlarm(failMsg, consumerQueue);
                // 发送失败队列消息
                PublishMessageRequest failRequest = getFailMsgRequest(consumerQueue, new ArrayList<>(failMsg.values()));
                // 如果是失败消息更新失败消息执行成功结果
                publishAndUpdateResultFailMsg(failRequest, consumerQueue, null);
            }
            if (checkOffsetVersion(consumerQueue) && consumerQueue.getOffset() > consumerQueueVersionDto.getOffset()) {
                consumerQueueVersionDto.setOffset(consumerQueue.getOffset());
                consumerQueueVersionDto.setOffsetVersion(consumerQueue.getOffsetVersion());
                consumerQueueVersionDto.setQueueOffsetId(consumerQueue.getQueueOffsetId());
                consumerQueueVersionDto.setConsumerGroupName(consumerQueue.getConsumerGroupName());
                consumerQueueVersionDto.setTopicName(consumerQueue.getTopicName());
                consumerQueueRef.get().setOffset(consumerQueue.getOffset());
            }
        }
    }

    @Override
    public Map<Long, TraceMessageDto> getSlowMsg() {
        return slowMsgMap;
    }
}