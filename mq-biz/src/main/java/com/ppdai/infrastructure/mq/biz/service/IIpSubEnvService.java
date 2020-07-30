package com.ppdai.infrastructure.mq.biz.service;

import java.util.Set;

public interface IIpSubEnvService {	
	Set<String> getSubEnvs(String consumerGroupName);
}
