package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PreDestroy;

import com.ppdai.infrastructure.mq.biz.entity.*;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.TimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.UserInfo;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.QueueGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueVo;

@Service
public class UiQueueService implements TimerService {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	@Autowired
	private QueueService queueService;
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private Message01Service message01Service;
	@Autowired
	private DbNodeService nodeService;
	@Autowired
	private TopicService topicService;
	@Autowired
	private AuditLogService uiAuditLogService;
	@Autowired
	private UiQueueOffsetService uiQueueOffsetService;
	@Autowired
	private DbNodeService dbNodeService;
	@Autowired
	private UiConsumerGroupTopicService uiConsumerGroupTopicService;
	@Autowired
	private ConsumerGroupService consumerGroupService;

	@Autowired
	private RoleService roleService;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private UserInfoHolder userInfoHolder;

	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private ThreadPoolExecutor executor = null;
	private volatile boolean isRunning = true;
	private AtomicReference<List<QueueVo>> queueListAvg = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<List<QueueVo>> queueListByDataSize = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<List<QueueVo>> queueWarningInfo = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<List<QueueVo>> queueListCount = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<Map<String, List<QueueVo>>> queueListCountMap = new AtomicReference<>(
			new ConcurrentHashMap<>());
	private volatile long messageCount = 0;
	private volatile long messageAvg = 0;
	private volatile long lastUpdateTime = 0;

