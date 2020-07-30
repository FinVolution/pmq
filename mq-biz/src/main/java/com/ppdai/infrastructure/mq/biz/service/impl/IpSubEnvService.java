package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.IIpSubEnvService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;

@Service
public class IpSubEnvService implements IIpSubEnvService {
	private Logger log = LoggerFactory.getLogger(IpSubEnvService.class);
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private ConsumerGroupService consumerGroupService;




	/**
	 * 根据消费者组名，获取该组的原始组对应的子环境有多少(只要消费者是注册状态的)
	 * 
	 * @param consumerGroupName
	 * @return
	 */
	@Override
	public Set<String> getSubEnvs(String consumerGroupName) {
		Set<String> subEnvSet = new HashSet<>();
		Map<String, Set<String>> consumerGroupsetMap = queueOffsetService.getSubEnvs();
		ConsumerGroupEntity consumerGroup = consumerGroupService.getCache().get(consumerGroupName);
		if (consumerGroup != null) {
			Set<String> subEnvs = consumerGroupsetMap.get(consumerGroup.getOriginName());
			if (subEnvs != null) {
				return subEnvs;
			}
		}
		return subEnvSet;
	}

}
