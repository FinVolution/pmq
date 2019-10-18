package com.ppdai.infrastructure.mq.client;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;

@RunWith(JUnit4.class)
public class MessageUtilTest {
	@Test
	public void messageUtilTest() {
		MessageUtil.addCatChain(Arrays.asList(new MessageDto()));
		MessageUtil.checkMessageExceed65535(null);
	}
}
