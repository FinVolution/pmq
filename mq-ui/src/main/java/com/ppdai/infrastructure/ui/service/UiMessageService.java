package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.trace.CatContext;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageResponse;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.MessageConditionRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.MessageGetByTopicRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.MessageGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageConditionResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageGetByTopicResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.MessageVo;

/**
 * @author liujianjun02
 */
@Service
public class UiMessageService {
	@Autowired
	private Message01Service message01Service;
	@Autowired
	private QueueService queueService;
	
	@Autowired
	private DbNodeService dbNodeService;
	@Autowired
	private TopicService topicService;
	@Autowired
	private SoaConfig soaConfig;

	public MessageGetListResponse getMessageByPage(MessageGetListRequest messageGetListRequest) {

		Map<String, Object> parameterMap = new HashMap<>();
		if (StringUtils.isEmpty(messageGetListRequest.getQueueId())) {
			return new MessageGetListResponse(new Long(0), null);
		}
		long pageSize = Long.valueOf(messageGetListRequest.getLimit());
		long start = (Long.valueOf(messageGetListRequest.getPage()) - 1) * pageSize;
		long maxId, minId, count = 0;
		List<MessageVo> messageVos = new ArrayList<>();
		QueueEntity queueEntity = queueService.get(Long.valueOf(messageGetListRequest.getQueueId()));
		maxId = messageGetListRequest.getMaxId();
		minId = messageGetListRequest.getMinId();
		count = maxId - minId - 1;
		if (count == 0) {
			return new MessageGetListResponse(count, null);
		}
		message01Service.setDbId(queueEntity.getDbNodeId());
		TopicEntity topicEntity = topicService.get(queueEntity.getTopicId());
		List<Message01Entity> message01EntityList=new ArrayList<>();

		if(topicEntity.getTopicType()==1){
			if(messageGetListRequest.getId()!=0L){
				Message01Entity message = message01Service.getMessageById(queueEntity.getTbName(), messageGetListRequest.getId());
				message01EntityList.add(message);
			}
			//根据id区间查询
			else if (StringUtils.isEmpty(messageGetListRequest.getBizId())&&StringUtils.isEmpty(messageGetListRequest.getTraceId())
					&&StringUtils.isEmpty(messageGetListRequest.getHeader())&&StringUtils.isEmpty(messageGetListRequest.getBody())) {
				long end1 = maxId - start - 1;
				long start1 = end1 - pageSize;
				if (start1 < minId) {
					start1 = minId;
				}
				message01EntityList = message01Service.getListDy(queueEntity.getTopicName(),queueEntity.getTbName(), start1, end1);
			}
			//条件筛选查询,不分页
			else {
				parameterMap.put("tbName",message01Service.getDbName()+"."+queueEntity.getTbName());
				parameterMap.put("bizId", messageGetListRequest.getBizId());
				parameterMap.put("traceId", messageGetListRequest.getTraceId());
				parameterMap.put("head", messageGetListRequest.getHeader());
				parameterMap.put("body", messageGetListRequest.getBody());
				parameterMap.put("start1",start);
				parameterMap.put("offset1",pageSize);
				parameterMap.put("maxId",maxId);
				parameterMap.put("minId",minId);

				message01EntityList = message01Service.getListByPage(parameterMap);

				message01Service.setDbId(queueEntity.getDbNodeId());
				count=message01Service.countByPage(parameterMap);

			}
		}
		else if(topicEntity.getTopicType()==2){
			//如果是失败topic，则根据正常分页逻辑查询
			parameterMap.put("tbName",message01Service.getDbName()+"."+queueEntity.getTbName());
			parameterMap.put("bizId", messageGetListRequest.getBizId());
			parameterMap.put("traceId", messageGetListRequest.getTraceId());
			parameterMap.put("head", messageGetListRequest.getHeader());
			parameterMap.put("body", messageGetListRequest.getBody());
			parameterMap.put("start1",start);
			parameterMap.put("offset1",pageSize);
			parameterMap.put("startTime",messageGetListRequest.getStartTime());
			parameterMap.put("endTime",messageGetListRequest.getEndTime());
			if(!StringUtils.isEmpty(messageGetListRequest.getRetryStatus())){
				parameterMap.put("retryStatus",Integer.parseInt(messageGetListRequest.getRetryStatus()));
				parameterMap.put("failMsgRetryCountSuc",Message01Service.failMsgRetryCountSuc);
			}
			message01EntityList = message01Service.getListByPage(parameterMap);

			message01Service.setDbId(queueEntity.getDbNodeId());
			count=message01Service.countByPage(parameterMap);

		}

		for (Message01Entity message01Entity:message01EntityList) {
			if(message01Entity!=null){				
				if(!Util.isEmpty(message01Entity.getHead())&&message01Entity.getHead().indexOf(CatContext.ROOT)!=-1){
					Map<String, String> head=JsonUtil.parseJson(message01Entity.getHead(),new TypeReference<Map<String, String>>() {
					});
					head.remove(CatContext.ROOT);
					head.remove(CatContext.PARENT);
					head.remove(CatContext.CHILD);
					message01Entity.setHead(JsonUtil.toJson(head));
				}

				MessageVo messageVo = new MessageVo(message01Entity);
				if (topicEntity != null) {
					messageVo.setType(topicEntity.getTopicType());
				}
				if(messageVo.getType()==2){
					//如果是失败topic,设置消息的重试状态
					if(messageVo.getRetryCount()>Message01Service.failMsgRetryCountSuc){
						messageVo.setFailMsgRetryStatus("重试成功");
						messageVo.setRetryCount(messageVo.getRetryCount()-Message01Service.failMsgRetryCountSuc);
					}else{
						messageVo.setFailMsgRetryStatus("重试失败");
					}
				}

				if("null".equals(messageVo.getTag())){
					messageVo.setTag(null);
				}


				messageVos.add(messageVo);
			}

		}

		return new MessageGetListResponse(count, messageVos);
	}

