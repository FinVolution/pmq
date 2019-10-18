package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerGroupUtil;
import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupRepository;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupDeleteResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;

@RunWith(JUnit4.class)
public class ConsumerGroupServiceImplTest extends AbstractTest{

	private ConsumerGroupRepository consumerGroupRepository;

	private NotifyMessageService notifyMessageService;

	private ConsumerGroupTopicService consumerGroupTopicService;

	private QueueOffsetService queueOffsetService;

	private UserInfoHolder userInfoHolder;

	private AuditLogService uiAuditLogService;

	private TopicService topicService;

	private ConsumerService consumerService;

	private RoleService roleService;

	private ConsumerGroupServiceImpl consumerGroupServiceImpl;

	@Before
	public void init() {

		super.init();

		consumerGroupRepository = mock(ConsumerGroupRepository.class);

		notifyMessageService = mock(NotifyMessageService.class);

		consumerGroupTopicService = mock(ConsumerGroupTopicService.class);

		queueOffsetService = mock(QueueOffsetService.class);

		userInfoHolder = mock(UserInfoHolder.class);

		uiAuditLogService = mock(AuditLogService.class);

		topicService = mock(TopicService.class);

		consumerService = mock(ConsumerService.class);

		roleService = mock(RoleService.class);

		consumerGroupServiceImpl = new ConsumerGroupServiceImpl();

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "consumerGroupRepository", consumerGroupRepository);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "notifyMessageService", notifyMessageService);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "consumerGroupTopicService", consumerGroupTopicService);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "queueOffsetService", queueOffsetService);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "userInfoHolder", userInfoHolder);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "uiAuditLogService", uiAuditLogService);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "topicService", topicService);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "consumerService", consumerService);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "roleService", roleService);

		ReflectionTestUtils.setField(consumerGroupServiceImpl, "soaConfig", soaConfig);

		consumerGroupServiceImpl.init();
	}

	@Test
	public void getLastMetaConsumerGroupTest() {
		when(consumerGroupRepository.getLastConsumerGroup(anyLong(), anyLong(), anyInt()))
				.thenReturn(new ArrayList<ConsumerGroupEntity>());
		consumerGroupServiceImpl.getLastMetaConsumerGroup(1L, 1L);
		verify(consumerGroupRepository).getLastConsumerGroup(anyLong(), anyLong(), anyInt());
	}

	@Test
	public void getLastRbConsumerGroupTest() {
		when(consumerGroupRepository.getLastConsumerGroup(anyLong(), anyLong(), anyInt()))
				.thenReturn(new ArrayList<ConsumerGroupEntity>());
		consumerGroupServiceImpl.getLastRbConsumerGroup(1L, 1L);
		verify(consumerGroupRepository).getLastConsumerGroup(anyLong(), anyLong(), anyInt());
	}

	@Test
	public void rbTest() {
		List<QueueOffsetEntity> queueOffsetEntities = new ArrayList<QueueOffsetEntity>();
		QueueOffsetEntity queueOffsetEntity = new QueueOffsetEntity();
		queueOffsetEntity.setId(1L);
		queueOffsetEntities.add(queueOffsetEntity);
		consumerGroupServiceImpl.rb(queueOffsetEntities);
		verify(queueOffsetService).updateConsumerId(any(QueueOffsetEntity.class));
		verify(notifyMessageService).insertBatch(anyListOf(NotifyMessageEntity.class));
		verify(consumerGroupRepository).updateRbVersion(anyListOf(Long.class));
	}

	@Test
	public void notifyRbTest() {
		consumerGroupServiceImpl.notifyRb(null);
		verify(consumerGroupRepository,times(0)).updateMetaVersion(anyListOf(Long.class));
		
		consumerGroupServiceImpl.notifyRb(1L);
		verify(consumerGroupRepository).updateRbVersion(anyListOf(Long.class));
		verify(notifyMessageService).insertBatch(anyListOf(NotifyMessageEntity.class));
	}

	@Test
	public void updateRbVersionTest() {
		consumerGroupServiceImpl.updateRbVersion(null);
		verify(consumerGroupRepository, times(0)).updateRbVersion(anyListOf(Long.class));
		consumerGroupServiceImpl.updateRbVersion(Arrays.asList(1L));
		verify(consumerGroupRepository).updateRbVersion(anyListOf(Long.class));
	}

	@Test
	public void notifyMetaTest() {
		consumerGroupServiceImpl.notifyMeta(null);
		verify(notifyMessageService,times(0)).insert(any(NotifyMessageEntity.class));
		
		consumerGroupServiceImpl.notifyMeta(1L);
		verify(notifyMessageService).insert(any(NotifyMessageEntity.class));

		consumerGroupServiceImpl.notifyMeta(Arrays.asList(1L));
		verify(notifyMessageService).insertBatch(anyListOf(NotifyMessageEntity.class));
	}

	@Test
	public void getByNamesTest() {
		assertEquals(true, consumerGroupServiceImpl.getByNames(null).size() == 0);

		List<ConsumerGroupEntity> consumerGroupEntities = new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setName("test");
		consumerGroupEntities.add(consumerGroupEntity);

		when(consumerGroupRepository.getByNames(anyListOf(String.class))).thenReturn(consumerGroupEntities);
		assertEquals(1, consumerGroupServiceImpl.getByNames(Arrays.asList("test")).size());
	}

	@Test
	public void notifyOffsetTest() {
		consumerGroupServiceImpl.notifyOffset(1L);
	}

	@Test
	public void getGroupTopicTest() {
		consumerGroupServiceImpl.getGroupTopic();
		verify(consumerGroupTopicService).getList();
	}

	// @Override
