package com.ppdai.infrastructure.mq.client;

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
	public void heartbeat(HeartbeatRequest request) {
		// TODO Auto-generated method stub
		
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
