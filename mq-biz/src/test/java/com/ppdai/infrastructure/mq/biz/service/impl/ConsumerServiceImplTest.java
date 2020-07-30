package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerGroupUtil;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerRepository;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.event.PartitionInfo;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupConsumerService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.EmailService;
import com.ppdai.infrastructure.mq.biz.service.LogService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;

@RunWith(JUnit4.class)
public class ConsumerServiceImplTest extends AbstractTest {

	private ConsumerRepository consumerRepository;

	private ConsumerGroupConsumerService consumerGroupConsumerService;

	private ConsumerGroupTopicService consumerGroupTopicService;

	private ConsumerGroupService consumerGroupService;

	private QueueOffsetService queueOffsetService;

	private TopicService topicService;

	private QueueService queueService;

	private DbNodeService dbNodeService;

	private Message01Service message01Service;

	private EmailService emailService;

	private EmailUtil emailUtil;

	private LogService logService;

	private UserInfoHolder userInfoHolder;

	private AuditLogService auditLogService;

	private ConsumerServiceImpl consumerServiceImpl;

	@Before
	public void init() {
		consumerServiceImpl = new ConsumerServiceImpl();

		object = consumerServiceImpl;

		consumerRepository = mockAndSet(ConsumerRepository.class);

		consumerGroupConsumerService = mockAndSet(ConsumerGroupConsumerService.class);

		consumerGroupTopicService = mockAndSet(ConsumerGroupTopicService.class);

		consumerGroupService = mockAndSet(ConsumerGroupService.class);

		userInfoHolder = mockAndSet(UserInfoHolder.class);

		topicService = mockAndSet(TopicService.class);

		queueOffsetService = mockAndSet(QueueOffsetService.class);

		queueService = mockAndSet(QueueService.class);

		dbNodeService = mockAndSet(DbNodeService.class);

		message01Service = mockAndSet(Message01Service.class);

		emailService = mockAndSet(EmailService.class);

		emailUtil = mockAndSet(EmailUtil.class);

		logService = mockAndSet(LogService.class);

		auditLogService = mockAndSet(AuditLogService.class);

		super.init();
		consumerServiceImpl.init();
		ReflectionTestUtils.setField(object, "soaConfig", soaConfig);
	}

	@Test
	public void checkVaildTest() {
		ConsumerRegisterRequest request = null;
		assertEquals(false, consumerServiceImpl.register(request).isSuc());

		request = new ConsumerRegisterRequest();
		assertEquals(false, consumerServiceImpl.register(request).isSuc());

		request.setClientIp("");
		assertEquals(false, consumerServiceImpl.register(request).isSuc());
		request.setClientIp("234234");
		request.setSdkVersion("");
		assertEquals(false, consumerServiceImpl.register(request).isSuc());
	}

	@Test
	public void registerTest() {
		ConsumerRegisterRequest request = new ConsumerRegisterRequest();
		request.setName("testt");
		request.setSdkVersion("1");
		consumerServiceImpl.register(request);
		verify(consumerRepository).register(any(ConsumerEntity.class));
		doThrow(new RuntimeException("fa")).when(consumerRepository).register(any(ConsumerEntity.class));
		when(consumerRepository.get(anyMapOf(String.class, Object.class))).thenReturn(new ConsumerEntity());
		consumerServiceImpl.register(request);
		verify(consumerRepository).get(anyMapOf(String.class, Object.class));
	}

	@Test
	public void deRegisterTest() {
		ConsumerDeRegisterRequest deRegisterRequest = new ConsumerDeRegisterRequest();
		assertEquals(false, consumerServiceImpl.deRegister(deRegisterRequest).isSuc());

		deRegisterRequest.setId(1);

		List<ConsumerGroupConsumerEntity> consumerGroupConsumers = new ArrayList<ConsumerGroupConsumerEntity>();
		ConsumerGroupConsumerEntity consumerGroupConsumerEntity = new ConsumerGroupConsumerEntity();
		consumerGroupConsumerEntity.setConsumerGroupId(1L);
		consumerGroupConsumers.add(consumerGroupConsumerEntity);

		when(consumerGroupConsumerService.getByConsumerIds(anyListOf(Long.class))).thenReturn(consumerGroupConsumers);

		Map<Long, ConsumerGroupEntity> consumerGroupMap = new HashMap<Long, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setId(2L);
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setOriginName("test1");

		consumerGroupMap.put(1L, consumerGroupEntity);
		when(consumerGroupService.getIdCache()).thenReturn(consumerGroupMap);

		Map<String, ConsumerGroupEntity> consumerGroupMap1 = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupMap1.put(consumerGroupEntity.getOriginName(), consumerGroupEntity);
		when(consumerGroupService.getCache()).thenReturn(consumerGroupMap1);

		consumerServiceImpl.deRegister(deRegisterRequest);

		verify(consumerGroupService).deleteConsumerGroup(anyLong(), anyBoolean());

		verify(consumerGroupConsumerService).deleteByConsumerIds(anyListOf(Long.class));
	}

