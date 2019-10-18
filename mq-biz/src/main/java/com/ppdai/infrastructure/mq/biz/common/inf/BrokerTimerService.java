package com.ppdai.infrastructure.mq.biz.common.inf;

public interface BrokerTimerService{
	void startBroker();
	void stopBroker();
	String info();
}
