package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupTopicRepository;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupTopicCreateResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;

@RunWith(JUnit4.class)
public class ConsumerGroupTopicServiceImplTest extends AbstractTest {

	private ConsumerGroupTopicServiceImpl consumerGroupTopicServiceImpl;

	private ConsumerGroupTopicRepository consumerGroupTopicRepository;

	private AuditLogService uiAuditLogService;

	private ConsumerGroupService consumerGroupService;

	private RoleService roleService;

	private UserInfoHolder userInfoHolder;

	private TopicService topicService;

	private QueueOffsetService queueOffsetService;

	@Before
	public void init() {

		super.init();

		consumerGroupTopicServiceImpl = new ConsumerGroupTopicServiceImpl();

		consumerGroupTopicRepository = mock(ConsumerGroupTopicRepository.class);

		uiAuditLogService = mock(AuditLogService.class);

		consumerGroupService = mock(ConsumerGroupService.class);

		roleService = mock(RoleService.class);

		userInfoHolder = mock(UserInfoHolder.class);

		topicService = mock(TopicService.class);

		queueOffsetService = mock(QueueOffsetService.class);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "consumerGroupTopicRepository",
				consumerGroupTopicRepository);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "uiAuditLogService", uiAuditLogService);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "consumerGroupService", consumerGroupService);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "roleService", roleService);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "userInfoHolder", userInfoHolder);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "topicService", topicService);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "queueOffsetService", queueOffsetService);

		ReflectionTestUtils.setField(consumerGroupTopicServiceImpl, "soaConfig", soaConfig);

		consumerGroupTopicServiceImpl.init();
	}

	@Test
	public void getCacheTest() {
		assertEquals(0, consumerGroupTopicServiceImpl.getCache().size());
		List<ConsumerGroupTopicEntity> consumerGroupEntities = new ArrayList<ConsumerGroupTopicEntity>();
		ConsumerGroupTopicEntity consumerGroupTopicEntity = new ConsumerGroupTopicEntity();
		consumerGroupEntities.add(consumerGroupTopicEntity);
		consumerGroupTopicEntity.setConsumerGroupName("Test");
		consumerGroupTopicEntity.setTopicName("test");
		consumerGroupTopicEntity.setConsumerGroupId(1);
		when(consumerGroupTopicRepository.getAll()).thenReturn(consumerGroupEntities);
		consumerGroupTopicServiceImpl.first.set(true);
		assertEquals(1, consumerGroupTopicServiceImpl.getCache().size());
	}

	@Test
	public void getGroupTopicTest() {
		assertEquals(0, consumerGroupTopicServiceImpl.getGroupTopic().size());
		List<ConsumerGroupTopicEntity> consumerGroupEntities = new ArrayList<ConsumerGroupTopicEntity>();
		ConsumerGroupTopicEntity consumerGroupTopicEntity = new ConsumerGroupTopicEntity();
		consumerGroupEntities.add(consumerGroupTopicEntity);
		consumerGroupTopicEntity.setConsumerGroupName("Test");
		consumerGroupTopicEntity.setTopicName("test");
		consumerGroupTopicEntity.setConsumerGroupId(1);
		when(consumerGroupTopicRepository.getAll()).thenReturn(consumerGroupEntities);
		consumerGroupTopicServiceImpl.first.set(true);
		assertEquals(1, consumerGroupTopicServiceImpl.getGroupTopic().size());
	}

	@Test
	public void deleteByConsumerGroupIdTest() {
		consumerGroupTopicServiceImpl.deleteByConsumerGroupId(1);
		verify(consumerGroupTopicRepository).deleteByConsumerGroupId(anyLong());
		verify(uiAuditLogService).recordAudit(anyString(), anyLong(), anyString());
	}

	@Test
	public void deleteByOriginTopicNameTest() {
		consumerGroupTopicServiceImpl.deleteByOriginTopicName(1, "test");
		verify(consumerGroupTopicRepository).deleteByOriginTopicName(anyLong(), anyString());
	}

	@Test
	public void getFailTopicNamesTest() {
		consumerGroupTopicServiceImpl.getFailTopicNames(1);
		verify(consumerGroupTopicRepository).getFailTopicNames(anyLong());
	}

	@Test
	public void getCorrespondConsumerGroupTopicTest() {
		consumerGroupTopicServiceImpl.getCorrespondConsumerGroupTopic(new HashMap<String, Object>());
		verify(consumerGroupTopicRepository).getCorrespondConsumerGroupTopic(anyMapOf(String.class, Object.class));
	}

	@Test
	public void updateEmailByGroupNameTest() {
		consumerGroupTopicServiceImpl.updateEmailByGroupName("1", "3");
		verify(consumerGroupTopicRepository).updateEmailByGroupName(anyString(), anyString());
	}

	@Test
	public void stopTest() {
		consumerGroupTopicServiceImpl.stop();
		assertEquals(false, consumerGroupTopicServiceImpl.isRunning);
	}

	@Test
	public void infoTest() {
		assertEquals(null, consumerGroupTopicServiceImpl.info());
	}

	@Test
	public void getCacheJsonTest() {
		assertEquals("{}", consumerGroupTopicServiceImpl.getCacheJson());
	}

	@Test
	public void getTopicSubscribeMapTest() {
		assertEquals(0, consumerGroupTopicServiceImpl.getTopicSubscribeMap().size());
		List<ConsumerGroupTopicEntity> consumerGroupEntities = new ArrayList<ConsumerGroupTopicEntity>();
		ConsumerGroupTopicEntity consumerGroupTopicEntity = new ConsumerGroupTopicEntity();
		consumerGroupEntities.add(consumerGroupTopicEntity);
		consumerGroupTopicEntity.setConsumerGroupName("Test");
		consumerGroupTopicEntity.setTopicName("test");
		consumerGroupTopicEntity.setConsumerGroupId(1);
		when(consumerGroupTopicRepository.getAll()).thenReturn(consumerGroupEntities);
		consumerGroupTopicServiceImpl.forceUpdateCache();
		assertEquals(1, consumerGroupTopicServiceImpl.getTopicSubscribeMap().size());
	}

	@Test
	public void subscribeTest() {
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setOriginName("test");
		consumerGroupEntity.setId(1L);
		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);
		when(consumerGroupService.getCache()).thenReturn(consumerGroupMap);

		ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest = new ConsumerGroupTopicCreateRequest();
		consumerGroupTopicCreateRequest.setConsumerGroupId(consumerGroupEntity.getId() + 1);
		consumerGroupTopicCreateRequest.setConsumerGroupName(consumerGroupEntity.getName());
		consumerGroupTopicCreateRequest.setTopicId(1L);
		consumerGroupTopicCreateRequest.setTopicName("test");
		consumerGroupTopicCreateRequest.setTopicType(1);
		consumerGroupTopicCreateRequest.setDelayProcessTime(1);
		consumerGroupTopicCreateRequest.setMaxLag(2000);
		consumerGroupTopicCreateRequest.setPullBatchSize(200);
		consumerGroupTopicCreateRequest.setRetryCount(2);
		consumerGroupTopicCreateRequest.setThreadSize(3);
		consumerGroupTopicCreateRequest.setTimeOut(1);
		consumerGroupTopicCreateRequest.setDelayPullTime(1);

		TopicEntity topicEntity = new TopicEntity();
		topicEntity.setAppId("123213");
		topicEntity.setId(1);
		topicEntity.setBusinessType("类型");

		when(topicService.get(anyLong())).thenReturn(topicEntity);
		TopicEntity failTopicEntity = new TopicEntity();
		failTopicEntity.setAppId("1232112133");
		failTopicEntity.setId(12);
		failTopicEntity.setBusinessType("类型");
		when(topicService.createFailTopic(any(TopicEntity.class), any(ConsumerGroupEntity.class)))
				.thenReturn(failTopicEntity);

		when(consumerGroupService.get(anyLong())).thenReturn(consumerGroupEntity);
		consumerGroupTopicServiceImpl.subscribe(consumerGroupTopicCreateRequest);

		verify(queueOffsetService, times(4)).createQueueOffset(any(ConsumerGroupTopicEntity.class));

	}

	@Test
	public void createConsumerGroupTopicAndFailTopicTest() {
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setOriginName("test");
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setOwnerIds("12313");
		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);

		ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest = new ConsumerGroupTopicCreateRequest();
		consumerGroupTopicCreateRequest.setConsumerGroupId(consumerGroupEntity.getId() + 1);
		consumerGroupTopicCreateRequest.setConsumerGroupName(consumerGroupEntity.getName());
		consumerGroupTopicCreateRequest.setTopicId(1L);
		consumerGroupTopicCreateRequest.setTopicName("test");
		consumerGroupTopicCreateRequest.setTopicType(1);
		consumerGroupTopicCreateRequest.setDelayProcessTime(1);
		consumerGroupTopicCreateRequest.setMaxLag(2000);
		consumerGroupTopicCreateRequest.setPullBatchSize(200);
		consumerGroupTopicCreateRequest.setRetryCount(2);
		consumerGroupTopicCreateRequest.setThreadSize(3);
		consumerGroupTopicCreateRequest.setTimeOut(1);
		consumerGroupTopicCreateRequest.setDelayPullTime(1);

		when(roleService.getRole(anyString(), anyString())).thenReturn(2);

		boolean rs = false;
		try {
			consumerGroupTopicServiceImpl.createConsumerGroupTopicAndFailTopic(consumerGroupTopicCreateRequest,
					consumerGroupMap);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
	}

	@Test
	public void createConsumerGroupTopicAndFailTopicTopicTest() {
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setOriginName("test");
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setOwnerIds("12313");
		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);

		ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest = new ConsumerGroupTopicCreateRequest();
		consumerGroupTopicCreateRequest.setConsumerGroupId(consumerGroupEntity.getId() + 1);
		consumerGroupTopicCreateRequest.setConsumerGroupName(consumerGroupEntity.getName());
		consumerGroupTopicCreateRequest.setTopicId(1L);
		consumerGroupTopicCreateRequest.setTopicName("fadfs");
		consumerGroupTopicCreateRequest.setTopicType(1);
		consumerGroupTopicCreateRequest.setDelayProcessTime(1);
		consumerGroupTopicCreateRequest.setMaxLag(2000);
		consumerGroupTopicCreateRequest.setPullBatchSize(200);
		consumerGroupTopicCreateRequest.setRetryCount(2);
		consumerGroupTopicCreateRequest.setThreadSize(3);
		consumerGroupTopicCreateRequest.setTimeOut(1);
		consumerGroupTopicCreateRequest.setDelayPullTime(1);

		when(roleService.getRole(anyString(), anyString())).thenReturn(1);

		TopicEntity topicEntity = new TopicEntity();
		topicEntity.setAppId("123213");
		topicEntity.setId(1);
		topicEntity.setBusinessType("类型");
		when(topicService.get(anyLong())).thenReturn(topicEntity);
		TopicEntity failTopicEntity = new TopicEntity();
		failTopicEntity.setAppId("1232112133");
		failTopicEntity.setId(12);
		failTopicEntity.setBusinessType("类型");
		when(topicService.createFailTopic(any(TopicEntity.class), any(ConsumerGroupEntity.class)))
				.thenReturn(failTopicEntity);
		consumerGroupTopicCreateRequest.setTopicName(null);

		ConsumerGroupTopicCreateResponse response = consumerGroupTopicServiceImpl
				.createConsumerGroupTopicAndFailTopic(consumerGroupTopicCreateRequest, consumerGroupMap);
		assertEquals("1", response.getCode());

		consumerGroupTopicCreateRequest.setTopicName("fadfs");
		doThrow(new RuntimeException("tets")).when(queueOffsetService)
				.createQueueOffset(any(ConsumerGroupTopicEntity.class));
		response = consumerGroupTopicServiceImpl.createConsumerGroupTopicAndFailTopic(consumerGroupTopicCreateRequest,
				consumerGroupMap);
		assertEquals("1", response.getCode());

	}

	@Test
	public void createConsumerGroupTopicTest() {
		ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest = new ConsumerGroupTopicCreateRequest();
		consumerGroupTopicCreateRequest.setConsumerGroupId(1L);
		consumerGroupTopicCreateRequest.setConsumerGroupName("test");
		consumerGroupTopicCreateRequest.setTopicId(1L);
		consumerGroupTopicCreateRequest.setTopicName("fadfs");
		consumerGroupTopicCreateRequest.setTopicType(1);
		consumerGroupTopicCreateRequest.setDelayProcessTime(1);
		consumerGroupTopicCreateRequest.setMaxLag(2000);
		consumerGroupTopicCreateRequest.setPullBatchSize(200);
		consumerGroupTopicCreateRequest.setRetryCount(2);
		consumerGroupTopicCreateRequest.setThreadSize(3);
		consumerGroupTopicCreateRequest.setTimeOut(1);
		consumerGroupTopicCreateRequest.setDelayPullTime(1);

		Map<String, ConsumerGroupTopicEntity> rs = new HashMap<String, ConsumerGroupTopicEntity>();
		rs.put(consumerGroupTopicCreateRequest.getConsumerGroupName() + "_"
				+ consumerGroupTopicCreateRequest.getTopicName(), new ConsumerGroupTopicEntity());
		consumerGroupTopicServiceImpl.groupTopicRefMap.set(rs);
		consumerGroupTopicServiceImpl.createConsumerGroupTopic(consumerGroupTopicCreateRequest);
		verify(consumerGroupTopicRepository, times(0)).insert(any(ConsumerGroupTopicEntity.class));

		rs.clear();
		consumerGroupTopicServiceImpl.createConsumerGroupTopic(consumerGroupTopicCreateRequest);
		verify(consumerGroupTopicRepository).insert(any(ConsumerGroupTopicEntity.class));
	}

	@Test
	public void deleteConsumerGroupTopicTest() {
		consumerGroupTopicServiceImpl.deleteConsumerGroupTopic(1L);

		ConsumerGroupTopicEntity consumerGroupTopicEntity = new ConsumerGroupTopicEntity();
		consumerGroupTopicEntity.setId(1L);
		consumerGroupTopicEntity.setConsumerGroupName("test");
		consumerGroupTopicEntity.setTopicName("test");
		consumerGroupTopicEntity.setOriginTopicName("test");

		when(consumerGroupTopicRepository.getById(anyLong())).thenReturn(consumerGroupTopicEntity);

		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setName(consumerGroupTopicEntity.getConsumerGroupName());
		consumerGroupEntity.setOwnerIds("test,test");
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setOriginName(consumerGroupEntity.getName());

		Map<String, ConsumerGroupEntity> groupMap = new HashMap<String, ConsumerGroupEntity>();
		groupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);
		when(consumerGroupService.getCache()).thenReturn(groupMap);

		when(roleService.getRole(anyString(), anyString())).thenReturn(2);

		assertEquals("1", consumerGroupTopicServiceImpl.deleteConsumerGroupTopic(1L).getCode());

		when(roleService.getRole(anyString(), anyString())).thenReturn(1);

		Map<String, List<ConsumerGroupTopicEntity>> topicSubscribeMap = new HashMap<String, List<ConsumerGroupTopicEntity>>();

		List<ConsumerGroupTopicEntity> consumerGroupTopicEntities = new ArrayList<ConsumerGroupTopicEntity>();
		ConsumerGroupTopicEntity consumerGroupTopicEntity2 = new ConsumerGroupTopicEntity();
		consumerGroupTopicEntity2.setId(2L);
		consumerGroupTopicEntity2.setTopicName(consumerGroupTopicEntity.getTopicName());
		consumerGroupTopicEntity2.setOriginTopicName("23432424");
		consumerGroupTopicEntity2.setConsumerGroupName(consumerGroupTopicEntity.getConsumerGroupName());
		consumerGroupTopicEntities.add(consumerGroupTopicEntity2);

		topicSubscribeMap.put(consumerGroupTopicEntity.getTopicName(), consumerGroupTopicEntities);
		consumerGroupTopicServiceImpl.topicSubscribeRefMap.set(topicSubscribeMap);

		consumerGroupTopicServiceImpl.deleteConsumerGroupTopic(1L);

		verify(topicService, times(2)).deleteFailTopic(anyListOf(String.class), anyLong());

		verify(queueOffsetService, times(2))
				.deleteByConsumerGroupIdAndOriginTopicName(any(ConsumerGroupTopicEntity.class));

		verify(consumerGroupService, times(2)).deleteTopicNameFromConsumerGroup(any(ConsumerGroupTopicEntity.class));

		verify(consumerGroupTopicRepository, times(2)).deleteByOriginTopicName(anyLong(), anyString());

		verify(consumerGroupService, times(2)).notifyMeta(anyLong());
	}

	@Test
	public void checkChangedTest() {
		assertEquals(true, consumerGroupTopicServiceImpl.checkChanged());
		LastUpdateEntity lastUpdateEntity = new LastUpdateEntity();
		lastUpdateEntity.setCount(1);
		lastUpdateEntity.setMaxId(2L);
		when(consumerGroupTopicRepository.getLastUpdate()).thenReturn(lastUpdateEntity);
		assertEquals(true, consumerGroupTopicServiceImpl.checkChanged());
		Map<Long, Map<String, ConsumerGroupTopicEntity>> map = new HashMap<Long, Map<String, ConsumerGroupTopicEntity>>();
		map.put(1L, new HashMap<String, ConsumerGroupTopicEntity>());
		consumerGroupTopicServiceImpl.consumerGroupTopicRefMap.set(map);
		assertEquals(false, consumerGroupTopicServiceImpl.checkChanged());

		lastUpdateEntity = new LastUpdateEntity();
		lastUpdateEntity.setCount(11);
		lastUpdateEntity.setMaxId(2L);
		consumerGroupTopicServiceImpl.lastUpdateEntity = lastUpdateEntity;
		assertEquals(true, consumerGroupTopicServiceImpl.checkChanged());
		// when(consumerGroupRepository.getLastUpdate()).thenReturn(lastUpdateEntity);
		assertEquals(false, consumerGroupTopicServiceImpl.checkChanged());

		doThrow(new RuntimeException("test")).when(consumerGroupTopicRepository).getLastUpdate();
		assertEquals(false, consumerGroupTopicServiceImpl.checkChanged());

		consumerGroupTopicServiceImpl.lastTime = System.currentTimeMillis() - soaConfig.getMqMetaRebuildMaxInterval()
				- 10;
		assertEquals(true, consumerGroupTopicServiceImpl.checkChanged());
	}

	@Test
	public void forceUpdateCacheTest() {
		List<ConsumerGroupTopicEntity> consumerGroupEntities = new ArrayList<ConsumerGroupTopicEntity>();
		ConsumerGroupTopicEntity consumerGroupEntity = new ConsumerGroupTopicEntity();
		consumerGroupEntity.setConsumerGroupName("test");
		consumerGroupEntity.setTopicName("test");
		consumerGroupEntity.setId(1L);
		consumerGroupEntities.add(consumerGroupEntity);
		when(consumerGroupTopicRepository.getAll()).thenReturn(consumerGroupEntities);
		consumerGroupTopicServiceImpl.forceUpdateCache();
		assertEquals(1, consumerGroupTopicServiceImpl.consumerGroupTopicRefMap.get().size());

		consumerGroupTopicServiceImpl.lastUpdateEntity = new LastUpdateEntity();
		doThrow(new RuntimeException("test")).when(consumerGroupTopicRepository).getAll();
		consumerGroupTopicServiceImpl.forceUpdateCache();
		assertEquals(null, consumerGroupTopicServiceImpl.lastUpdateEntity);
	}

	@Test
	public void startTest() {
		List<ConsumerGroupTopicEntity> consumerGroupEntities = new ArrayList<ConsumerGroupTopicEntity>();
		ConsumerGroupTopicEntity consumerGroupEntity = new ConsumerGroupTopicEntity();
		consumerGroupEntity.setConsumerGroupName("test");
		consumerGroupEntity.setTopicName("test");
		consumerGroupEntity.setId(1L);
		consumerGroupEntities.add(consumerGroupEntity);
		when(consumerGroupTopicRepository.getAll()).thenReturn(consumerGroupEntities);
		consumerGroupTopicServiceImpl.start();
		assertEquals(1, consumerGroupTopicServiceImpl.consumerGroupTopicRefMap.get().size());
		consumerGroupTopicServiceImpl.stop();
	}

	@Test
	public void doDeleteTest() {		
		ConsumerGroupTopicEntity consumerGroupTopicEntity = new ConsumerGroupTopicEntity();
		consumerGroupTopicEntity.setConsumerGroupId(1);
		consumerGroupTopicEntity.setConsumerGroupName("test");
		consumerGroupTopicEntity.setOriginTopicName("test");
		doThrow(new RuntimeException("fda")).when(topicService).deleteFailTopic(anyListOf(String.class), anyLong());

		boolean rs = false;
		try {
			consumerGroupTopicServiceImpl.doDelete(consumerGroupTopicEntity);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		reset(topicService);
		rs=false;
		doThrow(new RuntimeException("fda")).when(queueOffsetService).deleteByConsumerGroupIdAndOriginTopicName(any(ConsumerGroupTopicEntity.class));
		try {
			consumerGroupTopicServiceImpl.doDelete(consumerGroupTopicEntity);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		reset(queueOffsetService);
		rs=false;
		doThrow(new RuntimeException("fda")).when(consumerGroupService).deleteTopicNameFromConsumerGroup(any(ConsumerGroupTopicEntity.class));
		try {
			consumerGroupTopicServiceImpl.doDelete(consumerGroupTopicEntity);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		reset(consumerGroupService);
		rs=false;
		doThrow(new RuntimeException("fda")).when(consumerGroupTopicRepository).deleteByOriginTopicName(anyLong(),anyString());
		try {
			consumerGroupTopicServiceImpl.doDelete(consumerGroupTopicEntity);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		reset(consumerGroupTopicRepository);
		rs=false;
		doThrow(new RuntimeException("fda")).when(consumerGroupService).notifyRb(anyLong());

		try {
			consumerGroupTopicServiceImpl.doDelete(consumerGroupTopicEntity);
		} catch (Exception e) {
			rs = true;
		}
		assertEquals(true, rs);
		
	}
}
