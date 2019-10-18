package com.ppdai.infrastructure.mq.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.ApplicationContext;

@RunWith(JUnit4.class)
public class MqSpringUtilTest {
	@Test
	public void test() {
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		MqSpringUtil mqSpringUtil = new MqSpringUtil();
		mqSpringUtil.setApplicationContext(applicationContext);
		Object rs = new Object();
		when(applicationContext.getBean("a")).thenReturn(rs);
		assertEquals(rs, MqSpringUtil.getBean("a"));

		when(applicationContext.getBean("b")).thenThrow(new RuntimeException("test"));
		assertEquals(null, MqSpringUtil.getBean("b"));
		
		Test1 test1 = new Test1();
		when(applicationContext.getBean(eq(Test1.class))).thenReturn(test1);
		assertEquals(test1, MqSpringUtil.getBean(Test1.class));
		
		when(applicationContext.getBean(eq(Test2.class))).thenThrow(new RuntimeException("test"));
		assertEquals(null, MqSpringUtil.getBean(Test2.class));
		
		Map<String,Test1> map=new HashMap<String, MqSpringUtilTest.Test1>();
		when(applicationContext.getBeansOfType(eq(Test1.class))).thenReturn(map);
		assertEquals(map, MqSpringUtil.getBeans(Test1.class));
		
		when(applicationContext.getBeansOfType(eq(Test2.class))).thenThrow(new RuntimeException("test"));
		assertEquals(null, MqSpringUtil.getBeans(Test2.class));
		
		
		
		Test1 rs1 = new Test1();
		when(applicationContext.getBean("a",Test1.class)).thenReturn(rs1);
		assertEquals(rs1, MqSpringUtil.getBean("a",Test1.class));

		when(applicationContext.getBean("b",Test1.class)).thenThrow(new RuntimeException("test"));
		assertEquals(null, MqSpringUtil.getBean("b",Test1.class));
	}

	class Test1 {

	}

	class Test2 {

	}
}