	@Override
	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			lastUpdateTime = System.currentTimeMillis() - soaConfig.getMqReportInterval() * 2;
			executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(50),
					SoaThreadFactory.create("UiQueueService", true), new ThreadPoolExecutor.DiscardOldestPolicy());
			executor.execute(() -> {
				while (isRunning) {
					try {
						if (System.currentTimeMillis() - lastAccessTime < soaConfig.getMqReportInterval()
								|| System.currentTimeMillis() - lastAccessTime > 1000 * 60 * 60 * 60) {
							if (System.currentTimeMillis() - lastUpdateTime > soaConfig.getMqReportInterval()) {
								initCache();
								initMessageCount();
								if (queueListAvg.get().size() > 0) {
									lastUpdateTime = System.currentTimeMillis();
									lastAccessTime = System.currentTimeMillis() - soaConfig.getMqReportInterval() * 2;
								}
							}
						}
					} catch (Throwable e) {
						log.error("UiQueueServiceImpl_initCache_error", e);
					}
					if (queueListAvg.get().size() == 0) {
						Util.sleep(10 * 1000);
					} else {
						Util.sleep(2000);
					}
				}
			});
		}
	}

	public Map<String, List<QueueVo>> getQueueListCount() {
		return queueListCountMap.get();
	}


	public List<QueueVo> getQueueListAvg() {
		return queueListAvg.get();
	}

	private boolean initCache() {
		Transaction transaction = Tracer.newTransaction("UiQueueService", "initCache");
		transaction.setStatus(Transaction.SUCCESS);
		try {
			List<QueueEntity> queueList = queueService.getAllLocatedQueue();
			if (queueList.size() == 0)
				return false;
			List<QueueVo> queueVosAvg = new ArrayList<>(queueList.size());
			List<QueueVo> queueVosCount = new ArrayList<>(queueList.size());
			List<QueueVo> queueVosByDataSize = new ArrayList<>(queueList.size());
			Map<String, List<QueueVo>> queueVoMap = new ConcurrentHashMap<>();
			Map<Long, Long> queueMaxIdMap = queueService.getMax();
			if (queueMaxIdMap.size() == 0)
				return false;
			Map<String, TopicEntity> topicMap = topicService.getCache();
			if (topicMap.size() == 0)
				return false;
			for (QueueEntity queueEntity : queueList) {
				TopicEntity topicEntity = topicMap.get(queueEntity.getTopicName());
				if (topicEntity != null && queueMaxIdMap.size() != 0) {
					QueueVo queueVo = new QueueVo(queueEntity);
					queueVo.setDbReadOnly(dbNodeService.getCache().get(queueEntity.getDbNodeId()).getReadOnly());
					if (!queueMaxIdMap.containsKey(queueEntity.getId())) {
						log.info("queueid " + queueEntity.getId() + " not exist!");
						continue;
					}
					long maxId = queueMaxIdMap.get(queueEntity.getId());

					queueVo.setMaxId(maxId);

					message01Service.setDbId(queueEntity.getDbNodeId());

					// 取倒数第一条消息
					Message01Entity message01Entity1 = message01Service.getMinIdMsg(queueEntity.getTbName());
					TableInfoEntity tableInfo = message01Service.getSingleTableInfoFromCache(queueEntity);
					queueVo.setMsgCount(tableInfo.getTbRows());
					queueVo.setAvgCount(queueVo.getMsgCount() / topicEntity.getSaveDayNum());
					// 插入Table空间信息
					queueVo.setDataSize(tableInfo.getDataSize());
					if(!StringUtils.isEmpty(topicEntity.getOwnerNames())){
						queueVo.setTopicOwnerName(topicEntity.getOwnerNames());
						queueVo.setSaveDayNum(topicEntity.getSaveDayNum());
					}

					if (message01Entity1 != null) {
						queueVo.setMinTime(message01Entity1.getSendTime());
						if ((System.currentTimeMillis()
								- message01Entity1.getSendTime().getTime()) > (topicEntity.getSaveDayNum() + 1) * 24
								* 60 * 60 * 1000) {
							queueVo.setIsException(1);
						}
					}
					queueVosAvg.add(queueVo);
					queueVosCount.add(queueVo);
					queueVosByDataSize.add(queueVo);
					if (queueVoMap.containsKey(queueVo.getTopicName())) {
						queueVoMap.get(queueVo.getTopicName()).add(queueVo);
					} else {
						List<QueueVo> queueVoList2 = new ArrayList<>();
						queueVoList2.add(queueVo);
						queueVoMap.put(queueVo.getTopicName(), queueVoList2);
					}
				}
			}
			queueSort(queueVosAvg);
			queueSort(queueVosCount);
			queueSortByDataSize(queueVosByDataSize);
			queueListAvg.set(queueVosAvg);
			queueListCount.set(queueVosCount);
			queueListCountMap.set(queueVoMap);
			queueListByDataSize.set(queueVosByDataSize);
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			log.error("UiQueueService_initCache_error", e);
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
		return true;
	}

	private void initMessageCount() {
		long count = 0;
		long avg = 0;
		for (QueueVo queueVo : queueListAvg.get()) {
			count += queueVo.getMsgCount();
			avg += queueVo.getAvgCount();
		}
		messageCount = count;
		messageAvg = avg;
	}

	public long getMessageCount() {
		return messageCount;
	}

	public long getMessageAvg() {
		return messageAvg;
	}

	private void queueSort(List<QueueVo> queueVoList) {
		// 按照消息总量
		Collections.sort(queueVoList, new Comparator<QueueVo>() {
			@Override
			public int compare(QueueVo q1, QueueVo q2) {
				if(q1.getMsgCount()>q2.getMsgCount()){
					return -1;
				}else if(q1.getMsgCount()<q2.getMsgCount()){
					return 1;
				}
				return 0;
			}
		});
	}

	private void queueSortByDataSize(List<QueueVo> queueVoList) {
		// 按照消息容量大小
		Collections.sort(queueVoList, new Comparator<QueueVo>() {
			@Override
			public int compare(QueueVo q1, QueueVo q2) {
				if(q1.getDataSize()>q2.getDataSize()){
					return -1;
				}else if(q1.getDataSize()<q2.getDataSize()){
					return 1;
				}
				return 0;
			}
		});
	}

	public List<QueueEntity> getBestRemoveQueue(Long topicId) {
		List<Long> nodeIds;
		nodeIds = queueService.getTopDistributedNodes(topicId);
		if (CollectionUtils.isEmpty(nodeIds)) {
			return null;
		}
		for (Long nodeId : nodeIds) {
			if (getQueuesByTopicAndNodeId(topicId, nodeId).size() > 1) {
				return queueService.getDistributedList(Lists.newArrayList(nodeId), topicId);
			}
		}
		return null;
	}

	private List<QueueEntity> getQueuesByTopicAndNodeId(Long topicId, Long nodeId) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(QueueEntity.FdTopicId, topicId);
		conditionMap.put(QueueEntity.FdDbNodeId, nodeId);
		return queueService.getList(conditionMap);
	}

	/**
	 *
	 */
	public QueueCountResponse count(int nodeType) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(QueueEntity.FdNodeType, nodeType);
		Long allCount = queueService.count(conditionMap);
		conditionMap.put(QueueEntity.FdTopicId, "0");
		Long undistributedCount = queueService.count(conditionMap);
		Long distributedCount = allCount - undistributedCount;
		Map<String, Long> resultMap = new HashMap<>();
		resultMap.put("allCount", allCount);
		resultMap.put("distributedCount", distributedCount);
		resultMap.put("undistributedCount", undistributedCount);

		return new QueueCountResponse(resultMap);
	}

	public int countByTopicId(Long topicId) {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(QueueEntity.FdTopicId, topicId);
		return (int) queueService.count(conditionMap);
	}


	public void deleteMessageFromTopic(List<QueueEntity> queueEntities, long topicId) {
		for (QueueEntity queueEntity : queueEntities) {
			queueService.doDeleteMessage(queueEntity);
			uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId,
					"清空队列消息，queue: " + JsonUtil.toJson(queueEntity));
		}
	}


	public QueueGetListResponse queryByPage(QueueGetListRequest queueGetListRequest) {
		Map<String, Object> conditionMap = new HashMap<>(16);
		UserInfo userInfo = userInfoHolder.getUser();
		if (!StringUtils.isEmpty(queueGetListRequest.getId())) {
			conditionMap.put(QueueEntity.FdId, queueGetListRequest.getId());
		}
		if (!StringUtils.isEmpty(queueGetListRequest.getTopicName())) {
			conditionMap.put(QueueEntity.FdTopicName, queueGetListRequest.getTopicName());
		}
		if (!StringUtils.isEmpty(queueGetListRequest.getDbNodeId())) {
			conditionMap.put(QueueEntity.FdDbNodeId, Long.valueOf(queueGetListRequest.getDbNodeId()));
		}
		if (!StringUtils.isEmpty(queueGetListRequest.getNodeType())) {
			conditionMap.put(QueueEntity.FdNodeType, Integer.valueOf(queueGetListRequest.getNodeType()));
		}
		if (!StringUtils.isEmpty(queueGetListRequest.getReadOnly())) {
			conditionMap.put(QueueEntity.FdReadOnly, Integer.valueOf(queueGetListRequest.getReadOnly()));
		}
		if (!StringUtils.isEmpty(queueGetListRequest.getDistributeType())) {
			conditionMap.put(QueueEntity.FdDistributeType, Integer.valueOf(queueGetListRequest.getDistributeType()));
		}
		long count = queueService.countBy(conditionMap);
		if (count == 0) {
			return new QueueGetListResponse(count, null);
		}
		List<QueueEntity> queueEntityList = queueService.getListBy(conditionMap, Long.valueOf(queueGetListRequest.getPage()),
				Long.valueOf(queueGetListRequest.getLimit()));
		List<QueueVo> queueVos = new ArrayList<>();
		for (QueueEntity queueEntity : queueEntityList) {
			QueueVo queueVo = new QueueVo(queueEntity);
			queueVo.setDbReadOnly(dbNodeService.getCache().get(queueEntity.getDbNodeId()).getReadOnly());
			if (isAdmin(userInfo.getUserId())) {
				queueVo.setRole(1);
			} else if (isTopicOwner(userInfo.getUserId(), queueEntity.getTopicName())) {
				queueVo.setRole(1);
			}
			message01Service.setDbId(queueEntity.getDbNodeId());
			long maxId = message01Service.getMaxId(queueEntity.getTbName());
			long minId = queueEntity.getMinId();
			queueVo.setMsgCount(maxId - minId - 1);
			queueVos.add(queueVo);
		}
		return new QueueGetListResponse(count, queueVos);
	}

	public QueueReadOnlyResponse readOnly(Long queueId, int isReadOnly) {
		String msg = isReadOnly == 1 ? "读写" : "只读";
		if (queueId == null) {
			throw new CheckFailException("队列Id不能为空。");
		}
		QueueEntity queueEntity = queueService.get(queueId);
		if (queueEntity == null) {
			throw new CheckFailException("队列Id无效。");
		}
		if (StringUtils.isEmpty(queueEntity.getTopicName())) {
			throw new CheckFailException("该队列还未被分配");
		}
		if (isReadOnly == 2) {
			if (!isCanSetReadOnly(queueId, queueEntity)) {
				uiAuditLogService.recordAudit(QueueEntity.TABLE_NAME, queueId, "设置Topic:" + queueEntity.getTopicName()
						+ "下id为：" + queueId + "的队列状态为" + msg + "失败，该Topic下其他queue已经设置为只读");
				throw new CheckFailException("Topic下其他queue已经设置为只读，" + queueId + "不能设置为只读。");
			}
		}
		queueEntity.setReadOnly(isReadOnly);
		int result = queueService.update(queueEntity);
		if (result > 0) {
			uiAuditLogService.recordAudit(QueueEntity.TABLE_NAME, queueId,
					"设置Topic:" + queueEntity.getTopicName() + "下id为：" + queueId + "的队列状态为" + msg + "成功");
		} else {
			uiAuditLogService.recordAudit(QueueEntity.TABLE_NAME, queueId,
					"设置Topic:" + queueEntity.getTopicName() + "下id为：" + queueId + "的队列状态为" + msg + "失败");
		}
		return new QueueReadOnlyResponse();
	}

	private boolean isCanSetReadOnly(Long queueId, QueueEntity queueEntity) {
		boolean flag = false;
		List<Long> nodeIds = queueService.getTopDistributedNodes(queueEntity.getTopicId());
		List<QueueEntity> queueDistributedList = queueService.getDistributedList(nodeIds, queueEntity.getTopicId());
		for (QueueEntity queue : queueDistributedList) {
			if (queue.getId() != queueId) {
				DbNodeEntity dbNodeEntity = dbNodeService.get(queue.getDbNodeId());
				if (dbNodeEntity.getReadOnly() == 1 && queue.getReadOnly() == 1) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	public BaseUiResponse remove(Long queueId) {
		if (queueId == null) {
			throw new CheckFailException("队列Id不能为空。");
		}
		QueueEntity queueEntity = queueService.get(queueId);
		if (queueEntity == null) {
			throw new CheckFailException("队列Id无效。");
		}
		Long dbNodeId = queueEntity.getDbNodeId();
		DbNodeEntity dbNodeEntity = nodeService.get(dbNodeId);
		if (dbNodeEntity == null) {
			throw new CheckFailException("该队列所使用的数据节点[" + dbNodeId + "]无效。");
		}
		// 如果该队列所在数据节点时读写状态，而且该队列也是读写状态，此时无法移除。
		if (dbNodeEntity.getReadOnly() == 1 && queueEntity.getReadOnly() == 1) {
			throw new CheckFailException("该队列不是只读，无法移除。");
		}
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put("queueId", queueId);
		List<QueueOffsetEntity> queueOffsetEntities = queueOffsetService.getList(conditionMap);
		message01Service.setDbId(queueEntity.getDbNodeId());
		// maxId为最大id+1
		long maxId = queueService.getMaxId(queueEntity.getId(), queueEntity.getTbName());
		// 若干对应的queueId已经有偏移量了，就要保证所有消息都被消费，才能移除。
		if (!CollectionUtils.isEmpty(queueOffsetEntities)) {
			for (QueueOffsetEntity queueOffsetEntity : queueOffsetEntities) {
				//如果是广播模式的原始消费者组，则跳过检测
				if(queueOffsetEntity.getConsumerGroupMode()==2&&
						queueOffsetEntity.getConsumerGroupName().equals(queueOffsetEntity.getOriginConsumerGroupName())){
					continue;

				}

				if(queueOffsetEntity.getOffset()!=maxId-1){
					throw new CheckFailException("Id为"+queueOffsetEntity.getId() + "的queueOffset，对应的队列中，还有消息未被消费，不能移除。");
				}
			}
		}
		Long topicId = queueEntity.getTopicId();
		doRemove(queueEntity, topicId);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "移除queue：" + JsonUtil.toJson(queueEntity));
		return new BaseUiResponse();
	}

	public void forceRemove(Long queueId) {
		QueueEntity queueEntity = queueService.get(queueId);
		Long topicId = queueEntity.getTopicId();
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(QueueOffsetEntity.FdQueueId, queueId);
		List<QueueOffsetEntity> queueOffsetEntityList = queueOffsetService.getList(conditionMap);
		for (QueueOffsetEntity queueOffsetEntity : queueOffsetEntityList) {
			if (queueOffsetEntity.getConsumerId() != 0) {
				throw new CheckFailException(queueOffsetEntity.getId() + "该队列下消息正在被消费，不能移除。");
			}
		}
		doRemove(queueEntity, topicId);
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "强制移除queue：" + JsonUtil.toJson(queueEntity));
	}

	private void doRemove(QueueEntity queueEntity, Long topicId) {
		uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicId, "清空队列消息之前" + queueEntity.getId());
		deleteMessageFromTopic(Lists.newArrayList(queueEntity), topicId);
		uiQueueOffsetService.deleteByQueueId(queueEntity.getId(), topicId);
		queueService.update(queueEntity);
		queueChangeRb(topicId);
	}

	public void queueChangeRb(Long topicId) {
		List<ConsumerGroupTopicEntity> consumerGroupTopicList = uiConsumerGroupTopicService.findByTopicId(topicId);
		List<Long> consumerIds = new ArrayList<>();
		if (!CollectionUtils.isEmpty(consumerGroupTopicList) && consumerGroupTopicList.size() != 0) {
			for (ConsumerGroupTopicEntity consumerGroupTopicEntity : consumerGroupTopicList) {
				consumerIds.add(consumerGroupTopicEntity.getConsumerGroupId());
			}
			consumerGroupService.notifyRb(consumerIds);
		}
	}


	public boolean isAdmin(String userId) {
		if (StringUtils.hasLength(userId)) {
			return roleService.isAdmin(userId);
		}
		return false;
	}

	public boolean isTopicOwner(String userId, String topicName) {
		if (StringUtils.hasLength(userId) && StringUtils.hasLength(topicName)) {
			return Arrays.asList(topicService.getCache().get(topicName).getOwnerIds().split(",")).contains(userId);
		}
		return false;
	}

	public Map<String, TopicEntity> getManagerTopic(String userName, Map<String, TopicEntity> topicCacheData) {
		Map<String, TopicEntity> topicEntityMap = new HashMap<>(16);
		if (roleService.isAdmin(userName)) {
			topicEntityMap = topicCacheData;
		} else {
			Set<String> keySet = topicCacheData.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				TopicEntity topicEntity = topicCacheData.get(key);
				if (!Util.isEmpty(topicEntity.getOwnerIds()) && topicEntity.getOwnerIds().contains(userName)) {
					topicEntityMap.put(key, topicEntity);
				}
			}
			// 所管理的consumerGroup下的topic
			Map<String, ConsumerGroupEntity> consumerGroupEntityMap = consumerGroupService.getCache();
			Set<String> consumerGroupKeySet = consumerGroupEntityMap.keySet();
			Iterator<String> consumerGroupNames = consumerGroupKeySet.iterator();
			while (consumerGroupNames.hasNext()) {
				String consumerName = consumerGroupNames.next();
				ConsumerGroupEntity consumerGroupEntity = consumerGroupEntityMap.get(consumerName);
				if (!StringUtils.isEmpty(consumerGroupEntity.getName())
						&& consumerGroupEntity.getOwnerIds().contains(userName)) {
					String topics = consumerGroupEntity.getTopicNames();
					if (!Util.isEmpty(topics)) {
						String[] topicName = topics.split(",");
						for (int i = 0; i < topicName.length; i++) {
							TopicEntity topicEntity = topicCacheData.get(topicName[i]);
							topicEntityMap.put(topicName[i], topicEntity);
						}
					}
				}
			}
		}
		return topicEntityMap;
	}

	private long lastAccessTime = System.currentTimeMillis() * 2;

	public QueueReportResponse getQueueForReport(QueueGetListRequest queueGetListRequest, String userId) {
		lastAccessTime = System.currentTimeMillis();
		Map<String, Object> conditionMap = new HashMap<>(16);
		if (!StringUtils.isEmpty(queueGetListRequest.getTopicName())) {
			conditionMap.put(QueueEntity.FdTopicName, queueGetListRequest.getTopicName());
		}
		if (!StringUtils.isEmpty(queueGetListRequest.getNodeType())) {
			conditionMap.put(QueueEntity.FdNodeType, Integer.valueOf(queueGetListRequest.getNodeType()));
		}
		int page = Integer.parseInt(queueGetListRequest.getPage());
		int pageSize = Integer.parseInt(queueGetListRequest.getLimit());
		List<QueueVo> queueForPortal = new ArrayList<>(20000);
		List<QueueVo> qlist = null;

		// 如果根据消息平均数排序的列表
		if ("2".equals(queueGetListRequest.getSortTypeId())) {
			qlist = queueListAvg.get();
		} else if ("1".equals(queueGetListRequest.getSortTypeId())) {
			qlist = queueListCount.get();
		} else {
			qlist =queueListByDataSize.get();
		}
		for (QueueVo queueVo : qlist) {
			if (isAdmin(userId)) {
				queueVo.setRole(1);
			} else if (isTopicOwner(userId, queueVo.getTopicName())) {
				queueVo.setRole(1);
			}

			QueueVo temp = queueVo;
			if (!StringUtils.isEmpty(queueGetListRequest.getTopicName())) {
				if (!queueGetListRequest.getTopicName().equals(temp.getTopicName())) {
					temp = null;
				}
			}
			if (!StringUtils.isEmpty(queueGetListRequest.getNodeType())) {
				if (temp != null && !queueGetListRequest.getNodeType().equals(temp.getNodeType() + "")) {
					temp = null;
				}
			}
			if (!StringUtils.isEmpty(queueGetListRequest.getIsException()) && queueGetListRequest.getIsException() == 1) {
				if (temp != null && queueGetListRequest.getIsException() != temp.getIsException()) {
					temp = null;
				}
			}
			if (!StringUtils.isEmpty(queueGetListRequest.getIsException()) && queueGetListRequest.getIsException() == 2) {
				if (temp != null && temp.getMsgCount() != 0) {
					temp = null;
				}
			}
			if (!StringUtils.isEmpty(queueGetListRequest.getIsException()) && queueGetListRequest.getIsException() == 3) {
				if (temp != null && temp.getMsgCount() >= 0) {
					temp = null;
				}
			}
			if (!StringUtils.isEmpty(queueGetListRequest.getIp())) {
				if (temp != null && !temp.getIp().equals(queueGetListRequest.getIp())) {
					temp = null;
				}
			}

			if (temp != null) {
				queueForPortal.add(temp);
			}
		}
		int t = queueForPortal.size();
		if ((page * pageSize) > queueForPortal.size()) {
			queueForPortal = queueForPortal.subList((page - 1) * pageSize, queueForPortal.size());
		} else {
			queueForPortal = queueForPortal.subList((page - 1) * pageSize, page * pageSize);
		}
		return new QueueReportResponse(new Long(t), queueForPortal);
	}

	/**
	 *
	 * @param queueId
	 * @return
	 */
	public BaseUiResponse<String> getQueueMinId(long queueId){
		Map<Long, QueueEntity> queueMap=queueService.getAllQueueMap();
		QueueEntity queueEntity=queueMap.get(queueId);
		message01Service.setDbId(queueEntity.getDbNodeId());
		Long tableMinId=message01Service.getTableMinId(queueEntity.getTbName());
		long result=0;
		if(tableMinId!=null){
			result=tableMinId-1;
		}else{
			Map<Long, Long> queueMaxIdMap = queueService.getMax();
			if (queueMaxIdMap.containsKey(queueEntity.getId())) {
				long maxId = queueMaxIdMap.get(queueEntity.getId());
				result=maxId-1;
			}
		}

		if(result<0){
			result=0;
		}

		return new BaseUiResponse(result);

	}

	public QueueUpdateMinIdResponse updateMinId(Long queueId, Long minId){
		QueueUpdateMinIdResponse queueUpdateMinIdResponse=new QueueUpdateMinIdResponse();
		try{
			Map<Long, QueueEntity> queueMap=queueService.getAllQueueMap();
			QueueEntity queueEntity=queueMap.get(queueId);
			queueEntity.setMinId(minId);
			queueService.update(queueEntity);
			return queueUpdateMinIdResponse;
		}catch(Exception e){
			queueUpdateMinIdResponse.setCode("1");
			queueUpdateMinIdResponse.setMsg(e.getMessage());
			return queueUpdateMinIdResponse;
		}


	}

	public QueueGetListResponse getAbnormalMinId(QueueGetListRequest queueGetListRequest){
		Map<Long,QueueEntity>queueMap=queueService.getAllQueueMap();
		UserInfo userInfo = userInfoHolder.getUser();
		List<QueueVo> queueVos = new ArrayList<>();
		Map<Long,Long>maxIdMap=queueService.getMax();
		for (QueueEntity queueEntity:queueMap.values()) {
			QueueVo queueVo = new QueueVo(queueEntity);
			queueVo.setDbReadOnly(dbNodeService.getCache().get(queueEntity.getDbNodeId()).getReadOnly());
			if (isAdmin(userInfo.getUserId())) {
				queueVo.setRole(1);
			} else if (isTopicOwner(userInfo.getUserId(), queueEntity.getTopicName())) {
				queueVo.setRole(1);
			}
			long maxId=maxIdMap.get(queueEntity.getId());
			long minId = queueEntity.getMinId();
			queueVo.setMsgCount(maxId - minId - 1);
			if(queueVo.getMsgCount()<0){
				queueVos.add(queueVo);
			}

		}
		return new QueueGetListResponse(new Long(queueVos.size()), queueVos);
	}


	@Override
	@PreDestroy
	public void stop() {
		isRunning = false;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}
}