	public int checkQueueSlave(long queueId){
		QueueEntity queue=queueService.get(queueId);
		if(queue!=null){
			Map<Long, DbNodeEntity> dbNodeMap=dbNodeService.getCache();
			if(dbNodeMap.containsKey(queue.getDbNodeId())){
				return dbNodeService.hasSlave(dbNodeMap.get(queue.getDbNodeId()))?1:0;
			}
		}
		return 0;
	}
	public MessageConditionResponse getMessageRange(MessageConditionRequest messageConditionRequest) {
		QueueEntity queueEntity = queueService.get(Long.valueOf(messageConditionRequest.getQueueId()));
		Map<String, Object> data = new HashMap<>();
		long maxId = getMaxId(message01Service, queueEntity, messageConditionRequest);
		long minId = getMinId(message01Service, queueEntity, messageConditionRequest);
		data.put("minId", minId);
		data.put("maxId", maxId);
		return new MessageConditionResponse(data);
	}

	public List<QueueEntity> getQueueByTopicName(String topicName) {
		List<QueueEntity> result = null;
		Map<String, Object> parameter = new HashMap<>(16);
		parameter.put(QueueEntity.FdTopicName, topicName);
		result = queueService.getList(parameter);
		return result;
	}


	//@Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
	public PublishMessageResponse sendAllFailMessage(long queueId, List<Long>messageIds) {
		QueueEntity queueEntity = queueService.get(Long.valueOf(queueId));
		message01Service.setDbId(queueEntity.getDbNodeId());
		List<Message01Entity>failMessages=message01Service.getMessageByIds(queueEntity.getTbName(),messageIds);

		failMessages.forEach(t1->t1.setRetryCount(1));
		message01Service.setDbId(queueEntity.getDbNodeId());
		message01Service.insertBatchDy(queueEntity.getTopicName(),queueEntity.getTbName(),failMessages);
		//重复发送之后，删除原来的旧消息
		message01Service.setDbId(queueEntity.getDbNodeId());
		message01Service.deleteByIds(queueEntity.getTbName(),messageIds);
		PublishMessageResponse submitMessageResponse=new PublishMessageResponse();
		submitMessageResponse.setSuc(true);
		return submitMessageResponse;
	}


