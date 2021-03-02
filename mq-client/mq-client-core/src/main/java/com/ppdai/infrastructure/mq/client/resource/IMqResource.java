package com.ppdai.infrastructure.mq.client.resource;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.client.*;

public interface IMqResource {
	void setUrls(List<String> urlsTempG1, List<String> urlsTempG2);

	long register(ConsumerRegisterRequest request);

	void publishAndUpdateResultFailMsg(FailMsgPublishAndUpdateResultRequest request);

	void deRegister(ConsumerDeRegisterRequest request);

	GetMetaGroupResponse getMetaGroup(GetMetaGroupRequest request);

	GetTopicResponse getTopic(GetTopicRequest request);

	GetGroupTopicResponse getGroupTopic(GetGroupTopicRequest request);

	void addCat(CatRequest request);
	void rb(RbRequest request);
	boolean publish(PublishMessageRequest request, int retryTimes);

	boolean publish(PublishMessageRequest request);

	void commitOffset(CommitOffsetRequest request);

	ConsumerGroupRegisterResponse registerConsumerGroup(ConsumerGroupRegisterRequest request);

	HeartbeatResponse heartbeat(HeartbeatRequest request);

	GetConsumerGroupResponse getConsumerGroup(GetConsumerGroupRequest request);

	GetMessageCountResponse getMessageCount(GetMessageCountRequest request);

	PullDataResponse pullData(PullDataRequest request);

	GetTopicQueueIdsResponse getTopicQueueIds(GetTopicQueueIdsRequest request);

	void addLog(LogRequest request);

	void addOpLog(OpLogRequest request);

	void sendMail(SendMailRequest request);
	
	String getBrokerIp();

}
