package com.ppdai.infrastructure.mq.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.MqEnv;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerUtil;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.PropUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueDto;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerDeRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupRegisterResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerRegisterRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetGroupTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetMessageCountResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetTopicResponse;
import com.ppdai.infrastructure.mq.biz.dto.client.GroupTopicDto;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageRequest;
import com.ppdai.infrastructure.mq.biz.event.IAsynSubscriberSelector;
import com.ppdai.infrastructure.mq.biz.event.IMsgFilter;
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.biz.event.ISubscriberSelector;
import com.ppdai.infrastructure.mq.biz.event.PartitionInfo;
import com.ppdai.infrastructure.mq.biz.event.PostHandleListener;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;
import com.ppdai.infrastructure.mq.biz.event.PreSendListener;
import com.ppdai.infrastructure.mq.client.config.ClientConfigHelper;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqBrokerUrlRefreshService;
import com.ppdai.infrastructure.mq.client.core.IMqCheckService;
import com.ppdai.infrastructure.mq.client.core.IMqGroupExcutorService;
import com.ppdai.infrastructure.mq.client.core.IMqHeartbeatService;
import com.ppdai.infrastructure.mq.client.core.IMqQueueExcutorService;
import com.ppdai.infrastructure.mq.client.core.impl.MqMeticReporterService;
import com.ppdai.infrastructure.mq.client.core.impl.MqTopicQueueRefreshService;
import com.ppdai.infrastructure.mq.client.event.RegisterConsumerGroupListener;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.factory.MqFactory;
import com.ppdai.infrastructure.mq.client.resolver.ISubscriberResolver;

