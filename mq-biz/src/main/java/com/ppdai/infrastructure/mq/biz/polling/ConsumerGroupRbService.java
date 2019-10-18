package com.ppdai.infrastructure.mq.biz.polling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.LogDto;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageStatEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupConsumerService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.LogService;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageService;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageStatService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;

/*
 * 重平衡分配器
 */
@Service
public class ConsumerGroupRbService extends AbstractTimerService {
	private Logger log = LoggerFactory.getLogger(ConsumerGroupRbService.class);
	private volatile long lastNotifyMessageId = 0;
	private volatile NotifyMessageStatEntity messageStatEntity;
	@Autowired
	private NotifyMessageService notifyMessageService;
	@Autowired
	private NotifyMessageStatService notifyMessageStatService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private ConsumerGroupConsumerService consumerGroupConsumerService;
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private ConsumerService consumerService;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private LogService logService;
	@Autowired
	private AuditLogService auditLogService;
	// @Autowired
	// private EmailUtil emailUtil;

	@PostConstruct
	private void init() {
		super.init(Constants.RB, soaConfig.getRbCheckInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getRbCheckInterval();

			@Override
			public void run() {
				if (soaConfig.getRbCheckInterval() != interval) {
					interval = soaConfig.getRbCheckInterval();
					updateInterval(interval);
				}
			}
		});
	}

	public void doStart() {
		if (!soaConfig.isEnableRb()) {
			return;
		}
		if (lastMaster != isMaster) {
			if (!checkNotifyMessageStatId()) {
				initNotifyMessageStatId();
			}
		}
		long currentMaxId = getNotifyMessageId();
		if (currentMaxId == 0) {
			return;
		}
		List<ConsumerGroupEntity> consumerGroupEntities = consumerGroupService
				.getLastRbConsumerGroup(lastNotifyMessageId, currentMaxId);
		if (CollectionUtils.isEmpty(consumerGroupEntities)) {
			return;
		}
		Map<Long, ConsumerGroupQuqueVo> consumerGroupMap = new HashMap<>();
		initRbData(consumerGroupEntities, consumerGroupMap);
		for (ConsumerGroupQuqueVo t1 : consumerGroupMap.values()) {
			rb(t1);
			for (int i = 0; i < 3; i++) {
				try {
					if (super.isMaster) {
						consumerGroupService.rb(t1.queueOffsets);
					}
					break;
				} catch (Exception e) {
					log.error("doCheckRebalance_error", e);
					Util.sleep(5000);
				}
			}
			addRbCompleteLog(t1);
		}
		updateNotifyMessageId(currentMaxId);
		int count = consumerGroupConsumerService.deleteUnActiveConsumer();
		if (count > 0) {
			log.info("consumerGroupConsumer_empty,count is " + count);
		}
	}

	private void addRbCompleteLog(ConsumerGroupQuqueVo t1) {
		try {
			AuditLogEntity auditLog = new AuditLogEntity();
			auditLog.setTbName(ConsumerGroupEntity.TABLE_NAME);
			auditLog.setRefId(t1.consumerGroup.getId());
			auditLog.setInsertBy("broker-rq-" + IPUtil.getLocalIP());
			// 注意重平衡完成后，版本号还会继续升级但是不进行重平衡操作，所以在日志中会出现版本号不一致的情况，属于正常情况
			auditLog.setContent(t1.consumerGroup.getName() + "重平衡完毕！__version_is_" + t1.consumerGroup.getVersion());
			auditLogService.insert(auditLog);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void initRbData(List<ConsumerGroupEntity> consumerGroupEntities,
			Map<Long, ConsumerGroupQuqueVo> consumerGroupMap) {
		consumerGroupEntities.forEach(t1 -> {
			consumerGroupMap.put(t1.getId(), new ConsumerGroupQuqueVo());
			consumerGroupMap.get(t1.getId()).consumerGroup = t1;
		});
		// 获取需要重平衡的consumergroupid列表
		List<Long> consumerGroupIds = new ArrayList<>(consumerGroupMap.keySet());
		// 获取consumergroupid对应的group列表
		List<ConsumerGroupConsumerEntity> consumerGroupConsumerEntities = consumerService
				.getConsumerGroupByConsumerGroupIds(consumerGroupIds);

		Map<Long, String> logMap = new HashMap<Long, String>();
		for (ConsumerGroupConsumerEntity t1 : consumerGroupConsumerEntities) {
			// 检查黑白名单
			if (checkConsumerIp(consumerGroupMap.get(t1.getConsumerGroupId()).consumerGroup, t1)) {
				int consumerQuality = consumerGroupMap.get(t1.getConsumerGroupId()).consumerGroup.getConsumerQuality();
				if (consumerQuality > 0
						&& consumerGroupMap.get(t1.getConsumerGroupId()).consumers.size() < consumerQuality) {
					consumerGroupMap.get(t1.getConsumerGroupId()).consumers.add(t1);
				} else if (consumerQuality == 0) {
					consumerGroupMap.get(t1.getConsumerGroupId()).consumers.add(t1);
				} else {
					logMap.put(t1.getConsumerGroupId(),
							consumerGroupMap.get(t1.getConsumerGroupId()).consumerGroup.getName() + "允许的最大消费者数为:"
									+ consumerQuality + ",所以" + t1.getConsumerName() + "无法消费，处于待命状态。");
				}
			}
		}
		addDistributeLog(logMap);
		List<QueueOffsetEntity> queueOffsetEntities = queueOffsetService.getByConsumerGroupIds(consumerGroupIds);
		queueOffsetEntities.forEach(t1 -> {
			consumerGroupMap.get(t1.getConsumerGroupId()).queueOffsets.add(t1);
		});
	}

	private void addDistributeLog(Map<Long, String> logMap) {
		if (logMap != null && logMap.size() > 0) {
			List<AuditLogEntity> logs = new ArrayList<AuditLogEntity>();
			logMap.entrySet().forEach(t1 -> {
				AuditLogEntity auditLogEntity = new AuditLogEntity();
				auditLogEntity.setTbName(ConsumerGroupEntity.TABLE_NAME);
				auditLogEntity.setRefId(t1.getKey());
				auditLogEntity.setInsertBy("broker-rq-" + IPUtil.getLocalIP());
				auditLogEntity.setContent(t1.getValue());
				logs.add(auditLogEntity);
			});
			if (logs.size() > 0) {
				if (logs.size() < 50) {
					auditLogService.insertBatch(logs);
				} else {
					List<List<AuditLogEntity>> logs1 = Util.split(logs, 50);
					logs1.forEach(t2 -> {
						auditLogService.insertBatch(t2);
					});
				}
			}
		}
	}

	private boolean checkConsumerIp(ConsumerGroupEntity consumerGroupEntity, ConsumerGroupConsumerEntity t1) {
		AuditLogEntity auditLogEntity = new AuditLogEntity();
		auditLogEntity.setTbName(ConsumerGroupEntity.TABLE_NAME);
		auditLogEntity.setRefId(consumerGroupEntity.getId());
		auditLogEntity.setInsertBy("broker-rb-" + IPUtil.getLocalIP());
		try {
			if (!StringUtils.isEmpty(consumerGroupEntity.getIpBlackList())) {
				if (("," + consumerGroupEntity.getIpBlackList() + ",").indexOf(t1.getIp()) != -1) {
					auditLogEntity.setContent(
							"因为consumer ip，" + t1.getIp() + "在黑名单中，列表为" + consumerGroupEntity.getIpBlackList()
									+ ",所以不能参与订阅消费！__version_is_" + consumerGroupEntity.getVersion());
					return false;
				}
			}
			if (!StringUtils.isEmpty(consumerGroupEntity.getIpWhiteList())) {
				if (("," + consumerGroupEntity.getIpWhiteList() + ",").indexOf(t1.getIp()) != -1) {
					return true;
				} else {
					auditLogEntity.setContent(
							"因为consumer ip，" + t1.getIp() + "不在白名单中，列表为" + consumerGroupEntity.getIpWhiteList()
									+ ",所以不能参与订阅消费！--version is " + consumerGroupEntity.getVersion());
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (!StringUtils.isEmpty(auditLogEntity.getContent())) {
				auditLogService.insert(auditLogEntity);
			}
		}
		return true;
	}

	private void updateNotifyMessageId(long currentMaxId) {
		lastNotifyMessageId = currentMaxId;
		messageStatEntity.setNotifyMessageId(lastNotifyMessageId);
		notifyMessageStatService.update(messageStatEntity);
	}

	private long getNotifyMessageId() {
		// 当notifyMessage表清空的时候，最大的id可能会返回为0，还有一种就是没有新的重平衡产生也会返回为0
		long currentMaxId = notifyMessageService.getRbMaxId(lastNotifyMessageId);
		// 所以currentMaxId 只你取值为0，或者比
		// lastNotifyMessageId大，当currentMaxId为0，做个健壮性检查防止 NotifyMessageStat表数据清空
		if (currentMaxId == 0) {
			if (!checkNotifyMessageStatId()) {
				// 初始化NotifyMessageStat
				initNotifyMessageStatId();
				// 获取最小的minid
				lastNotifyMessageId = notifyMessageService.getRbMinId();
				currentMaxId = notifyMessageService.getRbMaxId();
			}
		}
		// else if (lastNotifyMessageId > currentMaxId) {
		// // 防止出现数据库，这种情况出现的原因是因为数据出现异常导致
		// lastNotifyMessageId = currentMaxId =
		// notifyMessageService.getRbMinId();
		// emailUtil.sendErrorMail("数据库发生异常，NotifyMessageId发生变化",
		// "将NotifyMessageId修改为" + lastNotifyMessageId);
		// log.error("db_error!");
		// }
		return currentMaxId;
	}

	private void rb(ConsumerGroupQuqueVo consumerGroupQuqueVo) {
		String preJson = JsonUtil.toJsonNull(consumerGroupQuqueVo);
		AuditLogEntity auditLogEntity = new AuditLogEntity();
		auditLogEntity.setTbName(ConsumerGroupEntity.TABLE_NAME);
		auditLogEntity.setRefId(consumerGroupQuqueVo.consumerGroup.getId());
		auditLogEntity.setInsertBy("broker-rq-" + IPUtil.getLocalIP());
		if (consumerGroupQuqueVo.consumers.size() > 0) {
			int count = 0;
			int size = consumerGroupQuqueVo.consumers.size();
			StringBuilder sr = new StringBuilder();
			for (QueueOffsetEntity t1 : consumerGroupQuqueVo.queueOffsets) {
				ConsumerGroupConsumerEntity t2 = consumerGroupQuqueVo.consumers.get(count);
				t1.setConsumerId(t2.getConsumerId());
				t1.setConsumerName(t2.getConsumerName());
				count = (count + 1) % size;
				sr.append(String.format("将queueOffsetId%s分配给消费者%s,", t1.getId(), t2.getConsumerName()));
			}
			auditLogEntity
					.setContent(sr.toString() + "__version_is_" + consumerGroupQuqueVo.consumerGroup.getVersion());
		} else {
			auditLogEntity.setContent("当前消费者组" + consumerGroupQuqueVo.consumerGroup.getName()
					+ "下的没有可用的消费者，所以清空此消费者组下的消费者！__version_is_" + consumerGroupQuqueVo.consumerGroup.getVersion());
			// 清空，说明没有可以用的consumer，有可能全部下线，有可能在黑白名单中
			for (QueueOffsetEntity t1 : consumerGroupQuqueVo.queueOffsets) {
				t1.setConsumerId(0);
				t1.setConsumerName("");
			}
		}
		String afterJson = JsonUtil.toJsonNull(consumerGroupQuqueVo);
		// 添加重平衡日志
		addRbLog(consumerGroupQuqueVo, preJson, afterJson);
		auditLogService.insert(auditLogEntity);
	}

	private void addRbLog(ConsumerGroupQuqueVo consumerGroupQuqueVo, String preJson, String afterJson) {
		LogDto logDto = new LogDto();
		logDto.setAction("ConsumerGroup_" + consumerGroupQuqueVo.consumerGroup.getName() + "_is_rb");
		logDto.setConsumerGroupId(consumerGroupQuqueVo.consumerGroup.getId());
		logDto.setConsumerGroupName(consumerGroupQuqueVo.consumerGroup.getName());
		logDto.setType(MqConst.WARN);
		logDto.setMsg(String.format("ip %s 地址计算完成重平衡，从%s 重平衡成%s,__version_is_%s", IPUtil.getLocalIP(), preJson,
				afterJson, consumerGroupQuqueVo.consumerGroup.getVersion()));
		logService.addBrokerLog(logDto);
	}

	// private boolean initNotifyMessageRecord() {
	// if (!checkNotifyMessageStatId()) {
	// initNotifyMessageStatId();
	// return false;
	// }
	// return true;
	// }

	private void initNotifyMessageStatId() {
		try {
			messageStatEntity = notifyMessageStatService.initNotifyMessageStat();
			lastNotifyMessageId = 0;
		} catch (Exception e) {
		}
	}

	private boolean checkNotifyMessageStatId() {
		messageStatEntity = notifyMessageStatService.get();
		if (messageStatEntity != null) {
			lastNotifyMessageId = messageStatEntity.getNotifyMessageId();
			return true;
		}
		return false;
	}

	@PreDestroy
	public void stopPortal() {
		super.stopPortal();
	}

	class ConsumerGroupQuqueVo {
		public ConsumerGroupEntity consumerGroup;
		public List<ConsumerGroupConsumerEntity> consumers = new ArrayList<>();
		public List<QueueOffsetEntity> queueOffsets = new ArrayList<>();
	}
}
