package com.ppdai.infrastructure.mq.client.event;

import java.util.ArrayList;
import java.util.List;

import com.ppdai.infrastructure.mq.biz.event.IAsynSubscriberSelector;
import com.ppdai.infrastructure.mq.biz.event.IMsgFilter;
import com.ppdai.infrastructure.mq.biz.event.ISubscriberSelector;
import com.ppdai.infrastructure.mq.biz.event.PostHandleListener;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;
import com.ppdai.infrastructure.mq.biz.event.PreSendListener;

public class MqEvent {
	private List<Runnable> initCompleted = new ArrayList<>();
	private PreHandleListener preHandleListener = null;
	private PostHandleListener postHandleListener = null;
	private ISubscriberSelector iSubscriberSelector = null;
	private IAsynSubscriberSelector iAsynSubscriberSelector = null;
	private List<Runnable> registerCompleted = new ArrayList<>();
	private List<RegisterConsumerGroupListener> registerConsumerGroupListeners = new ArrayList<>();
	private List<IMsgFilter> msgFilters = new ArrayList<>();

	public List<IMsgFilter> getMsgFilters() {
		return msgFilters;
	}

	public void setMsgFilters(List<IMsgFilter> msgFilters) {
		this.msgFilters = msgFilters;
	}

	public List<PreSendListener> getPreSendListeners() {
		return preSendListeners;
	}

	public void setPreSendListeners(List<PreSendListener> preSendListeners) {
		this.preSendListeners = preSendListeners;
	}

	private List<PreSendListener> preSendListeners = new ArrayList<>();

	public List<Runnable> getInitCompleted() {
		return initCompleted;
	}

	public void setInitCompleted(List<Runnable> initCompleted) {
		this.initCompleted = initCompleted;
	}

	public PreHandleListener getPreHandleListener() {
		return preHandleListener;
	}

	public void setPreHandleListener(PreHandleListener preHandleListener) {
		this.preHandleListener = preHandleListener;
	}

	public PostHandleListener getPostHandleListener() {
		return postHandleListener;
	}

	public void setPostHandleListener(PostHandleListener postHandleListener) {
		this.postHandleListener = postHandleListener;
	}

	public ISubscriberSelector getiSubscriberSelector() {
		return iSubscriberSelector;
	}

	public void setiSubscriberSelector(ISubscriberSelector iSubscriberSelector) {
		this.iSubscriberSelector = iSubscriberSelector;
	}

	public List<Runnable> getRegisterCompleted() {
		return registerCompleted;
	}

	public List<RegisterConsumerGroupListener> getRegisterConsumerGroupListeners() {
		return registerConsumerGroupListeners;
	}
	public IAsynSubscriberSelector getiAsynSubscriberSelector() {
		return iAsynSubscriberSelector;
	}

	public void setiAsynSubscriberSelector(IAsynSubscriberSelector iAsynSubscriberSelector) {
		this.iAsynSubscriberSelector = iAsynSubscriberSelector;
	}
}
