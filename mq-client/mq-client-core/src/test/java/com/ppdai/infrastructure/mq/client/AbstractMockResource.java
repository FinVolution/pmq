package com.ppdai.infrastructure.mq.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.client.*;
import com.ppdai.infrastructure.mq.client.resource.IMqResource;

public abstract class AbstractMockResource implements IMqResource{

	@Override
	public void setUrls(List<String> urlsTempG1, List<String> urlsTempG2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long register(ConsumerRegisterRequest request) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void publishAndUpdateResultFailMsg(FailMsgPublishAndUpdateResultRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deRegister(ConsumerDeRegisterRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GetMetaGroupResponse getMetaGroup(GetMetaGroupRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetTopicResponse getTopic(GetTopicRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetGroupTopicResponse getGroupTopic(GetGroupTopicRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCat(CatRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean publish(PublishMessageRequest request, int retryTimes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean publish(PublishMessageRequest request) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void commitOffset(CommitOffsetRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConsumerGroupRegisterResponse registerConsumerGroup(ConsumerGroupRegisterRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HeartbeatResponse heartbeat(HeartbeatRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetConsumerGroupResponse getConsumerGroup(GetConsumerGroupRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetMessageCountResponse getMessageCount(GetMessageCountRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PullDataResponse pullData(PullDataRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetTopicQueueIdsResponse getTopicQueueIds(GetTopicQueueIdsRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addLog(LogRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addOpLog(OpLogRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMail(SendMailRequest request) {
		// TODO Auto-generated method stub
		
	}

}
