//package com.ppdai.infrastructure.ui.service;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
//import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
//import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
//import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
//import com.ppdai.infrastructure.mq.biz.common.util.Util;
//import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
//import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
//import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
//import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
//import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupCreateRequest;
//import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
//import com.ppdai.infrastructure.mq.biz.dto.request.TopicCreateRequest;
//import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
//import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
//import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
//import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
//import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
//import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
//import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
//import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
//import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
//import com.ppdai.infrastructure.mq.biz.service.QueueService;
//import com.ppdai.infrastructure.mq.biz.service.TopicService;
//import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
//import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupServiceImpl;
//import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupTopicServiceImpl;
//import com.ppdai.infrastructure.mq.biz.service.impl.TopicServiceImpl;
//import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;
//import com.ppdai.infrastructure.mq.client.MqClient;
//import com.ppdai.infrastructure.mq.client.MqConfig;
//import com.ppdai.infrastructure.mq.client.config.ConsumerGroupTopicVo;
//import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
//import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
//
//@Service
//public class ServiceTest {
//	//private Logger log = LoggerFactory.getLogger(ServiceTest.class);
//	@Autowired
//	private UiTopicService uiTopicService;
//	// @Autowired
//	// private UiConsumerGroupService uiConsumerGroupService;
//	@Autowired
//	private TopicService topicService;
//
//	@Autowired
//	private ConsumerGroupService consumerGroupService;
//	@Autowired
//	private UiConsumerGroupTopicService uiConsumerGroupTopicService;
//	@Autowired
//	private UiQueueService uiQueueService;
//	@Autowired
//	private ConsumerGroupTopicService consumerGroupTopicService;
//	@Autowired
//	private QueueOffsetService queueOffsetService;
//	@Autowired
//	private Environment env;
//	@Autowired
//	private UiQueueOffsetService uiQueueOffsetService;
//	@Autowired
//	private QueueService queueService;
//
//	private ThreadPoolExecutor executor = null;
//
//	private volatile boolean sendFlag = true;
//
//	private final String TEST_USER_ID = "管理员";
//	private final String TEST_CONSUMER_GROUP = "FlowTestSub";
//	private final String TEST_TOPIC = "FlowTopic1";
//	private final String TEST_TOPIC1 = "FlowTopic2";
//	private volatile boolean executorStart = true;
//
//	public void allProcess() throws InterruptedException, CheckFailException {
//
//		sendFlag = true;
//		// 初始化
//		deleteTopicAndConsumer();
//
//		reset(null);
//
//		// 初始化客户端，主要为了reset之后,初始化值
//		MqClient.MqClientBase mqClientBase = initMqClient();
//
//		// 创建Topic和ConsumerGroup,并且订阅
//		List<String> topicGroup1 = new ArrayList<>();
//		topicGroup1.add(TEST_TOPIC);
//		topicGroup1.add(TEST_TOPIC1);
//
//		createTopicAndConsumerGroupAndSubscribe(topicGroup1, TEST_CONSUMER_GROUP);
//
//		Thread.sleep(10000);
//
//		// 订阅消费
//		registerAndConsume(mqClientBase, TEST_CONSUMER_GROUP, topicGroup1);
//
//		// 发送消息
//		sendMessage(mqClientBase, topicGroup1);
//
//		// 编辑QueueOffset
//		testQueueOffset(mqClientBase);
//
//		// 扩容
//		expand();
//		sendFlag = false;
//		Thread.sleep(120000);
//		reset(mqClientBase);
//		// 重新初始化
//		deleteTopicAndConsumer();
//
//	}
//
//	private void expand() {
//		Transaction catTransaction = null;
//		try {
//			catTransaction = Tracer.newTransaction("ServiceTest", "expand");
//			List<TopicEntity> topicEntityList = getTopicEntities(TEST_TOPIC);
//			uiTopicService.expandTopic(topicEntityList.get(0).getId());
//			catTransaction.setStatus(Transaction.SUCCESS);
//		} catch (Exception e) {
//			e.printStackTrace();
//			catTransaction.setStatus(e);
//		} finally {
//			catTransaction.complete();
//		}
//	}
//
//	private void testQueueOffset(MqClient.MqClientBase mqClientBase) throws InterruptedException {
//		Transaction catTransaction = null;
//		try {
//			catTransaction = Tracer.newTransaction("ServiceTest", "testQueueOffset");
//			// 获取QueueOffset的信息
//			List<QueueOffsetEntity> queueOffsetEntities = getQueueOffset(TEST_CONSUMER_GROUP, TEST_TOPIC);
//			assertEquals(3, queueOffsetEntities.size());
//
//			// 修改ConsumerGroupTopic的参数
//			List<ConsumerGroupTopicEntity> consumerGroupTopicEntities = getConsumerGroupTopicEntities(
//					TEST_CONSUMER_GROUP, TEST_TOPIC);
//			assertEquals(2, consumerGroupTopicEntities.size());
//			consumerGroupTopicEntities.get(0).setPullBatchSize(100);
//			consumerGroupTopicEntities.get(0).setConsumerBatchSize(60);
//			consumerGroupTopicEntities.get(0).setThreadSize(15);
//			consumerGroupTopicEntities.get(0).setRetryCount(3);
//			consumerGroupTopicEntities.get(0).setDelayProcessTime(10);
//			uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntities.get(0));
//			Thread.sleep(1000);
//			for (QueueOffsetEntity queueOffsetEntity : queueOffsetEntities) {
//				Map<Long, ConsumerQueueDto> queueList = getLongConsumerQueueDtoMap(mqClientBase, TEST_CONSUMER_GROUP);
//				// log.info(queueOffsetEntity.getQueueId() + "," +
//				// JsonUtil.toJson(queueList));
//				try {
//					assertEquals(100, queueList.get(queueOffsetEntity.getQueueId()).getPullBatchSize());
//					assertEquals(60, queueList.get(queueOffsetEntity.getQueueId()).getConsumerBatchSize());
//					assertEquals(15, queueList.get(queueOffsetEntity.getQueueId()).getThreadSize());
//					assertEquals(3, queueList.get(queueOffsetEntity.getQueueId()).getRetryCount());
//					assertEquals(10, queueList.get(queueOffsetEntity.getQueueId()).getDelayProcessTime());
//				} catch (Exception e) {
//					e.printStackTrace();
//					throw new RuntimeException(e);
//				}
//			}
//			QueueOffsetEntity testQueueOffset = queueOffsetEntities.get(0);
//			// 重置offset
//			uiQueueOffsetService.updateQueueOffset(testQueueOffset.getId(), 0);
//			// 等待，看是否消费，并观察消息应该还是继续发送的
//			Thread.sleep(20000);
//
//			// 设置只读,为后续缩容做准备
//			uiQueueService.readOnly(testQueueOffset.getQueueId(), 2);
//
//			QueueOffsetEntity stopQueueOffset = queueOffsetEntities.get(1);
//			// 停止消费，在前面的基础之上，此时应该不消费了
//			uiQueueOffsetService.updateStopFlag(stopQueueOffset.getId(), 1);
//			catTransaction.setStatus(Transaction.SUCCESS);
//		} catch (Exception e) {
//			e.printStackTrace();
//			catTransaction.setStatus(e);
//			throw e;
//		} finally {
//			catTransaction.complete();
//		}
//	}
//
//	private void sendMessage(MqClient.MqClientBase mqClientBase, List<String> topicNames) {
//		// 初始化线程
//		executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10),
//				SoaThreadFactory.create("mq-all-process-test", true), new ThreadPoolExecutor.DiscardOldestPolicy());
//		executorStart = true;
//		// 异步一直发消息
//		executor.execute(new Runnable() {
//			@Override
//			public void run() {
//				while (executorStart) {
//					try {
//						for (String topicName : topicNames) {
//							send(mqClientBase, topicName);
//						}
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//	}
//
//	private void createTopicAndConsumerGroupAndSubscribe(List<String> topicNames, String consumerGroupName)
//			throws InterruptedException {
//		Transaction catTransaction = null;
//		try {
//			catTransaction = Tracer.newTransaction("ServiceTest", "createTopicAndConsumerGroupAndSubscribe");
//			// 创建consumerGroup测试
//			buildConsumerGroup(consumerGroupName);
//			// 获取数据库中consumerGroup的值并比较
//			List<ConsumerGroupEntity> consumerGroupEntities = getConsumerGroupEntities(consumerGroupName);
//			assertEquals(1, consumerGroupEntities.size());
//			// 创建topic测试
//			for (String topicName : topicNames) {
//				buildTopic(topicName);
//				// 获取数据库中相关topic的值并比较
//				List<TopicEntity> topicEntityList = getTopicEntities(topicName);
//				assertEquals(1, topicEntityList.size());
//				assertEquals(3, queueService.getQueuesByTopicId(topicEntityList.get(0).getId()).size());
//
//				// 刷新缓存中的值
//				((TopicServiceImpl) topicService).updateCache();
//				((ConsumerGroupServiceImpl) consumerGroupService).updateCache();
//				Thread.sleep(1000);
//
//				// 订阅consumerGroup
//				consumerGroupTopicService.subscribe(createConsumerGroupTopicRequest(topicName,
//						topicEntityList.get(0).getId(), consumerGroupName, consumerGroupEntities.get(0).getId()));
//				assertEquals(1, uiTopicService.getFailTopic(topicName).size());
//				TopicEntity failTopic = uiTopicService.getFailTopic(topicName).get(0);
//				assertEquals(2, queueService.getQueuesByTopicId(failTopic.getId()).size());
//				assertEquals(getFailTopicName(consumerGroupName, topicName), failTopic.getName());
//				assertEquals(topicName, failTopic.getOriginName());
//				List<QueueEntity> failQueueList = queueService.getQueuesByTopicId(failTopic.getId());
//				assertEquals(2, failQueueList.size());
//
//				// 刷个缓存
//				((ConsumerGroupTopicServiceImpl) consumerGroupTopicService).updateCache();
//				((ConsumerGroupServiceImpl) consumerGroupService).updateCache();
//				catTransaction.setStatus(Transaction.SUCCESS);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			catTransaction.setStatus(e);
//			throw e;
//		} finally {
//			catTransaction.complete();
//		}
//	}
//
//	private String getFailTopicName(String groupName, String topicName) {
//		return groupName + "_" + topicName + "_fail";
//	}
//
//	private Map<Long, ConsumerQueueDto> getLongConsumerQueueDtoMap(MqClient.MqClientBase mqClientBase,
//			String groupName) {
//		boolean startFlag = true;
//		Map<String, ConsumerGroupOneDto> consumerGroupMap = null;
//		ConsumerGroupOneDto groupName1 = null;
//		Map<Long, ConsumerQueueDto> queues = null;
//
//		while (startFlag) {
//			consumerGroupMap = mqClientBase.getContext().getConsumerGroupMap();
//			if (consumerGroupMap != null) {
//				groupName1 = consumerGroupMap.get(groupName);
//				if (groupName1 != null) {
//					queues = groupName1.getQueues();
//					if (queues != null && queues.size() > 0) {
//						startFlag = false;
//					}
//				}
//
//			}
//		}
//		return queues;
//	}
//
//	private void send(MqClient.MqClientBase mqClientBase, String topic) {
//		Transaction catTransaction = null;
//		try {
//			catTransaction = Tracer.newTransaction("ServiceTest", "sendMessage");
//
//			if (sendFlag) {
//				// 发送单条消息
//				mqClientBase.publish(topic, "", new MessageDto(Util.formateDate(new Date())));
//			}
//			// 发送多条消息
//			List<ProducerDataDto> messageDtoList = new ArrayList<>();
//			for (int i = 0; i < 4; i++) {
//				messageDtoList.add(new ProducerDataDto(Util.formateDate(new Date())));
//			}
//			if (sendFlag) {
//				mqClientBase.publish(topic, "", messageDtoList);
//			}
//			catTransaction.setStatus(Transaction.SUCCESS);
//		} catch (MqNotInitException e) {
//			e.printStackTrace();
//			catTransaction.setStatus(e);
//		}
//		// catch (Exceed20Exception e) {
//		// e.printStackTrace();
//		// catTransaction.setStatus(e);
//		// }
//		catch (Exception e) {
//			catTransaction.setStatus(e);
//		} finally {
//			catTransaction.complete();
//		}
//	}
//
//	public void deleteTopicAndConsumer() {
//		ConsumerGroupEntity entity = consumerGroupService.getCache().get(TEST_CONSUMER_GROUP);
//		if (entity != null && consumerGroupTopicService.getCache().get(entity.getId()) != null) {
//			ConsumerGroupTopicEntity groupTopic = consumerGroupTopicService.getCache().get(entity.getId())
//					.get(TEST_TOPIC);
//			if (groupTopic != null) {
//				consumerGroupTopicService.deleteConsumerGroupTopic(groupTopic.getId());
//			}
//
//			groupTopic = consumerGroupTopicService.getCache().get(entity.getId()).get(TEST_TOPIC1);
//			if (groupTopic != null) {
//				consumerGroupTopicService.deleteConsumerGroupTopic(groupTopic.getId());
//			}
//		}
//
//		TopicEntity topicEntity = topicService.getCache().get(TEST_TOPIC);
//		if (topicEntity != null) {
//			uiTopicService.deleteTopic(topicEntity.getId());
//		}
//		topicEntity = topicService.getCache().get(TEST_TOPIC1);
//		if (topicEntity != null) {
//			uiTopicService.deleteTopic(topicEntity.getId());
//		}
//
//		if (entity != null) {
//			consumerGroupService.deleteConsumerGroup(entity.getId(), false);
//		}
//
//		CacheUpdateHelper.updateCache();
//	}
//
//	private boolean equalsRegardingNull(Object expected, Object actual) {
//		if (expected == null) {
//			return actual == null;
//		}
//
//		return isEquals(expected, actual);
//	}
//
//	private boolean isEquals(Object expected, Object actual) {
//		return expected.equals(actual);
//	}
//
//	public void assertEquals(Object expected, Object actual) {
//		if (equalsRegardingNull(expected, actual)) {
//			return;
//		} else {
//			failNotEquals(expected, actual);
//		}
//	}
//
//	private void assertEquals(long expected, long actual) {
//		if (expected != actual) {
//			failNotEquals(expected, actual);
//		}
//	}
//
//	private void assertEquals(int expected, int actual) {
//		if (expected != actual) {
//			failNotEquals(expected, actual);
//		}
//	}
//
//	private void failNotEquals(Object expected, Object actual) {
//		String errorMessage = "expected is <" + expected.toString() + ">, but actual is <" + actual.toString() + ">";
//		throw new AssertionError(errorMessage);
//	}
//
//	private MqClient.MqClientBase initMqClient() {
//		String netCard = env.getProperty("mq.network.netCard", "");
//		String url = env.getProperty("mq.broker.url", "");
//		String host = env.getProperty("mq.client.host", "");
//		if (Util.isEmpty(host)) {
//			host = IPUtil.getLocalIP(netCard);
//		}
//		MqConfig config = new MqConfig();
//		config.setIp(host);
//		config.setMetaMode(true);
//		config.setUrl(url);
//		config.setServerPort("");
//		MqClient.MqClientBase mqClientBase = new MqClient.MqClientBase();
//		mqClientBase.start(config);
//		return mqClientBase;
//	}
//
//	private void registerAndConsume(MqClient.MqClientBase mqClientBase, String consumerGroupName, List<String> topics) {
//		Transaction catTransaction = null;
//		try {
//			catTransaction = Tracer.newTransaction("ServiceTest", "registerAndConsume");
//			ConsumerGroupVo consumerGroup = new ConsumerGroupVo(consumerGroupName);
//			for (String topic : topics) {
//				ConsumerGroupTopicVo topicVo = new ConsumerGroupTopicVo();
//				topicVo.setName(topic);
//				topicVo.setSubscriber(new ISubscriber() {
//					@Override
//					public List<Long> onMessageReceived(List<MessageDto> message) {
//						System.out.println("处理消息数量：" + message.size());
//						return new ArrayList<>();
//					}
//				});
//				consumerGroup.addTopic(topicVo);
//			}
//			mqClientBase.registerConsumerGroup(consumerGroup);
//			catTransaction.setStatus(Transaction.SUCCESS);
//		} catch (Exception e) {
//			e.printStackTrace();
//			catTransaction.setStatus(e);
//			throw e;
//		} finally {
//			catTransaction.complete();
//		}
//	}
//
//	public void reset(MqClient.MqClientBase mqClientBase) {
//		if (executor != null) {
//			executorStart = false;
//			try {
//				Thread.sleep(1500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			executor.shutdownNow();
//		}
//		if (mqClientBase != null) {
//			mqClientBase.close();
//		}
//		// MqClient.stop();
//	}
//
//	public ConsumerGroupTopicCreateRequest createConsumerGroupTopicRequest(String topicName, Long topicId,
//                                                                           String consumerGroupName, Long groupId) {
//		ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest = new ConsumerGroupTopicCreateRequest();
//		consumerGroupTopicCreateRequest.setTopicName(topicName);
//		consumerGroupTopicCreateRequest.setTopicId(topicId);
//		consumerGroupTopicCreateRequest.setTopicType(1);
//		consumerGroupTopicCreateRequest.setOriginTopicName(topicName);
//		consumerGroupTopicCreateRequest.setAlarmEmails("****@****.com");
//		consumerGroupTopicCreateRequest.setConsumerGroupId(groupId);
//		consumerGroupTopicCreateRequest.setConsumerGroupName(consumerGroupName);
//		consumerGroupTopicCreateRequest.setThreadSize(10);
//		consumerGroupTopicCreateRequest.setRetryCount(2);
//		consumerGroupTopicCreateRequest.setMaxLag(10000);
//		consumerGroupTopicCreateRequest.setDelayProcessTime(2);
//		consumerGroupTopicCreateRequest.setPullBatchSize(50);
//		consumerGroupTopicCreateRequest.setConsumerBatchSize(50);
//		consumerGroupTopicCreateRequest.setDelayPullTime(5);
//		consumerGroupTopicCreateRequest.setTimeOut(0);
//		return consumerGroupTopicCreateRequest;
//	}
//
//	private List<ConsumerGroupTopicEntity> getConsumerGroupTopicEntities(String groupName, String topicName) {
//		Map<String, Object> conditionMap = new HashMap<>();
//		conditionMap.put(ConsumerGroupTopicEntity.FdConsumerGroupName, groupName);
//		conditionMap.put(ConsumerGroupTopicEntity.FdOriginTopicName, topicName);
//		return consumerGroupTopicService.getList(conditionMap);
//	}
//
//	private List<ConsumerGroupEntity> getConsumerGroupEntities(String name) {
//		Map<String, Object> conditionMap2 = new HashMap<>();
//		conditionMap2.put(TopicEntity.FdName, name);
//		return consumerGroupService.getList(conditionMap2);
//	}
//
//	private List<TopicEntity> getTopicEntities(String name) {
//		Map<String, Object> conditionMap = new HashMap<>();
//		conditionMap.put(TopicEntity.FdName, name);
//		return topicService.getList(conditionMap);
//	}
//
//	private List<QueueOffsetEntity> getQueueOffset(String groupName, String topicName) {
//		Map<String, Object> conditionMap = new HashMap<>();
//		if (!StringUtils.isEmpty(groupName)) {
//			conditionMap.put(QueueOffsetEntity.FdConsumerGroupName, groupName);
//		}
//		if (!StringUtils.isEmpty(topicName)) {
//			conditionMap.put(QueueOffsetEntity.FdTopicName, topicName);
//		}
//		return queueOffsetService.getList(conditionMap);
//	}
//
//	private void deleteTopic(Long id) {
//		uiTopicService.deleteTopic(id);
//	}
//
//	private void deleteConsumerGroup(Long id) {
//		consumerGroupService.deleteConsumerGroup(id, false);
//	}
//
//	public void buildTopic(String name) {
//		TopicCreateRequest topicCreateRequest = new TopicCreateRequest();
//		topicCreateRequest.setBusinessType("基础框架");
//		topicCreateRequest.setName(name);
//		topicCreateRequest.setDptName("基础框架");
//		topicCreateRequest.setEmails("test@gmail.com");
//		topicCreateRequest.setExpectDayCount(300);
//		topicCreateRequest.setMaxLag(10);
//		topicCreateRequest.setNormalFlag(0);
//		topicCreateRequest.setOwnerIds(TEST_USER_ID);
//		topicCreateRequest.setOwnerNames(TEST_USER_ID);
//		topicCreateRequest.setRemark("");
//		topicCreateRequest.setSaveDayNum(7);
//		topicCreateRequest.setTels("12345678901");
//		topicCreateRequest.setTopicType(1);
//		topicCreateRequest.setInsertBy(TEST_USER_ID);
//		topicCreateRequest.setConsumerFlag(1);
//		uiTopicService.createOrUpdateTopic(topicCreateRequest);
//	}
//
//	public void buildConsumerGroup(String name) {
//		ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
//		consumerGroupCreateRequest.setName(name);
//		consumerGroupCreateRequest.setOwnerNames(TEST_USER_ID);
//		consumerGroupCreateRequest.setOwnerIds(TEST_USER_ID);
//		consumerGroupCreateRequest.setAlarmFlag(1);
//		consumerGroupCreateRequest.setTraceFlag(0);
//		consumerGroupCreateRequest.setAlarmEmails("****@****.com");
//		consumerGroupCreateRequest.setTels("15793786768");
//		consumerGroupCreateRequest.setDptName("基础框架");
//		consumerGroupCreateRequest.setRemark("测试专用Consumer");
//		consumerGroupCreateRequest.setMode(1);
//		consumerGroupService.createConsumerGroup(consumerGroupCreateRequest);
//	}
//}
