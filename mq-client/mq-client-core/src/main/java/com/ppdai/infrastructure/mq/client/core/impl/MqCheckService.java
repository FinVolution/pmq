package com.ppdai.infrastructure.mq.client.core.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupMetaDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqContext;
import com.ppdai.infrastructure.mq.client.core.IMqCheckService;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public class MqCheckService implements IMqCheckService {
	private Logger log = LoggerFactory.getLogger(MqCheckService.class);
	private MqContext mqContext;
	private IMqResource mqResource;
	private volatile boolean isStop = false;
	private volatile boolean runStatus = false;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private ThreadPoolExecutor executor = null;

	public MqCheckService() {
//		this.mqContext = mqContext;
//		this.mqResource = mqContext.getMqResource();
		this( MqClient.getContext().getMqResource());
	}

	public MqCheckService(IMqResource mqResource) {
		this.mqContext = MqClient.getContext();
		this.mqResource = mqResource;
	}

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("MqCheckService", true),
					new ThreadPoolExecutor.DiscardOldestPolicy());
			isStop = false;
			runStatus = false;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					while (!isStop) {
						runStatus = true;
						Transaction transaction = Tracer.newTransaction("mq-group", "check");
						try {
							checkData();
							transaction.setStatus(Transaction.SUCCESS);
						} catch (Exception e) {
							transaction.setStatus(e);
						} finally {
							transaction.complete();
						}
						runStatus = false;
						Util.sleep(120000);
					}
				}
			});
		}
	}

	protected void checkData() {
		Map<String, Long> versionMap = new HashMap<>(mqContext.getConsumerGroupVersion().size());
		mqContext.getConsumerGroupVersion().entrySet().forEach(t1 -> {
			versionMap.put(t1.getKey(), 0L);
		});
		if (versionMap.size() == 0)
			return;
		String rs = doCheck(versionMap);
		int count = 0;
		while (count < 3 && !Util.isEmpty(rs)) {
			rs = doCheck(versionMap);
			Util.sleep(10000);
			count++;
		}
		if (!Util.isEmpty(rs)) {
			rs = rs + ",client Ip:" + mqContext.getConfig().getIp()+", consumer name is "+mqContext.getConsumerName()+", ";
			sendErrorEmail(rs);
			log.error(rs);
			throw new RuntimeException(rs);
		}
	}

	protected void sendErrorEmail(String rs) {
		SendMailRequest request = new SendMailRequest();
		request.setSubject("mq client check meta data error");
		request.setContent(rs);
		request.setType(2);
		request.setKey("mq client check meta fail");
		mqResource.sendMail(request);
	}

	protected String doCheck(Map<String, Long> map) {
		GetConsumerGroupRequest request = new GetConsumerGroupRequest();
		request.setConsumerId(mqContext.getConsumerId());
		request.setConsumerGroupVersion(map);
		GetConsumerGroupResponse response = mqResource.getConsumerGroup(request);
		try {
			if (response != null && response.getConsumerGroups() != null) {
				for (ConsumerGroupOneDto t1 : response.getConsumerGroups().values()) {
					String rs = checkConsumerGroup(t1);
					if (!Util.isEmpty(rs)) {
						return rs; 
					}
					String name = t1.getMeta().getName();
					ConsumerGroupOneDto loc = mqContext.getConsumerGroupMap().get(name);
					if (!Util.isEmpty(rs) || t1.getMeta() == null || loc == null || loc.getQueues() == null) {
						return rs;
					}
					rs = checkQueues(name, t1.getQueues(), loc.getQueues());
					if (!Util.isEmpty(rs)) {
						return rs + ",the response json is " + JsonUtil.toJsonNull(response);
					}
				}
			} else if (response == null) {
				return "the data is empty!";
			}
		} catch (Exception ex) {
			throw new RuntimeException("doCheck 异常！" + JsonUtil.toJson(response) + "," + ex.getMessage());
		}
		return "";
	}

	protected String checkQueues(String groupName, Map<Long, ConsumerQueueDto> queues,
			Map<Long, ConsumerQueueDto> localQueues) {
		if (queues.size() != localQueues.size()) {
			return "consumerGroupName_" + groupName + "_queue is incorrect! the cosumer id is "
					+ mqContext.getConsumerName() + ".";
		}
		StringBuilder rs = new StringBuilder();
		for (Map.Entry<Long, ConsumerQueueDto> t1 : queues.entrySet()) {
			if (!localQueues.containsKey(t1.getKey())) {
				rs.append("the consumerGroupName:" + groupName + " queueId_" + t1.getKey()
						+ " is not existed!\n");
				continue;
			}
			ConsumerQueueDto local = localQueues.get(t1.getKey());
			if (t1.getValue().getDelayProcessTime() != local.getDelayProcessTime()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " DelayProcessTime is incorrect,and the broker is " + t1.getValue().getDelayProcessTime()
						+ "，and local is" + local.getDelayProcessTime() + "!\n");
			}
			if (t1.getValue().getOffsetVersion() != local.getOffsetVersion()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " OffsetVersion is incorrect,and the broker is " + t1.getValue().getOffsetVersion()
						+ "，and local is " + local.getOffsetVersion() + "!\n");
			}
			if (!checkData(t1.getValue().getOriginTopicName(), local.getOriginTopicName())) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ "_OriginTopicName is incorrect,and the broker is " + t1.getValue().getOriginTopicName()
						+ "，and local is " + local.getOriginTopicName() + "!\n");
			}
			if (t1.getValue().getPullBatchSize() != local.getPullBatchSize()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " PullBatchSize is incorrect,and the broker is " + t1.getValue().getPullBatchSize()
						+ "，and local is " + local.getPullBatchSize() + "!\n");
			}
			if (t1.getValue().getQueueId() != local.getQueueId()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " QueueId is incorrect,and the broker is " + t1.getValue().getQueueId() + "，and local is "
						+ local.getQueueId() + "!\n");
			}
			if (t1.getValue().getRetryCount() != local.getRetryCount()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " RetryCount is incorrect,and the broker is " + t1.getValue().getRetryCount()
						+ "，and local is " + local.getRetryCount() + "!\n");
			}
			if (t1.getValue().getStopFlag() != local.getStopFlag()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " StopFlag is incorrect,and the broker is " + t1.getValue().getStopFlag() + "，and local is "
						+ local.getStopFlag() + "!\n");
			}

			if (!checkData(t1.getValue().getTag(), local.getTag())) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " tag is incorrect,and the broker is " + t1.getValue().getTag() + "，and local is "
						+ local.getTag() + "!\n");
			}

			if (t1.getValue().getThreadSize() != local.getThreadSize()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " ThreadSize is incorrect,and the broker is " + t1.getValue().getThreadSize()
						+ "，and local is " + local.getThreadSize() + "!\n");
			}
			if (t1.getValue().getTopicId() != local.getTopicId()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " TopicId is incorrect,and the broker is " + t1.getValue().getTopicId() + "，and local is "
						+ local.getTopicId() + "!\n");
			}

			if (!checkData(t1.getValue().getTopicName(), local.getTopicName())) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
			             + " TopicName不对服务器端为"+ t1.getValue().getTopicName() + "，and local is " + local.getTopicName() + "!\n");
			}

			if (t1.getValue().getTopicType() != local.getTopicType()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " TopicType is incorrect,and the broker is " + t1.getValue().getTopicType() + "，and local is "
						+ local.getTopicType() + "!\n");
			}

			if (t1.getValue().getTraceFlag() != local.getTraceFlag()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " TraceFlag is incorrect,and the broker is " + t1.getValue().getTraceFlag() + "，and local is "
						+ local.getTraceFlag() + "!\n");
			}

			if (t1.getValue().getTimeout() != local.getTimeout()) {
				rs.append("在 consumerGroupName:" + groupName + ",topic:" + t1.getValue().getTopicName()+"下,"
						+ " Timeout is incorrect,and the broker is " + t1.getValue().getTimeout() + "，and local is "
						+ local.getTimeout() + "!\n");
			}
		}
		return rs.toString();
	}

	protected boolean checkData(String remote, String local) {
		return (remote + "").equals(local + "");
	}

	protected String checkConsumerGroup(ConsumerGroupOneDto t1) {
		if (t1.getMeta() == null) {
			return "meta 为空 !";
		}
		String rs = checkGroupMeta(t1.getMeta());
		if (!Util.isEmpty(rs)) {
			return rs;
		}
		return null;
	}

	protected String checkGroupMeta(ConsumerGroupMetaDto meta) {
		if (Util.isEmpty(meta.getName()) || !mqContext.getConsumerGroupMap().containsKey(meta.getName())) {
			return "";
		}
		String rs = "";
		ConsumerGroupMetaDto local = mqContext.getConsumerGroupMap().get(meta.getName()).getMeta();
		if (meta.getMetaVersion() != local.getMetaVersion()) {
			rs += "consumerGroupName_" + meta.getName() + "_metaversion is incorrect! ";
		}
		if (meta.getRbVersion() != local.getRbVersion()) {
			rs += "consumerGroupName_" + meta.getName() + "_rbversion  is incorrect! ";
		}
		if (meta.getVersion() != local.getVersion()) {
			rs += "consumerGroupName_" + meta.getName() + "_version  is incorrect! ";
		}
		return rs;
	}

	@Override
	public void close() {
		isStop = true;
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
