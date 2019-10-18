package com.ppdai.infrastructure.mq.client.stat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MqClientStatControllerTest {

	@Test
	public void test() {
		MqClientStatController mqClientStatController=new MqClientStatController();
		mqClientStatController.data();
		mqClientStatController.dm();
		mqClientStatController.hs();
		mqClientStatController.th();
		mqClientStatController.trace();
	}
}
