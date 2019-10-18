package com.ppdai.infrastructure.mq.biz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ppdai.infrastructure.mq.biz.service.impl.AuditLogServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerCommitServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupCheckServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupConsumerCheckServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupTopicCheckServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerGroupTopicServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.ConsumerServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.DbNodeServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.EmailServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.LogServiceImplTest;
import com.ppdai.infrastructure.mq.biz.service.impl.Message01ServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses({ AuditLogServiceImplTest.class, ConsumerCommitServiceImplTest.class,
		ConsumerGroupCheckServiceImplTest.class, ConsumerGroupConsumerCheckServiceImplTest.class,
		ConsumerGroupServiceImplTest.class, ConsumerGroupTopicCheckServiceImplTest.class,
		ConsumerGroupTopicServiceImplTest.class, ConsumerGroupTopicServiceImplTest.class, ConsumerServiceImplTest.class,
		DbNodeServiceImplTest.class, DbNodeServiceImplTest.class, EmailServiceImplTest.class, LogServiceImplTest.class,
		Message01ServiceImplTest.class })
public class AllBizTests {

}