//	public ConsumerGroupTopicEntity getTopic(String consumerGroupName, String topicName) {
//		Map<String, ConsumerGroupEntity> cache = getCache();
//		if (!cache.containsKey(consumerGroupName)) {
//			return null;
//		}
//		if (!consumerGroupTopicService.getCache().containsKey(cache.get(consumerGroupName).getId())) {
//			return null;
//		}
//		return consumerGroupTopicService.getCache().get(cache.get(consumerGroupName).getId()).get(topicName);
//	}
	@Test
	public void getTopicTest() {
		Map<String, ConsumerGroupEntity> consumerGroupRefMap = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupRefMap.put("test", new ConsumerGroupEntity());
		consumerGroupRefMap.get("test").setId(1L);
		consumerGroupServiceImpl.consumerGroupRefMap.set(consumerGroupRefMap);

		assertEquals(null, consumerGroupServiceImpl.getTopic("test1", "tet"));

		Map<Long, Map<String, ConsumerGroupTopicEntity>> cacheMap = new HashMap<Long, Map<String, ConsumerGroupTopicEntity>>();
		Map<String, ConsumerGroupTopicEntity> dataMap = new HashMap<String, ConsumerGroupTopicEntity>();
		dataMap.put("test", new ConsumerGroupTopicEntity());
		cacheMap.put(1L, dataMap);
		when(consumerGroupTopicService.getCache()).thenReturn(cacheMap);
		
		consumerGroupRefMap = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupRefMap.put("test", new ConsumerGroupEntity());
		consumerGroupRefMap.get("test").setId(2L);		
		consumerGroupServiceImpl.consumerGroupRefMap.set(consumerGroupRefMap);
		assertEquals(null, consumerGroupServiceImpl.getTopic("test", "test"));		

		consumerGroupRefMap = new HashMap<String, ConsumerGroupEntity>();
		consumerGroupRefMap.put("test", new ConsumerGroupEntity());
		consumerGroupRefMap.get("test").setId(1L);		
		consumerGroupServiceImpl.consumerGroupRefMap.set(consumerGroupRefMap);		
		assertEquals(true, consumerGroupServiceImpl.getTopic("test", "test") != null);

	}

	@Test
	public void stopTest() {
		consumerGroupServiceImpl.stop();
		assertEquals(false, consumerGroupServiceImpl.isRunning);
	}

	@Test
	public void getByOwnerNamesTest() {
		consumerGroupServiceImpl.getByOwnerNames(null);
		verify(consumerGroupRepository).getByOwnerNames(anyMapOf(String.class, Object.class));
	}

	@Test
	public void countByOwnerNamesTest() {
		consumerGroupServiceImpl.countByOwnerNames(null);
		verify(consumerGroupRepository).countByOwnerNames(anyMapOf(String.class, Object.class));
	}

	@Test
	public void infoTest() {
		assertEquals(null, consumerGroupServiceImpl.info());
	}

	@Test
	public void notifyRbByNamesTest() {
		consumerGroupServiceImpl.notifyRbByNames(null);
		verify(notifyMessageService, times(0)).insertBatch(anyListOf(NotifyMessageEntity.class));
		consumerGroupServiceImpl.notifyRbByNames(Arrays.asList("test"));
		verify(notifyMessageService, times(0)).insertBatch(anyListOf(NotifyMessageEntity.class));
		Map<String, ConsumerGroupEntity> cacheData = new HashMap<String, ConsumerGroupEntity>();
		cacheData.put("test", new ConsumerGroupEntity());
		cacheData.get("test").setId(1L);
		consumerGroupServiceImpl.consumerGroupRefMap.set(cacheData);
		consumerGroupServiceImpl.notifyRbByNames(Arrays.asList("test"));
		verify(notifyMessageService).insertBatch(anyListOf(NotifyMessageEntity.class));
	}

	@Test
	public void getCacheJsonTest() {
		assertEquals("{}", consumerGroupServiceImpl.getCacheJson());
	}

	@Test
	public void createConsumerGroupSucTest() {
		when(userInfoHolder.getUserId()).thenReturn("1");
		// List<ConsumerGroupEntity> consumerGroupEntities =
		// consumerGroupRepository.getByNames(names);
		List<ConsumerGroupEntity> consumerGroupEntities = new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setName("test");
		consumerGroupEntities.add(consumerGroupEntity);
		AtomicInteger counter = new AtomicInteger(0);
		when(consumerGroupRepository.getByNames(Mockito.argThat(new ArgumentMatcher<List<String>>() {
			@Override
			public boolean matches(Object names) {
				counter.incrementAndGet();
				return counter.get() == 1 || counter.get() == 3;
			}
		}))).thenReturn(consumerGroupEntities);

		ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
		consumerGroupCreateRequest.setAlarmFlag(1);
		consumerGroupCreateRequest.setTraceFlag(1);
		consumerGroupCreateRequest.setMode(2);
		consumerGroupCreateRequest.setName(ConsumerGroupUtil.getBroadcastConsumerName("test", "123", 1L));
		consumerGroupCreateRequest.setIpFlag(1);
		consumerGroupCreateRequest.setIpList("213");
		boolean flag = false;
		try {
			// 因为已经存在
			consumerGroupServiceImpl.createConsumerGroup(consumerGroupCreateRequest);
		} catch (CheckFailException e) {
			flag = true;
		}
		assertEquals(true, flag);
		ArgumentCaptor<ConsumerGroupEntity> argument = ArgumentCaptor.forClass(ConsumerGroupEntity.class);
		consumerGroupServiceImpl.createConsumerGroup(consumerGroupCreateRequest);
		verify(consumerGroupRepository).insert(argument.capture());

		assertEquals("test", argument.getValue().getOriginName());
		assertEquals(null, argument.getValue().getIpWhiteList());
		assertEquals("213", argument.getValue().getIpBlackList());
	}

	@Test
	public void createConsumerGroupSuc1Test() {
		when(userInfoHolder.getUserId()).thenReturn("1");
		List<ConsumerGroupEntity> consumerGroupEntities = new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setName("test");
		consumerGroupEntities.add(consumerGroupEntity);
		AtomicInteger counter = new AtomicInteger(0);
		when(consumerGroupRepository.getByNames(Mockito.argThat(new ArgumentMatcher<List<String>>() {
			@Override
			public boolean matches(Object names) {
				counter.incrementAndGet();
				return counter.get() == 2;
			}
		}))).thenReturn(consumerGroupEntities);
		ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
		consumerGroupCreateRequest.setAlarmFlag(1);
		consumerGroupCreateRequest.setTraceFlag(1);
		consumerGroupCreateRequest.setMode(1);
		consumerGroupCreateRequest.setName("test");
		consumerGroupCreateRequest.setIpFlag(0);
		consumerGroupCreateRequest.setIpList("213");
		ArgumentCaptor<ConsumerGroupEntity> argument = ArgumentCaptor.forClass(ConsumerGroupEntity.class);
		consumerGroupServiceImpl.createConsumerGroup(consumerGroupCreateRequest);
		verify(consumerGroupRepository).insert(argument.capture());

		assertEquals("test", argument.getValue().getOriginName());
		assertEquals("213", argument.getValue().getIpWhiteList());
		assertEquals(null, argument.getValue().getIpBlackList());
	}

	private ConsumerGroupEntity buildConsumerGroupEntity() {
	    ConsumerGroupEntity consumerGroupEntity1=new ConsumerGroupEntity();
		consumerGroupEntity1.setId(1);
		consumerGroupEntity1.setAlarmEmails("afa");
		consumerGroupEntity1.setAlarmFlag(1);
		consumerGroupEntity1.setAppId("123");
		consumerGroupEntity1.setConsumerQuality(1);
		consumerGroupEntity1.setDptName("ab");
		consumerGroupEntity1.setMetaVersion(1L);
		consumerGroupEntity1.setMode(2);
		consumerGroupEntity1.setName("fa");
		consumerGroupEntity1.setOriginName("fafa");
		consumerGroupEntity1.setOwnerIds("123");
		consumerGroupEntity1.setOwnerNames("32423");
		consumerGroupEntity1.setRbVersion(2L);
		consumerGroupEntity1.setTopicNames("23424");
		consumerGroupEntity1.setTraceFlag(1);
		consumerGroupEntity1.setVersion(3L); 
		return consumerGroupEntity1;
	}
	@Test
	public void editConsumerGroupNoRightTest() {
		when(userInfoHolder.getUserId()).thenReturn("1");
		when(roleService.getRole(anyString(), anyString())).thenReturn(2);
		when(consumerGroupRepository.getById(anyLong())).thenReturn(buildConsumerGroupEntity());		
		List<ConsumerGroupEntity> consumerGroupEntities = new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setName("test");
		consumerGroupEntities.add(consumerGroupEntity);
		AtomicInteger counter = new AtomicInteger(0);
		when(consumerGroupRepository.getByNames(Mockito.argThat(new ArgumentMatcher<List<String>>() {
			@Override
			public boolean matches(Object names) {
				counter.incrementAndGet();
				return counter.get() == 2;
			}
		}))).thenReturn(consumerGroupEntities);
		ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
		consumerGroupCreateRequest.setId("1");
		consumerGroupCreateRequest.setAlarmFlag(1);
		consumerGroupCreateRequest.setTraceFlag(1);
		consumerGroupCreateRequest.setMode(1);
		consumerGroupCreateRequest.setName("test");
		consumerGroupCreateRequest.setIpFlag(0);
		consumerGroupCreateRequest.setIpList("213");
		ArgumentCaptor<ConsumerGroupEntity> argument = ArgumentCaptor.forClass(ConsumerGroupEntity.class);
		consumerGroupServiceImpl.createConsumerGroup(consumerGroupCreateRequest);
		verify(consumerGroupRepository,times(0)).update(argument.capture());		
	}
	
	@Test
	public void editConsumerGroupNormalTest() {
		when(userInfoHolder.getUserId()).thenReturn("1");
		when(roleService.getRole(anyString(), anyString())).thenReturn(1);
		when(consumerGroupRepository.getById(anyLong())).thenReturn(buildConsumerGroupEntity());	
		List<ConsumerGroupEntity> consumerGroupEntities = new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setName("test");
		consumerGroupEntities.add(consumerGroupEntity);
		AtomicInteger counter = new AtomicInteger(0);
		when(consumerGroupRepository.getByNames(Mockito.argThat(new ArgumentMatcher<List<String>>() {
			@Override
			public boolean matches(Object names) {
				counter.incrementAndGet();
				return counter.get() == 2;
			}
		}))).thenReturn(consumerGroupEntities);
		ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
		consumerGroupCreateRequest.setId("1");
		consumerGroupCreateRequest.setAppId("1234");
		consumerGroupCreateRequest.setAlarmFlag(1);
		consumerGroupCreateRequest.setTraceFlag(1);
		consumerGroupCreateRequest.setMode(1);
		consumerGroupCreateRequest.setName("test");
		consumerGroupCreateRequest.setIpFlag(0);
		consumerGroupCreateRequest.setIpList("213");
		ArgumentCaptor<ConsumerGroupEntity> argument = ArgumentCaptor.forClass(ConsumerGroupEntity.class);
		consumerGroupServiceImpl.createConsumerGroup(consumerGroupCreateRequest);
		verify(consumerGroupRepository).update(argument.capture()); 
		verify(topicService).updateFailTopic(any(ConsumerGroupEntity.class));
	}
	
	@Test
	public void editConsumerGroupBroadTest() {
		when(userInfoHolder.getUserId()).thenReturn("1");
		when(roleService.getRole(anyString(), anyString())).thenReturn(1);
		when(consumerGroupRepository.getById(anyLong())).thenReturn(buildConsumerGroupEntity());	
		List<ConsumerGroupEntity> consumerGroupEntities = new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity = new ConsumerGroupEntity();
		consumerGroupEntity.setId(1L);
		consumerGroupEntity.setName("test");
		consumerGroupEntities.add(consumerGroupEntity);
		AtomicInteger counter = new AtomicInteger(0);
		when(consumerGroupRepository.getByNames(Mockito.argThat(new ArgumentMatcher<List<String>>() {
			@Override
			public boolean matches(Object names) {
				counter.incrementAndGet();
				return counter.get() == 2;
			}
		}))).thenReturn(consumerGroupEntities);
		ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
		consumerGroupCreateRequest.setId("1");
		consumerGroupCreateRequest.setAlarmFlag(1);
		consumerGroupCreateRequest.setTraceFlag(1);
		consumerGroupCreateRequest.setMode(2);
		consumerGroupCreateRequest.setName("test");
		consumerGroupCreateRequest.setIpFlag(0);
		consumerGroupCreateRequest.setIpList("213");
		ArgumentCaptor<ConsumerGroupEntity> argument = ArgumentCaptor.forClass(ConsumerGroupEntity.class);
		consumerGroupServiceImpl.createConsumerGroup(consumerGroupCreateRequest);
		verify(consumerGroupRepository).updateByOriginName(argument.capture());
	}
	
	
	@Test
	public void deleteConsumerGroupImageTest() {
		ConsumerGroupEntity consumerGroupEntity=buildConsumerGroupEntity();
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setOriginName(consumerGroupEntity.getName());		
		when(consumerGroupRepository.getById(anyLong())).thenReturn(consumerGroupEntity);			
		Map<String, ConsumerGroupEntity> consumerGroupRefMap=new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity2=new ConsumerGroupEntity();
		consumerGroupEntity2.setName(consumerGroupEntity.getName());
		consumerGroupEntity2.setOriginName(consumerGroupEntity.getName());
		consumerGroupEntity2.setId(100L);
		consumerGroupRefMap.put(consumerGroupEntity2.getName(), consumerGroupEntity2);
		consumerGroupServiceImpl.consumerGroupRefMap.set(consumerGroupRefMap);		
		ConsumerGroupDeleteResponse response= consumerGroupServiceImpl.deleteConsumerGroup(1L,true);		
		assertEquals("1", response.getCode());
	}
	
	@Test
	public void deleteConsumerGroupNoRightTest() {
		ConsumerGroupEntity consumerGroupEntity=buildConsumerGroupEntity();
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setOriginName(consumerGroupEntity.getName());		
		when(consumerGroupRepository.getById(anyLong())).thenReturn(consumerGroupEntity);			
		Map<String, ConsumerGroupEntity> consumerGroupRefMap=new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity2=new ConsumerGroupEntity();
		consumerGroupEntity2.setName(consumerGroupEntity.getName());
		consumerGroupEntity2.setOriginName(consumerGroupEntity.getName());
		consumerGroupEntity2.setId(consumerGroupEntity.getId());
		consumerGroupRefMap.put(consumerGroupEntity2.getName(), consumerGroupEntity2);
		consumerGroupServiceImpl.consumerGroupRefMap.set(consumerGroupRefMap);		
		
		
		when(userInfoHolder.getUserId()).thenReturn("1");
		when(roleService.getRole(anyString(), anyString())).thenReturn(2);
				
		boolean rs=false;
		try {
			consumerGroupServiceImpl.deleteConsumerGroup(1L,true);	

		} catch (Exception e) {
			rs=true;
		}
		assertEquals(true, rs);
	}
	
	@Test
	public void deleteConsumerGroupHasConsumerTest() {
		ConsumerGroupEntity consumerGroupEntity=buildConsumerGroupEntity();
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setOriginName(consumerGroupEntity.getName());		
		when(consumerGroupRepository.getById(anyLong())).thenReturn(consumerGroupEntity);			
		Map<String, ConsumerGroupEntity> consumerGroupRefMap=new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity2=new ConsumerGroupEntity();
		consumerGroupEntity2.setName(consumerGroupEntity.getName());
		consumerGroupEntity2.setOriginName(consumerGroupEntity.getName());
		consumerGroupEntity2.setId(consumerGroupEntity.getId());
		consumerGroupRefMap.put(consumerGroupEntity2.getName(), consumerGroupEntity2);
		consumerGroupServiceImpl.consumerGroupRefMap.set(consumerGroupRefMap);	
				
		when(userInfoHolder.getUserId()).thenReturn("1");
		when(roleService.getRole(anyString(), anyString())).thenReturn(1);
		when(consumerService.getConsumerGroupByConsumerGroupIds(anyListOf(Long.class))).thenReturn(Arrays.asList(new ConsumerGroupConsumerEntity()));
		assertEquals("1", consumerGroupServiceImpl.deleteConsumerGroup(1L,true).getCode());
	}
	
	@Test
	public void deleteConsumerGroupSucTest() {
		ConsumerGroupEntity consumerGroupEntity=buildConsumerGroupEntity();
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setOriginName(consumerGroupEntity.getName());		
		when(consumerGroupRepository.getById(anyLong())).thenReturn(consumerGroupEntity);			
		Map<String, ConsumerGroupEntity> consumerGroupRefMap=new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity2=new ConsumerGroupEntity();
		consumerGroupEntity2.setName(consumerGroupEntity.getName());
		consumerGroupEntity2.setOriginName(consumerGroupEntity.getName());
		consumerGroupEntity2.setId(consumerGroupEntity.getId());
		consumerGroupRefMap.put(consumerGroupEntity2.getName(), consumerGroupEntity2);
		consumerGroupServiceImpl.consumerGroupRefMap.set(consumerGroupRefMap);	
				
		when(userInfoHolder.getUserId()).thenReturn("1");
		when(roleService.getRole(anyString(), anyString())).thenReturn(1);
		//when(consumerService.getConsumerGroupByConsumerGroupIds(anyListOf(Long.class))).thenReturn(Arrays.asList(new ConsumerGroupConsumerEntity()));
		consumerGroupServiceImpl.deleteConsumerGroup(1L,true).getCode();
		
		verify(consumerGroupRepository).delete(anyLong());
		verify(consumerGroupRepository).updateMetaVersion(anyListOf(Long.class));
	}
	
	@Test
	public void updateMetaVersionTest() {
		consumerGroupServiceImpl.updateMetaVersion(null);
		verify(consumerGroupRepository,times(0)).updateMetaVersion(anyListOf(Long.class));
	}
	
	@Test
	public void getIdCacheTest() {
		assertEquals(0, consumerGroupServiceImpl.getIdCache().size());
	}
	
	private ConsumerGroupTopicEntity buildConsumerGroupTopic() {		
		ConsumerGroupTopicEntity consumerGroupTopicEntity=new ConsumerGroupTopicEntity();
		consumerGroupTopicEntity.setTopicName("test");
		consumerGroupTopicEntity.setOriginTopicName("test");
		return consumerGroupTopicEntity;
	}
	@Test
	public void deleteTopicNameFromConsumerGroupTest() {
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setTopicNames("test,test2");
		when(consumerGroupRepository.getById(anyLong())).thenReturn(consumerGroupEntity);
		ConsumerGroupTopicEntity consumerGroupTopicEntity=buildConsumerGroupTopic();
		
		consumerGroupServiceImpl.deleteTopicNameFromConsumerGroup(consumerGroupTopicEntity);
		verify(consumerGroupRepository).update(any(ConsumerGroupEntity.class));
		assertEquals("test2", consumerGroupEntity.getTopicNames());
	}
	
	@Test
	public void addTopicNameToConsumerGroupTest() {
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setTopicNames("test3,test2");
		when(consumerGroupRepository.getById(anyLong())).thenReturn(consumerGroupEntity);
		ConsumerGroupTopicEntity consumerGroupTopicEntity=buildConsumerGroupTopic();
		
		consumerGroupServiceImpl.addTopicNameToConsumerGroup(consumerGroupTopicEntity);
		verify(consumerGroupRepository).update(any(ConsumerGroupEntity.class));
		assertEquals("test3,test2,test", consumerGroupEntity.getTopicNames());
		
		
		consumerGroupEntity=new ConsumerGroupEntity();
		
		when(consumerGroupRepository.getById(anyLong())).thenReturn(consumerGroupEntity);
		consumerGroupTopicEntity=buildConsumerGroupTopic();
		
		consumerGroupServiceImpl.addTopicNameToConsumerGroup(consumerGroupTopicEntity);
		verify(consumerGroupRepository,times(2)).update(any(ConsumerGroupEntity.class));
		assertEquals("test", consumerGroupEntity.getTopicNames());
	}
	
	@Test
	public void checkChangedTest() {
		assertEquals(true, consumerGroupServiceImpl.checkChanged());		
		LastUpdateEntity lastUpdateEntity=new LastUpdateEntity();
		lastUpdateEntity.setCount(1);
		lastUpdateEntity.setMaxId(2L);
		when(consumerGroupRepository.getLastUpdate()).thenReturn(lastUpdateEntity);
		assertEquals(true, consumerGroupServiceImpl.checkChanged());
		Map<String, ConsumerGroupEntity> map=new HashMap<String, ConsumerGroupEntity>();
		map.put("test", new ConsumerGroupEntity());
		consumerGroupServiceImpl.consumerGroupRefMap.set(map);
		assertEquals(false, consumerGroupServiceImpl.checkChanged());
		
		lastUpdateEntity=new LastUpdateEntity();
		lastUpdateEntity.setCount(11);
		lastUpdateEntity.setMaxId(2L);
		consumerGroupServiceImpl.lastUpdateEntity=lastUpdateEntity;
		assertEquals(true, consumerGroupServiceImpl.checkChanged());		
		//when(consumerGroupRepository.getLastUpdate()).thenReturn(lastUpdateEntity);
		assertEquals(false, consumerGroupServiceImpl.checkChanged());
		
		
		
		doThrow(new RuntimeException("test")).when(consumerGroupRepository).getLastUpdate();
		assertEquals(false, consumerGroupServiceImpl.checkChanged());
		
		consumerGroupServiceImpl.lastTime=System.currentTimeMillis()-soaConfig.getMqMetaRebuildMaxInterval()-10;
		assertEquals(true, consumerGroupServiceImpl.checkChanged());
	}
	
	@Test
	public void forceUpdateCacheTest() {
		List<ConsumerGroupEntity> consumerGroupEntities=new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setId(1L);
		consumerGroupEntities.add(consumerGroupEntity);
		when(consumerGroupRepository.getAll()).thenReturn(consumerGroupEntities);
		consumerGroupServiceImpl.forceUpdateCache();
		assertEquals(1, consumerGroupServiceImpl.consumerGroupRefMap.get().size());
		
		consumerGroupServiceImpl.lastUpdateEntity=new LastUpdateEntity();
		doThrow(new RuntimeException("test")).when(consumerGroupRepository).getAll();
		consumerGroupServiceImpl.forceUpdateCache();
		assertEquals(null, consumerGroupServiceImpl.lastUpdateEntity);		
	}
	
	@Test
	public void startTest() {
		List<ConsumerGroupEntity> consumerGroupEntities=new ArrayList<ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setId(1L);
		consumerGroupEntities.add(consumerGroupEntity);
		when(consumerGroupRepository.getAll()).thenReturn(consumerGroupEntities);
		consumerGroupServiceImpl.start();
		assertEquals(1, consumerGroupServiceImpl.consumerGroupRefMap.get().size());
		consumerGroupServiceImpl.stop();
	}
}
