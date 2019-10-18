package com.ppdai.infrastructure.mq.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ppdai.infrastructure.mq.client.config.ClientConfighelperTest;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVoTest;
import com.ppdai.infrastructure.mq.client.core.impl.ConsumerPollingServiceTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqBrokerUrlRefreshServiceTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqCheckServiceTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqGroupExcutorServiceTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqHeartbeatServiceTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqMeticReporterServiceTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqMetricReporterTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqQueueExcutorServiceTest;
import com.ppdai.infrastructure.mq.client.core.impl.MqTopicQueueRefreshServiceTest;
import com.ppdai.infrastructure.mq.client.exception.ExceptionTest;
import com.ppdai.infrastructure.mq.client.factory.MqFactoryTest;
import com.ppdai.infrastructure.mq.client.resource.MqResourceTest;

@RunWith(Suite.class)
@SuiteClasses({ ClientConfighelperTest.class, ConsumerPollingServiceTest.class, MqBrokerUrlRefreshServiceTest.class,
		MqCheckServiceTest.class, MqGroupExcutorServiceTest.class, MqHeartbeatServiceTest.class,
		MqQueueExcutorServiceTest.class, MqMetricReporterTest.class, MqMeticReporterServiceTest.class,
		MqTopicQueueRefreshServiceTest.class, ConsumerGroupVoTest.class, MqFactoryTest.class, ExceptionTest.class,
		MqResourceTest.class, MqClientTest.class, MessageUtilTest.class, MqConfigTest.class })
public class AllClientCoreTests {

}
