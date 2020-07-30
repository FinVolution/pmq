package com.ppdai.infrastructure.mq.biz.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.codahale.metrics.Counter;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.ConsumerGroupChangedListener;
import com.ppdai.infrastructure.mq.biz.common.metric.MetricSingleton;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupMetaDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.common.DbService;

@Component
public class ConsumerGroupCacheServiceImpl implements ConsumerGroupCacheService {
	private Logger log = LoggerFactory.getLogger(ConsumerGroupCacheServiceImpl.class);
	// 记录上次的最大id
	private volatile long lastMaxId = 0;
	private volatile boolean stop = true;
	// private final Object lockObj = new Object();
	private volatile long currentMaxId = 0;

	@Autowired
	private NotifyMessageService notifyMessageService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private QueueOffsetService queueOffsetService;

	@Autowired
	private DbService dbService;

	private volatile Date lastDate = new Date();
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("ConsumerGroupCacheService", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());

	private List<ConsumerGroupChangedListener> lstListener = new ArrayList<>();
	// key为consumerGroupName
	private AtomicReference<Map<String, ConsumerGroupDto>> consumerGroupRefMap = new AtomicReference<>(
			new ConcurrentHashMap<>());

	private Counter initConsumerGroupCounter = null;
	private Counter pollingCounter = null;
	private TraceMessage traceMax = TraceFactory.getInstance("GroupCacheMaxId");
	@Autowired
	private SoaConfig soaConfig;

	@Override
	public synchronized void startBroker() {
		if (stop) {
			stop = false;
			initConsumerGroupCounter = MetricSingleton.getMetricRegistry().counter("initConsumerGroup");
			pollingCounter = MetricSingleton.getMetricRegistry().counter("pollingData");
			lastMaxId = notifyMessageService.getDataMaxId();
			Transaction transaction = Tracer.newTransaction("ConsumerGroupCacheService", "init");
			try {
				initData();
				transaction.setStatus(Transaction.SUCCESS);
			} catch (Exception e) {
				transaction.setStatus(e);
				log.error("ConsumerGroupCacheService_error,异常", e);
				throw e;
			} finally {
				transaction.complete();
			}
			lastDate = new Date();
			executor.execute(() -> {
				checkPollingData();
			});
		}
	}

	private synchronized void initData() {
		Map<String, ConsumerGroupDto> dataMap = doInitData();
//		String json = JsonUtil.toJson(dataMap);
//		log.info("cache_data_is " + json);
		if (dataMap.size() > 0) {
			consumerGroupRefMap.set(dataMap);
		}
		log.info("ConsumerGroup_init_suc, 初始化完成！");
		initConsumerGroupCounter.inc();
	}

	private Map<String, ConsumerGroupDto> doInitData() {
		List<ConsumerGroupEntity> consumerGroupEntities = consumerGroupService.getList();
		List<QueueOffsetEntity> queueOffsetEntities = queueOffsetService.getAllBasic();
		List<ConsumerGroupTopicEntity> consumerGroupTopicEntities = consumerGroupService.getGroupTopic();
		Map<Long, ConsumerGroupVo> consumerMap = new HashMap<>(consumerGroupEntities.size());
		consumerGroupEntities.forEach(t1 -> {
			consumerMap.put(t1.getId(), new ConsumerGroupVo(t1));
		});
		consumerGroupTopicEntities.forEach(t1 -> {
			if (consumerMap.containsKey(t1.getConsumerGroupId())) {
				consumerMap.get(t1.getConsumerGroupId()).topics.put(t1.getTopicId(), t1);
			}
		});
		queueOffsetEntities.forEach(t1 -> {
			if (!StringUtils.isEmpty(t1.getConsumerName())) {
				if (consumerMap.containsKey(t1.getConsumerGroupId())) {
					consumerMap.get(t1.getConsumerGroupId()).queueOffsets.add(t1);
				}
			}
		});
		return convertResult(consumerMap);
	}

