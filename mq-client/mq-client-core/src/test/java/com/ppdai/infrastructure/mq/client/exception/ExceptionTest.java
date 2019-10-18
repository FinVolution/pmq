package com.ppdai.infrastructure.mq.client.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExceptionTest {

	@Test
	public void test() {
		MqNotInitException mqNotInitException = new MqNotInitException();
		Exceed20Exception mqExceed20Exception = new Exceed20Exception("tttt");
		ContentExceed65535Exception contentExceed65535Exception = new ContentExceed65535Exception();
	}
}
