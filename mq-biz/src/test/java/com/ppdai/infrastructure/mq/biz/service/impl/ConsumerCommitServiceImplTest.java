package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.entity.OffsetVersionEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;

@RunWith(JUnit4.class)
public class ConsumerCommitServiceImplTest {
	ConsumerCommitServiceImpl consumerCommitServiceImpl = null;
	QueueOffsetService queueOffsetService = null;

	@Before
	public void init() {
		consumerCommitServiceImpl = new ConsumerCommitServiceImpl();
		queueOffsetService = mock(QueueOffsetService.class);
		SoaConfig soaConfig = new SoaConfig();
		Environment env = mock(Environment.class);
		ReflectionTestUtils.setField(soaConfig, "env", env);
		ReflectionTestUtils.setField(consumerCommitServiceImpl, "queueOffsetService", queueOffsetService);
		ReflectionTestUtils.setField(consumerCommitServiceImpl, "soaConfig", soaConfig);
		when(env.getProperty(anyString(), anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return (String) args[1];
			}
		});
	}

	@Test
	public void commitOffsetTest() {
		CommitOffsetRequest request = new CommitOffsetRequest();
		request.setFlag(1);
		List<ConsumerQueueVersionDto> queueOffsets = buildQueueOffsets();
		request.setQueueOffsets(queueOffsets);
		//init();
		when(queueOffsetService.commitOffset(any(QueueOffsetEntity.class))).thenReturn(1);
		consumerCommitServiceImpl.commitOffset(request);
		verify(queueOffsetService).commitOffset(any(QueueOffsetEntity.class));
		
		queueOffsets = buildQueueOffsets();
		request.setQueueOffsets(queueOffsets);
		request.setFlag(0);
		queueOffsets.get(0).setOffsetVersion(queueOffsets.get(0).getOffsetVersion() + 1);
		consumerCommitServiceImpl.commitOffset(request);
		verify(queueOffsetService,times(1)).commitOffset(any(QueueOffsetEntity.class));
		queueOffsets.get(0).setOffset(queueOffsets.get(0).getOffset() + 1);
		consumerCommitServiceImpl.commitOffset(request);
		verify(queueOffsetService,times(1)).commitOffset(any(QueueOffsetEntity.class));
	}

	private List<ConsumerQueueVersionDto> buildQueueOffsets() {
		List<ConsumerQueueVersionDto> queueOffsets = new ArrayList<ConsumerQueueVersionDto>();
		ConsumerQueueVersionDto consumerQueueVersionDto = new ConsumerQueueVersionDto();
		queueOffsets.add(consumerQueueVersionDto);
		consumerQueueVersionDto.setConsumerGroupName("test");
		consumerQueueVersionDto.setOffset(1L);
		consumerQueueVersionDto.setOffsetVersion(1L);
		consumerQueueVersionDto.setQueueOffsetId(1L);
		consumerQueueVersionDto.setTopicName("test");
		return queueOffsets;
	}
	
	
	private OffsetVersionEntity buildOffsetVersionEntity() {
		OffsetVersionEntity offsetVersionEntity = new OffsetVersionEntity();		
		offsetVersionEntity.setOffset(1L);
		offsetVersionEntity.setOffsetVersion(1L);
		offsetVersionEntity.setId(1L);
		return offsetVersionEntity;
	}
	
	@Test
	public void doCommitTest() {		
		//init();
		Map<Long, OffsetVersionEntity> quMap=new HashMap<Long, OffsetVersionEntity>();
		OffsetVersionEntity offsetVersionEntity=buildOffsetVersionEntity();
		quMap.put(offsetVersionEntity.getId(), offsetVersionEntity);		
		when(queueOffsetService.getOffsetVersion()).thenReturn(quMap);
		
		Map<Long, ConsumerQueueVersionDto> mapAppPolling=new HashMap<Long, ConsumerQueueVersionDto>();
		List<ConsumerQueueVersionDto> queueOffsets = buildQueueOffsets();
		mapAppPolling.put(queueOffsets.get(0).getQueueOffsetId(), queueOffsets.get(0));
		consumerCommitServiceImpl.mapAppPolling.set(mapAppPolling);
		consumerCommitServiceImpl.doCommit();
		verify(queueOffsetService,never()).commitOffset(any(QueueOffsetEntity.class));
		
		ConsumerQueueVersionDto consumerQueueVersionDto=JsonUtil.copy(queueOffsets.get(0),ConsumerQueueVersionDto.class);
		consumerQueueVersionDto.setQueueOffsetId(consumerQueueVersionDto.getQueueOffsetId()+1);
		mapAppPolling.put(consumerQueueVersionDto.getQueueOffsetId(), consumerQueueVersionDto);
		
		consumerCommitServiceImpl.mapAppPolling.set(mapAppPolling);
		consumerCommitServiceImpl.doCommit();
		verify(queueOffsetService,never()).commitOffset(any(QueueOffsetEntity.class));
		
	}
	
	@Test
	public void doCommitOffset1Test() {
		//init();
		when(queueOffsetService.commitOffset(any(QueueOffsetEntity.class))).thenReturn(1);
		ConsumerQueueVersionDto request=buildQueueOffsets().get(0);		
		Map<Long, OffsetVersionEntity> quMap=new HashMap<Long, OffsetVersionEntity>();
		OffsetVersionEntity offsetVersionEntity=buildOffsetVersionEntity();
		offsetVersionEntity.setOffset(request.getOffset()+1);
		quMap.put(offsetVersionEntity.getId(), offsetVersionEntity);			
		consumerCommitServiceImpl.doCommitOffset(request, 1, quMap, 2);	
		
		verify(queueOffsetService,never()).commitOffset(any(QueueOffsetEntity.class));
		
		request.setOffset(offsetVersionEntity.getOffset()+1);
        consumerCommitServiceImpl.doCommitOffset(request, 1, quMap, 2);	
		
		verify(queueOffsetService).commitOffset(any(QueueOffsetEntity.class));
		
		request.setOffset(offsetVersionEntity.getOffset());
		request.setOffsetVersion(offsetVersionEntity.getOffsetVersion()+1);
        consumerCommitServiceImpl.doCommitOffset(request, 1, quMap, 2);	
        verify(queueOffsetService,times(2)).commitOffset(any(QueueOffsetEntity.class));        
        when(queueOffsetService.commitOffset(any(QueueOffsetEntity.class))).thenThrow(new RuntimeException());        
        request.setOffsetVersion(offsetVersionEntity.getOffsetVersion()+1);
        assertEquals(false, consumerCommitServiceImpl.doCommitOffset(request, 1, quMap, 2));
        assertEquals(1, consumerCommitServiceImpl.failMapAppPolling.size());
		
	}
	
	@Test
	public void clearOldDataTest() {
		//init();
		consumerCommitServiceImpl.lastTime=1L;
		consumerCommitServiceImpl.failMapAppPolling.put(1L, new ConsumerQueueVersionDto());
		consumerCommitServiceImpl.clearOldData();
		assertEquals(1, consumerCommitServiceImpl.mapAppPolling.get().size());	
		assertEquals(0, consumerCommitServiceImpl.failMapAppPolling.size());	
		
	}
	
	@Test
	public void otherTest() {
		//init();
		consumerCommitServiceImpl.startBroker();
		consumerCommitServiceImpl.stopBroker();
		consumerCommitServiceImpl.info();
		consumerCommitServiceImpl.getCache();
	}
}
