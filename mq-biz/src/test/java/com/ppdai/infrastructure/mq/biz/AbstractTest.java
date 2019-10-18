package com.ppdai.infrastructure.mq.biz;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;

public abstract class AbstractTest {

	public Object object;
	public SoaConfig soaConfig;
	//public Environment env;
	private Map<String, String> propertyMap=new ConcurrentHashMap<>();
	public void init() {
		soaConfig = new SoaConfig();
		Environment env = mock(Environment.class);
		ReflectionTestUtils.setField(soaConfig, "env", env);
		when(env.getProperty(anyString(), anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				if(propertyMap.containsKey(args[0])) {
					return propertyMap.get(args[0]);
				}
				return (String) args[1];
			}
		});		
	}
	public void setProperty(String key,String value) {
		propertyMap.put(key, value);
	}
	public void clear(String key) {
		propertyMap.remove(key);
	}
	public int search(String str, String strRes) {
		int n = 0;// 计数器
		int index = 0;// 指定字符的长度
		index = str.indexOf(strRes);
		while (index != -1) {
			n++;
			index = str.indexOf(strRes, index + 1);
		}
		return n;
	}
	
	public <T> T mockAndSet(Class<T> t) {
		T t1=mock(t);
		String name=t.getName().substring(t.getName().lastIndexOf('.')+1);
		ReflectionTestUtils.setField(object, (name.toCharArray()[0]+"").toLowerCase()+name.substring(1),
				t1);
		return t1;
	}
}
