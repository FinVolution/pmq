package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.dal.meta.AuditLogRepository;
import com.ppdai.infrastructure.mq.biz.dto.request.AuditLogRequest;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;

@RunWith(JUnit4.class)
public class AuditLogServiceImplTest {
	@Test
	public void insertTest() {
		AuditLogServiceImpl auditLogServiceImpl=new AuditLogServiceImpl();
		UserInfoHolder userInfoHolder=mock(UserInfoHolder.class);
		AuditLogRepository auditLogRepository=mock(AuditLogRepository.class);
		ReflectionTestUtils.setField(auditLogServiceImpl, "userInfoHolder", userInfoHolder);
		ReflectionTestUtils.setField(auditLogServiceImpl, "auditLogRepository", auditLogRepository);
		auditLogServiceImpl.init();
		AuditLogEntity entity=new AuditLogEntity();
		entity.setContent("test");
		entity.setId(1L);
		entity.setRefId(1L);
		auditLogServiceImpl.insert(entity);
		verify(auditLogRepository).insert(entity);
	}
	
	@Test
	public void getMindIdTest() {
		AuditLogServiceImpl auditLogServiceImpl=new AuditLogServiceImpl();
		UserInfoHolder userInfoHolder=mock(UserInfoHolder.class);
		AuditLogRepository auditLogRepository=mock(AuditLogRepository.class);
		ReflectionTestUtils.setField(auditLogServiceImpl, "userInfoHolder", userInfoHolder);
		ReflectionTestUtils.setField(auditLogServiceImpl, "auditLogRepository", auditLogRepository);
		auditLogServiceImpl.init();	
		when(auditLogRepository.getMinId()).thenReturn(null);
		assertEquals(0L,  auditLogServiceImpl.getMindId());		
		when(auditLogRepository.getMinId()).thenReturn(1L);
		assertEquals(1L,  auditLogServiceImpl.getMindId());	
	}
	
	@Test
	public void deleteByTest() {
		AuditLogServiceImpl auditLogServiceImpl=new AuditLogServiceImpl();
		UserInfoHolder userInfoHolder=mock(UserInfoHolder.class);
		AuditLogRepository auditLogRepository=mock(AuditLogRepository.class);
		ReflectionTestUtils.setField(auditLogServiceImpl, "userInfoHolder", userInfoHolder);
		ReflectionTestUtils.setField(auditLogServiceImpl, "auditLogRepository", auditLogRepository);
		auditLogServiceImpl.init();	
		doNothing().when(auditLogRepository).delete(0);
		auditLogServiceImpl.delete(0);
		verify(auditLogRepository).delete(anyLong());
	}
	
	@Test
	public void logListTest() {
		AuditLogServiceImpl auditLogServiceImpl=new AuditLogServiceImpl();
		UserInfoHolder userInfoHolder=mock(UserInfoHolder.class);
		AuditLogRepository auditLogRepository=mock(AuditLogRepository.class);
		ReflectionTestUtils.setField(auditLogServiceImpl, "userInfoHolder", userInfoHolder);
		ReflectionTestUtils.setField(auditLogServiceImpl, "auditLogRepository", auditLogRepository);
		auditLogServiceImpl.init();	
		AuditLogRequest  request=new AuditLogRequest();
		request.setTbName("test");
		request.setContent("tet");
		request.setLimit("1");
		request.setPage("1");
		request.setRefId("1");
		when(auditLogRepository.count(any(Map.class))).thenReturn(0L);
		assertEquals(0L, (long)(auditLogServiceImpl.logList(request).getCount()));
		when(auditLogRepository.count(any(Map.class))).thenReturn(1L);
		assertEquals(1L, (long)(auditLogServiceImpl.logList(request).getCount()));
		
	}
}