	@Test
	public void heartbeatTest() {
		consumerServiceImpl.heartbeat(Arrays.asList(1L));
		verify(consumerRepository).heartbeat(anyListOf(Long.class));
	}

	@Test
	public void getConsumerGroupByConsumerIdsTest() {
		assertEquals(0, consumerServiceImpl.getConsumerGroupByConsumerIds(null).size());

		consumerServiceImpl.getConsumerGroupByConsumerIds(Arrays.asList(1L));

		verify(consumerGroupConsumerService).getByConsumerIds(anyListOf(Long.class));

	}

	@Test
	public void getConsumerGroupByConsumerGroupIdsTest() {
		assertEquals(0, consumerServiceImpl.getConsumerGroupByConsumerGroupIds(null).size());

		consumerServiceImpl.getConsumerGroupByConsumerGroupIds(Arrays.asList(1L));

		verify(consumerGroupConsumerService).getByConsumerGroupIds(anyListOf(Long.class));
	}

	@Test
	public void registerConsumerGroupTest() {
		ConsumerGroupRegisterRequest request = new ConsumerGroupRegisterRequest();
		assertEquals(false, consumerServiceImpl.registerConsumerGroup(request).isSuc());
		assertEquals(false, consumerServiceImpl.registerConsumerGroup(null).isSuc());

		ConsumerEntity consumerEntity = new ConsumerEntity();
		consumerEntity.setName("test");
		when(consumerRepository.getById(anyLong())).thenReturn(consumerEntity);

		request.setConsumerName("test");
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("test", Arrays.asList("test"));
		request.setConsumerGroupNames(map);
		assertEquals(false, consumerServiceImpl.registerConsumerGroup(request).isSuc());

		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setTopicNames("test");
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setOriginName("test");
		consumerGroupEntity.setMetaVersion(0L);
		consumerGroupEntity.setRbVersion(0);
		consumerGroupEntity.setTraceFlag(0);
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);
		when(consumerGroupService.getCache()).thenReturn(consumerGroupMap);

		Map<Long, Map<String, ConsumerGroupTopicEntity>> ctMap = new HashMap<Long, Map<String, ConsumerGroupTopicEntity>>();
		ConsumerGroupTopicEntity consumerGroupTopicEntity = new ConsumerGroupTopicEntity();
		consumerGroupTopicEntity.setConsumerGroupName("test");
		consumerGroupTopicEntity.setTopicName("test");
		consumerGroupTopicEntity.setTopicType(1);
		consumerGroupTopicEntity.setConsumerBatchSize(1);
		consumerGroupTopicEntity.setConsumerGroupId(1);
		consumerGroupTopicEntity.setDelayProcessTime(1);
		consumerGroupTopicEntity.setMaxLag(1);
		consumerGroupTopicEntity.setMaxPullTime(1);
		consumerGroupTopicEntity.setPullBatchSize(1);
		consumerGroupTopicEntity.setPullBatchSize(1);
		consumerGroupTopicEntity.setOriginTopicName("test");
		consumerGroupTopicEntity.setRetryCount(1);
		consumerGroupTopicEntity.setThreadSize(1);
		consumerGroupTopicEntity.setTimeOut(1);
		consumerGroupTopicEntity.setTopicId(1);
		Map<String, ConsumerGroupTopicEntity> map2 = new HashMap<String, ConsumerGroupTopicEntity>();
		map2.put(consumerGroupTopicEntity.getTopicName(), consumerGroupTopicEntity);
		ctMap.put(consumerGroupEntity.getId(), map2);
		when(consumerGroupTopicService.getCache()).thenReturn(ctMap);

		ArgumentCaptor<ConsumerGroupTopicCreateRequest> argumentCaptor = ArgumentCaptor
				.forClass(ConsumerGroupTopicCreateRequest.class);
		Map<String, ConsumerGroupEntity> consumerGroupMap1 = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupMap1.put(
				ConsumerGroupUtil.getBroadcastConsumerName(consumerGroupEntity.getName(), request.getClientIp(), 0),
				consumerGroupEntity);

		when(consumerGroupService.getByNames(anyListOf(String.class))).thenReturn(consumerGroupMap1);
		consumerServiceImpl.registerConsumerGroup(request);
		verify(consumerGroupTopicService).subscribe(argumentCaptor.capture());
		verify(consumerGroupService).insert(any(ConsumerGroupEntity.class));
		assertEquals(
				ConsumerGroupUtil.getBroadcastConsumerName(consumerGroupEntity.getName(), request.getClientIp(), 0),
				argumentCaptor.getValue().getConsumerGroupName());

