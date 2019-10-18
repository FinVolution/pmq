package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.LogDto;
import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.LogService;

@Service
public class LogServiceImpl implements LogService {
	private Logger log = LoggerFactory.getLogger(LogServiceImpl.class);
//	@Autowired
//	private TopicService topicService;
//	@Autowired
//	private SoaConfig soaConfig;
//	@Autowired
//	private Environment env;
	private Map<Integer, String> logType = new HashMap<>();
	{
		logType.put(1, "error");
		logType.put(2, "warn");
		logType.put(3, "info");
		logType.put(4, "debug");
	}
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private AuditLogService auditLogService;

	/*
	 * 日志类型，1 表示error，2，表示严重，3，表示info，4，表示debug
	 */
	@Override
	public void addConsumerLog(LogRequest request) {
		if (request == null) {
			return;
		}
		String logContent = getLog(request);
		if (request.getType() >= 3) {
			Map<String, ConsumerGroupEntity> cache = consumerGroupService.getCache();
			if (!StringUtils.isEmpty(request.getConsumerGroupName())
					&& cache.containsKey(request.getConsumerGroupName())) {
				if (cache.get(request.getConsumerGroupName()).getTraceFlag() == 1) {
					log.info(getLog(request));
					// return;
				}
			}
		} else {
			log.info(getLog(request));
		}
		addMsgLog(request, logContent);
	}

	private void addMsgLog(LogRequest request, String logContent) {
		
	}

	private String getLog(LogRequest log) {
		String rs = "consumerGroupName_" + log.getConsumerGroupName() + "_topic_" + log.getTopicName()
				+ "_consumerName_" + log.getConsumerName() + "_action_" + log.getAction() + ",json is "
				+ JsonUtil.toJsonNull(log);
		return rs.replaceAll(" ", "_").replaceAll("\\|", "_").replaceAll("\\.", "_") + log.getMsg();
	}

	@Override
	public void addBrokerLog(LogDto request) {
		if (request.getType() >= 3) {
			Map<String, ConsumerGroupEntity> cache = consumerGroupService.getCache();
			if (!StringUtils.isEmpty(request.getConsumerGroupName())
					&& cache.containsKey(request.getConsumerGroupName())) {
				if (cache.get(request.getConsumerGroupName()).getTraceFlag() == 1) {
					log.info(getLog(request));
					return;
				}
			}
		} else if (request.getType() == 1) {
			log.error(getLog(request), request.getThrowable());
		} else if (request.getType() == 2) {
			log.warn(getLog(request));
		}
	}

	@Override
	public void addOpLog(OpLogRequest request) {
		if (request == null || StringUtils.isEmpty(request.getConsumerGroupName())) {
			return;
		}
		Map<String, ConsumerGroupEntity> cache = consumerGroupService.getCache();
		if (!cache.containsKey(request.getConsumerGroupName())) {
			return;
		}
		AuditLogEntity auditLog = new AuditLogEntity();
		auditLog.setContent(request.getContent());
		auditLog.setTbName(ConsumerGroupEntity.TABLE_NAME);
		auditLog.setRefId(cache.get(request.getConsumerGroupName()).getId());
		auditLog.setInsertBy(request.getConsumerName());
		auditLogService.insert(auditLog);
	}
}