public class MqClient {
	private static Logger log = LoggerFactory.getLogger(MqClient.class);
	private static AtomicBoolean initFlag = new AtomicBoolean(false);
	private static AtomicBoolean registerFlag = new AtomicBoolean(false);
	private static AtomicBoolean startFlag = new AtomicBoolean(false);
	private static AtomicBoolean asynFlag = new AtomicBoolean(false);
	private static BlockingQueue<PublishMessageRequest> msgsAsyn = null;
	private static IMqBrokerUrlRefreshService mqBrokerUrlRefreshService = null;
	private static IMqHeartbeatService mqHeartbeatService = null;
	private static IMqCheckService mqCheckService = null;
	private static MqContext mqContext = new MqContext();
	private static IConsumerPollingService consumerPollingService = null;
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 5L, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(50), SoaThreadFactory.create("MqClient", true),
			new ThreadPoolExecutor.CallerRunsPolicy());

	private static IMqFactory mqFactory = new MqFactory();
	private static MqEnvironment mqEnvironment = null;
	private static ISubscriberResolver subscriberResolver;
	private static Object lockObj = new Object();

	public static MqEnvironment getMqEnvironment() {
		return mqEnvironment;
	}

	public static void setMqEnvironment(MqEnvironment mqEnvironment) {
		MqClient.mqEnvironment = mqEnvironment;
		getContext().setMqEnvironment(mqEnvironment);
	}

	public static ISubscriberResolver getSubscriberResolver() {
		return subscriberResolver;
	}

	public static void setSubscriberResolver(ISubscriberResolver subscriberResolver) {
		MqClient.subscriberResolver = subscriberResolver;
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				close();
			}
		});
	}

	public static void registerInitEvent(Runnable runnable) {
		synchronized (lockObj) {
			getContext().getMqEvent().getInitCompleted().add(runnable);
		}
	}

	public static void registerPreHandleEvent(PreHandleListener preHandleListener1) {
		synchronized (lockObj) {
			getContext().getMqEvent().setPreHandleListener(preHandleListener1);
		}
	}

	public static void registerPostHandleEvent(PostHandleListener postHandleListener1) {
		synchronized (lockObj) {
			getContext().getMqEvent().setPostHandleListener(postHandleListener1);
		}
	}

	public static void registerConsumerGroupEvent(RegisterConsumerGroupListener postHandleListener1) {
		synchronized (lockObj) {
			getContext().getMqEvent().getRegisterConsumerGroupListeners().add(postHandleListener1);
		}
	}

	public static void registerISubscriberSelector(ISubscriberSelector iSubscriberSelector1) {
		synchronized (lockObj) {
			getContext().getMqEvent().setiSubscriberSelector(iSubscriberSelector1);
		}
	}

	public static void registerIAsynSubscriberSelector(IAsynSubscriberSelector iSubscriberSelector1) {
		synchronized (lockObj) {
			getContext().getMqEvent().setiAsynSubscriberSelector(iSubscriberSelector1);
		}
	}

	public static void registerCompletedEvent(Runnable runnable) {
		synchronized (lockObj) {
			getContext().getMqEvent().getRegisterCompleted().add(runnable);
		}
	}

	public static void registerMsgFilterEvent(IMsgFilter msgFilter) {
		synchronized (lockObj) {
			getContext().getMqEvent().getMsgFilters().add(msgFilter);
		}
	}

	public static void registerPreSendEvent(PreSendListener preSendListener) {
		synchronized (lockObj) {
			getContext().getMqEvent().getPreSendListeners().add(preSendListener);
		}
	}

	public static boolean publishAsyn(final String topic, final String token, List<ProducerDataDto> messages)
			throws MqNotInitException, ContentExceed65535Exception {
		return publishAsyn(topic, token, messages, null);
	}

	public static void setMqFactory(IMqFactory mqFactory) {
		MqClient.mqFactory = mqFactory;
	}

	public static boolean isAsynAvailable() {
		return msgsAsyn.remainingCapacity() > 0;
	}

	public static boolean start() {
		if (startFlag.compareAndSet(false, true)) {
			registerConsumerGroup();
		}
		return false;
	}

	public static boolean start(String brokerUrl) {
		if (mqContext.getConfig() == null) {
			MqConfig config = new MqConfig();
			config.setIp(IPUtil.getLocalIP());
			config.setMetaMode(true);
			config.setServerPort("");
			config.setUrl(brokerUrl);
			mqContext.setConfig(config);
		} else {
			if (Util.isEmpty(mqContext.getConfig().getUrl())) {
				mqContext.getConfig().setUrl(brokerUrl);
			}
		}
		return start(mqContext.getConfig());
	}

	public static boolean start(MqConfig config) {
		if (startFlag.compareAndSet(false, true)) {
			init(config);
			registerConsumerGroup();
			return true;
		}
		return false;
	}

	private static AtomicBoolean restartFlag = new AtomicBoolean(false);

	public static void reStart() {
		if (restartFlag.compareAndSet(false, true)) {
			executor.submit(new Runnable() {
				public void run() {
					try {
						// 因为删除close之前，所以需要先备份订阅信息
						Map<String, ConsumerGroupVo> consumerGroups = mqContext.getOrignConfig();
						close();
						if (consumerGroups != null && consumerGroups.size() > 0) {
							while (true) {
								try {
									registerConsumerGroup(consumerGroups);
									break;
								} catch (Exception e) {
									log.error("restart_error", e);
									registerFlag.set(false);
									Util.sleep(1000);
								}
							}
						}
					} catch (Exception e) {
						log.error("restart_error", e);
					} finally {
						restartFlag.set(false);
					}
				}
			});
		}
	}

	// start=init+registerconsumergroup

	public static void init(MqConfig config) {
		if (initFlag.compareAndSet(false, true)) {
			doInit(config);
			fireInitEvent();
			log.info("mq_client has  inited，初始化完成");
		}
	}

	public static boolean hasInit() {
		boolean flag = initFlag.get();
		if (!flag && getContext().getConfig() != null && !Util.isEmpty(getContext().getConfig().getUrl())) {
			synchronized (MqClient.class) {
				flag = initFlag.get();
				if (!flag && getContext().getConfig() != null && !Util.isEmpty(getContext().getConfig().getUrl())) {
					init(getContext().getConfig());
					flag = initFlag.get();
				}
			}
		}
		return flag;
	}

	private static void fireInitEvent() {
		mqContext.getMqEvent().getInitCompleted().forEach(t1 -> {
			try {
				t1.run();
			} catch (Exception e) {
				log.error("fireInitEvent error", e);
			}
		});
	}

	private static void fireRegisterEvent() {
		mqContext.getMqEvent().getRegisterCompleted().forEach(t1 -> {
			try {
				t1.run();
			} catch (Exception e) {
				log.error("fireRegisterEvent error", e);
			}
		});
	}

	private static void fireConsumerGroupRegisterEvent(ConsumerGroupVo vo) {
		mqContext.getMqEvent().getRegisterConsumerGroupListeners().forEach(t1 -> {
			try {
				t1.complete(vo);
			} catch (Exception e) {
				log.error("fireRegisterEvent error", e);
			}
		});
	}

	private static void doInit(MqConfig config) {
		mqContext.setConsumerName(
				ConsumerUtil.getConsumerId(config.getIp(), PropUtil.getProcessId() + "", config.getServerPort()));
		if (mqContext.getMqResource() == null) {
			mqContext.setMqResource(
					getMqFactory().createMqResource(config.getUrl(), config.getReadTimeOut(), config.getReadTimeOut()));
		}
		mqContext.setConfig(config);
		// if (config.getAsynCapacity() < 2000) {
		// log.info("异步缓冲队列至少2000！");
		// config.setAsynCapacity(2000);
		// }
		if (msgsAsyn == null) {
			msgsAsyn = new ArrayBlockingQueue<>(config.getAsynCapacity());
		}
		mqBrokerUrlRefreshService = mqFactory.createMqBrokerUrlRefreshService();
		mqBrokerUrlRefreshService.start();
	}

	private static void register() {
		if (registerFlag.compareAndSet(false, true)) {
			ConsumerRegisterRequest request = new ConsumerRegisterRequest();
			try {
				request.setName(mqContext.getConsumerName());
				// request.setSdkVersion(mqContext.getSdkVersion());
				request.setClientIp(mqContext.getConfig().getIp());
				mqContext.setConsumerId(mqContext.getMqResource().register(request));
				mqHeartbeatService = mqFactory.createMqHeartbeatService();
				mqHeartbeatService.start();
				// MqHeartbeatService.getInstance().start(mqContext);
				log.info("ConsumerName:" + mqContext.getConsumerName() + " has registed,注册成功！consumerId 为"
						+ mqContext.getConsumerId());
				fireRegisterEvent();
			} catch (Exception e) {
				registerFlag.set(false);
				log.error("register_error,注册失败，register_error,and json is {}", JsonUtil.toJson(request));
				throw new RuntimeException(e);
			}
		}
	}

	private static void deRegister() {
		if (mqContext.getConsumerId() > 0) {
			try {
				ConsumerDeRegisterRequest request = new ConsumerDeRegisterRequest();
				request.setId(mqContext.getConsumerId());
				mqContext.getMqResource().deRegister(request);
				mqContext.setConsumerId(0);
				log.info("ConsumerName:" + mqContext.getConsumerName() + "deresiter_suc,注销成功！");
			} catch (Exception e) {
				log.info("ConsumerName:" + mqContext.getConsumerName() + "注销失败,deRegister_error！");
			}
		}
	}

	private static boolean registerConsumerGroup() {
		Map<String, ConsumerGroupVo> localConfig = new ClientConfigHelper(mqContext).getConfig();
		return registerConsumerGroup(localConfig);
	}

	public static boolean registerConsumerGroup(Map<String, ConsumerGroupVo> groups) {
		if (groups == null || groups.size() == 0) {
			return false;
		}
		if (hasInit()) {
			log.info("已经初始化完成！");
			return doRegisterConsumerGroup(groups);
		} else {
			log.warn("系统为初始化，启动异步注册！");
			executor.execute(new Runnable() {

				public void run() {
					while (!hasInit()) {
						Util.sleep(2000);
					}
					try {
						doRegisterConsumerGroup(groups);
					} catch (Exception e) {
						log.error("doRegisterConsumerGroup_error", e);
					}
				}
			});
			return true;
		}
	}

	private static boolean doRegisterConsumerGroup(Map<String, ConsumerGroupVo> groups) {
		Map<String, List<String>> consumerGroupNames = new HashMap<>();
		String groupNames = "";
		for (ConsumerGroupVo consumerGroup : groups.values()) {
			if (!checkVaild(consumerGroup)) {
				return false;
			}
			if (mqContext.getConsumerGroupVersion().containsKey(consumerGroup.getMeta().getName())) {
				log.info("ConsumerGroup:" + consumerGroup.getMeta().getName() + " has  subscribed,已订阅！");
				return false;
			}
			if (Util.isEmpty(consumerGroup.getMeta().getOriginName())) {
				consumerGroup.getMeta().setOriginName(consumerGroup.getMeta().getName());
			}
			if (consumerGroup.getTopics() != null) {
				consumerGroupNames.put(consumerGroup.getMeta().getOriginName(),
						new ArrayList<>(consumerGroup.getTopics().keySet()));
			} else {
				consumerGroupNames.put(consumerGroup.getMeta().getOriginName(), new ArrayList<>());
			}
			groupNames += consumerGroup.getMeta().getName() + ",";
		}
		register();
		ConsumerGroupRegisterRequest request = new ConsumerGroupRegisterRequest();
		request.setConsumerGroupNames(consumerGroupNames);
		request.setConsumerId(mqContext.getConsumerId());
		request.setClientIp(mqContext.getConfig().getIp());
		request.setConsumerName(mqContext.getConsumerName());
		if (MqClient.getMqEnvironment() != null) {
			if (MqEnv.FAT == MqClient.getMqEnvironment().getEnv()) {
				request.setSubEnv(MqClient.getMqEnvironment().getSubEnv().toLowerCase());
			}
		}
		try {
			ConsumerGroupRegisterResponse consumerGroupRegisterResponse = mqContext.getMqResource()
					.registerConsumerGroup(request);
			if (consumerGroupRegisterResponse.isSuc()) {
				Map<String, String> broadcastConsumerGroupNames = consumerGroupRegisterResponse
						.getConsumerGroupNameNew();
				for (ConsumerGroupVo consumerGroup : groups.values()) {
					if (broadcastConsumerGroupNames != null
							&& broadcastConsumerGroupNames.containsKey(consumerGroup.getMeta().getOriginName())) {
						consumerGroup.getMeta()
								.setName(broadcastConsumerGroupNames.get(consumerGroup.getMeta().getOriginName()));
					}
					mqContext.getConfigConsumerGroup().put(consumerGroup.getMeta().getName(), consumerGroup);
					mqContext.getConsumerGroupVersion().put(consumerGroup.getMeta().getName(), 0L);
					fireConsumerGroupRegisterEvent(consumerGroup);
				}
				consumerPollingService = mqFactory.createConsumerPollingService();
				consumerPollingService.start();
				mqCheckService = mqFactory.createMqCheckService();
				mqCheckService.start();
				// MqCheckService.getInstance().start(mqContext);
				log.info(groupNames + "  subscribe_suc,订阅成功！ and json is " + JsonUtil.toJson(request));
			} else {
				throw new RuntimeException("registerConsumerGroup_error, the req is" + JsonUtil.toJsonNull(request)
						+ ",and resp is " + JsonUtil.toJson(consumerGroupRegisterResponse));
			}
		} catch (Exception e) {
			log.error("consumer_group_register_error", e);
			throw new RuntimeException(e);
		}
		return true;
	}

	public static synchronized boolean registerConsumerGroup(ConsumerGroupVo consumerGroup) {
		Map<String, ConsumerGroupVo> groups = new HashMap<>();
		if (!checkVaild(consumerGroup))
			return false;
		groups.put(consumerGroup.getMeta().getName(), consumerGroup);
		return registerConsumerGroup(groups);
	}

	private static boolean checkVaild(ConsumerGroupVo localConfig) {
		if (localConfig == null) {
			throw new IllegalArgumentException("ConsumerGroupVo can't be null,不能为空");
		}
		if (localConfig.getMeta() == null || Util.isEmpty(localConfig.getMeta().getName())) {
			throw new IllegalArgumentException("ConsumerGroupName can't be null,不能为空");
		}
		return true;
	}

	public static boolean publish(final String topic, final String token, ProducerDataDto message)
			throws MqNotInitException, ContentExceed65535Exception {

		return publish(topic, token, Arrays.asList(message));

	}

	public static boolean publish(final String topic, final String token, List<ProducerDataDto> messages)
			throws MqNotInitException, ContentExceed65535Exception {
		return publish(topic, token, messages, null);
	}

	public static boolean publish(String topic, String token, ProducerDataDto message,
			IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub
		return publish(topic, token, Arrays.asList(message), iPartitionSelector);
	}

	public static boolean publish(String topic, String token, List<ProducerDataDto> messages,
			IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
		// TODO Auto-generated method stub
		if (!hasInit()) {
			throw new MqNotInitException();
		}
		PublishMessageRequest request = null;
		if (messages != null && messages.size() > 0) {
			for (ProducerDataDto t1 : messages) {
				if (MessageUtil.checkMessageExceed65535(t1.getBody())) {
					throw new ContentExceed65535Exception();
				}
				t1.setPartitionInfo(getPartitionId(topic, t1, iPartitionSelector));
			}
			try {
				request = new PublishMessageRequest();
				request.setClientIp(mqContext.getConfig().getIp());
				request.setToken(token);
				request.setTopicName(topic);
				request.setMsgs(messages);
				checkBody(messages);
				return mqContext.getMqResource().publish(request, mqContext.getConfig().getPbRetryTimes());
			} catch (Exception e) {
				log.error("publish_error,and request json is " + JsonUtil.toJsonNull(request), e);
				return false;
			}
		}
		return true;
	}

	public static boolean publishAsyn(final String topic, final String token, List<ProducerDataDto> messages,
			IPartitionSelector iPartitionSelector) throws ContentExceed65535Exception {
		MqTopicQueueRefreshService.getInstance().start();
		if (messages != null && messages.size() > 0) {

			for (ProducerDataDto t1 : messages) {
				if (MessageUtil.checkMessageExceed65535(t1.getBody())) {
					throw new ContentExceed65535Exception();
				}
				t1.setPartitionInfo(getPartitionId(topic, t1, iPartitionSelector));
			}
			PublishMessageRequest request = null;
			try {
				request = new PublishMessageRequest();
				request.setClientIp(mqContext.getConfig().getIp());
				request.setToken(token);
				request.setTopicName(topic);
				request.setMsgs(messages);
				request.setSynFlag(0);
				boolean rs = msgsAsyn.offer(request);
				publishAsyn();
				return rs;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	private static PartitionInfo getPartitionId(String topic, ProducerDataDto message,
			IPartitionSelector iPartitionSelector) {
		try {
			if (iPartitionSelector != null && !Util.isEmpty(topic)) {
				PartitionInfo partitionInfo = iPartitionSelector.getPartitionId(topic, message,
						MqTopicQueueRefreshService.getInstance().getTopicQueueIds(topic));
				return partitionInfo;
			}
		} catch (Exception e) {
			log.error("getPartitionId_error", e);
		}
		return null;
	}

	private static void publishAsyn() {
		if (asynFlag.compareAndSet(false, true)) {
			executor.submit(new Runnable() {
				public void run() {
					PublishMessageRequest preRequest = null;
					PublishMessageRequest request = null;
					long lastTime = System.currentTimeMillis();
					while (true) {
						if (!hasInit()) {
							Util.sleep(50);
						}
						try {
							request = msgsAsyn.poll();
						} catch (Exception e) {

						}
						try {
							if (request != null) {
								if (preRequest != null && request.getTopicName().equals(preRequest.getTopicName())) {
									checkBody(request.getMsgs());
									preRequest.getMsgs().addAll(request.getMsgs());									
									if (preRequest.getMsgs().size() > 10 || System.currentTimeMillis()
											- lastTime > mqContext.getConfig().getPublishAsynTimeout()) {
										mqContext.getMqResource().publish(preRequest,
												mqContext.getConfig().getPbRetryTimes());
										lastTime = System.currentTimeMillis();
										preRequest = null;
									}

								} else if (preRequest != null) {									
									checkBody(request.getMsgs());
									mqContext.getMqResource().publish(preRequest,
											mqContext.getConfig().getPbRetryTimes());
									mqContext.getMqResource().publish(request, mqContext.getConfig().getPbRetryTimes());
									lastTime = System.currentTimeMillis();
									preRequest = null;
								} else {
									checkBody(request.getMsgs());
									preRequest = request;
								}
							} else if (preRequest != null) {
								checkBody(preRequest.getMsgs());
								mqContext.getMqResource().publish(preRequest, mqContext.getConfig().getPbRetryTimes());
								lastTime = System.currentTimeMillis();
								preRequest = null;
							} else {
								Util.sleep(10);
							}
						} catch (Exception e) {
							log.error("publish_aysn_error,and request json is " + JsonUtil.toJsonNull(request), e);
						}
					}
				}
			});
		}

	}

	private static void doPublishAsyn() {
		long startTime = System.currentTimeMillis();
		while (msgsAsyn != null && !msgsAsyn.isEmpty() && System.currentTimeMillis() - startTime < 10000) {
			PublishMessageRequest request = null;
			try {
				request = msgsAsyn.poll();
				// System.out.println("t-"+msgsAsyn.size());
			} catch (Exception e) {

			}
			if (request != null) {
				mqContext.getMqResource().publish(request, mqContext.getConfig().getPbRetryTimes());
			}
		}
	}

	private static void checkBody(List<ProducerDataDto> msgs) {
		for (ProducerDataDto t1 : msgs) {
			checkBody(t1);
		}
	}

	public static boolean publishAsyn(final String topic, final String token, ProducerDataDto message)
			throws ContentExceed65535Exception {
		return publishAsyn(topic, token, Arrays.asList(message), null);
	}

	public static boolean publishAsyn(final String topic, final String token, ProducerDataDto message,
			IPartitionSelector iPartitionSelector) throws ContentExceed65535Exception {
		return publishAsyn(topic, token, Arrays.asList(message), iPartitionSelector);
	}

	public static MqContext getContext() {
		return mqContext;
	}

	public static long fetchMessageCount(String groupName, List<String> topicNames) {
		if (groupName == null || groupName.trim().length() == 0) {
			throw new RuntimeException("groupName不能为空！");
		}
		GetMessageCountRequest request = new GetMessageCountRequest();
		request.setConsumerGroupName(groupName);
		request.setTopics(topicNames);
		GetMessageCountResponse response = mqContext.getMqResource().getMessageCount(request);
		if (response != null) {
			return response.getCount();
		}
		throw new RuntimeException("获取消息数量异常！");

	}

	// 此close表示退出消费
	public static void close() {
		Transaction transaction = Tracer.newTransaction("mq-client", "close-client");
		try {
			doPublishAsyn();
			if (consumerPollingService != null) {
				consumerPollingService.close();
				consumerPollingService = null;
			}
			// ConsumerPollingService.getInstance().close();
			deRegister();
			if (mqBrokerUrlRefreshService != null) {
				mqBrokerUrlRefreshService.close();
				mqBrokerUrlRefreshService = null;
			}
			// MqBrokerUrlRefreshService.getInstance().close();
			if (mqCheckService != null) {
				mqCheckService.close();
				mqCheckService = null;
			}
			// MqCheckService.getInstance().close();
			if (mqHeartbeatService != null) {
				mqHeartbeatService.close();
				mqHeartbeatService = null;
			}
			// MqHeartbeatService.getInstance().close();
			MqMeticReporterService.getInstance().close();
			mqContext.clear();
			// initFlag.set(false);
			registerFlag.set(false);
			startFlag.set(false);
			// asynFlag.set(false);
			mqFactory = new MqFactory();
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			transaction.setStatus(e);
		} finally {
			transaction.complete();
		}
	}

	public static List<String> getTopic(String consumerGroupName) {
		if (Util.isEmpty(consumerGroupName)) {
			return new ArrayList<>();
		}
		GetTopicRequest request = new GetTopicRequest();
		request.setConsumerGroupName(consumerGroupName);
		GetTopicResponse response = getContext().getMqResource().getTopic(request);
		if (response == null) {
			return new ArrayList<>();
		} else {
			return response.getTopics();
		}
	}

	public static List<GroupTopicDto> getGroupTopic(List<String> consumerGroupNames) {
		if (consumerGroupNames == null || consumerGroupNames.size() == 0) {
			return new ArrayList<>();
		}
		GetGroupTopicRequest request = new GetGroupTopicRequest();
		request.setConsumerGroupNames(consumerGroupNames);
		GetGroupTopicResponse response = getContext().getMqResource().getGroupTopic(request);
		if (response == null) {
			return new ArrayList<>();
		} else {
			return response.getGroupTopics();
		}
	}

	public static IMqFactory getMqFactory() {
		// TODO Auto-generated method stub
		return mqFactory;
	}

	public static void checkBody(ProducerDataDto producerDataDto) {
		mqContext.getMqEvent().getPreSendListeners().forEach(t1 -> {
			try {
				t1.onPreSend(producerDataDto);
			} catch (Exception e) {
				log.error("onPreSend_error", e);
			}
		});
	}

	public static void commit(List<MessageDto> failMsgs, ConsumerQueueDto consumerQueue) {
		IConsumerPollingService consumerPollingService = MqClient.getMqFactory().createConsumerPollingService();
		Map<String, IMqGroupExcutorService> groups = consumerPollingService.getMqExcutors();
		if (groups != null) {
			if (groups.containsKey(consumerQueue.getConsumerGroupName())) {
				IMqGroupExcutorService iMqGroupExcutorService = groups.get(consumerQueue.getConsumerGroupName());
				Map<Long, IMqQueueExcutorService> queues = iMqGroupExcutorService.getQueueEx();
				if (queues.containsKey(consumerQueue.getQueueId())) {
					queues.get(consumerQueue.getQueueId()).commit(failMsgs, consumerQueue);
				}
			}
		}
	}
}
