package com.ppdai.infrastructure.mq.biz.dto.client;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.BaseRequest;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;

public class PublishMessageRequest extends BaseRequest {
	private List<ProducerDataDto> msgs;
	private String topicName;
	private String token;
	//表示此次发送是否是同步还是异步，默认是同步
	private int synFlag=1;

	public List<ProducerDataDto> getMsgs() {
		return msgs;
	}

	public void setMsgs(List<ProducerDataDto> msgs) {
		this.msgs = msgs;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}	
	public int getSynFlag() {
		return synFlag;
	}

	public void setSynFlag(int synFlag) {
		this.synFlag = synFlag;
	}	
}
