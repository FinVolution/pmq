package com.ppdai.infrastructure.mq.client.hystrix;

import java.util.ArrayList;
import java.util.List;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.event.ISubscriber;
import com.ppdai.infrastructure.mq.client.MessageUtil;

public class MessageInvokeCommandForThreadIsolation extends HystrixCommand<List<Long>> {
	private ISubscriber iSubscriber1 = null;
	private List<MessageDto> dtos;
	private ConsumerQueueDto pre1;
	public MessageInvokeCommandForThreadIsolation(String consumerGroupName, ConsumerQueueDto pre, List<MessageDto> dtos,
			ISubscriber iSubscriber) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(consumerGroupName))
				.andCommandKey(HystrixCommandKey.Factory.asKey(consumerGroupName + "." + pre.getOriginTopicName()))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(consumerGroupName + "." + pre.getQueueId()))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(pre.getThreadSize()))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						// .withExecutionTimeoutInMilliseconds(pre.getTimeout() * 1000)
						.withExecutionTimeoutEnabled(true)
						.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
						.withCircuitBreakerEnabled(false)));
		this.iSubscriber1 = iSubscriber;
		this.pre1 = pre;
		this.dtos = dtos;		
	}

	@Override
	protected List<Long> run() throws Exception {
		return invoke(dtos, iSubscriber1, pre1);
	}

	public static List<Long> invoke(List<MessageDto> dtos, ISubscriber iSubscriber, ConsumerQueueDto pre)
			throws Exception {
		MessageUtil.addCatChain(dtos);
		List<Long> failIds = null;
		failIds = iSubscriber.onMessageReceived(dtos);
		if (failIds == null) {
			failIds = new ArrayList<>();
		}
		return failIds;
	}

}
