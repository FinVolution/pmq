package com.ppdai.infrastructure.mq.biz.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TopicUtilTest {

	@Test
	public void getFailTopicNameTest() {
		assertEquals("1_2_fail", TopicUtil.getFailTopicName("1", "2"));
	}
}
