package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerGroupUtil;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerRepository;
import com.ppdai.infrastructure.mq.biz.dto.LogDto;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.event.PartitionInfo;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupConsumerService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.EmailService;
import com.ppdai.infrastructure.mq.biz.service.LogService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;

/**
 * @author dal-generator
 */
@Service
public class ConsumerServiceImpl extends AbstractBaseService<ConsumerEntity> implements ConsumerService {
	private Logger log = LoggerFactory.getLogger(ConsumerServiceImpl.class);
	@Autowired
	private ConsumerRepository consumerRepository;

	@Autowired
	private ConsumerGroupConsumerService consumerGroupConsumerService;

	@Autowired
	private ConsumerGroupTopicService consumerGroupTopicService;

	@Autowired
	private ConsumerGroupService consumerGroupService;

	@Autowired
	private QueueOffsetService queueOffsetService;

	@Autowired
	private TopicService topicService;

	@Autowired
	private QueueService queueService;

	@Autowired
	private DbNodeService dbNodeService;

	@Autowired
	private Message01Service message01Service;

	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private LogService logService;

	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private UserInfoHolder userInfoHolder;
	@Autowired
	private AuditLogService auditLogService;

	private AtomicReference<Map<String, AtomicInteger>> counter = new AtomicReference<Map<String, AtomicInteger>>(
			new ConcurrentHashMap<>(1000));
	// 记录topic和dbnode 失败的时间
	protected Map<String, Long> dbFailMap = new ConcurrentHashMap<>();

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(50), SoaThreadFactory.create("ConsumerServiceImpl", true),
			new ThreadPoolExecutor.DiscardOldestPolicy());

	@PostConstruct
	protected void init() {
		super.setBaseRepository(consumerRepository);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				counter.set(new ConcurrentHashMap<>(1000));
				Util.sleep(30 * 1000);
			}
		});
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ConsumerRegisterResponse register(ConsumerRegisterRequest request) {
		ConsumerRegisterResponse response = new ConsumerRegisterResponse();
		response.setSuc(true);
		checkVaild(request, response);
		if (!response.isSuc()) {
			return response;
		}
		addRegisterLog(request);
		ConsumerEntity consumerEntity = doRegisterConsumer(request);
		response.setId(consumerEntity.getId());
		if (soaConfig.getSdkVersion().compareTo(request.getSdkVersion()) > 0) {
			response.setMsg(
					"当前mq3客户端的版本已经落后了，最新版本为:" + soaConfig.getSdkVersion() + ",当前版本为:" + request.getSdkVersion());
		}
		return response;
	}

	private void addRegisterLog(ConsumerRegisterRequest request) {
		LogDto logDto = new LogDto();
		logDto.setAction("is_register");
		logDto.setConsumerName(request.getName());
		logDto.setType(MqConst.INFO);
		logService.addBrokerLog(logDto);
	}

	private ConsumerEntity doRegisterConsumer(ConsumerRegisterRequest request) {
		ConsumerEntity consumerEntity = new ConsumerEntity();
		try {
			consumerEntity.setName(request.getName());
			consumerEntity.setSdkVersion(request.getSdkVersion());
			consumerEntity.setLan(request.getLan());
			consumerEntity.setIp(request.getClientIp());
			consumerEntity.setHeartTime(new Date());
			consumerRepository.register(consumerEntity);
		} catch (Exception e) {
			Map<String, Object> condition = new HashMap<>();
			condition.put(ConsumerEntity.FdName, request.getName());
			consumerEntity = consumerRepository.get(condition);
		}
		return consumerEntity;
	}

	protected Map<String, ConsumerGroupEntity> checkTopic(ConsumerGroupRegisterRequest request,
			ConsumerGroupRegisterResponse response) {

		Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService
				.getByNames(new ArrayList<>(request.getConsumerGroupNames().keySet()));
		if (consumerGroupMap.size() == 0) {
			response.setSuc(false);
			response.setMsg(String.join(".", request.getConsumerGroupNames().keySet()) + "不存在");
			return consumerGroupMap;
		}
		List<AuditLogEntity> auditLogs = new ArrayList<AuditLogEntity>();
		for (String name : request.getConsumerGroupNames().keySet()) {
			//防止出现数据查询时，会忽略大小写，所以但是使用的时候 是需要区分大小写的，所以需要二次判断
			if (!consumerGroupMap.containsKey(name)) {
				response.setSuc(false);
				response.setMsg("consumergroup_" + name + "不存在");
				return consumerGroupMap;
			} else {
				ConsumerGroupEntity entity = consumerGroupMap.get(name);
				String topicNames = "," + entity.getTopicNames() + ",";
				List<String> topics = request.getConsumerGroupNames().get(name);
				String topicRs = "";
				for (String topicName : topics) {
					if (topicNames.indexOf("," + topicName + ",") == -1) {
						// response.setSuc(false);
						// response.setMsg(name + "下," + topicName + "不存在");
						topicRs += "客户端中，消费者组:" + name + "与topic:" + topicName + "的订阅关系，在后台管理界面中不存在。会出现客户端中此topic："
								+ topicName + "没有消息被消费！";
						// return consumerGroupMap;
					} else {
						while (topicNames.indexOf("," + topicName + ",") != -1) {
							topicNames = topicNames.replaceFirst("," + topicName + ",", ",");
						}
					}
				}
				if (!StringUtils.isEmpty(topicNames.replaceAll(",", ""))) {
					// response.setSuc(false);
					// response.setMsg(entity.getName() + "下，" + topicNames + "没有被订阅！");
					topicRs += "客户端中，消费者组" + name + "下,topic：" + topicNames + "在管理界面上订阅了，但是在客户端没有被订阅消费，请注意！会出现客户端topic："
							+ topicNames + "的消息不会被消费,产生堆积。";
					// return consumerGroupMap;
				}
				if (!StringUtils.isEmpty(topicRs)) {
					AuditLogEntity auditLog = new AuditLogEntity();
					auditLog.setTbName(ConsumerGroupEntity.TABLE_NAME);
					if (!entity.getName().contentEquals(entity.getOriginName())) {
						auditLog.setRefId(consumerGroupService.getCache().get(entity.getOriginName()).getId());
					} else {
						auditLog.setRefId(entity.getId());
					}
					auditLog.setInsertBy(request.getClientIp());
					auditLog.setContent(topicRs);
					auditLogs.add(auditLog);
				}
			}
		}
		if (auditLogs.size() > 0) {
			auditLogService.insertBatch(auditLogs);
		}
		return consumerGroupMap;
	}

	protected void checkVaild(ConsumerRegisterRequest request, ConsumerRegisterResponse response) {
		if (request == null) {
			response.setSuc(false);
			response.setMsg("ConsumerRegisterRequest不能为空！");
			return;
		}
		if (StringUtils.isEmpty(request.getName())) {
			response.setSuc(false);
			response.setMsg("ConsumerName不能为空！");
			return;
		}
		if (StringUtils.isEmpty(request.getClientIp())) {
			response.setSuc(false);
			response.setMsg("Ip不能为空！");
			return;
		}
		if (StringUtils.isEmpty(request.getSdkVersion())) {
			response.setSuc(false);
			response.setMsg("SdkVersion不能为空！");
			return;
		}
	}

	@Override
	// @Transactional(rollbackFor = Exception.class)
	public ConsumerDeRegisterResponse deRegister(ConsumerDeRegisterRequest deRegisterRequest) {
		ConsumerDeRegisterResponse response = new ConsumerDeRegisterResponse();
		response.setSuc(true);
		if (deRegisterRequest == null || deRegisterRequest.getId() == 0) {
			response.setSuc(false);
			response.setMsg("ConsumerDeRegisterRequest id 不能为空！");
			return response;
		}
		doDeleteConsumer(Arrays.asList(deRegisterRequest.getId()), 1);
		return response;
	}

	@Override
	public void heartbeat(List<Long> ids) {
		if (!CollectionUtils.isEmpty(ids)) {
			consumerRepository.heartbeat(ids);
		}
	}

	@Override
	public List<ConsumerGroupConsumerEntity> getConsumerGroupByConsumerIds(List<Long> consumerIds) {
		if (CollectionUtils.isEmpty(consumerIds)) {
			return new ArrayList<>();
		}
		return consumerGroupConsumerService.getByConsumerIds(consumerIds);
	}

	@Override
	public List<ConsumerGroupConsumerEntity> getConsumerGroupByConsumerGroupIds(List<Long> consumerGroupIds) {
		if (CollectionUtils.isEmpty(consumerGroupIds)) {
			return new ArrayList<>();
		}
		// TODO Auto-generated method stub
		return consumerGroupConsumerService.getByConsumerGroupIds(consumerGroupIds);
	}

	// private void deleteConsumerGroupConsumerByConsumerId(long consumerId) {
	// consumerGroupConsumerService.deleteByConsumerId(consumerId);
	// }

	@Override
	public ConsumerGroupRegisterResponse registerConsumerGroup(ConsumerGroupRegisterRequest request) {
		ConsumerGroupRegisterResponse response = new ConsumerGroupRegisterResponse();
		response.setSuc(true);
		if(request==null) {
			response.setSuc(false);
			response.setMsg("参数不能为空！");
			return response;
		}
		ConsumerEntity consumerEntity = get(request.getConsumerId());
		if (consumerEntity == null) {
			response.setSuc(false);
			response.setMsg("ConsumerId_" + request.getConsumerId() + "不存在！");
			return response;
		}
		response.setBroadcastConsumerGroupName(new HashMap<>());
		// 检查广播模式
		checkBroadcast(request, response);
		if(!response.isSuc()) {
			return response;
		}
		doRegisterConsumerGroup(request, response, consumerEntity);
		if (!response.isSuc()) {
			addRegisterConsumerGroupLog(request, response);
		}
		return response;
	}

	@Transactional(rollbackFor = Exception.class)
	protected void checkBroadcast(ConsumerGroupRegisterRequest request, ConsumerGroupRegisterResponse response) {
		Map<String, ConsumerGroupEntity> map = consumerGroupService.getCache();
		if (request == null) {
			response.setSuc(false);
			response.setMsg("参数不能为空！");
			return;
		}
		if (request.getConsumerGroupNames() == null || request.getConsumerGroupNames().size() == 0) {
			response.setSuc(false);
			response.setMsg("消费者组不能为空！");
			return;
		}
		// 后续有删除操作，此处注意ConcurrentModificationException 异常
		List<String> consumerGroupNames = new ArrayList<>(request.getConsumerGroupNames().keySet());
		for (String name : consumerGroupNames) {
			if (!map.containsKey(name)) {
				response.setSuc(false);
				response.setMsg("消费者组" + name + "不存在！");
				return;
			}
		}
		createBroadcast(request, consumerGroupNames, map, response);

	}

	protected void createBroadcast(ConsumerGroupRegisterRequest request, List<String> consumerGroupNames,
			Map<String, ConsumerGroupEntity> map, ConsumerGroupRegisterResponse response) {
		Map<Long, Map<String, ConsumerGroupTopicEntity>> ctMap = consumerGroupTopicService.getCache();
		for (String name : consumerGroupNames) {
			ConsumerGroupEntity consumerGroupEntity = map.get(name);
			if (consumerGroupEntity.getMode() == 2) {
				// 创建消费者组
				ConsumerGroupEntity consumerGroupEntityNew = JsonUtil.copy(consumerGroupEntity,
						ConsumerGroupEntity.class);
				consumerGroupEntityNew.setName(ConsumerGroupUtil.getBroadcastConsumerName(consumerGroupEntity.getName(),
						request.getClientIp(), request.getConsumerId()));
				consumerGroupEntityNew.setId(0);
				consumerGroupService.insert(consumerGroupEntityNew);
				consumerGroupService.updateCache();
				Map<String, ConsumerGroupTopicEntity> consumerTopics = ctMap.get(consumerGroupEntity.getId());
				if (consumerTopics != null) {
					for (Map.Entry<String, ConsumerGroupTopicEntity> entry : consumerTopics.entrySet()) {
						if (entry.getValue().getTopicType() == 1) {
							ConsumerGroupTopicCreateRequest request2 = new ConsumerGroupTopicCreateRequest();
							request2.setAlarmEmails(entry.getValue().getAlarmEmails());
							request2.setConsumerBatchSize(entry.getValue().getConsumerBatchSize());
							request2.setConsumerGroupId(consumerGroupEntityNew.getId());
							request2.setConsumerGroupName(consumerGroupEntityNew.getName());
							request2.setDelayProcessTime(entry.getValue().getDelayProcessTime());
							request2.setDelayPullTime(entry.getValue().getMaxPullTime());
							request2.setMaxLag(entry.getValue().getMaxLag());
							request2.setOriginTopicName(entry.getValue().getOriginTopicName());
							request2.setPullBatchSize(entry.getValue().getPullBatchSize());
							request2.setRetryCount(entry.getValue().getRetryCount());
							request2.setTag(entry.getValue().getTag());
							request2.setThreadSize(entry.getValue().getThreadSize());
							request2.setTopicId(entry.getValue().getTopicId());
							request2.setTopicName(entry.getValue().getTopicName());
							request2.setTopicType(entry.getValue().getTopicType());
							request2.setTimeOut(entry.getValue().getTimeOut());
							consumerGroupTopicService.subscribe(request2);
						}
					}
				}
				request.getConsumerGroupNames().put(consumerGroupEntityNew.getName(),
						request.getConsumerGroupNames().get(name));
				// 注意此时容易出现 ConcurrentModificationException 异常
				request.getConsumerGroupNames().remove(name);
				response.getBroadcastConsumerGroupName().put(name, consumerGroupEntityNew.getName());
			}
		}
	}

	protected void addRegisterConsumerGroupLog(ConsumerGroupRegisterRequest request,
			ConsumerGroupRegisterResponse response) {
		String json = JsonUtil.toJsonNull(request);
		if (request != null && request.getConsumerGroupNames() != null) {
			List<AuditLogEntity> auditLogs = new ArrayList<>(request.getConsumerGroupNames().size());
			Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
			request.getConsumerGroupNames().keySet().forEach(t1 -> {
				ConsumerGroupEntity temp = consumerGroupMap.get(t1);
				if (temp != null) {
					AuditLogEntity auditLog = new AuditLogEntity();
					auditLog.setContent("注册失败！入参是：" + json + ",原因是:" + response.getMsg());
					auditLog.setTbName(ConsumerGroupEntity.TABLE_NAME);
					auditLog.setRefId(temp.getId());
					auditLogs.add(auditLog);
				}
			});
			auditLogService.insertBatch(auditLogs);
		} else {
			AuditLogEntity auditLog = new AuditLogEntity();
			auditLog.setContent("注册失败！入参是：" + json + ",原因是:" + response.getMsg());
			auditLog.setTbName(ConsumerGroupEntity.TABLE_NAME);
			auditLog.setRefId(0);
			auditLogService.insert(auditLog);
		}
	}

	protected void doRegisterConsumerGroup(ConsumerGroupRegisterRequest request, ConsumerGroupRegisterResponse response,
			ConsumerEntity consumerEntity) {
		response.setSuc(true);
		Map<String, ConsumerGroupEntity> consumerGroupMap = checkTopic(request, response);
		if (!response.isSuc()) {
			return;
		}

		List<ConsumerGroupConsumerEntity> consumerGroupConsumerEntities = new ArrayList<>();
		// consumergroupid 列表
		List<Long> ids = new ArrayList<>();
		List<AuditLogEntity> auditLogs = new ArrayList<>();
		request.getConsumerGroupNames().keySet().forEach(t1 -> {
			if (("," + consumerEntity.getConsumerGroupNames() + ",").indexOf("," + t1 + ",") == -1) {
				if (StringUtils.isEmpty(consumerEntity.getConsumerGroupNames())) {
					consumerEntity.setConsumerGroupNames(t1);
				} else {
					consumerEntity.setConsumerGroupNames(consumerEntity.getConsumerGroupNames() + "," + t1);
				}
			}
			ConsumerGroupConsumerEntity consumerGroupConsumerEntity = new ConsumerGroupConsumerEntity();
			consumerGroupConsumerEntity.setConsumerGroupId(consumerGroupMap.get(t1).getId());
			consumerGroupConsumerEntity.setConsumerId(request.getConsumerId());
			consumerGroupConsumerEntity.setConsumerName(request.getConsumerName());
			consumerGroupConsumerEntity.setIp(request.getClientIp());
			consumerGroupConsumerEntities.add(consumerGroupConsumerEntity);

			AuditLogEntity auditLog = new AuditLogEntity();
			auditLog.setTbName(ConsumerGroupEntity.TABLE_NAME);
			auditLog.setRefId(consumerGroupConsumerEntity.getConsumerGroupId());
			auditLog.setInsertBy(request.getClientIp());
			if (checkInBlackIp(consumerEntity, t1)) {
				auditLog.setContent(request.getConsumerName() + "订阅了" + t1 + ",但是客户端ip在黑名单("
						+ consumerGroupMap.get(t1).getIpBlackList() + ")中所以不触发重平衡！");
				// ids.add(consumerGroupConsumerEntity.getConsumerGroupId());
			} else {
				auditLog.setContent(request.getConsumerName() + "订阅了" + t1 + ",所以需要重平衡！");
				ids.add(consumerGroupConsumerEntity.getConsumerGroupId());
			}
			auditLogs.add(auditLog);
			addRegisterConsumerGroupLog(consumerGroupConsumerEntity, t1, request.getConsumerGroupNames().get(t1));
		});
		auditLogService.insertBatch(auditLogs);
		doRegisterConsumerGroup(consumerEntity, consumerGroupConsumerEntities, ids);

	}

	@Transactional(rollbackFor = Exception.class)
	private void doRegisterConsumerGroup(ConsumerEntity consumerEntity,
			List<ConsumerGroupConsumerEntity> consumerGroupConsumerEntities, List<Long> ids) {
		update(consumerEntity);
		registConsumerGroupConsumer(consumerGroupConsumerEntities);
		consumerGroupService.notifyRb(ids);
	}

	protected boolean checkInBlackIp(ConsumerEntity consumerEntity, String consumerGroupName) {
		ConsumerGroupEntity consumerGroupEntity = consumerGroupService.getCache().get(consumerGroupName);
		if (consumerGroupEntity != null && !Util.isEmpty(consumerEntity.getIp())) {
			if ((consumerGroupEntity.getIpBlackList() + "").indexOf(consumerEntity.getIp()) != -1) {
				return true;
			}
		}
		return false;
	}

	private void addRegisterConsumerGroupLog(ConsumerGroupConsumerEntity t1, String consumerGroupName,
			List<String> topics) {
		LogDto logDto = new LogDto();
		logDto.setAction("is_RegisterConsumerGroup");
		logDto.setConsumerName(t1.getConsumerName());
		logDto.setConsumerGroupId(t1.getConsumerGroupId());
		logDto.setConsumerGroupName(consumerGroupName);
		logDto.setType(MqConst.INFO);
		logDto.setMsg("topics " + JsonUtil.toJsonNull(topics) + " is check vaild!");
		logService.addBrokerLog(logDto);

	}

	protected void registConsumerGroupConsumer(List<ConsumerGroupConsumerEntity> consumerGroupConsumerEntities) {
		try {
			if (CollectionUtils.isEmpty(consumerGroupConsumerEntities)) {
				return;
			}
			consumerGroupConsumerService.insertBatch(consumerGroupConsumerEntities);
		} catch (Exception e) {
			consumerGroupConsumerEntities.forEach(t1 -> {
				try {
					consumerGroupConsumerService.insert(t1);
				} catch (Exception e1) {
				}
			});
		}
	}

	protected AtomicInteger totalMax = new AtomicInteger(0);
	protected Map<String, AtomicInteger> topicPerMax = new ConcurrentHashMap<>();

	@Override
	public PublishMessageResponse publish(PublishMessageRequest request) {
		PublishMessageResponse response = new PublishMessageResponse();
		checkVaild(request, response);
		if (!response.isSuc()) {
			return response;
		}
		try {
			if (!checkTopicRate(request, response)) {
				return response;
			}
			Map<String, List<QueueEntity>> queueMap = queueService.getAllLocatedTopicWriteQueue();
			Map<String, List<QueueEntity>> topicQueueMap = queueService.getAllLocatedTopicQueue();
			if (queueMap.containsKey(request.getTopicName()) || topicQueueMap.containsKey(request.getTopicName())) {
				List<QueueEntity> queueEntities = queueMap.get(request.getTopicName());
				if (queueEntities == null || queueEntities.size() == 0) {
					response.setSuc(false);
					response.setMsg("topic_" + request.getTopicName() + "_and_has_no_queue!");
					if (topicQueueMap.containsKey(request.getTopicName()) && soaConfig.getPublishMode() == 1) {
						queueEntities = topicQueueMap.get(request.getTopicName());
						updateQueueCache(request.getTopicName());
					} else {
						updateQueueCache(request.getTopicName());
						return response;
					}
				}
				if (queueEntities.size() > 0) {
					saveMsg(request, response, queueEntities);
					// 删除老的失败的消息
					// deleteOldFailMsg(request, response);
				}
			} else {
				response.setSuc(false);
				response.setMsg("topic1_" + request.getTopicName() + "_and_has_no_queue!");
				return response;
			}
		} catch (Exception e) {
			log.error("publish_error", e);
			response.setSuc(false);
			response.setMsg(e.getMessage());
		} finally {
			if (soaConfig.getEnableTopicRate() == 1) {
				totalMax.decrementAndGet();
				topicPerMax.get(request.getTopicName()).decrementAndGet();
			}
		}
		return response;
	}

	private int deleteOldFailMsg(PublishMessageRequest request, ProducerDataDto t1, QueueEntity temp) {
		if (temp != null && temp.getNodeType() == 2 && temp.getTopicName().equals(request.getTopicName())) {
			message01Service.setDbId(temp.getDbNodeId());
			return message01Service.deleteOldFailMsg(temp.getTbName(), t1.getId(), t1.getRetryCount() - 1);
		}
		return 0;
	}

	protected volatile long lastTime = 0;

	protected void updateQueueCache(String topicName) {
		if (System.currentTimeMillis() - lastTime > 10 * 1000) {
			lastTime = System.currentTimeMillis();
			try {
				emailUtil.sendErrorMail(topicName + "没有可用的队列请注意", "没有可用的队列，请注意！！");
				queueService.resetCache();
				queueService.updateCache();
			} catch (Exception e) {

			}
		}
	}

	protected boolean checkTopicRate(PublishMessageRequest request, PublishMessageResponse response) {
		// 关闭限速
		if (soaConfig.getEnableTopicRate() == 0) {
			return true;
		}		
		if (!topicPerMax.containsKey(request.getTopicName())) {
			synchronized (this) {
				if (!topicPerMax.containsKey(request.getTopicName())) {
					topicPerMax.put(request.getTopicName(), new AtomicInteger(0));
				}
			}
		}

		int totalMax1 = totalMax.incrementAndGet();
		int topicMax1 = topicPerMax.get(request.getTopicName()).incrementAndGet();
		if (soaConfig.getTopicFlag(request.getTopicName()).equals("0")) {
			response.setMsg(String.format("当前topic被设置为禁止发送topic", request.getTopicName()));
			response.setSleepTime(0);
			response.setCode(MqConstanst.YES);
			response.setSuc(false);
			return false;
		}
		if (soaConfig.getTopicHostMax() > 0 && totalMax1 > soaConfig.getTopicHostMax()) {
			response.setMsg(String.format("当前发送超过最大并发数了，需要降速,最大值为%s,当前值为%s", soaConfig.getTopicHostMax(), totalMax1));
			response.setSleepTime(Math.round(Math.random() * 1000));
			response.setCode(MqConstanst.NO);
			response.setSuc(false);
			return false;
		}
		int topicPer = soaConfig.getTopicPerMax(request.getTopicName());
		if (topicPer > 0 && topicMax1 > topicPer) {
			response.setMsg(String.format("当前topic发送超过最大并发数了，需要降速,最大值为%s,当前值为%s", topicPer, topicMax1));
			response.setSleepTime(Math.round(Math.random() * 1000));
			response.setCode(MqConstanst.NO);
			response.setSuc(false);
			return false;
		}
		return true;
	}

	private void saveMsg(PublishMessageRequest request, PublishMessageResponse response,
			List<QueueEntity> queueEntities) {
		if (request.getSynFlag() == 1) {
			saveSynMsg(request, response, queueEntities);
		} else {
			saveAsynMsg(request, response, queueEntities);
		}
	}

	protected void saveAsynMsg(PublishMessageRequest request, PublishMessageResponse response,
			List<QueueEntity> queueEntities) {
		Map<Long, QueueEntity> queueMap = new HashMap<>();
		queueEntities.forEach(t1 -> {
			queueMap.put(t1.getId(), t1);
		});
		Map<String, PartitionInfo> partitionMap = new HashMap<>();
		// 根据将消息根据 queueid归类
		Map<Long, List<Message01Entity>> msgQueueMap = new HashMap<>();
		createMsg(request, msgQueueMap, partitionMap);
		for (Map.Entry<Long, List<Message01Entity>> entry : msgQueueMap.entrySet()) {
			if (queueMap.containsKey(entry.getKey())) {
				doSaveMsg(request, response, Arrays.asList(queueMap.get(entry.getKey())), entry.getValue());
			} else if (entry.getKey() == Long.MAX_VALUE) {
				doSaveMsg(request, response, queueEntities, entry.getValue());
			} else {
				entry.getValue().forEach(t1 -> {
					if (partitionMap.containsKey(t1.getTraceId())) {
						if (partitionMap.get(t1.getTraceId()).getStrictMode() == 0) {
							doSaveMsg(request, response, queueEntities, Arrays.asList(t1));
						}
					}
				});
			}
		}
	}

	private void createMsg(PublishMessageRequest request, Map<Long, List<Message01Entity>> queueMsg,
			Map<String, PartitionInfo> partitionMap) {
		request.getMsgs().forEach(t1 -> {
			Message01Entity entity = new Message01Entity();
			entity.setBizId(t1.getBizId());
			entity.setBody(t1.getBody());
			entity.setHead(JsonUtil.toJson(t1.getHead()));
			entity.setRetryCount(t1.getRetryCount());
			entity.setTag(t1.getTag() + "");
			if (StringUtils.isEmpty(t1.getTraceId())) {
				t1.setTraceId(UUID.randomUUID().toString().replaceAll("-", ""));
			}
			entity.setTraceId(t1.getTraceId());
			entity.setSendIp(request.getClientIp());
			if (t1.getPartitionInfo() != null) {
				if (!queueMsg.containsKey(t1.getPartitionInfo().getQueueId())) {
					queueMsg.put(t1.getPartitionInfo().getQueueId(), new ArrayList<>());
				}
				queueMsg.get(t1.getPartitionInfo().getQueueId()).add(entity);
				partitionMap.put(t1.getTraceId(), t1.getPartitionInfo());
			} else {
				if (!queueMsg.containsKey(Long.MAX_VALUE)) {
					queueMsg.put(Long.MAX_VALUE, new ArrayList<>());
				}
				queueMsg.get(Long.MAX_VALUE).add(entity);
			}

		});

	}

	private void saveSynMsg(PublishMessageRequest request, PublishMessageResponse response,
			List<QueueEntity> queueEntities) {
		List<Message01Entity> message01Entities = new ArrayList<>(request.getMsgs().size());
		createMsg(request, message01Entities);
		doSaveMsg(request, response, queueEntities, message01Entities);
	}

	private void doSaveMsg(PublishMessageRequest request, PublishMessageResponse response,
			List<QueueEntity> queueEntities, List<Message01Entity> message01Entities) {
		int tryCount = 0;
		int queueSize = queueEntities.size();
		Exception last = null;
		Transaction transaction = Tracer.newTransaction("Publish", request.getTopicName());
		String key = request.getTopicName();
		Map<String, AtomicInteger> counterTemp = counter.get();
		if (!counterTemp.containsKey(key)) {
			counterTemp.put(key, new AtomicInteger(0));
		}
		counterTemp.get(key).compareAndSet(Integer.MAX_VALUE, 0);
		int count = counterTemp.get(key).incrementAndGet();
		while (tryCount <= queueSize) {
			try {
				QueueEntity temp = queueEntities.get(count % queueEntities.size());
				count++;
				if (!checkFailTime(request.getTopicName(), temp, null)) {
					continue;
				}
				doSaveMsg(message01Entities, request, response, temp);
				last = null;
				if (response.isSuc()) {
					transaction.setStatus(Transaction.SUCCESS);
					addPublishLog(message01Entities, request, MqConst.INFO, null);
				} else {
					transaction.setStatus(response.getMsg());
					last = new RuntimeException(response.getMsg());
				}
				break;
			} catch (Exception e) {
				tryCount++;
				response.setSuc(false);
				response.setMsg("消息保存失败！");
				last = e;
			}
		}
		if (last != null) {
			transaction.setStatus(last);
			addPublishLog(message01Entities, request, MqConst.ERROR, last);
			sendPublishFailMail(request, last, 2);
		}
		transaction.complete();
	}

	private void createMsg(PublishMessageRequest request, List<Message01Entity> message01Entities) {
		request.getMsgs().forEach(t1 -> {
			Message01Entity entity = new Message01Entity();
			entity.setBizId(t1.getBizId());
			entity.setBody(t1.getBody());
			entity.setHead(JsonUtil.toJson(t1.getHead()));
			entity.setRetryCount(t1.getRetryCount());
			entity.setTag(t1.getTag() + "");
			if (StringUtils.isEmpty(t1.getTraceId())) {
				t1.setTraceId(UUID.randomUUID().toString().replaceAll("-", ""));
			}
			entity.setTraceId(t1.getTraceId());
			entity.setSendIp(request.getClientIp());
			message01Entities.add(entity);
		});
	}

	protected void sendPublishFailMail(PublishMessageRequest request, Exception last, int type) {
		if (soaConfig.enableSendFailTopicMail(request.getTopicName())) {
			SendMailRequest request2 = new SendMailRequest();
			request2.setSubject("topic:" + request.getTopicName() + "最终发送失败");
			request2.setContent(last.getMessage());
			request2.setType(type);
			request2.setTopicName(request.getTopicName());
			request2.setKey("topic:" + request.getTopicName() + "-发送失败！");
			emailService.sendProduceMail(request2);
		}
	}

	private void addPublishLog(List<Message01Entity> message01Entities, PublishMessageRequest request, int info,
			Throwable th) {
		message01Entities.forEach(t1 -> {
			LogDto logDto = new LogDto();
			logDto.setAction("message_publish");
			logDto.setBizId(t1.getBizId());
			logDto.setTopicName(request.getTopicName());
			logDto.setTraceId(t1.getTraceId());

			if (info == MqConst.ERROR) {
				logDto.setThrowable(th);
				logDto.setAction("message_publish_error");
				logDto.setMsg(JsonUtil.toJsonNull(t1));
			}
			// logDto.setMsg(JsonUtil.toJsonNull(t1));
			logDto.setType(info);
			logService.addBrokerLog(logDto);

		});
	}

	protected void checkVaild(PublishMessageRequest request, PublishMessageResponse response) {
		response.setSuc(true);
		if (request == null) {
			response.setSuc(false);
			response.setMsg("request is null!");
			return;
		}
		if (CollectionUtils.isEmpty(request.getMsgs())) {
			response.setSuc(false);
			response.setMsg("topic_" + request.getTopicName() + "_msg_is_null!");
			return;
		}
		Map<String, TopicEntity> cacheData = topicService.getCache();
		if (!cacheData.containsKey(request.getTopicName())) {
			response.setSuc(false);
			response.setMsg("topic_" + request.getTopicName() + "_is_not_exist!");
			return;
		}
		if (!StringUtils.isEmpty(cacheData.get(request.getTopicName()).getToken())
				&& !("" + cacheData.get(request.getTopicName()).getToken()).equals(request.getToken())) {
			response.setSuc(false);
			response.setMsg(
					"topic_" + request.getTopicName() + "_and_token_" + request.getToken() + "_is_not_correct!");
			return;
		}
	}

	// private AtomicInteger counter111=new AtomicInteger(0);
	protected void doSaveMsg(List<Message01Entity> message01Entities, PublishMessageRequest request,
			PublishMessageResponse response, QueueEntity temp) {
		// Transaction transaction = Tracer.newTransaction("PubInner-" +
		// temp.getIp(), request.getTopicName());
		message01Service.setDbId(temp.getDbNodeId());
		// Transaction transaction = Tracer.newTransaction("Publish-Data",
		// temp.getIp());
		try {
			message01Service.insertBatchDy(request.getTopicName(), temp.getTbName(), message01Entities);
			dbFailMap.put(getFailDbUp(temp), System.currentTimeMillis() - soaConfig.getDbFailWaitTime() * 2000L);
			response.setSuc(true);
			// transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			// sendPublishFailMail(request, e, 1);
			// transaction.setStatus(e);
			if (e.getCause() instanceof DataIntegrityViolationException) {
				response.setSuc(false);
				response.setMsg(e.getMessage());
				return;
			}
			dbFailMap.put(getFailDbUp(temp), System.currentTimeMillis());
			// transaction.setStatus(e);
			throw new RuntimeException(e);
		}
//		finally {
//			transaction.complete();
//		}
	}

	protected boolean checkFailTime(String topicName, QueueEntity entity, List<String> logLst) {
		if (dbFailMap.containsKey(getFailDbUp(entity))
				&& (System.currentTimeMillis() - dbFailMap.get(getFailDbUp(entity))) < soaConfig.getDbFailWaitTime()
						* 1000L) {
			if (logLst == null) {
				log.info("topicName_{}_queueid_{}_is_fail", topicName, entity.getId());
			} 
			return false;
		}
		return true;
	}

	@Override
	public PullDataResponse pullData(PullDataRequest request) {
		PullDataResponse response = new PullDataResponse();
		response.setSuc(true);
		Map<Long, QueueEntity> data = queueService.getAllQueueMap();
		checkVaild(request, response, data);
		if (!response.isSuc()) {
			return response;
		}
		QueueEntity temp = data.get(request.getQueueId());
		Map<Long, DbNodeEntity> dbNodeMap = dbNodeService.getCache();
		List<Message01Entity> entities = new ArrayList<>();
		Transaction transaction = null;
		if (checkFailTime(request.getTopicName(), temp, null) && checkStatus(temp, dbNodeMap)) {
			message01Service.setDbId(temp.getDbNodeId());
			transaction = Tracer.newTransaction("Pull-Data", temp.getIp());
			try {
				entities = message01Service.getListDy(temp.getTopicName(), temp.getTbName(), request.getOffsetStart(),
						request.getOffsetEnd());
				transaction.setStatus(Transaction.SUCCESS);
				dbFailMap.put(getFailDbUp(temp), System.currentTimeMillis() - soaConfig.getDbFailWaitTime() * 2000L);
			} catch (Exception e) {
				transaction.setStatus(e);
				dbFailMap.put(getFailDbUp(temp), System.currentTimeMillis());
				// TODO: handle exception
			}

		} else {
			transaction = Tracer.newTransaction("PullData", "PullData-wait");
			transaction.setStatus(Transaction.SUCCESS);

		}
		transaction.complete();
		List<MessageDto> messageDtos = converMessageDto(entities);
		response.setMsgs(messageDtos);
		return response;
	}

	// private String getFailDbUp(String topicName, String dbIp) {
	// // return topicName + "_" + id;
	// return dbIp;
	// }

	private String getFailDbUp(QueueEntity queueEntity) {
		// return topicName + "_" + id;
		return queueEntity.getIp();
	}

	private List<MessageDto> converMessageDto(List<Message01Entity> entities) {
		List<MessageDto> messageDtos = new ArrayList<>(entities.size());
		entities.forEach(t1 -> {
			MessageDto messageDto = new MessageDto();
			messageDto.setBizId(t1.getBizId());
			messageDto.setBody(t1.getBody());
			messageDto.setHead(JsonUtil.parseJson(t1.getHead(), new TypeReference<Map<String, String>>() {
			}));
			messageDto.setId(t1.getId());
			messageDto.setRetryCount(t1.getRetryCount());
			messageDto.setTag(t1.getTag());
			messageDto.setTraceId(t1.getTraceId());
			messageDto.setSendTime(t1.getSendTime());
			messageDto.setSendIp(t1.getSendIp());
			messageDtos.add(messageDto);
		});
		return messageDtos;
	}

	protected boolean checkStatus(QueueEntity temp, Map<Long, DbNodeEntity> dbNodeMap) {
		if (!dbNodeMap.containsKey(temp.getDbNodeId())) {			
			return false;
		}
		if (dbNodeMap.get(temp.getDbNodeId()).getReadOnly() == 3) {			
			return false;
		}
		return true;
	}

	private void checkVaild(PullDataRequest request, PullDataResponse response, Map<Long, QueueEntity> data) {
		if (request == null) {
			response.setSuc(false);
			response.setMsg("参数不能为空！");
			return;

		}
		if (request.getQueueId() <= 0 || request.getOffsetStart() < 0
				|| request.getOffsetStart() >= request.getOffsetEnd()) {
			response.setSuc(false);
			response.setMsg("参数不对！");
			return;
		}
		if (!data.containsKey(request.getQueueId())) {
			response.setSuc(false);
			response.setMsg("queueId_" + request.getQueueId() + "_is_not_exist！");
			return;
		}

	}

	@Override
	public GetMessageCountResponse getMessageCount(GetMessageCountRequest request) {
		GetMessageCountResponse response = new GetMessageCountResponse();
		response.setSuc(true);
		if (request==null||StringUtils.isEmpty(request.getConsumerGroupName())) {
			response.setSuc(false);
			response.setMsg("ConsumerGroupName不能为空！");
			return response;
		}
		Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
		if (!consumerGroupMap.containsKey(request.getConsumerGroupName())) {
			response.setSuc(false);
			response.setMsg("ConsumerGroupName不存在！");
			return response;
		}

		Map<String, Map<String, List<QueueOffsetEntity>>> map = queueOffsetService.getCache();
		List<QueueOffsetEntity> rs = new ArrayList<>();
		if (!CollectionUtils.isEmpty(request.getTopics())) {
			request.getTopics().forEach(t1 -> {
				if (map.get(request.getConsumerGroupName()).containsKey(t1)) {
					rs.addAll(map.get(request.getConsumerGroupName()).get(t1));
				}
			});
		} else {
			map.get(request.getConsumerGroupName()).values().forEach(t1 -> {
				rs.addAll(t1);
			});
		}
		Map<Long, QueueEntity> queues = queueService.getAllQueueMap();
		List<Long> ids = new ArrayList<>(rs.size());
		rs.forEach(t1 -> {
			ids.add(t1.getId());
		});
		long offsetSum = queueOffsetService.getOffsetSumByIds(ids);
		long totalCount = 0;
		for (QueueOffsetEntity t1 : rs) {
			QueueEntity temp = queues.get(t1.getQueueId());
			try {
				message01Service.setDbId(temp.getDbNodeId());
				long maxId = queueService.getMaxId(temp.getId(), temp.getTbName());
				totalCount = totalCount + maxId - 1;
			} catch (Exception e) {
			}
		}
		totalCount = totalCount - offsetSum;
		response.setCount(totalCount);
		return response;
	}

	@Override
	public List<ConsumerEntity> findByHeartTimeInterval(long heartTimeInterval) {
		return consumerRepository.findByHeartTimeInterval(heartTimeInterval);
	}

	@Override
	public boolean deleteByConsumers(List<ConsumerEntity> consumers) {
		if (CollectionUtils.isEmpty(consumers))
			return true;
		List<Long> consumerIds = new ArrayList<>();
		for (ConsumerEntity consumer : consumers) {
			consumerIds.add(consumer.getId());
		}
		return doDeleteConsumer(consumerIds, 0);
	}

	// 0 表示心跳超时类型，1表示下线类型
	private boolean doDeleteConsumer(List<Long> consumerIds, int type) {
		boolean result = false;

		List<Long> consumerGroupIds = new ArrayList<>();
		List<Long> broadConsumerGroupIds = new ArrayList<>();

		List<ConsumerGroupConsumerEntity> consumerGroupConsumers = consumerGroupConsumerService
				.getByConsumerIds(consumerIds);
		List<AuditLogEntity> auditLogs = new ArrayList<>();

		Map<Long, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getIdCache();
		Map<String, ConsumerGroupEntity> consumerGroupNameMap = consumerGroupService.getCache();
		for (ConsumerGroupConsumerEntity consumerGroupConsumer : consumerGroupConsumers) {
			ConsumerGroupEntity temp = consumerGroupMap.get(consumerGroupConsumer.getConsumerGroupId());
			long consumerGroupId = consumerGroupConsumer.getConsumerGroupId();
			if (temp != null) {
				// 虚拟广播消费者组
				if (temp.getMode() == 2 && !temp.getOriginName().equals(temp.getName())) {
					broadConsumerGroupIds.add(temp.getId());
					consumerGroupId = consumerGroupNameMap.get(temp.getOriginName()).getId();
				}
			}
			consumerGroupIds.add(consumerGroupConsumer.getConsumerGroupId());
			AuditLogEntity auditLog = new AuditLogEntity();
			auditLog.setInsertBy("broker-" + IPUtil.getLocalIP());
			auditLog.setTbName(ConsumerGroupEntity.TABLE_NAME);
			auditLog.setRefId(consumerGroupId);
			setDoDeleteConsumerLog(type, consumerGroupConsumer, auditLog);
			auditLogs.add(auditLog);
		}
		try {
			deleteBroadConsumerGroup(broadConsumerGroupIds);
			auditLogService.insertBatch(auditLogs);
			doDeleteConsumerIds(consumerIds, consumerGroupIds);
			addDeleteByConsumersLog(consumerGroupConsumers);
			result = true;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return result;
	}

	protected void setDoDeleteConsumerLog(int type, ConsumerGroupConsumerEntity consumerGroupConsumer,
			AuditLogEntity auditLog) {
		if (type == 0) {
			auditLog.setContent(consumerGroupConsumer.getConsumerName() + "超过"
					+ soaConfig.getConsumerInactivityTime() + "秒未发送心跳,此consumer会被删除,将进行重平衡处理！");
		} else if (type == 1) {
			auditLog.setContent(consumerGroupConsumer.getConsumerName() + "下线，此consumer会被删除,将进行重平衡处理！");
		}
	}

	private void deleteBroadConsumerGroup(List<Long> broadConsumerGroupIds) {
		if (!CollectionUtils.isEmpty(broadConsumerGroupIds)) {
			boolean flag = Util.isEmpty(userInfoHolder.getUserId());
			if (flag) {
				userInfoHolder.setUserId(soaConfig.getMqAdminUser());
			}
			try {
				broadConsumerGroupIds.forEach(t1 -> {
					consumerGroupService.deleteConsumerGroup(t1, false);
				});
			} catch (Exception e) {
			}
			if (flag) {
				userInfoHolder.clear();
			}
		}
	}

	private void addDeleteByConsumersLog(List<ConsumerGroupConsumerEntity> consumerGroupConsumers) {
		for (ConsumerGroupConsumerEntity t1 : consumerGroupConsumers) {
			LogDto logDto = new LogDto();
			logDto.setAction("is_deleteByConsumers");
			// logDto.setConsumerGroupName(t1.getn);
			logDto.setConsumerName(t1.getConsumerName());
			logDto.setConsumerGroupId(t1.getConsumerGroupId());
			logDto.setType(MqConst.WARN);
			logService.addBrokerLog(logDto);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	private void doDeleteConsumerIds(List<Long> consumerIds, List<Long> consumerGroupIds) {
		consumerGroupConsumerService.deleteByConsumerIds(consumerIds);
		queueOffsetService.setConsumserIdsToNull(consumerIds);
		consumerRepository.batchDelete(consumerIds);
		consumerGroupService.notifyRb(consumerGroupIds);
	}

	@Override
	public ConsumerEntity getConsumerByConsumerGroupId(Long consumerGroupId) {
		// TODO Auto-generated method stub
		return consumerRepository.getConsumerByConsumerGroupId(consumerGroupId);
	}

	@Override
	public long countBy(Map<String, Object> conditionMap) {
		return consumerRepository.countBy(conditionMap);
	}

	@Override
	public List<ConsumerEntity> getListBy(Map<String, Object> conditionMap) {
		return consumerRepository.getListBy(conditionMap);
	}

	@Override
	public FailMsgPublishAndUpdateResultResponse publishAndUpdateResultFailMsg(
			FailMsgPublishAndUpdateResultRequest request) {
		FailMsgPublishAndUpdateResultResponse response = new FailMsgPublishAndUpdateResultResponse();
		response.setSuc(true);
		QueueEntity queue = queueService.getAllQueueMap().get(request.getQueueId());
		if (request.getFailMsg() != null) {
			PublishMessageResponse publishMessageResponse = publish(request.getFailMsg());
			response.setSuc(publishMessageResponse.isSuc());
			// 删除老的失败消息
			if (request.getFailMsg().getMsgs() != null) {
				request.getFailMsg().getMsgs().forEach(t1 -> {
					deleteOldFailMsg(request.getFailMsg(), t1, queue);
				});
			}
		}
		if (!CollectionUtils.isEmpty(request.getIds())) {
			if (queue != null && queue.getNodeType() == 2 && !CollectionUtils.isEmpty(request.getIds())) {
				message01Service.setDbId(queue.getDbNodeId());
				message01Service.updateFailMsgResult(queue.getTbName(), request.getIds(),
						Message01Service.failMsgRetryCountSuc);
			}
		}
		return response;
	}

}
