package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.dto.LogDto;
import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;

@RunWith(JUnit4.class)
public class LogServiceImplTest extends AbstractTest {	
	private ConsumerGroupService consumerGroupService;
	private AuditLogService auditLogService;	
	private LogServiceImpl logServiceImpl;
	
	@Before
	public void init() {
		logServiceImpl = new LogServiceImpl();
		object = logServiceImpl;
		auditLogService = mockAndSet(AuditLogService.class);
		consumerGroupService=mockAndSet(ConsumerGroupService.class);		
		super.init();
	}
	
	@Test
	public void addConsumerLogTest() {
		LogRequest request=new LogRequest();
		logServiceImpl.addConsumerLog(null);
		request.setConsumerGroupName("test");
		request.setType(1);
		logServiceImpl.addConsumerLog(request);
		
		request.setType(3);
		Map<String, ConsumerGroupEntity> map =new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setTraceFlag(1);
		map.put(request.getConsumerGroupName(), consumerGroupEntity);
	    when(consumerGroupService.getCache()).thenReturn(map);
	    logServiceImpl.addConsumerLog(request);
	}
	
	@Test
	public void addBrokerLogTest() {		
		LogDto logDto=new LogDto();
		logDto.setConsumerGroupName("Test");
		logDto.setType(1);
		Map<String, ConsumerGroupEntity> map =new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setTraceFlag(1);
		map.put(logDto.getConsumerGroupName(), consumerGroupEntity);
	    when(consumerGroupService.getCache()).thenReturn(map);
	    
	    logServiceImpl.addBrokerLog(logDto);
	    logDto.setType(2);
	    logServiceImpl.addBrokerLog(logDto);
	    logDto.setType(3);
	    logServiceImpl.addBrokerLog(logDto);
	}
	
	@Test
	public void addOpLogTest() {
		OpLogRequest logRequest=new OpLogRequest();
		logServiceImpl.addOpLog(logRequest);
		verify(auditLogService,times(0)).insert(any(AuditLogEntity.class));
		logServiceImpl.addOpLog(null);
		verify(auditLogService,times(0)).insert(any(AuditLogEntity.class));
		logRequest.setConsumerGroupName("test");
		logServiceImpl.addOpLog(logRequest);
		verify(auditLogService,times(0)).insert(any(AuditLogEntity.class));
		
		Map<String, ConsumerGroupEntity> map =new HashMap<String, ConsumerGroupEntity>();
		ConsumerGroupEntity consumerGroupEntity=new ConsumerGroupEntity();
		consumerGroupEntity.setTraceFlag(1);
		map.put(logRequest.getConsumerGroupName(), consumerGroupEntity);
	    when(consumerGroupService.getCache()).thenReturn(map);
	    logServiceImpl.addOpLog(logRequest);
		verify(auditLogService,times(1)).insert(any(AuditLogEntity.class));
	    
	}
}
