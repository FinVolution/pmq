package com.ppdai.infrastructure.mq.biz.service.impl;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.AbstractTest;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.dal.meta.MqLockRepository;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.common.DbService;

@RunWith(JUnit4.class)
public class MqLockServiceImplTest extends AbstractTest {
	private SoaConfig soaConfig;
    private MqLockRepository mqLockRepository;
    private DbService dbService;
    private EmailUtil emailUtil;
    private MqLockServiceImpl mqLockServiceImpl;
    
    @Before
	public void init() {
    	
		mqLockRepository = mock(MqLockRepository.class);
		mqLockServiceImpl = new MqLockServiceImpl(mqLockRepository);
		object = mqLockServiceImpl;
		dbService = mockAndSet(DbService.class);
		super.init();
	}
}
