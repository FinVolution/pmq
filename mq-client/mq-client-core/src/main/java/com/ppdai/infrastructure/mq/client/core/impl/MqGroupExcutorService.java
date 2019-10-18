package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;
import com.ppdai.infrastructure.mq.client.MqClient.IMqClientBase;
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
	private IMqClientBase mqClientBase;

	public MqGroupExcutorService(IMqClientBase mqClientBase) {
		this(mqClientBase, mqClientBase.getContext().getMqResource());
	}

	public MqGroupExcutorService(IMqClientBase mqClientBase, IMqResource mqResource) {
		this.mqContext = mqClientBase.getContext();
		this.mqResource = mqResource;
		this.mqFactory = mqClientBase.getMqFactory();
		this.mqClientBase = mqClientBase;
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
			// 提交偏移
			commitMessage();
			log.info("stop pull,停止拉取" + consumerGroupOne.getMeta().getName());
			// 停止拉取
			closeQueues();
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

	// 停掉定时器
	protected void closeQueues() {
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

	// 启动时必须是连续三次，版本号没有发生变化的时候才启动
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
					IMqQueueExcutorService mqQueueExcutorService = mqFactory.createMqQueueExcutorService(mqClientBase,
							localConsumerGroup.getMeta().getName(), t1);
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

	// 批量提交，目的是为了提高网络请求次数，当需要关闭或者重平衡的时候 需要快速提交信息
	protected void commitMessage() {
		if (localConsumerGroup != null && localConsumerGroup.getQueues() != null
				&& localConsumerGroup.getQueues().size() > 0) {
			Transaction transaction = Tracer.newTransaction("mq-group",
					"commitMessage-" + localConsumerGroup.getMeta().getName());
			try {
				CommitOffsetRequest request = new CommitOffsetRequest();
				List<ConsumerQueueVersionDto> queueVersionDtos = new ArrayList<>();
				request.setQueueOffsets(queueVersionDtos);
				localConsumerGroup.getQueues().values().forEach(t1 -> {
					ConsumerQueueVersionDto consumerQueueVersionDto = new ConsumerQueueVersionDto();
					consumerQueueVersionDto.setOffset(t1.getOffset());
					consumerQueueVersionDto.setQueueOffsetId(t1.getQueueOffsetId());
					consumerQueueVersionDto.setOffsetVersion(t1.getOffsetVersion());
					consumerQueueVersionDto.setConsumerGroupName(t1.getConsumerGroupName());
					consumerQueueVersionDto.setTopicName(t1.getTopicName());
					queueVersionDtos.add(consumerQueueVersionDto);
				});
				request.setFlag(1);
				mqResource.commitOffset(request);
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
			// 提交偏移
			commitMessage();
			// 停止拉取
			closeQueues();
			isRunning = false;
		}
	}
}
