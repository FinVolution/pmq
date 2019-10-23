package com.ppdai.infrastructure.mq.biz.polling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.EmailService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;

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
	private QueueOffsetService queueOffsetService;
	@Autowired
	private TopicService topicService;
	@Autowired
	private Message01Service message01Service;

	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private EmailService emailService;

	@Autowired
	private EmailUtil emailUtil;

	@Autowired
	private Environment env;

	private ThreadPoolExecutor executor = null;

	@PostConstruct
	private void init() {
		super.init(Constants.MESSAGE_CLEAN, soaConfig.getCleanMessageInterval(), soaConfig);
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

		List<QueueOffsetEntity> queueOffsetCache = queueOffsetService.getCacheData();
		Map<Long, List<QueueOffsetEntity>> queueMinOffset = new HashMap<>();
		queueOffsetCache.forEach(t1 -> {
			if (!queueMinOffset.containsKey(t1.getQueueId())) {
				queueMinOffset.put(t1.getQueueId(), new ArrayList<>());
			}
			queueMinOffset.get(t1.getQueueId()).add(t1);
		});
		if (dbNodeTopicMap.size() == 0) {
			return;
		}
		createThreadExcutor(dbNodeTopicMap.size());
		CountDownLatch countDownLatch = new CountDownLatch(executor.getPoolSize());
		AtomicInteger queueCounter = new AtomicInteger(0);
		log.info("begin to clean all db queue!");
		for (Map.Entry<String, Map<String, TopicVo>> entry1 : dbNodeTopicMap.entrySet()) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					Transaction transaction = Tracer.newTransaction("mq-msg", "clear-msg-" + entry1.getKey());
					try {
						clearOneDbData(queueMinOffset, entry1, queueCounter, queueCountMap.get(0));
						transaction.setStatus(Transaction.SUCCESS);
					} catch (Exception e) {
						transaction.setStatus(e);
					}
					transaction.complete();
					countDownLatch.countDown();
				}
			});
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {

		}
		log.info("end to clean all db queue!");
	}

	private void clearOneDbData(Map<Long, List<QueueOffsetEntity>> queueMinOffset,
			Map.Entry<String, Map<String, TopicVo>> entry1, AtomicInteger counter, int queueTotal) {
		Map<String, TopicVo> dataTopic = entry1.getValue();
		for (Map.Entry<String, TopicVo> entry : dataTopic.entrySet()) {
			deleteOldData(entry.getValue(), queueMinOffset, counter, queueTotal, entry1.getKey());
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
			} catch (Exception e) {

			}
			executor = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(50), SoaThreadFactory.create("MessageCleanService-%d", true),
					new ThreadPoolExecutor.DiscardOldestPolicy());
		}
	}

	public void deleteOldData(TopicVo topic, Map<Long, List<QueueOffsetEntity>> queueMinOffset, AtomicInteger counter,
			int queueTotal, String ip) {
		if (!(CollectionUtils.isEmpty(soaConfig.getClearTopics())
				&& !soaConfig.getClearTopics().contains(topic.topic.getName())))
			return;
		int days = topic.topic.getSaveDayNum();
		if (days <= 0) {
			return;
		}
		for (QueueEntity queueEntity : topic.queues) {
			clearOneQueue(topic.topic, queueEntity, queueMinOffset, counter.incrementAndGet(), queueTotal, ip);
		}
	}

	private void clearOneQueue(TopicEntity topicEntity, QueueEntity queueEntity,
			Map<Long, List<QueueOffsetEntity>> queueMinOffset, int count, int tcount, String ip) {
		long minId = queueEntity.getMinId();
		if (!checkMinId(queueEntity)) {
			return;
		}
		Map<String, ConsumerGroupEntity> groupCache = consumerGroupService.getCache();
		int cleanSize = soaConfig.getCleanBatchSize();
		log.info("clean queue {} of {} ,ip is {}", count, tcount, ip);
		while (true && super.isMaster && soaConfig.isEnbaleMessageClean()) {
			topicEntity=topicService.getCache().get(topicEntity.getName());
			if(topicEntity==null) {
				break;
			}
			QueueEntity temp = queueService.getAllQueueMap().get(queueEntity.getId());
			if (temp != null && temp.getTopicId() != queueEntity.getTopicId()) {
				// 说明队列分配发生了变化
				break;
			}
			Transaction transaction = Tracer.newTransaction("mq-msg", "clear-msg");
			transaction.setStatus(Transaction.SUCCESS);
			try {
				message01Service.setDbId(queueEntity.getDbNodeId());
				long nextId = minId + cleanSize;
				Message01Entity message01Entity = message01Service.getMessageById(queueEntity.getTbName(), nextId);
				if (message01Entity == null) {
					// 下面这部分代码是为了防止出现一些异常情况数据清理出现间隔
					message01Service.setDbId(queueEntity.getDbNodeId());
					message01Entity = message01Service.getNearByMessageById(queueEntity.getTbName(), minId);
					if (message01Entity != null) {
						cleanSize = updateCleanSize(cleanSize);
						log.info("min_error,need to skip some data,and minid is " + minId + ",near id is "
								+ message01Entity.getId());
						minId = message01Entity.getId();
						nextId = minId;
					}
				} else {
					cleanSize = soaConfig.getCleanBatchSize();
				}
				if (message01Entity != null) {
					// 是否在保留天数之内
					if (!greaterThanNow(message01Entity.getSendTime(), topicEntity.getSaveDayNum())) {
						// 最小id是否超过偏移量，同时判断是否超过保留天数两天
						if (!checkConsumerGroup(topicEntity, queueEntity, message01Entity, queueMinOffset, groupCache,
								minId)) {
							break;
						}
						Transaction transaction1 = Tracer.newTransaction("mq-msg", "clear-msg1");
						try {
							minId = deleteData(topicEntity, queueEntity, count, tcount, ip, minId, message01Entity);
							transaction1.setStatus(Transaction.SUCCESS);
						} catch (Exception e) {
							transaction1.setStatus(e);
						}
						transaction1.complete();

					} else {
						log.warn(
								"topic_stop_{},queue_{}数据无需清理！更新最小minid为{},下一个id为{},保留天数为{},下一个清理消息时间为：{},queue {} of {},ip is {}",
								topicEntity.getName(), queueEntity.getId(), minId, nextId, topicEntity.getSaveDayNum(),
								Util.formateDate(message01Entity.getSendTime()), count, tcount, ip);
						break;
					}
				} else {
					log.warn("topic_stop_{},queue_{}没有数据需要清理！更新最小minid为{},下一个id为{},保留天数为{},queue {} of {}, ip is {}",
							topicEntity.getName(), queueEntity.getId(), minId, nextId, topicEntity.getSaveDayNum(),
							count, tcount, ip);
					break;
				}

			} catch (Exception e) {
				transaction.setStatus(e);
			} finally {
				transaction.complete();
			}
			Util.sleep(soaConfig.getCleanSleepTime());
		}
	}

	private boolean checkMinId(QueueEntity queueEntity) {
		Map<Long, Long> queueMax = queueService.getMax();
		if (queueMax.containsKey(queueEntity.getId())) {
			if (queueEntity.getMinId() >= queueMax.get(queueEntity.getId())) {
				emailUtil.sendErrorMail("queue:" + queueEntity.getId() + "中minid数据不对",
						"minid:" + queueEntity.getMinId() + ",maxId:" + (queueMax.get(queueEntity.getId()) - 1));
				log.warn("queue:" + queueEntity.getId() + "中minid数据不对,minid:" + queueEntity.getMinId() + ",maxId:"
						+ (queueMax.get(queueEntity.getId()) - 1));
				return false;
			}
		}
		return true;
	}

	private long deleteData(TopicEntity topicEntity, QueueEntity queueEntity, int count, int tcount, String ip,
			long minId, Message01Entity message01Entity) {
		// 是否在skip时间
		long sleepTime = getSkipTime();
		if (sleepTime != 0) {
			log.info("当前时间在skip时间内，需要等待" + sleepTime + "ms");
			Util.sleep(sleepTime);
		}
		long lastId = minId;
		minId = message01Entity.getId();
		queueService.updateMinId(queueEntity.getId(), minId);
		// 说明数据已经过期需要删除
		message01Service.setDbId(queueEntity.getDbNodeId());
		message01Service.deleteDy(queueEntity.getTbName(), lastId, message01Entity.getId());
		log.warn("topic_delete_{},queue_{}有数据过期了需要删除！更新最小minid为{},保留天数为{},message 时间为：{},queue {} of {} ,ip is {}",
				topicEntity.getName(), queueEntity.getId(), minId, topicEntity.getSaveDayNum(),
				Util.formateDate(message01Entity.getSendTime()), count, tcount, ip);
		return minId;
	}

	private int updateCleanSize(int cleanSize) {
		if (cleanSize == soaConfig.getCleanBatchSize()) {
			cleanSize = 0;
		}
		cleanSize = cleanSize + 100;
		if (cleanSize > soaConfig.getCleanBatchSize()) {
			cleanSize = 100;
		}
		return cleanSize;
	}

	private boolean checkConsumerGroup(TopicEntity topicEntity, QueueEntity queueEntity,
			Message01Entity message01Entity, Map<Long, List<QueueOffsetEntity>> queueMinOffsets,
			Map<String, ConsumerGroupEntity> groupCache, long minId) {
		if (!queueMinOffsets.containsKey(queueEntity.getId()))
			return true;
		boolean flag = true;
		List<QueueOffsetEntity> qs = queueMinOffsets.get(queueEntity.getId());
		for (QueueOffsetEntity queueMinOffset : qs) {
			// topic数据清理规则，如果出现当前id比便宜大，则延迟两天清理
			if (message01Entity.getId() > queueMinOffset.getOffset()
					&& greaterThanNow(message01Entity.getSendTime(), topicEntity.getSaveDayNum() + 2)) {
				String content = "在消费者组" + queueMinOffset.getConsumerGroupName() + ",topic:"
						+ queueMinOffset.getTopicName() + ",queue:" + queueMinOffset.getQueueId() + "的偏移量("
						+ queueMinOffset.getOffset() + ")超过topic保留期限(最小id为" + message01Entity.getId() + ",对应时间为"
						+ Util.formateDate(message01Entity.getSendTime()) + ",保留天数为" + (topicEntity.getSaveDayNum() + 2)
						+ ")，还未消费,请足够重视，提高消费速度，否则会在两天后，过期消息会被清除！";
				if (env.getProperty("mq.message.warn", "0").equals("1")) {
					// 说明出现了历史堆积的消息太长时间没有被消费了，需要发送告警邮件提醒
					sendWarnMail(queueMinOffset, groupCache, content, queueMinOffset.getConsumerGroupName() + ",topic:"
							+ queueMinOffset.getTopicName() + ",queue:" + queueMinOffset.getQueueId());
				}
				log.warn("topic_stop_" + topicEntity.getName() + content);
				flag = false;
			}
		}
		return flag;
	}

	private boolean greaterThanNow(Date time, int days) {
		return time.getTime() + days * 86400000 > System.currentTimeMillis();
	}

	private void sendWarnMail(QueueOffsetEntity queueOffsetEntity, Map<String, ConsumerGroupEntity> groupCache,
			String content, String key) {
		SendMailRequest request = new SendMailRequest();
		request.setConsumerGroupName(queueOffsetEntity.getConsumerGroupName());
		request.setTopicName(queueOffsetEntity.getTopicName());
		request.setContent(content);
		request.setKey(key);
		request.setSubject(queueOffsetEntity.getConsumerGroupName() + "消费堆积，超过topic保留时间");
		request.setType(1);
		emailService.sendConsumerMail(request);
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
