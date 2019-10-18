package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;

@RunWith(JUnit4.class)
public class ConsumerGroupCheckServiceImplTest extends AbstractTest {

	private ConsumerGroupCheckServiceImpl consumerGroupCheckServiceImpl;
	private ConsumerGroupService consumerGroupService;
	private ConsumerGroupTopicService consumerGroupTopicService;
	private TopicService topicService;
	
	@Before
	public void init() {
		consumerGroupCheckServiceImpl=new ConsumerGroupCheckServiceImpl();
		consumerGroupService=mock(ConsumerGroupService.class);
		consumerGroupTopicService=mock(ConsumerGroupTopicService.class);
		topicService=mock(TopicService.class);
		ReflectionTestUtils.setField(consumerGroupCheckServiceImpl, "consumerGroupService", consumerGroupService);
		ReflectionTestUtils.setField(consumerGroupCheckServiceImpl, "consumerGroupTopicService", consumerGroupTopicService);
		ReflectionTestUtils.setField(consumerGroupCheckServiceImpl, "topicService", topicService);
	}
	
	@Test
	public void checkItemTest() {
		assertEquals(true, consumerGroupCheckServiceImpl.checkItem()!=null);
	}
	
	@Test
	public void checkResultTest() {
		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<>();//consumerGroupService.getCache();
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setOriginName("test1");
		consumerGroupEntity.setTopicNames("test");
		consumerGroupEntity.setIpBlackList("1");
		consumerGroupEntity.setIpWhiteList("2");
		consumerGroupEntity.setMode(1);
		consumerGroupEntity.setConsumerQuality(-1);
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);
		when(consumerGroupService.getCache()).thenReturn(consumerGroupMap);
        Map<String, ConsumerGroupTopicEntity> consumerGroupTopicMap = new HashMap<>();//consumerGroupTopicService.getGroupTopic();
        ConsumerGroupTopicEntity consumerGroupTopicEntity=new ConsumerGroupTopicEntity();
        consumerGroupTopicEntity.setConsumerGroupName(consumerGroupEntity.getName());
        consumerGroupTopicEntity.setTopicName(consumerGroupEntity.getTopicNames());
        consumerGroupTopicMap.put("1", consumerGroupTopicEntity);
        when(consumerGroupTopicService.getGroupTopic()).thenReturn(consumerGroupTopicMap);
        Map<String,TopicEntity> topicMap=new HashMap<>();//topicService.getCache();
        when(topicService.getCache()).thenReturn(topicMap);
        
        assertEquals(5, search(consumerGroupCheckServiceImpl.checkResult(),"<br/>"));
	}
	@Test
	public void checkResult1Test() {
		Map<String, ConsumerGroupEntity> consumerGroupMap = new HashMap<>();//consumerGroupService.getCache();
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setName("test");
		consumerGroupEntity.setOriginName("test1");
		consumerGroupEntity.setTopicNames("test");
		consumerGroupEntity.setIpBlackList("1");
		consumerGroupEntity.setIpWhiteList("2");
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setConsumerQuality(-1);
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);
		
		consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setName("Test");
		consumerGroupEntity.setOriginName("test1");
		consumerGroupEntity.setTopicNames("test");
		consumerGroupEntity.setIpBlackList("1");
		consumerGroupEntity.setIpWhiteList("2");
		consumerGroupEntity.setMode(2);
		consumerGroupEntity.setConsumerQuality(-1);
		consumerGroupMap.put(consumerGroupEntity.getName(), consumerGroupEntity);
		
		
		when(consumerGroupService.getCache()).thenReturn(consumerGroupMap);
        Map<String, ConsumerGroupTopicEntity> consumerGroupTopicMap = new HashMap<>();//consumerGroupTopicService.getGroupTopic();
        ConsumerGroupTopicEntity consumerGroupTopicEntity=new ConsumerGroupTopicEntity();
        consumerGroupTopicEntity.setConsumerGroupName(consumerGroupEntity.getName());
        consumerGroupTopicEntity.setTopicName(consumerGroupEntity.getTopicNames());
        consumerGroupTopicMap.put("1", consumerGroupTopicEntity);
        when(consumerGroupTopicService.getGroupTopic()).thenReturn(consumerGroupTopicMap);
        Map<String,TopicEntity> topicMap=new HashMap<>();//topicService.getCache();
        when(topicService.getCache()).thenReturn(topicMap);
        
        assertEquals(11, search(consumerGroupCheckServiceImpl.checkResult(),"<br/>"));
	}
	
	

}
