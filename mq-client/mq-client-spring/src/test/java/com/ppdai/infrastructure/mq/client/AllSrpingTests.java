package com.ppdai.infrastructure.mq.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ppdai.infrastructure.mq.client.bootstrap.MqClientBootstrapListenerTest;
import com.ppdai.infrastructure.mq.client.bootstrap.MqClientShutdownListenerTest;
import com.ppdai.infrastructure.mq.client.bootstrap.MqClientStartupTest;
import com.ppdai.infrastructure.mq.client.bootstrap.MqEnvPropTest;
import com.ppdai.infrastructure.mq.client.stat.MqClientStatControllerTest;

@RunWith(Suite.class)
@SuiteClasses({ MqClientBootstrapListenerTest.class, MqClientStartupTest.class, MqClientShutdownListenerTest.class,
		MqSpringUtilTest.class ,MqEnvPropTest.class,MqClientStatControllerTest.class})
public class AllSrpingTests {

}