		assertEquals(
				ConsumerGroupUtil.getBroadcastConsumerName(consumerGroupEntity.getName(), request.getClientIp(), 0),
				consumerEntity.getConsumerGroupNames());

	}

	@Test
	public void checkBroadcastTest() {
		ConsumerGroupRegisterRequest request = null;
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		consumerServiceImpl.checkBroadcastAndSubEnv(request, response);
		assertEquals(false, response.isSuc());
		request = new ConsumerGroupRegisterRequest();
		consumerServiceImpl.checkBroadcastAndSubEnv(request, response);
		assertEquals(false, response.isSuc());
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("test", new ArrayList<String>());
		request.setConsumerGroupNames(map);
		consumerServiceImpl.checkBroadcastAndSubEnv(request, response);
		assertEquals(false, response.isSuc());
	}

	@Test
	public void publishTest() {
		PublishMessageRequest request = new PublishMessageRequest();
		request.setTopicName("test");
		request.setMsgs(Arrays.asList(new ProducerDataDto()));
		Map<String, TopicEntity> cacheData = new HashMap<String, TopicEntity>();
		cacheData.put(request.getTopicName(), new TopicEntity());
		when(topicService.getCache()).thenReturn(cacheData);
		Map<String, List<QueueEntity>> queueMap = new HashMap<String, List<QueueEntity>>();
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setDbNodeId(1);
		queueEntity.setIp("127.0.0.1");
		queueMap.put(request.getTopicName(), Arrays.asList(queueEntity));
		when(queueService.getAllLocatedTopicWriteQueue()).thenReturn(queueMap);
		setProperty("mq.publish.rate.enable", "1");
		consumerServiceImpl.publish(request);
		clear("mq.publish.rate.enable");
		verify(message01Service).insertBatchDy(anyString(), anyString(), anyListOf(Message01Entity.class));
		// System.out.println(response.getMsg());
	}

	@Test
	public void publish1Test() {
		// 测试无可写队列，但是是非严格模式
		PublishMessageRequest request = new PublishMessageRequest();
		request.setTopicName("test");
		request.setMsgs(Arrays.asList(new ProducerDataDto()));
		Map<String, TopicEntity> cacheData = new HashMap<String, TopicEntity>();
		cacheData.put(request.getTopicName(), new TopicEntity());
		when(topicService.getCache()).thenReturn(cacheData);
		Map<String, List<QueueEntity>> queueMap = new HashMap<String, List<QueueEntity>>();
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setDbNodeId(1);
		queueEntity.setIp("127.0.0.1");
		queueMap.put(request.getTopicName(), Arrays.asList(queueEntity));
		when(queueService.getAllLocatedTopicQueue()).thenReturn(queueMap);
		consumerServiceImpl.lastTime = System.currentTimeMillis() - 30 * 1000;
		consumerServiceImpl.publish(request);
		verify(message01Service).insertBatchDy(anyString(), anyString(), anyListOf(Message01Entity.class));

		request.setSynFlag(0);
		consumerServiceImpl.publish(request);
		verify(message01Service, times(2)).insertBatchDy(anyString(), anyString(), anyListOf(Message01Entity.class));
	}

	@Test
	public void publish2Test() {
		// 测试无可写队列，但是是严格模式
		PublishMessageRequest request = new PublishMessageRequest();
		request.setTopicName("test");
		request.setMsgs(Arrays.asList(new ProducerDataDto()));
		Map<String, TopicEntity> cacheData = new HashMap<String, TopicEntity>();
		cacheData.put(request.getTopicName(), new TopicEntity());
		when(topicService.getCache()).thenReturn(cacheData);
		Map<String, List<QueueEntity>> queueMap = new HashMap<String, List<QueueEntity>>();
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setDbNodeId(1);
		queueEntity.setIp("127.0.0.1");
		queueMap.put(request.getTopicName(), Arrays.asList(queueEntity));
		when(queueService.getAllLocatedTopicQueue()).thenReturn(queueMap);
		setProperty("mq.publish.mode", "0");
		PublishMessageResponse response = consumerServiceImpl.publish(request);
		clear("mq.publish.mode");
		assertEquals(false, response.isSuc());
	}

	@Test
	public void publish3Test() {
		// 测试无队列
		PublishMessageRequest request = new PublishMessageRequest();
		request.setTopicName("test");
		request.setMsgs(Arrays.asList(new ProducerDataDto()));
		Map<String, TopicEntity> cacheData = new HashMap<String, TopicEntity>();
		cacheData.put(request.getTopicName(), new TopicEntity());
		when(topicService.getCache()).thenReturn(cacheData);
//		Map<String, List<QueueEntity>> queueMap = new HashMap<String, List<QueueEntity>>();
//		QueueEntity queueEntity = new QueueEntity();
//		queueEntity.setDbNodeId(1);
//		queueEntity.setIp("127.0.0.1");
//		queueMap.put(request.getTopicName(), Arrays.asList(queueEntity));
//		when(queueService.getAllLocatedTopicQueue()).thenReturn(queueMap);
//		setProperty("mq.publish.mode", "0");
		PublishMessageResponse response = consumerServiceImpl.publish(request);
		clear("mq.publish.mode");
		assertEquals(false, response.isSuc());
	}

	@Test
	public void publicsh_checkVaildTest() {
		PublishMessageRequest request = null;
		PublishMessageResponse response = new PublishMessageResponse();
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(false, response.isSuc());
		request = new PublishMessageRequest();
		response.setSuc(true);
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(false, response.isSuc());

		request.setTopicName("fa");
		request.setMsgs(Arrays.asList(new ProducerDataDto()));
		response.setSuc(true);
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(false, response.isSuc());

		Map<String, TopicEntity> cacheData = new HashMap<String, TopicEntity>();
		cacheData.put(request.getTopicName(), new TopicEntity());
		when(topicService.getCache()).thenReturn(cacheData);

		response.setSuc(true);
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(true, response.isSuc());

		cacheData.get(request.getTopicName()).setToken("tet");
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(false, response.isSuc());

	}

	@Test
	public void publish_checkTopicRateTest() {
		PublishMessageRequest request = new PublishMessageRequest();
		PublishMessageResponse response = new PublishMessageResponse();
		setProperty("mq.publish.rate.enable", "0");
		assertEquals(true, consumerServiceImpl.checkTopicRate(request, response));
		setProperty("mq.publish.rate.enable", "1");
		request.setTopicName("test");
		setProperty(String.format("mq.topic.%s.flag", request.getTopicName()), "0");
		assertEquals(false, consumerServiceImpl.checkTopicRate(request, response));
		clear(String.format("mq.topic.%s.flag", request.getTopicName()));
		consumerServiceImpl.totalMax.set(1000);
		setProperty("mq.topic.host.max", "500");
		assertEquals(false, consumerServiceImpl.checkTopicRate(request, response));
		consumerServiceImpl.totalMax.set(100);
		setProperty("mq.topic.host.max", "0");
		consumerServiceImpl.topicPerMax.get(request.getTopicName()).set(1000);
		setProperty(String.format("mq.topic.%s.host.max", request.getTopicName()), "500");
		assertEquals(false, consumerServiceImpl.checkTopicRate(request, response));
		consumerServiceImpl.topicPerMax.get(request.getTopicName()).set(10);
		assertEquals(true, consumerServiceImpl.checkTopicRate(request, response));
	}

	@Test
	public void checkFailTimeTest() {
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setDbNodeId(1);
		queueEntity.setIp("127.0.0.1");
		assertEquals(true, consumerServiceImpl.checkFailTime("test", queueEntity, null));

		consumerServiceImpl.dbFailMap.put(queueEntity.getIp(), System.currentTimeMillis());
		assertEquals(false, consumerServiceImpl.checkFailTime("test", queueEntity, null));
		assertEquals(false, consumerServiceImpl.checkFailTime("test", queueEntity, new ArrayList<String>()));
		consumerServiceImpl.dbFailMap.put(queueEntity.getIp(),
				System.currentTimeMillis() - soaConfig.getDbFailWaitTime() * 2000L);
		assertEquals(true, consumerServiceImpl.checkFailTime("test", queueEntity, null));
	}

	@Test
	public void pullData_checkVaildTest() {
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setDbNodeId(1);
		queueEntity.setIp("127.0.0.1");
		queueEntity.setId(1L);
		Map<Long, QueueEntity> data = new HashMap<Long, QueueEntity>();
		data.put(queueEntity.getId(), queueEntity);
		when(queueService.getAllQueueMap()).thenReturn(data);
		PullDataRequest request = null;
		PullDataResponse response = consumerServiceImpl.pullData(request);
		assertEquals(false, response.isSuc());

		request = new PullDataRequest();
		request.setQueueId(-1);
		response = consumerServiceImpl.pullData(request);
		assertEquals(false, response.isSuc());
		request.setQueueId(2);

		request.setOffsetStart(-1);
		response = consumerServiceImpl.pullData(request);
		assertEquals(false, response.isSuc());

		request.setOffsetStart(1);
		response = consumerServiceImpl.pullData(request);
		assertEquals(false, response.isSuc());

		request.setOffsetEnd(2);
		response = consumerServiceImpl.pullData(request);
		assertEquals(false, response.isSuc());
	}

	@Test
	public void pullData_checkStatusTest() {
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setDbNodeId(1);
		queueEntity.setIp("127.0.0.1");
		queueEntity.setId(1L);
		Map<Long, QueueEntity> data = new HashMap<Long, QueueEntity>();
		data.put(queueEntity.getId(), queueEntity);

		Map<Long, DbNodeEntity> dbNodeMap = new HashMap<Long, DbNodeEntity>();
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setId(2);
		dbNodeMap.put(dbNodeEntity.getId(), dbNodeEntity);

		setProperty("mq.consumer.isDbStatusLog", "true");
		assertEquals(false, consumerServiceImpl.checkStatus(queueEntity, dbNodeMap));
		clear("mq.consumer.isDbStatusLog");

		dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setReadOnly(3);
		dbNodeEntity.setId(queueEntity.getDbNodeId());
		dbNodeMap.put(dbNodeEntity.getId(), dbNodeEntity);
		assertEquals(false, consumerServiceImpl.checkStatus(queueEntity, dbNodeMap));
		dbNodeEntity.setReadOnly(1);
		assertEquals(true, consumerServiceImpl.checkStatus(queueEntity, dbNodeMap));
	}

	@Test
	public void pullDataTest() {
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setDbNodeId(1);
		queueEntity.setIp("127.0.0.1");
		queueEntity.setId(1L);
		Map<Long, QueueEntity> data = new HashMap<Long, QueueEntity>();
		data.put(queueEntity.getId(), queueEntity);
		when(queueService.getAllQueueMap()).thenReturn(data);

		// Map<Long, DbNodeEntity> dbNodeMap = dbNodeService.getCache();
		Map<Long, DbNodeEntity> dbNodeMap = new HashMap<Long, DbNodeEntity>();
		DbNodeEntity dbNodeEntity = new DbNodeEntity();
		dbNodeEntity.setId(queueEntity.getDbNodeId());
		dbNodeMap.put(dbNodeEntity.getId(), dbNodeEntity);

		when(dbNodeService.getCache()).thenReturn(dbNodeMap);

		PullDataRequest request = new PullDataRequest();
		request.setQueueId(queueEntity.getId());
		request.setOffsetStart(1);
		request.setOffsetEnd(2);
		request.setTopicName("test");
		List<Message01Entity> msEntities = new ArrayList<Message01Entity>();
		Message01Entity message01Entity = new Message01Entity();
		message01Entity.setBody("fa");
		message01Entity.setId(1);
		msEntities.add(message01Entity);
		when(message01Service.getListDy(anyString(), anyString(), anyLong(), anyLong())).thenReturn(msEntities);
		consumerServiceImpl.pullData(request);
		verify(message01Service).getListDy(anyString(), anyString(), anyLong(), anyLong());
		reset(message01Service);
		doThrow(new RuntimeException("test")).when(message01Service).getListDy(anyString(), anyString(), anyLong(),
				anyLong());
		consumerServiceImpl.pullData(request);
		assertEquals(1, consumerServiceImpl.dbFailMap.size());

		when(dbNodeService.getCache()).thenReturn(new HashMap<Long, DbNodeEntity>());
		consumerServiceImpl.pullData(request);
		verify(message01Service).getListDy(anyString(), anyString(), anyLong(), anyLong());

	}

	@Test
	public void getMessageCountTest() {
		GetMessageCountResponse response = null;
		response = consumerServiceImpl.getMessageCount(null);
		assertEquals(false, response.isSuc());
		GetMessageCountRequest request = new GetMessageCountRequest();

		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setName("test");
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);
		when(consumerGroupService.getCache()).thenReturn(consumerGroupMap);

		response = consumerServiceImpl.getMessageCount(request);
		assertEquals(false, response.isSuc());

		request.setConsumerGroupName("faf");
		response = consumerServiceImpl.getMessageCount(request);
		assertEquals(false, response.isSuc());

		request.setConsumerGroupName(consumerGroupEntity.getName());

		Map<String, Map<String, List<QueueOffsetEntity>>> map = new HashMap<String, Map<String, List<QueueOffsetEntity>>>();
		Map<String, List<QueueOffsetEntity>> map2 = new HashMap<String, List<QueueOffsetEntity>>();

		QueueOffsetEntity queueOffsetEntity = new QueueOffsetEntity();
		queueOffsetEntity.setQueueId(1);
		map2.put("test", Arrays.asList(queueOffsetEntity));
		map.put(consumerGroupEntity.getName(), map2);
		when(queueOffsetService.getCache()).thenReturn(map);

		Map<Long, QueueEntity> queues = new HashMap<Long, QueueEntity>();
		queues.put(queueOffsetEntity.getQueueId(), new QueueEntity());
		when(queueService.getAllQueueMap()).thenReturn(queues);
		// queueService.getAllQueueMap();
		response = consumerServiceImpl.getMessageCount(request);
		assertEquals(true, response.isSuc());
		verify(queueService).getMaxId(anyLong(), anyString());

		request.setTopics(Arrays.asList("test"));
		response = consumerServiceImpl.getMessageCount(request);
		assertEquals(true, response.isSuc());
		verify(queueService, times(2)).getMaxId(anyLong(), anyString());

	}

	@Test
	public void findByHeartTimeIntervalTest() {
		consumerServiceImpl.findByHeartTimeInterval(5);
		verify(consumerRepository).findByHeartTimeInterval(anyLong());
	}

	@Test
	public void deleteByConsumersTest() {
		consumerServiceImpl.deleteByConsumers(null);
		verify(consumerGroupService, times(0)).deleteConsumerGroup(anyLong(), anyBoolean());

		List<ConsumerEntity> consumers = new ArrayList<ConsumerEntity>();
		ConsumerEntity consumerEntity = new ConsumerEntity();
		consumerEntity.setId(1);
		consumers.add(consumerEntity);

		consumerServiceImpl.deleteByConsumers(consumers);
	}

	@Test
	public void getConsumerByConsumerGroupIdTest() {
		consumerServiceImpl.getConsumerByConsumerGroupId(1L);
		verify(consumerRepository).getConsumerByConsumerGroupId(1L);
	}

	@Test
	public void countByTest() {
		consumerServiceImpl.countBy(new HashMap<String, Object>());
		verify(consumerRepository).countBy(anyMapOf(String.class, Object.class));
	}

	@Test
	public void getListByTest() {
		consumerServiceImpl.getListBy(new HashMap<String, Object>());
		verify(consumerRepository).getListBy(anyMapOf(String.class, Object.class));
	}

	@Test
	public void publishAndUpdateResultFailMsgTest() {
		FailMsgPublishAndUpdateResultRequest request = new FailMsgPublishAndUpdateResultRequest();
		request.setQueueId(1);
		List<Long> ids = Arrays.asList(1L, 2L);
		request.setIds(ids);
		PublishMessageRequest failMsg = new PublishMessageRequest();
		failMsg.setTopicName("test");
		List<ProducerDataDto> msgs = new ArrayList<ProducerDataDto>();
		ProducerDataDto producerDataDto = new ProducerDataDto();
		producerDataDto.setBizId("test");
		producerDataDto.setBody("test");
		msgs.add(producerDataDto);
		failMsg.setMsgs(msgs);
		request.setFailMsg(failMsg);

		Map<Long, QueueEntity> queueMap = new HashMap<Long, QueueEntity>();
		QueueEntity queueEntity = new QueueEntity();
		queueEntity.setId(request.getQueueId());
		queueEntity.setTopicName("test");
		queueEntity.setNodeType(2);
		queueMap.put(request.getQueueId(), queueEntity);

		when(queueService.getAllQueueMap()).thenReturn(queueMap);
		consumerServiceImpl.publishAndUpdateResultFailMsg(request);

		verify(message01Service).deleteOldFailMsg(anyString(), anyLong(), anyInt());
		verify(message01Service).updateFailMsgResult(anyString(), anyListOf(Long.class), anyInt());
	}

	@Test
	public void checkTopicTest() {
		ConsumerGroupRegisterRequest request = new ConsumerGroupRegisterRequest();
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);

		Map<String, List<String>> consumerGroupNames = new HashMap<String, List<String>>();
		consumerGroupNames.put("test", new ArrayList<String>());
		request.setConsumerGroupNames(consumerGroupNames);

		consumerServiceImpl.checkTopic(request, response);
		assertEquals(false, response.isSuc());

		consumerGroupNames.get("test").add("test");

		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<String, ConsumerGroupEntity>();
		// consumerGroupMap.pu
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setOriginName(consumerGroupEntity.getName());
		consumerGroupMap.put("test1", consumerGroupEntity);
		when(consumerGroupService.getByNames(anyListOf(String.class))).thenReturn(consumerGroupMap);
		response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		consumerServiceImpl.checkTopic(request, response);
		assertEquals(false, response.isSuc());

		consumerGroupMap.put("test", consumerGroupEntity);
		response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		consumerServiceImpl.checkTopic(request, response);
		assertEquals(true, response.isSuc());

		consumerGroupEntity.setOriginName(consumerGroupEntity.getName() + "_1");
		consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setName("test_1");
		consumerGroupEntity.setOriginName(consumerGroupEntity.getName());
		consumerGroupMap.put("test_1", consumerGroupEntity);
		response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		when(consumerGroupService.getCache()).thenReturn(consumerGroupMap);
		consumerServiceImpl.checkTopic(request, response);
		assertEquals(true, response.isSuc());
	}

	@Test
	public void updateQueueCacheTest() {
		doThrow(new RuntimeException()).when(queueService).updateCache();
		consumerServiceImpl.updateQueueCache("test");
	}

	@Test
	public void registConsumerGroupConsumerTest() {
		doThrow(new RuntimeException()).when(consumerGroupConsumerService)
				.insertBatch(anyListOf(ConsumerGroupConsumerEntity.class));
		List<ConsumerGroupConsumerEntity> consumerGroupConsumerEntities = new ArrayList<ConsumerGroupConsumerEntity>(2);
		consumerGroupConsumerEntities.add(new ConsumerGroupConsumerEntity());
		consumerServiceImpl.registConsumerGroupConsumer(consumerGroupConsumerEntities);
		verify(consumerGroupConsumerService).insert(any(ConsumerGroupConsumerEntity.class));
	}

	@Test
	public void registConsumer_checkVaild_Test() {
		ConsumerRegisterResponse response = new ConsumerRegisterResponse();
		response.setSuc(true);
		consumerServiceImpl.checkVaild(null, response);
		assertEquals(false, response.isSuc());

		response.setSuc(true);
		ConsumerRegisterRequest request = new ConsumerRegisterRequest();
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(false, response.isSuc());

		response.setSuc(true);
		request.setName("test");
		request.setClientIp("");
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(false, response.isSuc());

		response.setSuc(true);
		request = new ConsumerRegisterRequest();
		request.setName("test");
		request.setSdkVersion("");
		consumerServiceImpl.checkVaild(request, response);
		assertEquals(false, response.isSuc());
	}	