	/**
	 * 查找最大id，初始值为1，若设置截止时间，则取到截止时间的最后一条消息的id，若没有则取队列最大id
	 * @param message01Service
	 * @param queueEntity
	 * @param messageConditionRequest
	 * @return
	 */
	private long getMaxId(Message01Service message01Service, QueueEntity queueEntity,
			MessageConditionRequest messageConditionRequest) {
		long maxId = 1;
		message01Service.setDbId(queueEntity.getDbNodeId());
		if (StringUtils.isEmpty(messageConditionRequest.getEndTime())) {
			maxId = message01Service.getMaxId(queueEntity.getTbName());
		} else {
			List<Message01Entity> list = message01Service.getListByTime(queueEntity.getTbName(),
					messageConditionRequest.getEndTime());
			if (!CollectionUtils.isEmpty(list)) {
				Message01Entity message01Entity = list.get(list.size() - 1);
				maxId = message01Entity.getId();
				maxId++;
			}
		}
		return maxId;
	}

	/**
	 * 查找最小id，初始值为0，若设置起始时间，则取起始时间消息第一条消息的id，若没有则为队列最小id
	 * @param message01Service
	 * @param queueEntity
	 * @param messageConditionRequest
	 * @return
	 */
	private long getMinId(Message01Service message01Service, QueueEntity queueEntity,
			MessageConditionRequest messageConditionRequest) {
		long minId = queueEntity.getMinId();
		if(!StringUtils.isEmpty(messageConditionRequest.getStartTime())){
			message01Service.setDbId(queueEntity.getDbNodeId());
			List<Message01Entity> list = message01Service.getListByTime(queueEntity.getTbName(), messageConditionRequest.getStartTime());
			if (!CollectionUtils.isEmpty(list)) {
				Message01Entity message01Entity = list.get(0);
				minId = message01Entity.getId();
			}
		}
		return minId;
	}


	public MessageGetByTopicResponse getMessageByTopic(MessageGetByTopicRequest messageGetByTopicRequest){
		   Map<String,TopicEntity> topicMap=topicService.getCache();
           if(soaConfig.isPro()){
           	return new MessageGetByTopicResponse("1","生产环境不可以调用该接口");
		   }

		   if(StringUtils.isEmpty(messageGetByTopicRequest.getBizId())||StringUtils.isEmpty(messageGetByTopicRequest.getTopicName())){
           	return new MessageGetByTopicResponse("1","业务id和topic不能为空");
		   }

		   if(topicMap.containsKey(messageGetByTopicRequest.getTopicName())){
			   List<QueueEntity> queueList=queueService.getQueuesByTopicId(topicMap.get(messageGetByTopicRequest.getTopicName()).getId());
			   List<Message01Entity> messageList=new ArrayList<>();
			   Map<String, Object> parameterMap = new HashMap<>();
			   for (QueueEntity queueEntity:queueList) {
				   message01Service.setDbId(queueEntity.getDbNodeId());
				   parameterMap.put("tbName",message01Service.getDbName()+"."+queueEntity.getTbName());
				   parameterMap.put("bizId", messageGetByTopicRequest.getBizId());
				   parameterMap.put("start1",0);
				   parameterMap.put("offset1",1000);
				   message01Service.setDbId(queueEntity.getDbNodeId());
				   messageList.addAll(message01Service.getListByPage(parameterMap));
			   }
			   return new MessageGetByTopicResponse(messageList);
		   }else{
			   return new MessageGetByTopicResponse("1","topic不存在");
		   }

	}
}
