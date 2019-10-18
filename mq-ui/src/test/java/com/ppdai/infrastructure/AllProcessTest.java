package com.ppdai.infrastructure;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.TopicUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.ui.service.UiConsumerGroupTopicService;
import com.ppdai.infrastructure.ui.service.UiQueueOffsetService;
import com.ppdai.infrastructure.ui.service.UiTopicService;

public class AllProcessTest extends AbstractIntegrationTest {
	@Autowired
	private UiTopicService uiTopicService;
	@Autowired
	private UserInfoHolder userInfoHolder;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private UiConsumerGroupTopicService uiConsumerGroupTopicService;
	@Autowired
	private ConsumerGroupTopicService consumerGroupTopicService;
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private Message01Service message01Service;
	@Autowired
	private QueueService queueService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private UiQueueOffsetService uiQueueOffsetService;
	private ThreadPoolExecutor executor = null;

	// private volatile boolean sendFlag = true;
	private AtomicInteger sendCount = new AtomicInteger(0);

	MqClient.MqClientBase mqClientBase = null;
	MqClient.MqClientBase mqClientBase1 = null;
	List<String> topicGroup1 = new ArrayList<>();

	@After
	public void clear1() {
		reset(mqClientBase);
		reset(mqClientBase1);
		// 重新初始化
		deleteTopicAndConsumer();
	}

	@Test
	public void allProcess() throws InterruptedException, CheckFailException {
		userInfoHolder.setUserId(soaConfig.getMqAdminUser());
		try {
			init(1, 0);
			// createExcutor();
			// 发送消息
			sendMessage(mqClientBase, mqClientBase1, topicGroup1, "");
			checkMsg(topicGroup1.size() * 4);
			editTag("test1,test2");
			Util.sleep(10000);
			counter.set(0);
			sendMessage(mqClientBase, mqClientBase1, topicGroup1, "test3");
			checkMsg(4);
			Util.sleep(10000);

			editTag("");
			Util.sleep(10000);
			
			testWhiteBlackIp();
			
			// 编辑QueueOffset
			testQueueOffset(mqClientBase, mqClientBase1);

			// 测试超时熔断功能
			init(1, 0);
			// 单位是秒
			editTimeOut(1);
			counter.set(0);
			// sleep20秒
			sleepTime = 3000L;
			sendMessage(mqClientBase, mqClientBase1, topicGroup1, "");
			checkMsg(20);
			sleepTime = 0L;
			// 扩容
			expand();
			// sendFlag = false;
			// Thread.sleep(120000);

			// 测试广播消息----------------------------------------------
			init(2, 0);
			counter.set(0);
			sendMessage(mqClientBase, mqClientBase1, topicGroup1, "");
			checkMsg(topicGroup1.size() * 4 * 2);

			// 测试哨兵消息
			init(1, 1);
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put(QueueOffsetEntity.FdConsumerGroupName, TEST_CONSUMER_GROUP);
			List<QueueOffsetEntity> topicQueueOffsets = queueOffsetService.getList(conditionMap);
			String consumerName = topicQueueOffsets.get(0).getConsumerName();
			topicQueueOffsets.forEach(t1 -> {
				assertEquals("哨兵模式异常！", consumerName, t1.getConsumerName());
			});

		} finally {

		}

	}

	private void testWhiteBlackIp() {
		List<ConsumerGroupEntity> consumerGroupEntities = getConsumerGroupEntities(TEST_CONSUMER_GROUP);
		consumerGroupEntities.get(0).setIpWhiteList("0.0.0.0");
		consumerGroupService.update(consumerGroupEntities.get(0));
		consumerGroupService.notifyRb(consumerGroupEntities.get(0).getId());
		
		Util.sleep(10000);
		
		assertEquals("黑白名单异常！", 0, CollectionUtils.isEmpty(mqClientBase.getContext().getConsumerGroupMap().get(TEST_CONSUMER_GROUP).getQueues())?0:1);
		assertEquals("黑白名单异常！", 0, CollectionUtils.isEmpty(mqClientBase1.getContext().getConsumerGroupMap().get(TEST_CONSUMER_GROUP).getQueues())?0:1);
		
		
		consumerGroupEntities = getConsumerGroupEntities(TEST_CONSUMER_GROUP);
		consumerGroupEntities.get(0).setIpWhiteList("");
		consumerGroupService.update(consumerGroupEntities.get(0));
		consumerGroupService.notifyRb(consumerGroupEntities.get(0).getId());
		
		Util.sleep(10000);
		
		assertEquals("黑白名单异常！", 1, CollectionUtils.isEmpty(mqClientBase.getContext().getConsumerGroupMap().get(TEST_CONSUMER_GROUP).getQueues())?0:1);
		assertEquals("黑白名单异常！", 1, CollectionUtils.isEmpty(mqClientBase1.getContext().getConsumerGroupMap().get(TEST_CONSUMER_GROUP).getQueues())?0:1);
		
	}