//	@Test
//	public void setDoDeleteConsumerLogTest() {
//		ConsumerGroupConsumerEntity consumerGroupConsumer = new ConsumerGroupConsumerEntity();
//		AuditLogEntity auditLog = new AuditLogEntity();
//		consumerServiceImpl.setDoDeleteConsumerLog(0, consumerGroupConsumer, auditLog);
//		consumerServiceImpl.setDoDeleteConsumerLog(1, consumerGroupConsumer, auditLog);
//	}
	
	@Test
	public void doSaveMsgTest() {
		List<Message01Entity> message01Entities=new ArrayList<Message01Entity>();
		PublishMessageRequest request=new PublishMessageRequest();
		PublishMessageResponse response=new PublishMessageResponse();
		QueueEntity temp=new QueueEntity();
		temp.setIp("fafd");
		doThrow(new RuntimeException(new DataIntegrityViolationException(""))).when(message01Service).insertBatchDy(anyString(),anyString(),anyListOf(Message01Entity.class));
		consumerServiceImpl.doSaveMsg(message01Entities, request, response, temp);
		assertEquals(true, consumerServiceImpl.dbFailMap.size()==0);
		reset(message01Service);
		
		doThrow(new RuntimeException("")).when(message01Service).insertBatchDy(anyString(),anyString(),anyListOf(Message01Entity.class));
		try {
			consumerServiceImpl.doSaveMsg(message01Entities, request, response, temp);
		} catch (Exception e) {
			// TODO: handle exception
		}
		assertEquals(true, consumerServiceImpl.dbFailMap.size()>0);
	}
	
	@Test
	public void saveAsynMsg_NoPartition_Test() {
		PublishMessageRequest request=new PublishMessageRequest();
		PublishMessageResponse response=new PublishMessageResponse();
		List<QueueEntity> queueEntities=new ArrayList<QueueEntity>(3);		
		initQueueEntity(queueEntities);		
		request.setTopicName("Test1");		
		List<ProducerDataDto> msgs=new ArrayList<ProducerDataDto>();
		ProducerDataDto producerDataDto=new ProducerDataDto();
		producerDataDto.setBody("faf");		
		msgs.add(producerDataDto);		
		request.setMsgs(msgs);		 
		consumerServiceImpl.saveSynMsg1(request,response,queueEntities);
		verify(message01Service).insertBatchDy(anyString(), anyString(), anyListOf(Message01Entity.class));
	}

	private void initQueueEntity(List<QueueEntity> queueEntities) {
		QueueEntity queueEntity=new QueueEntity();
		queueEntity.setId(1);
		queueEntity.setTopicName("Test1");
		queueEntity.setIp("tett");
		queueEntities.add(queueEntity);
		
		queueEntity=new QueueEntity();
		queueEntity.setId(2);
		queueEntity.setTopicName("Test1");
		queueEntity.setIp("tett");
		queueEntities.add(queueEntity);
		
		queueEntity=new QueueEntity();
		queueEntity.setId(3);
		queueEntity.setTopicName("Test3");
		queueEntity.setIp("tett");
		queueEntities.add(queueEntity);
	}
	
	@Test
	public void saveAsynMsg_Partition_Test() {
		PublishMessageRequest request=new PublishMessageRequest();
		PublishMessageResponse response=new PublishMessageResponse();
		List<QueueEntity> queueEntities=new ArrayList<QueueEntity>(3);		
		initQueueEntity(queueEntities);				
		request.setTopicName("Test1");
		
		List<ProducerDataDto> msgs=new ArrayList<ProducerDataDto>();
		ProducerDataDto producerDataDto=new ProducerDataDto();
		producerDataDto.setBody("faf");	
		PartitionInfo partitionInfo=new PartitionInfo();
		partitionInfo.setQueueId(1);
		producerDataDto.setPartitionInfo(partitionInfo);
		msgs.add(producerDataDto);		
		request.setMsgs(msgs);		
		consumerServiceImpl.saveSynMsg1(request,response,queueEntities);
		verify(message01Service).insertBatchDy(anyString(), anyString(), anyListOf(Message01Entity.class));
	}
	
	@Test
	public void saveAsynMsg_Partition2_Test() {
		PublishMessageRequest request=new PublishMessageRequest();
		PublishMessageResponse response=new PublishMessageResponse();
		List<QueueEntity> queueEntities=new ArrayList<QueueEntity>(2);
		
		initQueueEntity(queueEntities);		
		
		request.setTopicName("Test1");
		
		List<ProducerDataDto> msgs=new ArrayList<ProducerDataDto>();
		ProducerDataDto producerDataDto=new ProducerDataDto();
		producerDataDto.setBody("faf");	
		PartitionInfo partitionInfo=new PartitionInfo();
		partitionInfo.setQueueId(5);
		partitionInfo.setStrictMode(0);
		producerDataDto.setPartitionInfo(partitionInfo);
		producerDataDto.setTraceId("111111111111111");
		msgs.add(producerDataDto);		
		request.setMsgs(msgs);		
		consumerServiceImpl.saveSynMsg1(request,response,queueEntities);
		verify(message01Service).insertBatchDy(anyString(), anyString(), anyListOf(Message01Entity.class));
	}
	
	@Test
	public void saveAsynMsg_Partition3_Test() {
		PublishMessageRequest request=new PublishMessageRequest();
		PublishMessageResponse response=new PublishMessageResponse();
		List<QueueEntity> queueEntities=new ArrayList<QueueEntity>(3);
		
		initQueueEntity(queueEntities);		
		
		request.setTopicName("Test1");
		
		List<ProducerDataDto> msgs=new ArrayList<ProducerDataDto>();
		ProducerDataDto producerDataDto=new ProducerDataDto();
		producerDataDto.setBody("faf");	
		PartitionInfo partitionInfo=new PartitionInfo();
		partitionInfo.setQueueId(5);
		partitionInfo.setStrictMode(1);
		producerDataDto.setPartitionInfo(partitionInfo);
		producerDataDto.setTraceId("111111111111111");
		msgs.add(producerDataDto);		
		request.setMsgs(msgs);		
		consumerServiceImpl.saveSynMsg1(request,response,queueEntities);
		verify(message01Service,times(0)).insertBatchDy(anyString(), anyString(), anyListOf(Message01Entity.class));
	}
	
	@Test
	public void sendPublishFailMailTest() {
		PublishMessageRequest request=new PublishMessageRequest();
		Exception last=new RuntimeException("testt");
		
		request.setTopicName("Test");
		consumerServiceImpl.sendPublishFailMail(request, last, 1);
		verify(emailService).sendProduceMail(any(SendMailRequest.class));
	}
	
	@Test
	public void addRegisterConsumerGroupLogTest() {
		ConsumerGroupRegisterRequest request=new ConsumerGroupRegisterRequest();
		ConsumerGroupRegisterResponse response=new ConsumerGroupRegisterResponse();
		Map<String, List<String>> consumerGroupNames=new HashMap<String, List<String>>();
		consumerGroupNames.put("Test", new ArrayList<String>());
		request.setConsumerGroupNames(consumerGroupNames);
		
		Map<String, ConsumerGroupEntity> conMap=new HashMap<String, ConsumerGroupEntity>();
		conMap.put("Test", new ConsumerGroupEntity());
		
		when(consumerGroupService.getCache()).thenReturn(conMap);
		consumerServiceImpl.addRegisterConsumerGroupLog(request, response);
		verify(auditLogService).insertBatch(anyListOf(AuditLogEntity.class));
		
		request.setConsumerGroupNames(null);
		consumerServiceImpl.addRegisterConsumerGroupLog(request, response);
		verify(auditLogService).insert(any(AuditLogEntity.class));
	}
}
