package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ppdai.infrastructure.mq.biz.common.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public class MqGroupExcutorService implements IMqGroupExcutorService {
    private Logger log = LoggerFactory.getLogger(MqGroupExcutorService.class);
    private volatile int versionCount = 0;
    private volatile boolean isRunning = false;
    private volatile ConsumerGroupOneDto localConsumerGroup;
    // private volatile Map<Long, MqQueueExcutorService> mqEx = new
    // ConcurrentHashMap<>();
    private volatile Map<Long, IMqQueueExcutorService> mqEx = new ConcurrentHashMap<>();
    private MqContext mqContext;
    private IMqResource mqResource;
    private IMqFactory mqFactory = null;

    public MqGroupExcutorService() {
        this(MqClient.getContext().getMqResource());
    }

    public MqGroupExcutorService(IMqResource mqResource) {
        this.mqContext = MqClient.getContext();
        this.mqResource = mqResource;
        this.mqFactory = MqClient.getMqFactory();
    }

    // 重平衡或者更新信息
    public void rbOrUpdate(ConsumerGroupOneDto consumerGroupOne, String serverIp) {
        Transaction transaction = Tracer.newTransaction("mq-group",
                "rbOrUpdate-" + consumerGroupOne.getMeta().getName());
        try {
            mqContext.getConsumerGroupMap().put(consumerGroupOne.getMeta().getName(), consumerGroupOne);
            if (localConsumerGroup == null) {
                localConsumerGroup = new ConsumerGroupOneDto();
                localConsumerGroup.setMeta(consumerGroupOne.getMeta());
                if (consumerGroupOne.getQueues() != null) {
                    localConsumerGroup.setQueues(new ConcurrentHashMap<>(consumerGroupOne.getQueues()));
                }
                versionCount = 0;
                addOpLog(consumerGroupOne,
                        " receive init data,从服务端" + serverIp + "收到初始化数据," + JsonUtil.toJson(localConsumerGroup));
            }
            if (consumerGroupOne.getMeta().getRbVersion() > localConsumerGroup.getMeta().getRbVersion()) {
                doRb(consumerGroupOne, serverIp);
            }
            if (consumerGroupOne.getMeta().getMetaVersion() > localConsumerGroup.getMeta().getMetaVersion()) {
                log.info("meta data changed,元数据发生变更" + consumerGroupOne.getMeta().getName());
                String preJson = JsonUtil.toJson(localConsumerGroup);
                localConsumerGroup.getMeta().setMetaVersion(consumerGroupOne.getMeta().getMetaVersion());
                updateMeta(consumerGroupOne);
                addOpLog(consumerGroupOne, "receive data and pre data is ,从服务端" + serverIp + "收到元数据,更新之前的数据为" + preJson
                        + ",更新为" + JsonUtil.toJson(localConsumerGroup));
            }
            localConsumerGroup.getMeta().setVersion(consumerGroupOne.getMeta().getVersion());
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    protected void doRb(ConsumerGroupOneDto consumerGroupOne, String serverIp) {
        log.info("raised rebalance,发生重平衡" + consumerGroupOne.getMeta().getName());
        // 当重平衡版本号不一致的时候，需要先停止当前的任务
        versionCount = 0;
        if (isRunning) {
            log.info("commit offset,提交偏移" + consumerGroupOne.getMeta().getName());
            close();
            //降低消息重复消费的概率
            if(mqContext.getConfig().getRbTimes()<=1){
                Util.sleep(1000L);
            }
            addOpLog(consumerGroupOne, "提交偏移,停止拉取,commit offset,stop pull");
        }
        log.info("update offset version,更新重平衡版本号" + consumerGroupOne.getMeta().getName());
        localConsumerGroup.getMeta().setRbVersion(consumerGroupOne.getMeta().getRbVersion());
        if (localConsumerGroup.getQueues() != null) {
            localConsumerGroup.setQueues(new ConcurrentHashMap<>(consumerGroupOne.getQueues()));
        } else {
            localConsumerGroup.setQueues(new ConcurrentHashMap<>(15));
        }
        isRunning = false;
        addOpLog(consumerGroupOne,
                "receive rebalance data,从服务端" + serverIp + "收到重平衡数据," + JsonUtil.toJson(localConsumerGroup));
    }

    protected void addOpLog(ConsumerGroupOneDto consumerGroupOne, String content) {
        OpLogRequest opLogRequest = new OpLogRequest();
        opLogRequest.setConsumerGroupName(consumerGroupOne.getMeta().getName());
        opLogRequest.setConsumerName(mqContext.getConsumerName());
        opLogRequest.setContent("消费端,consumer_" + mqContext.getConsumerName() + "," + content + "__version_is_"
                + consumerGroupOne.getMeta().getVersion());
        mqResource.addOpLog(opLogRequest);
    }

    protected void updateMeta(ConsumerGroupOneDto consumerGroupOne) {
        Transaction transaction = Tracer.newTransaction("mq-group",
                "updateMeta-" + consumerGroupOne.getMeta().getName());
        try {
            mqContext.getConsumerGroupMap().put(consumerGroupOne.getMeta().getName(), consumerGroupOne);
            if (consumerGroupOne.getQueues() != null) {
                consumerGroupOne.getQueues().entrySet().forEach(t1 -> {
                    if (localConsumerGroup.getQueues() == null) {
                        localConsumerGroup.setQueues(new ConcurrentHashMap<>(15));
                    }
                    if (t1.getKey() == t1.getValue().getQueueId()) {
                        localConsumerGroup.getQueues().put(t1.getKey(), t1.getValue());
                        // 防止此时queue定时服务还没有启动
                        if (mqEx.containsKey(t1.getKey())) {
                            // 更新执行线程的元数据信息，由具体的执行类来更新相关信息
                            mqEx.get(t1.getKey()).updateQueueMeta(t1.getValue());
                        }
                    }
                });
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }

    // 启动时必须是连续三次，版本号没有发生变化的时候才启动
    @Override
    public void start() {
        if (!isRunning) {
            versionCount++;
            log.info("retry_" + localConsumerGroup.getMeta().getName() + "_version_"
                    + localConsumerGroup.getMeta().getRbVersion() + "_retrying_" + versionCount + " of "
                    + mqContext.getConfig().getRbTimes() + " times");
            if (versionCount >= mqContext.getConfig().getRbTimes()) {
                doStartQueue();
                isRunning = true;
            }
        }
    }

    protected void doStartQueue() {
        if (localConsumerGroup != null && localConsumerGroup.getQueues() != null
                && localConsumerGroup.getQueues().size() > 0) {
            Transaction transaction = Tracer.newTransaction("mq-group",
                    "doStasrtQueue-" + localConsumerGroup.getMeta().getName());
            try {
                localConsumerGroup.getQueues().values().forEach(t1 -> {
                    IMqQueueExcutorService mqQueueExcutorService = mqFactory
                            .createMqQueueExcutorService(localConsumerGroup.getMeta().getName(), t1);
                    mqEx.put(t1.getQueueId(), mqQueueExcutorService);
                    mqQueueExcutorService.start();
                    log.info("queueid_{}_started.", t1.getQueueId());
                });
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                log.error("doStasrtQueue_error", e);
                transaction.setStatus(e);
            } finally {
                transaction.complete();
            }
        }

    }

    private void commitMessage() {
        if (localConsumerGroup != null && localConsumerGroup.getQueues() != null
                && localConsumerGroup.getQueues().size() > 0) {
            Transaction transaction = Tracer.newTransaction("mq-group",
                    "commitMessage-" + localConsumerGroup.getMeta().getName());
            try {
                CommitOffsetRequest request = new CommitOffsetRequest();
                List<ConsumerQueueVersionDto> queueVersionDtos = new ArrayList<>();
                request.setQueueOffsets(queueVersionDtos);
                mqEx.entrySet().forEach(t -> {
                    long start = System.currentTimeMillis();
                    while (!t.getValue().hasFininshed()) {
                        Util.sleep(10);
                        if (System.currentTimeMillis() - start > 10000) {
                            break;
                        }
                    }
                    ConsumerQueueVersionDto consumerQueueVersionDto = t.getValue().getLast();
                    if (consumerQueueVersionDto != null) {
                        log.info("queueId:" + t.getKey() + " is fininshed:" + t.getValue().hasFininshed());
                        queueVersionDtos.add(consumerQueueVersionDto);
                    }
                });
                request.setFlag(1);
                if (queueVersionDtos.size() > 0) {
                    log.info("commit:" + localConsumerGroup.getMeta().getName() + " offset,commit size is " + JsonUtil.toJsonNull(queueVersionDtos));
                    mqResource.commitOffset(request);
                    versionCount = 0;
                }
                //Util.sleep(100_000L);
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                transaction.setStatus(e);
            } finally {
                transaction.complete();
            }
        }

    }

    // 停掉定时器
    private void closeQueues() {
        if (isRunning && localConsumerGroup != null && mqEx != null && mqEx.size() > 0) {
            Transaction transaction = Tracer.newTransaction("mq-group",
                    "closeQueues-" + localConsumerGroup.getMeta().getName());
            try {
                mqEx.values().forEach(t1 -> {
                    t1.close();
                });
                mqEx.clear();
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                transaction.setStatus(e);
            } finally {
                transaction.complete();
            }
        }
    }

    private void stopQueues() {
        if (isRunning && localConsumerGroup != null && mqEx != null && mqEx.size() > 0) {
            Transaction transaction = Tracer.newTransaction("mq-group",
                    "stopQueues-" + localConsumerGroup.getMeta().getName());
            try {
                mqEx.values().forEach(t1 -> {
                    t1.stop();
                });
                transaction.setStatus(Transaction.SUCCESS);
            } catch (Exception e) {
                transaction.setStatus(e);
            } finally {
                transaction.complete();
            }
        }
    }

    @Override
    public void close() {
        if (isRunning) {
            stopQueues();
            // 提交偏移
            commitMessage();
            // 停止拉取
            closeQueues();
            isRunning = false;
        }
    }

    @Override
    public Map<Long, IMqQueueExcutorService> getQueueEx() {
        // TODO Auto-generated method stub
        return mqEx;
    }
}
