package com.ppdai.infrastructure.mq.client.event;

import java.util.ArrayList;
import java.util.List;

import com.ppdai.infrastructure.mq.biz.event.ISubscriberSelector;
import com.ppdai.infrastructure.mq.biz.event.PostHandleListener;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;

public class MqEvent {
	//初始化完成事件
	private List<Runnable> initCompleted = new ArrayList<>();
	//消息消费前事件
	private PreHandleListener preHandleListener = null;
	//消息消费后事件
	private PostHandleListener postHandleListener = null;
	//topic订阅类处理接口
	private ISubscriberSelector iSubscriberSelector = null;
	//客户度注册完成事件
	private List<Runnable> registerCompleted = new ArrayList<>();
	//消费者组注册完成事件,在广播消息中使用
	private List<RegisterConsumerGroupListener> registerConsumerGroupListeners = new ArrayList<>();

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
	
}
