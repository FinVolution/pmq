package com.ppdai.infrastructure.mq.client.resource;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.client.CatRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.FailMsgPublishAndUpdateResultRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMetaGroupResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicQueueIdsResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.HeartbeatRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.OpLogRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.PullDataResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;

public interface IMqResource {
	void setUrls(List<String> urlsTempG1, List<String> urlsTempG2);

	long register(ConsumerRegisterRequest request);

	void publishAndUpdateResultFailMsg(FailMsgPublishAndUpdateResultRequest request);

	void deRegister(ConsumerDeRegisterRequest request);

	GetMetaGroupResponse getMetaGroup(GetMetaGroupRequest request);

	GetTopicResponse getTopic(GetTopicRequest request);

	GetGroupTopicResponse getGroupTopic(GetGroupTopicRequest request);

	void addCat(CatRequest request);

	boolean publish(PublishMessageRequest request, int retryTimes);

	boolean publish(PublishMessageRequest request);

	void commitOffset(CommitOffsetRequest request);

	ConsumerGroupRegisterResponse registerConsumerGroup(ConsumerGroupRegisterRequest request);

	void heartbeat(HeartbeatRequest request);

	GetConsumerGroupResponse getConsumerGroup(GetConsumerGroupRequest request);

	GetMessageCountResponse getMessageCount(GetMessageCountRequest request);

	PullDataResponse pullData(PullDataRequest request);

	GetTopicQueueIdsResponse getTopicQueueIds(GetTopicQueueIdsRequest request);

	void addLog(LogRequest request);

	void addOpLog(OpLogRequest request);

	void sendMail(SendMailRequest request);
	
	String getBrokerIp();

}
