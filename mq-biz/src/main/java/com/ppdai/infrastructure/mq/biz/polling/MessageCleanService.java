package com.ppdai.infrastructure.mq.biz.polling;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.TableInfoEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/*
 * 历史消息定时清理
 */
@Component
public class MessageCleanService extends AbstractTimerService {
	private Logger log = LoggerFactory.getLogger(MessageCleanService.class);
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private QueueService queueService;
	@Autowired
	private TopicService topicService;
	@Autowired
	private Message01Service message01Service;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private EmailUtil emailUtil;
	private ThreadPoolExecutor executor = null;
	private AtomicLong counter = new AtomicLong(0);
	private Map<String, AtomicLong> topicMap = new ConcurrentHashMap<>(1000);
	private long start = 0;
	private String startDate = "";

	@PostConstruct
	private void init() {
		super.init("mq_message_clean_sk", soaConfig.getCleanMessageInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getCleanMessageInterval();

			@Override
			public void run() {
				if (soaConfig.getCleanMessageInterval() != interval) {
					interval = soaConfig.getCleanMessageInterval();
					updateInterval(interval);
				}
			}
		});
	}

	@Override
	public void doStart() {
		counter.set(0);
		topicMap.clear();
		start = System.currentTimeMillis();
		startDate = Util.formateDate(new Date());
		Map<String, TopicEntity> data = topicService.getCache();
		// 第一级key为数据库物理机ip,第二级为topic名称，第三季为topic对应的queue
		Map<String, Map<String, TopicVo>> dbNodeTopicMap = new HashMap<>();
		// 获取已分配topic的queue
		Map<String, List<QueueEntity>> queue = queueService.getAllLocatedTopicQueue();
		// 记录需要清理的总的表的数量
		Map<Integer, Integer> queueCountMap = new HashMap<>();
		queueCountMap.put(0, 0);
		queue.entrySet().forEach(t1 -> {
			if (data.containsKey(t1.getKey())) {
				t1.getValue().forEach(t2 -> {
					if (!dbNodeTopicMap.containsKey(t2.getIp())) {
						dbNodeTopicMap.put(t2.getIp(), new HashMap<>(data.size()));
					}
					Map<String, TopicVo> topicMap = dbNodeTopicMap.get(t2.getIp());
					if (!topicMap.containsKey(t2.getTopicName())) {
						TopicVo tVo = new TopicVo();
						tVo.topic = data.get(t2.getTopicName());
						topicMap.put(t2.getTopicName(), tVo);
					}
					dbNodeTopicMap.get(t2.getIp()).get(t2.getTopicName()).queues.add(t2);
				});
				queueCountMap.put(0, queueCountMap.get(0) + t1.getValue().size());
			}
		});

		if (dbNodeTopicMap.size() == 0) {
			return;
		}
		createThreadExcutor(dbNodeTopicMap.size());
		CountDownLatch countDownLatch = new CountDownLatch(dbNodeTopicMap.size());

		log.info("begin to clean all db queue!");
		for (Map.Entry<String, Map<String, TopicVo>> entry1 : dbNodeTopicMap.entrySet()) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					Transaction transaction = Tracer.newTransaction("mq-msg", "clear-msg-" + entry1.getKey());
					try {
						clearOneDbData(entry1);
						transaction.setStatus(Transaction.SUCCESS);
					} catch (Throwable e) {
						transaction.setStatus(e);
					}
					transaction.complete();
					countDownLatch.countDown();
				}
			});
		}
		try {
			countDownLatch.await();
		} catch (Throwable e) {
			log.error("", e);
		}
		sendWarnMail();
		consumerGroupService.deleteUnuseBroadConsumerGroup();
		log.info("end to clean all db queue!");
	}

	private void clearOneDbData(
			Map.Entry<String, Map<String, TopicVo>> entry1) {
		Map<String, TopicVo> dataTopic = entry1.getValue();
		int count = 0;
		for (Map.Entry<String, TopicVo> entry : dataTopic.entrySet()) {
			if (isMaster()) {
				deleteOldData(entry.getValue(), entry1.getKey());
				count++;
				log.info("deleted " + entry.getKey() + "," + count + " of " + dataTopic.size() + " in ip " + entry1.getKey());
			} else {
				return;
			}
		}
	}

	private void createThreadExcutor(int size) {
		if (executor == null) {
			executor = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(50), SoaThreadFactory.create("MessageCleanService-%d", true),
					new ThreadPoolExecutor.DiscardOldestPolicy());
		}
		if (executor.getCorePoolSize() != size) {
			try {
				executor.shutdown();
				executor = null;
			} catch (Throwable e) {

			}
			executor = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(50), SoaThreadFactory.create("MessageCleanService-%d", true),
					new ThreadPoolExecutor.DiscardOldestPolicy());
		}
	}

	public void deleteOldData(TopicVo topic, String ip) {
		if (!(CollectionUtils.isEmpty(soaConfig.getClearTopics())
				&& !soaConfig.getClearTopics().contains(topic.topic.getName()))) {
			return;
		}
		/*if(!"OfflineAdjustAmount".equalsIgnoreCase(topic.topic.getName())){
			return;
		}*/
		String date1 = getNextDate(topic);
		for (QueueEntity queueEntity : topic.queues) {
			clearOneQueue(topic.topic, queueEntity, ip, date1);
		}
	}

	private String getNextDate(TopicVo topic) {
		int saveDays = topic.topic.getSaveDayNum();
		if (saveDays <= 0) {
			saveDays = 7;
		}

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -saveDays);
		date = calendar.getTime();
		return Util.formateDate(date);
	}

	private void clearOneQueue(TopicEntity topicEntity, QueueEntity queueEntity, String ip, String date) {
		long lastMinId = 0;
		long nextId = 0;
		boolean flag=false;
		int clearCount=0;
		while (true && isMaster() && soaConfig.isEnbaleMessageClean()) {
			int size = soaConfig.getCleanBatchSize();
			flag=false;
			topicEntity = topicService.getCache().get(topicEntity.getName());
			if (topicEntity == null) {
				break;
			}
			QueueEntity temp = queueService.getAllQueueMap().get(queueEntity.getId());
			if (temp != null && temp.getTopicId() != queueEntity.getTopicId()) {
				// 说明队列分配发生了变化
				break;
			}
			//Transaction transaction = Tracer.newTransaction("mq-msg", "clear-msg-" + topicEntity.getName());
			Transaction transaction = Tracer.newTransaction("msg-clear", "clear-msg-" + topicEntity.getName());
			try {
				if (lastMinId == 0) {
					TableInfoEntity tableInfoEntity = null;
					message01Service.setDbId(queueEntity.getDbNodeId());
					Long minId = message01Service.getTableMinId(queueEntity.getTbName());
					if (minId == null || minId == 0) {
						transaction.setStatus(Transaction.SUCCESS);
						tableInfoEntity = message01Service.getSingleTableInfoFromCache(temp);
						if (tableInfoEntity != null) {
							lastMinId = tableInfoEntity.getMaxId() - tableInfoEntity.getTbRows() - 1;
						}
						break;
					} else {
						lastMinId = minId;
						nextId = minId;
					}
					//此处代码是为了防止出现数据删除空洞的情况
					if (tableInfoEntity == null) {
						tableInfoEntity = message01Service.getSingleTableInfoFromCache(temp);
					}
					if (tableInfoEntity != null) {
						if(tableInfoEntity.getTbRows()>size){
							message01Service.setDbId(queueEntity.getDbNodeId());
							nextId = message01Service.getNextId(queueEntity.getTbName(), minId, size);
							flag=true;
						}else{
							nextId=nextId+size;
							flag=true;
						}
					}
				}
				if(!flag){
					nextId=nextId+size;
				}
				long sleepTime = getSkipTime();
				if (sleepTime != 0) {
					log.info("当前时间在skip时间内，需要等待" + sleepTime + "ms");
					Util.sleep(sleepTime);
				}
				message01Service.setDbId(queueEntity.getDbNodeId());
				int rows = message01Service.deleteDy(queueEntity.getTbName(), nextId, date, size);
				if (rows == 0) {
					transaction.setStatus(Transaction.SUCCESS);
					break;
				} else {
					counter.addAndGet(rows);
					lastMinId = lastMinId + rows;
					clearCount++;
					log.info("删除topic:" + queueEntity.getTopicName() + "," + rows + "条！总清理"+counter+"条！");
				}
				if (!topicMap.containsKey(topicEntity.getName())) {
					topicMap.put(topicEntity.getName(), new AtomicLong(0));
				}
				topicMap.get(topicEntity.getName()).addAndGet(rows);
				if(clearCount>20){
					if (queueEntity.getMinId() < lastMinId) {
						queueService.updateMinId(queueEntity.getId(), lastMinId);
					}
					clearCount=0;
				}
				transaction.setStatus(Transaction.SUCCESS);
			} catch (Throwable e) {
				transaction.setStatus(e);
			} finally {
				transaction.complete();
			}
			Util.sleep(soaConfig.getCleanSleepTime());
		}
		if (clearCount > 0) {
			if (queueEntity.getMinId() < lastMinId) {
				queueService.updateMinId(queueEntity.getId(), lastMinId);
			}
		} else if (queueEntity.getMinId() + 1 < lastMinId) {
			queueService.updateMinId(queueEntity.getId(), lastMinId);
		}
	}

	private void sendWarnMail() {
		emailUtil.sendInfoMail("消息清理完成", "从" + startDate + "开始,本次清理耗时" + (System.currentTimeMillis() - start) / 60_000 + "分钟,等待时间为" + soaConfig._getSkipTime +
				",共删除条数为" + counter.get() + ",每个topic清理条数为" + JsonUtil.toJsonNull(topicMap));
	}

	private long getSkipTime() {
		List<SoaConfig.TimeRange> ranges = soaConfig.getSkipTime();
		if (CollectionUtils.isEmpty(ranges)) {
			return 0L;
		}
		Calendar calendar = Calendar.getInstance();
		int hourMinute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		for (SoaConfig.TimeRange range : ranges) {
			if (range.start <= hourMinute && range.end >= hourMinute) {
				return (range.end - hourMinute) * 60000L;
			}
		}
		return 0L;
	}

	@PreDestroy
	public void stopPortal() {
		super.stopPortal();
	}

	class TopicVo {
		public TopicEntity topic;
		public List<QueueEntity> queues = new ArrayList<>();
	}
}
