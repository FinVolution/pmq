package com.ppdai.infrastructure.mq.biz;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ppdai.infrastructure.mq.biz.common.util.ClassLoaderUtilTest;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerGroupUtilTest;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClientTest;
import com.ppdai.infrastructure.mq.biz.common.util.TopicUtilTest;
import com.ppdai.infrastructure.mq.biz.common.util.UtilTest;

@RunWith(Suite.class)
@SuiteClasses({ ClassLoaderUtilTest.class, ConsumerGroupUtilTest.class, HttpClientTest.class, TopicUtilTest.class,
		UtilTest.class })
public class AllCoreTest {

}
