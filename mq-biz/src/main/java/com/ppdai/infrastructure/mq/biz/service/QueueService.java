package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.AnalyseDto;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */

public interface QueueService extends BaseService<QueueEntity> {
	/*
	 * key为topicName
	 */
	Map<String, List<QueueEntity>> getAllLocatedTopicQueue();

	/*
	 * key为topicName
	 */
	Map<String, List<QueueEntity>> getAllLocatedTopicWriteQueue();

	/*
	 * key为queueId
	 */
	Map<Long, QueueEntity> getAllQueueMap();

	List<QueueEntity> getAllLocatedQueue();

//	List<Long> getTopUndistributedNodes(int topNum, int nodeType, List<Long> nodeIds);
//
	List<QueueEntity> getDistributedList(List<Long> nodeIds, Long topicId);
//
//	List<QueueEntity> getUndistributedList(List<Long> nodeIds, int nodeType);

	List<Long> getTopDistributedNodes(Long topicId);

	void updateWithLock(QueueEntity queueEntity);

	// key为queueid，值为最大id+1
	Map<Long, Long> getMax();

	long getMaxId(long queueId, String tbName);

	void updateForDbNodeChange(String ip, String dbName, String oldIp, String oldDbName);

	List<String> getTableNamesByDbNode(Long dbNodeId);

	List<AnalyseDto> countTopicByNodeId(Long id, Long page, Long limit);

	/**
	 * 根据参数dbNodeId获取该节点下所有Topic的分布节点
	 *
	 * @param dbNodeId
	 * @return
	 */
	List<AnalyseDto> getDistributedNodes(Long dbNodeId);

	Map<Long, AnalyseDto> getQueueQuantity();

	int updateMinId(Long id, Long minId);

	long getLastVersion();

	void resetCache();

	void updateCache();

	void forceUpdateCache();
	

	List<QueueEntity> getQueuesByTopicId(long topicId);

	void deleteMessage(List<QueueEntity> queueEntities, long consumerGroupId);

	void doDeleteMessage(QueueEntity queueEntity);

	List<QueueEntity> getTopUndistributed(int topNum, int nodeType, Long topicId);

}