	private Map<String, ConsumerGroupDto> convertResult(Map<Long, ConsumerGroupVo> consumerMap) {
		Map<String, ConsumerGroupDto> dataMap = new ConcurrentHashMap<>(consumerMap.size());
		consumerMap.values().forEach(t1 -> {
			ConsumerGroupEntity t2 = t1.consumerGroup;
			ConsumerGroupDto consumerGroupDto = new ConsumerGroupDto();
			dataMap.put(t2.getName(), consumerGroupDto);
			ConsumerGroupMetaDto consumerGroupMeta = new ConsumerGroupMetaDto();
			consumerGroupMeta.setMetaVersion(t2.getMetaVersion());
			consumerGroupMeta.setName(t2.getName());
			consumerGroupMeta.setRbVersion(t2.getRbVersion());
			consumerGroupMeta.setVersion(t2.getVersion());
			consumerGroupDto.setMeta(consumerGroupMeta);
			// key为consumerid，里面的key为topicid
			Map<Long, Map<Long, ConsumerQueueDto>> consumers = new HashMap<>();
			consumerGroupDto.setConsumers(consumers);
			t1.queueOffsets.forEach(t3 -> {
				if (!StringUtils.isEmpty(t3.getOriginTopicName())) {
					if (!consumers.containsKey(t3.getConsumerId())) {
						// consumers.putIfAbsent(t3.getConsumerId(), new HashMap<>());
						consumers.put(t3.getConsumerId(), new HashMap<>());
					}

					ConsumerQueueDto consumerQueueDto = getConsumerQueue(t1, t2, t3);
					consumers.get(t3.getConsumerId()).put(consumerQueueDto.getQueueId(), consumerQueueDto);
				} else {
					setCatError(t3.getTopicName());
					log.error("OriginTopicName_is_empty_queueOffsetId_{}", t3.getId());
				}
			});
		});
		return dataMap;
	}

	private void setCatError(String topicName) {
		Transaction transaction = Tracer.newTransaction("ConsumerGroupCacheService", topicName);
		transaction.setStatus(topicName + "_origin_is_emtpy");
		transaction.complete();
	}

	private ConsumerQueueDto getConsumerQueue(ConsumerGroupVo t2, ConsumerGroupEntity t1, QueueOffsetEntity t3) {
		ConsumerQueueDto consumerQueueDto = new ConsumerQueueDto();
		consumerQueueDto.setOffsetVersion(t3.getOffsetVersion());
		consumerQueueDto.setQueueId(t3.getQueueId());
		consumerQueueDto.setQueueOffsetId(t3.getId());
		consumerQueueDto.setTopicId(t3.getTopicId());
		consumerQueueDto.setTopicName(t3.getTopicName());
		consumerQueueDto.setStopFlag(t3.getStopFlag());
		consumerQueueDto.setTopicType(t3.getTopicType());
		consumerQueueDto.setOriginTopicName(t3.getOriginTopicName());
		consumerQueueDto.setTraceFlag(t1.getTraceFlag());
		consumerQueueDto.setConsumerGroupName(t1.getName());
		consumerQueueDto.setTopicName(t3.getTopicName());
		consumerQueueDto.setOffset(t3.getOffset());
		ConsumerGroupTopicEntity temp = t2.topics.get(t3.getTopicId());
		if (temp != null) {
			consumerQueueDto.setDelayProcessTime(temp.getDelayProcessTime());
			consumerQueueDto.setThreadSize(temp.getThreadSize());
			consumerQueueDto.setPullBatchSize(temp.getPullBatchSize());
			consumerQueueDto.setConsumerBatchSize(temp.getConsumerBatchSize());
			consumerQueueDto.setRetryCount(temp.getRetryCount());
			consumerQueueDto.setTag(temp.getTag());
			consumerQueueDto.setMaxPullTime(temp.getMaxPullTime());
			consumerQueueDto.setTimeout(temp.getTimeOut());
		} else {
			// 大部分情况不会调用此代码，防止极端情况
			consumerQueueDto.setDelayProcessTime(0);
			consumerQueueDto.setThreadSize(10);
			consumerQueueDto.setConsumerBatchSize(1);
			consumerQueueDto.setPullBatchSize(50);
			consumerQueueDto.setRetryCount(10);
			consumerQueueDto.setTag(null);
			consumerQueueDto.setMaxPullTime(5);
			consumerQueueDto.setTimeout(0);
		}
		return consumerQueueDto;
	}

	public synchronized void addListener(ConsumerGroupChangedListener listener) {
		if (!lstListener.contains(listener)) {
			lstListener.add(listener);
		}
	}

	private void checkPollingData() {
		while (!stop) {
			doCheckPollingData();
			Util.sleep(soaConfig.getCheckPollingDataInterval());
		}
	}

	private void doCheckPollingData() {
		Transaction catTransaction = Tracer.newTransaction("ConsumerGroupCacheService", "DoCheckPollingData");
		TraceMessageItem item = new TraceMessageItem();
		try {
			if (reInit()) {
				item.status = "reInit";
				item.msg = "currentMaxId:" + currentMaxId + ",maxId:" + lastMaxId + ",dbtime is "
						+ Util.formateDate(dbService.getDbTime());
			} else {
				currentMaxId = notifyMessageService.getDataMaxId(lastMaxId);
				if (currentMaxId > 0 && currentMaxId > lastMaxId) {
					item.status = "maxId-" + lastMaxId;
					updateCache();
					lastMaxId = currentMaxId;
					item.msg = "currentMaxId:" + currentMaxId + ",maxId:" + lastMaxId + ",dbtime is "
							+ Util.formateDate(dbService.getDbTime());
				} else {
					item.status = "maxId-nodata";
				}
			}

			catTransaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			log.error("doCheckPollingData_error,更新异常", e);
			catTransaction.setStatus(e);
		} finally {
			catTransaction.complete();
			traceMax.add(item);
		}
	}