	private void init(int consumerGroupType, int consumerQuality) {
		reset(mqClientBase);
		reset(mqClientBase1);
		// 重新初始化
		deleteTopicAndConsumer();
		Util.sleep((soaConfig.getReinitInterval() + 1) * 1000);
		mqClientBase = initMqClient();
		mqClientBase1 = initMqClient();
		topicGroup1 = new ArrayList<>();
		topicGroup1.add(TEST_TOPIC);
		topicGroup1.add(TEST_TOPIC1);
		buildTopic(topicGroup1);
		buildConsumerGroup(TEST_CONSUMER_GROUP, consumerGroupType, consumerQuality);
		subscribe(topicGroup1, TEST_CONSUMER_GROUP);
		Util.sleep(15000);
		registerAndConsumer(mqClientBase, TEST_CONSUMER_GROUP, topicGroup1);
		registerAndConsumer(mqClientBase1, TEST_CONSUMER_GROUP, topicGroup1);
		Util.sleep(mqClientBase.getContext().getConfig().getRbTimes() * 15000);
	}

	private void editTag(String tag) {
		// 修改ConsumerGroupTopic的参数
		List<ConsumerGroupTopicEntity> consumerGroupTopicEntities = getConsumerGroupTopicEntities(TEST_CONSUMER_GROUP,
				TEST_TOPIC);
		// assertEquals(2, consumerGroupTopicEntities.size());
		consumerGroupTopicEntities.get(0).setPullBatchSize(100);
		consumerGroupTopicEntities.get(0).setConsumerBatchSize(60);
		consumerGroupTopicEntities.get(0).setThreadSize(15);
		consumerGroupTopicEntities.get(0).setRetryCount(3);
		consumerGroupTopicEntities.get(0).setDelayProcessTime(10);
		consumerGroupTopicEntities.get(0).setTag(tag);
		uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntities.get(0));

	}

	private void editTimeOut(int timeOut) {
		// 修改ConsumerGroupTopic的参数
		List<ConsumerGroupTopicEntity> consumerGroupTopicEntities = getConsumerGroupTopicEntities(TEST_CONSUMER_GROUP,
				TEST_TOPIC);
		consumerGroupTopicEntities.get(0).setPullBatchSize(100);
		consumerGroupTopicEntities.get(0).setConsumerBatchSize(60);
		consumerGroupTopicEntities.get(0).setThreadSize(15);
		consumerGroupTopicEntities.get(0).setRetryCount(3);
		consumerGroupTopicEntities.get(0).setDelayProcessTime(1);
		consumerGroupTopicEntities.get(0).setTimeOut(timeOut);
		uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntities.get(0));

		consumerGroupTopicEntities = getConsumerGroupTopicEntities(TEST_CONSUMER_GROUP,
				TopicUtil.getFailTopicName(TEST_CONSUMER_GROUP, TEST_TOPIC));
		consumerGroupTopicEntities.get(0).setDelayProcessTime(1);
		uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntities.get(0));

	}

	private void checkMsg(int expectCount) {
		int temp = 0;
		while (temp < 20) {
			try {
				if (counter.get() == expectCount) {
					break;
				}

			} catch (Exception e) {

			}
			temp++;
			Util.sleep(4000);
		}
		assertEquals("发送消费数量不对", counter.get(), counter.get(), expectCount);

	}

	private void expand() {
		Transaction catTransaction = null;
		try {
			catTransaction = Tracer.newTransaction("ServiceTest", "expand");
			List<TopicEntity> topicEntityList = getTopicEntities(TEST_TOPIC);
			uiTopicService.expandTopic(topicEntityList.get(0).getId());
			catTransaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			catTransaction.setStatus(e);
		} finally {
			catTransaction.complete();
		}
	}

	private void testQueueOffset(MqClient.MqClientBase mqClientBase, MqClient.MqClientBase mqClientBase1)
			throws InterruptedException {
		Transaction catTransaction = null;
		try {
			catTransaction = Tracer.newTransaction("ServiceTest", "testQueueOffset");
			// 获取QueueOffset的信息
			List<QueueOffsetEntity> queueOffsetEntities = getQueueOffset(TEST_CONSUMER_GROUP, TEST_TOPIC);
			assertEquals(TOPIC_DAY_COUNT, queueOffsetEntities.size());

			List<ConsumerGroupTopicEntity> consumerGroupTopicEntities = getConsumerGroupTopicEntities(
					TEST_CONSUMER_GROUP, TopicUtil.getFailTopicName(TEST_CONSUMER_GROUP, TEST_TOPIC));
			assertEquals(1, consumerGroupTopicEntities.size());
			consumerGroupTopicEntities.get(0).setDelayProcessTime(1);
			uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntities.get(0));

			// 修改ConsumerGroupTopic的参数
			consumerGroupTopicEntities = getConsumerGroupTopicEntities(TEST_CONSUMER_GROUP, TEST_TOPIC);
			assertEquals(1, consumerGroupTopicEntities.size());
			consumerGroupTopicEntities.get(0).setPullBatchSize(100);
			consumerGroupTopicEntities.get(0).setConsumerBatchSize(60);
			consumerGroupTopicEntities.get(0).setThreadSize(15);
			consumerGroupTopicEntities.get(0).setRetryCount(3);
			consumerGroupTopicEntities.get(0).setDelayProcessTime(10);
			uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntities.get(0));

			uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntities.get(0));
			Thread.sleep(5000);
			CacheUpdateHelper.forceUpdateCache();
			for (QueueOffsetEntity queueOffsetEntity : queueOffsetEntities) {
				Map<Long, ConsumerQueueDto> queueList = getLongConsumerQueueDtoMap(mqClientBase, TEST_CONSUMER_GROUP);
				if (mqClientBase1.getContext().getConsumerGroupVersion().size() > 0) {
					queueList.putAll(getLongConsumerQueueDtoMap(mqClientBase1, TEST_CONSUMER_GROUP));
				}
				assertEquals(100, queueList.get(queueOffsetEntity.getQueueId()).getPullBatchSize());
				assertEquals(60, queueList.get(queueOffsetEntity.getQueueId()).getConsumerBatchSize());
				assertEquals(15, queueList.get(queueOffsetEntity.getQueueId()).getThreadSize());
				assertEquals(3, queueList.get(queueOffsetEntity.getQueueId()).getRetryCount());
				assertEquals(10, queueList.get(queueOffsetEntity.getQueueId()).getDelayProcessTime());
			}
			QueueOffsetEntity testQueueOffset = queueOffsetEntities.get(0);
			counter.set(0);
			// 重置offset
			uiQueueOffsetService.updateQueueOffset(testQueueOffset.getId(), 0);

			// 等待，看是否消费，并观察消息应该还是继续发送的
			Thread.sleep(30000);
			CacheUpdateHelper.forceUpdateCache();
			assertEquals("修改偏移不正确", testQueueOffset.getOffset(), counter.get());

			// 设置只读,为后续缩容做准备
			// uiQueueService.readOnly(testQueueOffset.getQueueId(), 2);

			QueueOffsetEntity stopQueueOffset = queueOffsetEntities.get(0);
			// 停止消费，在前面的基础之上，此时应该不消费了
			uiQueueOffsetService.updateStopFlag(stopQueueOffset.getId(), 1);

			counter.set(0);
			// 重置offset
			uiQueueOffsetService.updateQueueOffset(stopQueueOffset.getId(), 0);
			// 等待，看是否消费，并观察消息应该还是继续发送的
			Util.sleep(10000);
			CacheUpdateHelper.forceUpdateCache();
			// 测试失败消息
			setErrorFlag(true);
			uiQueueOffsetService.updateStopFlag(stopQueueOffset.getId(), 0);

			// 等待，看是否消费，并观察消息应该还是继续发送的
			Util.sleep(20000);
			assertEquals("失败消息", counter.get(), testQueueOffset.getOffset() * 3);

			setErrorFlag(false);
			queueOffsetEntities = getQueueOffset(TEST_CONSUMER_GROUP,
					TopicUtil.getFailTopicName(TEST_CONSUMER_GROUP, TEST_TOPIC));
			QueueOffsetEntity queueFailQueueOffset = queueOffsetEntities.get(0);
			// Map<Long, QueueEntity> queues
			QueueEntity temp = queueService.getAllQueueMap().get(queueFailQueueOffset.getQueueId());
			int retryCount = 0;
			if (temp != null) {
				List<Long> ids = new ArrayList<>();
				while (ids.size() == 0) {
					message01Service.setDbId(temp.getDbNodeId());
					List<Message01Entity> message01Entities = message01Service.getListDy(temp.getTopicName(),
							temp.getTbName(), 0, 100);
					for (Message01Entity t1 : message01Entities) {
						ids.add(t1.getId());
						retryCount = t1.getRetryCount() - 1;
					}
					if (ids.size() == 0) {
						queueFailQueueOffset = queueOffsetEntities.get(1);
						// Map<Long, QueueEntity> queues
						temp = queueService.getAllQueueMap().get(queueFailQueueOffset.getQueueId());
					}
				}
				message01Service.setDbId(temp.getDbNodeId());
				message01Service.updateFailMsgResult(temp.getTbName(), ids, -1);
				counter.set(0);
				uiQueueOffsetService.updateQueueOffset(queueFailQueueOffset.getId(), 0);
				// 等待，看是否消费，并观察消息应该还是继续发送的
				Util.sleep(10000L);
				// 8表示总的发送条数
				assertEquals("失败消息消费成功", counter.get(), sendCount.get() / 2);
				Util.sleep(10000L);
				CacheUpdateHelper.forceUpdateCache();
				message01Service.setDbId(temp.getDbNodeId());
				List<Message01Entity> message01Entities = message01Service.getListDy(temp.getTopicName(),
						temp.getTbName(), 0, 100);
				for (Message01Entity t1 : message01Entities) {
					assertEquals("重试次数标记", retryCount + Message01Service.failMsgRetryCountSuc, t1.getRetryCount());
				}
			}

			catTransaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			catTransaction.setStatus(e);
			throw e;
		} finally {
			catTransaction.complete();
		}
	}

	private void sendMessage(MqClient.MqClientBase mqClientBase, MqClient.MqClientBase mqClientBase1,
			List<String> topicNames, String tag) {

		for (String topicName : topicNames) {
			send(mqClientBase, topicName, 4, tag);
		}
		Util.sleep(4000);

	}

	private Map<Long, ConsumerQueueDto> getLongConsumerQueueDtoMap(MqClient.MqClientBase mqClientBase,
			String groupName) {
		boolean startFlag = true;
		Map<String, ConsumerGroupOneDto> consumerGroupMap = null;
		ConsumerGroupOneDto groupName1 = null;
		Map<Long, ConsumerQueueDto> queues = null;

		while (startFlag) {
			consumerGroupMap = mqClientBase.getContext().getConsumerGroupMap();
			if (consumerGroupMap != null) {
				groupName1 = consumerGroupMap.get(groupName);
				if (groupName1 != null) {
					queues = groupName1.getQueues();
					if (queues != null && queues.size() > 0) {
						startFlag = false;
					}
				}

			}
		}
		return queues;
	}

	private void send(MqClient.MqClientBase mqClientBase, String topic, int count, String tag) {
		try {
			Boolean rs = false;
			// 发送多条消息
			List<ProducerDataDto> messageDtoList = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				ProducerDataDto producerDataDto = new ProducerDataDto();
				producerDataDto.setBody(Util.formateDate(new Date()) + "--" + i);
				producerDataDto.setTag(tag);
				messageDtoList.add(producerDataDto);
				sendCount.incrementAndGet();
			}
			rs = mqClientBase.publish(topic, "", messageDtoList);
			assertEquals(rs, true);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void reset(MqClient.MqClientBase mqClientBase) {
		try {
			if (executor != null) {
				// executorStart = false;
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				executor.shutdownNow();
			}
			if (mqClientBase != null) {
				mqClientBase.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		CacheUpdateHelper.forceUpdateCache();
	}

	private List<ConsumerGroupTopicEntity> getConsumerGroupTopicEntities(String groupName, String topicName) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(ConsumerGroupTopicEntity.FdConsumerGroupName, groupName);
		conditionMap.put(ConsumerGroupTopicEntity.FdTopicName, topicName);
		return consumerGroupTopicService.getList(conditionMap);
	}

	private List<QueueOffsetEntity> getQueueOffset(String groupName, String topicName) {
		Map<String, Object> conditionMap = new HashMap<>();
		if (!StringUtils.isEmpty(groupName)) {
			conditionMap.put(QueueOffsetEntity.FdConsumerGroupName, groupName);
		}
		if (!StringUtils.isEmpty(topicName)) {
			conditionMap.put(QueueOffsetEntity.FdTopicName, topicName);
		}
		return queueOffsetService.getList(conditionMap);
	}

}
