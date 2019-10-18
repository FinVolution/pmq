package com.ppdai.infrastructure.mq.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ppdai.infrastructure.mq.biz.AllCoreTest;
import com.ppdai.infrastructure.mq.client.stat.MqFilterTest;
import com.ppdai.infrastructure.mq.client.stat.MqHandlerTest;
import com.ppdai.infrastructure.mq.client.stat.StatServiceTest;

@RunWith(Suite.class)
@SuiteClasses({AllCoreTest.class,AllSrpingTests.class,AllClientCoreTests.class, MqFilterTest.class, MqHandlerTest.class, StatServiceTest.class, MqBootstrapScanConfigTest.class})
public class AllTests {

}