	private void updateCache() {
		Transaction catTransaction1 = Tracer.newTransaction("ConsumerGroupCacheService", "ConsumerGroupEntity");
		List<ConsumerGroupEntity> consumerGroupEntities = new ArrayList<>();
		try {
			pollingCounter.inc();
			consumerGroupEntities = consumerGroupService.getLastMetaConsumerGroup(lastMaxId, currentMaxId);
			catTransaction1.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			catTransaction1.setStatus(e);
			log.error("getLastMetaConsumerGroup_error", e);
		} finally {
			catTransaction1.complete();
		}
		// 更新缓存
		updateCacheData(consumerGroupEntities);
		executor.submit(() -> {
			fireListener();
		});

	}

	private void updateCacheData(List<ConsumerGroupEntity> consumerGroupEntities) {
		Map<String, ConsumerGroupDto> dataMap = new HashMap<>();
		if (consumerGroupEntities.size() > 10) {
			dataMap = doInitData();
		} else {
			List<Long> ids = new ArrayList<>(consumerGroupEntities.size());
			consumerGroupEntities.forEach(t1 -> {
				ids.add(t1.getId());
			});

			List<QueueOffsetEntity> queueOffsetEntities = queueOffsetService.getByConsumerGroupIds(ids);
			List<ConsumerGroupTopicEntity> consumerGroupTopicEntities = consumerGroupService.getGroupTopic();
			Map<Long, ConsumerGroupVo> consumerMap = new HashMap<>(consumerGroupEntities.size());
			consumerGroupEntities.forEach(t1 -> {
				consumerMap.put(t1.getId(), new ConsumerGroupVo(t1));
			});
			consumerGroupTopicEntities.forEach(t1 -> {
				if (consumerMap.containsKey(t1.getConsumerGroupId())) {
					consumerMap.get(t1.getConsumerGroupId()).topics.put(t1.getTopicId(), t1);
				}
			});
			queueOffsetEntities.forEach(t1 -> {
				if (!StringUtils.isEmpty(t1.getConsumerName())) {
					if (consumerMap.containsKey(t1.getConsumerGroupId())) {
						consumerMap.get(t1.getConsumerGroupId()).queueOffsets.add(t1);
					}
				}
			});
			dataMap = convertResult(consumerMap);
		}
		Map<String, ConsumerGroupDto> cache = consumerGroupRefMap.get();
		dataMap.entrySet().forEach(t1 -> {
			if (!cache.containsKey(t1.getKey())) {
				cache.put(t1.getKey(), t1.getValue());
			} else {
				if (cache.get(t1.getKey()).getMeta() != null
						&& cache.get(t1.getKey()).getMeta().getVersion() < t1.getValue().getMeta().getVersion()) {
					cache.put(t1.getKey(), t1.getValue());
				}
			}
		});

	}

	private boolean reInit() throws Exception {
		// 为了保险起见过30秒重新构建一次
		if (soaConfig.isEnableRebuild()
				&& System.currentTimeMillis() - soaConfig.getReinitInterval() * 1000 > lastDate.getTime()) {
			currentMaxId = lastMaxId = notifyMessageService.getDataMaxId();
			try {
				initData();
				executor.execute(() -> {
					fireListener();
				});
				log.info("重新初始化数据");
			} catch (Exception e) {
				log.error("ConsumerGroupCacheService_reint_error,异常", e);
				throw e;
			}
			lastDate = new Date();
			// continue;
			return true;
		}
		return false;
	}

	private void fireListener() {
		for (ConsumerGroupChangedListener listener : lstListener) {
			try {
				listener.onChanged();
			} catch (Exception e) {
			}
		}
	}

	public Map<String, ConsumerGroupDto> getCache() {
		// String json=JsonUtil.toJson(consumerGroupRefMap.get());
		// log.info("cache_data_is "+json);
		return consumerGroupRefMap.get();
	}

	@PreDestroy
	private void close() {
		stopBroker();
	}

	class ConsumerGroupVo {
		public ConsumerGroupEntity consumerGroup;
		// key为topicid
		public Map<Long, ConsumerGroupTopicEntity> topics = new HashMap<>();
		public List<QueueOffsetEntity> queueOffsets = new ArrayList<>();

		public ConsumerGroupVo(ConsumerGroupEntity consumerGroup) {
			this.consumerGroup = consumerGroup;
		}
	}

	@Override
	public void stopBroker() {
		stop = true;
		try {
			executor.shutdown();
			executor = null;
		} catch (Exception e) {
		}
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}
}
