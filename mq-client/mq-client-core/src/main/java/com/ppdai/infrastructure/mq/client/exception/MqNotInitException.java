package com.ppdai.infrastructure.mq.client.exception;

@SuppressWarnings("serial")
public class MqNotInitException extends Exception{
	public MqNotInitException() {
		super("mqbroker地址未初始化，mq3.0 springboot 客户端中，默认是在BeanPostProcessor事件中完成初始化，请确保调用顺序！");
	}
}
