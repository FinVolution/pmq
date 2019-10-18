package com.ppdai.infrastructure.mq.biz.common.inf;

public interface TimerService {
	void start();
	void stop();
	String info();
}
