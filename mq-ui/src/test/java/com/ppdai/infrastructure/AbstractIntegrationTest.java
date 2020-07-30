//package com.ppdai.infrastructure;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.env.Environment;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
//import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
//import com.ppdai.infrastructure.mq.biz.common.util.TopicUtil;
//import com.ppdai.infrastructure.mq.biz.common.util.Util;
//import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
//import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupCreateRequest;
//import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
//import com.ppdai.infrastructure.mq.biz.dto.request.TopicCreateRequest;
//import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
//import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
//import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
//import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
//import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
//import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
//import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
//import com.ppdai.infrastructure.mq.biz.service.QueueService;
//import com.ppdai.infrastructure.mq.biz.service.TopicService;
//import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
//import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
//import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupServiceImpl;
//import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupTopicServiceImpl;
//import com.ppdai.infrastructure.mq.biz.service.impl.QueueServiceImpl;
//import com.ppdai.infrastructure.mq.biz.service.impl.TopicServiceImpl;
//import com.ppdai.infrastructure.mq.client.MqClient;
//import com.ppdai.infrastructure.mq.client.MqConfig;
//import com.ppdai.infrastructure.mq.client.config.ConsumerGroupTopicVo;
//import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
//import com.ppdai.infrastructure.ui.MqUiApplication;
//import com.ppdai.infrastructure.ui.service.UiTopicService;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = { MqUiApplication.class })
//// @ActiveProfiles({"runtime","ppd"})
//abstract public class AbstractIntegrationTest {
//
//	public final String TEST_USER_ID = "管理员";
//	public final String TEST_CONSUMER_GROUP = "FlowTestSub";
//	public final String TEST_TOPIC = "FlowTopic1";
//	public final String TEST_TOPIC1 = "FlowTopic2";
//	public final int TOPIC_DAY_COUNT = 1;
//	public Counter counter = new Counter();
//
//	public static volatile long sleepTime = 0;
//
//	@Autowired
//	UserInfoHolder userInfoHolder;
//	@Autowired
//	SoaConfig soaConfig;
//
//	@Autowired
//	private UiTopicService uiTopicService;
//	@Autowired
//	private TopicService topicService;
//
//	@Autowired
//	private ConsumerGroupService consumerGroupService;
//	@Autowired
//	private ConsumerGroupTopicService consumerGroupTopicService;
//	@Autowired
//	private Environment env;
//	@Autowired
//	private QueueService queueService;
//
//	private AtomicBoolean errorFlag = new AtomicBoolean(false);
//
//	@Before
//	public void setUp() throws Exception {
//		//
//	}
//
//	public void setUserId(String userId) {
//		userInfoHolder.setUserId(userId);
//	}
//
//	@After
//	public void clear() {
//		userInfoHolder.clear();
//	}
//
//	public class Counter {
//		private int count = 0;
//
//		public synchronized void inc(int size) {
//			count += size;
//		}
//
//		public int get() {
//			return count;
//		}
//
//		public synchronized void set(int initv) {
//			count = initv;
//		}
//	}
//
//	public void setErrorFlag(boolean flag) {
//		errorFlag.set(flag);
//	}
//
//	public boolean getErrorFlag() {
//		return errorFlag.get();
//	}
//
//	public MqClient.MqClientBase initMqClient() {
//		String netCard = env.getProperty("mq.network.netCard", "");
//		String url = env.getProperty("mq.broker.url", "");
//		String host = env.getProperty("mq.client.host", "");
//		if (Util.isEmpty(host)) {
//			host = IPUtil.getLocalIP(netCard);
//		}
//		MqConfig config = new MqConfig();
//		config.setIp(host);
//		config.setMetaMode(false);
//		config.setUrl(url);
//		config.setServerPort("");
//		config.setRbTimes(1);
//		MqClient.MqClientBase mqClientBase = new MqClient.MqClientBase();
//		mqClientBase.start(config);
//		return mqClientBase;
//	}
//
//	public void registerAndConsumer(MqClient.MqClientBase mqClientBase, String consumerGroupName, List<String> topics) {
//		try {
//			ConsumerGroupVo consumerGroup = new ConsumerGroupVo(consumerGroupName);
//			for (String topic : topics) {
//				ConsumerGroupTopicVo topicVo = new ConsumerGroupTopicVo();
//				topicVo.setName(topic);
//				topicVo.setSubscriber(new ISubscriber() {
//					@Override
//					public List<Long> onMessageReceived(List<MessageDto> message) {
//						counter.inc(message.size());
//						System.out.println("处理消息数量：" + message.size());
//						if (errorFlag.get()) {
//							throw new RuntimeException("异常测试");
//						}
//						if (sleepTime > 0) {
//							Util.sleep(sleepTime);
//						}
//						return new ArrayList<>();
//					}
//				});
//				consumerGroup.addTopic(topicVo);
//			}
//			mqClientBase.registerConsumerGroup(consumerGroup);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//	
//	
//	public void buildConsumerGroup(String name, int mode,int consumerQuality) {
//		ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
//		consumerGroupCreateRequest.setName(name);
//		consumerGroupCreateRequest.setOwnerNames(TEST_USER_ID);
//		consumerGroupCreateRequest.setOwnerIds(TEST_USER_ID);
//		consumerGroupCreateRequest.setAlarmFlag(1);
//		consumerGroupCreateRequest.setTraceFlag(0);
//		consumerGroupCreateRequest.setAlarmEmails("*****@****.com");
//		consumerGroupCreateRequest.setTels("15793786768");
//		consumerGroupCreateRequest.setDptName("基础框架");
//		consumerGroupCreateRequest.setRemark("测试专用Consumer");
//		consumerGroupCreateRequest.setMode(mode);
//		consumerGroupCreateRequest.setConsumerQuality(consumerQuality);
//		consumerGroupService.createConsumerGroup(consumerGroupCreateRequest);
//
//		// 获取数据库中consumerGroup的值并比较
//		List<ConsumerGroupEntity> consumerGroupEntities = getConsumerGroupEntities(name);
//		assertEquals(1, consumerGroupEntities.size());
//		consumerGroupService.updateCache();
//	}
//
//	public List<ConsumerGroupEntity> getConsumerGroupEntities(String name) {
//		Map<String, Object> conditionMap2 = new HashMap<>();
//		conditionMap2.put(TopicEntity.FdOriginName, name);
//		return consumerGroupService.getList(conditionMap2);
//	}
//
//	private void buildTopic(String name) {
//		TopicCreateRequest topicCreateRequest = new TopicCreateRequest();
//		topicCreateRequest.setBusinessType("基础框架");
//		topicCreateRequest.setName(name);
//		topicCreateRequest.setDptName("基础框架");
//		topicCreateRequest.setEmails("test@gmail.com");
//		topicCreateRequest.setExpectDayCount(100 * TOPIC_DAY_COUNT);
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
//	private ConsumerGroupTopicCreateRequest createConsumerGroupTopicRequest(String topicName, Long topicId,
//			String consumerGroupName, Long groupId) {
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
//	public void buildTopic(List<String> topicNames) {
//		// 创建topic测试
//		for (String topicName : topicNames) {
//			buildTopic(topicName);
//			// 获取数据库中相关topic的值并比较
//			List<TopicEntity> topicEntityList = getTopicEntities(topicName);
//			assertEquals(1, topicEntityList.size());
//			assertEquals(TOPIC_DAY_COUNT, queueService.getQueuesByTopicId(topicEntityList.get(0).getId()).size());
//
//			// 刷新缓存中的值
//			((TopicServiceImpl) topicService).updateCache();
//			((QueueServiceImpl) queueService).updateCache();
//			((ConsumerGroupServiceImpl) consumerGroupService).updateCache();
//			Util.sleep(1000);
//		}
//	}
//
//	public void subscribe(List<String> topicNames, String consumerGroupName) {
//		ConsumerGroupEntity consumerGroupEntity = consumerGroupService.getCache().get(consumerGroupName);
//		assertEquals(consumerGroupEntity != null, true);
//		topicNames.forEach(topicName -> {
//			TopicEntity topicEntity = topicService.getCache().get(topicName);
//			assertEquals(topicEntity != null, true);
//			// 订阅consumerGroup
//			consumerGroupTopicService.subscribe(createConsumerGroupTopicRequest(topicName, topicEntity.getId(),
//					consumerGroupName, consumerGroupEntity.getId()));
//			assertEquals(1, uiTopicService.getFailTopic(topicName).size());
//			TopicEntity failTopic = uiTopicService.getFailTopic(topicName).get(0);
//			assertEquals(2, queueService.getQueuesByTopicId(failTopic.getId()).size());
//			assertEquals(TopicUtil.getFailTopicName(consumerGroupName, topicName), failTopic.getName());
//			assertEquals(topicName, failTopic.getOriginName());
//			List<QueueEntity> failQueueList = queueService.getQueuesByTopicId(failTopic.getId());
//			assertEquals(2, failQueueList.size());
//			// 刷个缓存
//			((ConsumerGroupTopicServiceImpl) consumerGroupTopicService).updateCache();
//			((ConsumerGroupServiceImpl) consumerGroupService).updateCache();
//		});
//	}
//
//	public List<TopicEntity> getTopicEntities(String name) {
//		Map<String, Object> conditionMap = new HashMap<>();
//		conditionMap.put(TopicEntity.FdName, name);
//		return topicService.getList(conditionMap);
//	}
//
//	public void deleteTopicAndConsumer() {
//		CacheUpdateHelper.forceUpdateCache();
//		List<ConsumerGroupEntity> entitys = getConsumerGroupEntities(TEST_CONSUMER_GROUP);
//		ConsumerGroupEntity entity = null;
//		for (ConsumerGroupEntity t1 : entitys) {
//			if (t1.getName().equals(t1.getOriginName())) {
//				entity = t1;
//			} 
//		}
//
//		if (entity != null && consumerGroupTopicService.getCache().get(entity.getId()) != null) {
//			Map<String, ConsumerGroupTopicEntity> groupTopicMap = consumerGroupTopicService.getCache()
//					.get(entity.getId());
//			if (groupTopicMap != null) {
//				ConsumerGroupTopicEntity groupTopic = groupTopicMap.get(TEST_TOPIC);
//				if (groupTopic != null) {
//					try {
//				    	consumerGroupTopicService.deleteConsumerGroupTopic(groupTopic.getId());
//					}catch (Exception e) {
//						// TODO: handle exception
//					}
//				}
//				Util.sleep(5000);
//				groupTopic = groupTopicMap.get(TEST_TOPIC1);
//				if (groupTopic != null) {
//					try {
//						consumerGroupTopicService.deleteConsumerGroupTopic(groupTopic.getId());
//					}catch (Exception e) {
//						// TODO: handle exception
//					}
//				}
//			}
//		}
//		CacheUpdateHelper.forceUpdateCache();		
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
//			try {
//				consumerGroupService.deleteConsumerGroup(entity.getId(), false);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//
//		CacheUpdateHelper.forceUpdateCache();	
//	}
//}
