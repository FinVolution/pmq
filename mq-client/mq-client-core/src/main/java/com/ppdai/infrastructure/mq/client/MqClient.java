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

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.PropUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
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
import com.ppdai.infrastructure.mq.biz.event.IPartitionSelector;
import com.ppdai.infrastructure.mq.biz.event.ISubscriberSelector;
import com.ppdai.infrastructure.mq.biz.event.PartitionInfo;
import com.ppdai.infrastructure.mq.biz.event.PostHandleListener;
import com.ppdai.infrastructure.mq.biz.event.PreHandleListener;
import com.ppdai.infrastructure.mq.client.config.ClientConfigHelper;
import com.ppdai.infrastructure.mq.client.config.ConsumerGroupVo;
import com.ppdai.infrastructure.mq.client.core.IConsumerPollingService;
import com.ppdai.infrastructure.mq.client.core.IMqBrokerUrlRefreshService;
import com.ppdai.infrastructure.mq.client.core.IMqCheckService;
import com.ppdai.infrastructure.mq.client.core.IMqHeartbeatService;
import com.ppdai.infrastructure.mq.client.core.impl.MqMeticReporterService;
import com.ppdai.infrastructure.mq.client.core.impl.MqTopicQueueRefreshService;
import com.ppdai.infrastructure.mq.client.event.RegisterConsumerGroupListener;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;
import com.ppdai.infrastructure.mq.client.factory.IMqFactory;
import com.ppdai.infrastructure.mq.client.factory.MqFactory;

public class MqClient {
	private static Logger log = LoggerFactory.getLogger(MqClient.class);
	private static IMqClientBase instance = new MqClientBase();

	private static IMqClientBase getInstance() {
		return instance;
	}

	protected static void setInstance(IMqClientBase instance1) {
		instance = instance1;
	}

	public static synchronized void registerInitEvent(Runnable runnable) {
		getInstance().getContext().getMqEvent().getInitCompleted().add(runnable);
	}

	public static synchronized void registerPreHandleEvent(PreHandleListener preHandleListener1) {
		getInstance().getContext().getMqEvent().setPreHandleListener(preHandleListener1);
	}

	public static synchronized void registerPostHandleEvent(PostHandleListener postHandleListener1) {
		getInstance().getContext().getMqEvent().setPostHandleListener(postHandleListener1);
	}

	public static synchronized void registerConsumerGroupEvent(RegisterConsumerGroupListener postHandleListener1) {
		getInstance().getContext().getMqEvent().getRegisterConsumerGroupListeners().add(postHandleListener1);
	}

	public static synchronized void registerISubscriberSelector(ISubscriberSelector iSubscriberSelector1) {
		getInstance().getContext().getMqEvent().setiSubscriberSelector(iSubscriberSelector1); 
	}

	public static synchronized void registerCompletedEvent(Runnable runnable) {
		getInstance().getContext().getMqEvent().getRegisterCompleted().add(runnable);
	}

	// 快捷方式
	public static synchronized boolean start(String url) {
		boolean flag = getInstance().start(url);
		return flag;
	}

	public static synchronized boolean start() {
		boolean flag = getInstance().start();
		return flag;
	}

	public static synchronized boolean start(MqConfig config) {
		boolean flag = getInstance().start(config);
		return flag;
	}

	// 初始化数据
	public static void init(MqConfig config) {
		getInstance().init(config);
	}

	public static boolean hasInit() {
		return getInstance().hasInit();
	}

	public static synchronized void reStart() {
		getInstance().reStart();
	}

	public static boolean isAsynAvailable() {
		return getInstance().isAsynAvailable();
	}

	public static synchronized boolean registerConsumerGroup(ConsumerGroupVo consumerGroup) {
		return getInstance().registerConsumerGroup(consumerGroup);
	}

	public static boolean publish(final String topic, final String token, ProducerDataDto message)
			throws MqNotInitException, ContentExceed65535Exception {
		return publish(topic, token, Arrays.asList(message));
	}

	public static boolean publish(final String topic, final String token, List<ProducerDataDto> messages)
			throws MqNotInitException, ContentExceed65535Exception {
		return publish(topic, token, messages,null);
	}
	public static boolean publish(final String topic, final String token, ProducerDataDto message,IPartitionSelector iPartitionSelector)
			throws MqNotInitException, ContentExceed65535Exception {
		return publish(topic, token, Arrays.asList(message),iPartitionSelector);
	}

	public static boolean publish(final String topic, final String token, List<ProducerDataDto> messages,IPartitionSelector iPartitionSelector)
			throws MqNotInitException, ContentExceed65535Exception {
		return getInstance().publish(topic, token, messages,iPartitionSelector);
	}
	public static void publishAsyn(final String topic, final String token, ProducerDataDto message)
			throws MqNotInitException, ContentExceed65535Exception {
		publishAsyn(topic, token, Arrays.asList(message));
	}

	public static void publishAsyn(final String topic, final String token, List<ProducerDataDto> messages)
			throws MqNotInitException, ContentExceed65535Exception {
		publishAsyn(topic, token, messages, null);
	}

	public static void publishAsyn(final String topic, final String token, ProducerDataDto message,
			IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
		publishAsyn(topic, token, Arrays.asList(message), iPartitionSelector);
	}

	public static void publishAsyn(final String topic, final String token, List<ProducerDataDto> messages,
			IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
		getInstance().publishAsyn(topic, token, messages, iPartitionSelector);
	}

	public static MqContext getContext() {
		return getInstance().getContext();
	}

	public static long fetchMessageCount(String groupName, List<String> topicNames) {
		// 防止用户高频拉取消息
		Util.sleep(1000);
		return getInstance().fetchMessageCount(groupName, topicNames);
	}

	public synchronized static void close() {
		getInstance().close();
		log.info("mq3 客户端关闭！");
	}

	public synchronized static void stop() {
		getInstance().stop();
		log.info("mq3 客户端stop！");
	}

	public static List<String> getTopic(String consumerGroupName) {
		return getInstance().getTopic(consumerGroupName);
	}

	public static List<GroupTopicDto> getGroupTopic(List<String> consumerGroupNames) {
		return getInstance().getGroupTopic(consumerGroupNames);
	}

	public static class MqClientBase implements IMqClientBase {
		private Logger log = LoggerFactory.getLogger(MqClientBase.class);
		private MqContext mqContext = new MqContext();
		private AtomicBoolean initFlag = new AtomicBoolean(false);
		private AtomicBoolean registerFlag = new AtomicBoolean(false);
		private AtomicBoolean startFlag = new AtomicBoolean(false);
		private AtomicBoolean asynFlag = new AtomicBoolean(false);

		private BlockingQueue<PublishMessageRequest> msgsAsyn = null;
		private IMqBrokerUrlRefreshService mqBrokerUrlRefreshService = null;
		private IMqHeartbeatService mqHeartbeatService = null;
		private IMqCheckService mqCheckService = null;
		private IConsumerPollingService consumerPollingService = null;

		private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 5L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(50), SoaThreadFactory.create("MqClientBase", true),
				new ThreadPoolExecutor.CallerRunsPolicy());

		private IMqFactory mqFactory = null;

		public MqClientBase() {
			mqFactory = new MqFactory();
		}

		public MqClientBase(IMqFactory mqFactory) {
			this.mqFactory = mqFactory;
		}

		@Override
		public boolean isAsynAvailable() {
			return msgsAsyn.remainingCapacity() > 0;
		}

		@Override
		public boolean start() {
			return start(true);
		}

		// 是否自动注册消费者组
		private boolean start(boolean autoRegist) {
			if (startFlag.compareAndSet(false, true)) {
				// init(config,new MqResource(config.getUrl()));
				if (autoRegist) {
					registerConsumerGroup();
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean start(String brokerUrl) {
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

		@Override
		public boolean start(MqConfig config) {
			if (startFlag.compareAndSet(false, true)) {
				init(config);
				registerConsumerGroup();
				return true;
			}
			return false;
		}

		@Override
		public void reStart() {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					// 因为删除close之前，所以需要先备份订阅信息
					Map<String, ConsumerGroupVo> consumerGroups = mqContext.getOrignConfig();
					stop();
					// 注册接口订阅关系保持不变
					// init(mqContext.getConfig());
					if (consumerGroups == null || consumerGroups.size() == 0) {
						start();
					} else {
						start(false);
						registerConsumerGroup(consumerGroups);
					}
				}
			});
		}

		// start=init+registerconsumergroup
		@Override
		public void init(MqConfig config) {
			if (initFlag.compareAndSet(false, true)) {
				doInit(config);
				fireInitEvent();
				log.info("mq_client has  inited，初始化完成");
			}
		}

		@Override
		public boolean hasInit() {
			return initFlag.get();
		}

		private void fireInitEvent() {
			mqContext.getMqEvent().getInitCompleted().forEach(t1 -> {
				try {
					t1.run();
				} catch (Exception e) {
					log.error("fireInitEvent error", e);
				}
			});
		}

		private void fireRegisterEvent() {
			mqContext.getMqEvent().getRegisterCompleted().forEach(t1 -> {
				try {
					t1.run();
				} catch (Exception e) {
					log.error("fireRegisterEvent error", e);
				}
			});
		}

		private void fireConsumerGroupRegisterEvent(ConsumerGroupVo vo) {
			mqContext.getMqEvent().getRegisterConsumerGroupListeners().forEach(t1 -> {
				try {
					t1.complete(vo);
				} catch (Exception e) {
					log.error("fireRegisterEvent error", e);
				}
			});
		}

		private void doInit(MqConfig config) {
			if (!Util.isEmpty(config.getServerPort())) {
				if (config.getServerPort().indexOf('|') != -1) {
					mqContext.setConsumerName(String.format("%s|%s|%s%s", config.getIp(), PropUtil.getProcessId(),
							System.currentTimeMillis() % 10000, config.getServerPort()));
				} else {
					mqContext.setConsumerName(String.format("%s|%s|%s|%s", config.getIp(), PropUtil.getProcessId(),
							System.currentTimeMillis() % 10000, config.getServerPort()));
				}
			} else {
				mqContext.setConsumerName(String.format("%s|%s|%s", config.getIp(), PropUtil.getProcessId(),
						System.currentTimeMillis() % 10000));
			}
			if (mqContext.getMqResource() == null) {
				mqContext.setMqResource(getMqFactory().createMqResource(config.getUrl(), config.getReadTimeOut(),
						config.getReadTimeOut()));
			}
			mqContext.setConfig(config);
//			if (config.getAsynCapacity() < 2000) {
//				log.info("异步缓冲队列至少2000！");
//				config.setAsynCapacity(2000);
//			}
			msgsAsyn = new ArrayBlockingQueue<>(config.getAsynCapacity());

			mqBrokerUrlRefreshService = mqFactory.createMqBrokerUrlRefreshService(this);
			mqBrokerUrlRefreshService.start();
		}

		private void register() {
			if (registerFlag.compareAndSet(false, true)) {
				ConsumerRegisterRequest request = new ConsumerRegisterRequest();
				try {
					request.setLan(mqContext.getLan());
					request.setName(mqContext.getConsumerName());
					// request.setSdkVersion(mqContext.getSdkVersion());
					request.setClientIp(mqContext.getConfig().getIp());
					mqContext.setConsumerId(mqContext.getMqResource().register(request));
					mqHeartbeatService = mqFactory.createMqHeartbeatService(this);
					mqHeartbeatService.start();
					// MqHeartbeatService.getInstance().start(mqContext);
					log.info("ConsumerName:" + mqContext.getConsumerName() + " has registed,注册成功！consumerId 为"
							+ mqContext.getConsumerId());
					fireRegisterEvent();
				} catch (Exception e) {
					log.error("register_error,注册失败，register_error,and json is {}", JsonUtil.toJson(request));
					throw new RuntimeException(e);
				}
			}
		}

		private void deRegister() {
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

		private synchronized boolean registerConsumerGroup() {
			Map<String, ConsumerGroupVo> localConfig = new ClientConfigHelper(mqContext).getConfig();
			return registerConsumerGroup(localConfig);
		}

		@Override
		public synchronized boolean registerConsumerGroup(Map<String, ConsumerGroupVo> groups) {
			if (groups == null || groups.size() == 0) {
				return false;
			}
			if (hasInit()) {
				log.info("已经初始化完成！");
				return doRegisterConsumerGroup(groups);
			} else {
				log.warn("系统为初始化，启动异步注册！");
				executor.execute(new Runnable() {
					@Override
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

		private boolean doRegisterConsumerGroup(Map<String, ConsumerGroupVo> groups) {
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
				consumerGroupNames.put(consumerGroup.getMeta().getOriginName(),
						new ArrayList<>(consumerGroup.getTopics().keySet()));
				groupNames += consumerGroup.getMeta().getName() + ",";
			}
			register();
			ConsumerGroupRegisterRequest request = new ConsumerGroupRegisterRequest();
			request.setConsumerGroupNames(consumerGroupNames);
			request.setConsumerId(mqContext.getConsumerId());
			request.setClientIp(mqContext.getConfig().getIp());
			request.setConsumerName(mqContext.getConsumerName());
			try {
				ConsumerGroupRegisterResponse consumerGroupRegisterResponse = mqContext.getMqResource()
						.registerConsumerGroup(request);
				if (consumerGroupRegisterResponse.isSuc()) {
					Map<String, String> broadcastConsumerGroupNames = consumerGroupRegisterResponse
							.getBroadcastConsumerGroupName();
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
					consumerPollingService = mqFactory.createConsumerPollingService(this);
					consumerPollingService.start();
					mqCheckService = mqFactory.createMqCheckService(this);
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

		@Override
		public synchronized boolean registerConsumerGroup(ConsumerGroupVo consumerGroup) {
			Map<String, ConsumerGroupVo> groups = new HashMap<>();
			if (!checkVaild(consumerGroup))
				return false;
			groups.put(consumerGroup.getMeta().getName(), consumerGroup);
			return registerConsumerGroup(groups);
		}

		private boolean checkVaild(ConsumerGroupVo localConfig) {
			if (localConfig == null) {
				throw new IllegalArgumentException("ConsumerGroupVo can't be null,不能为空");
			}
			if (localConfig.getMeta() == null || Util.isEmpty(localConfig.getMeta().getName())) {
				throw new IllegalArgumentException("ConsumerGroupName can't be null,不能为空");
			}
			if (localConfig.getTopics() == null || localConfig.getTopics().size() == 0) {
				throw new IllegalArgumentException("ConsumerGroupTopic can't be null,不能为空");
			}			
			return true;
		}

		@Override
		public boolean publish(final String topic, final String token, ProducerDataDto message)
				throws MqNotInitException, ContentExceed65535Exception {
			
			return publish(topic, token, Arrays.asList(message));
				
		}

		@Override
		public boolean publish(final String topic, final String token, List<ProducerDataDto> messages)
				throws MqNotInitException, ContentExceed65535Exception {
			return publish(topic, token, messages,null);
		}
		
		@Override
		public boolean publish(String topic, String token, ProducerDataDto message,
				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception {
			// TODO Auto-generated method stub
			return publish(topic, token, Arrays.asList(message),iPartitionSelector);
		}

		@Override
		public boolean publish(String topic, String token, List<ProducerDataDto> messages,
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
					log.error("publish_error", e);
					return false;
				}
			}
			return true;
		}

		@Override
		public void publishAsyn(final String topic, final String token, List<ProducerDataDto> messages,
				IPartitionSelector iPartitionSelector) throws ContentExceed65535Exception {
			MqTopicQueueRefreshService.getInstance(this).start();
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
					msgsAsyn.offer(request);
					publishAsyn();
				} catch (Exception e) {
				}
			}
		}

		private PartitionInfo getPartitionId(String topic, ProducerDataDto message,
				IPartitionSelector iPartitionSelector) {
			try {
				if (iPartitionSelector != null && !Util.isEmpty(topic)) {
					PartitionInfo partitionInfo = iPartitionSelector.getPartitionId(topic, message,
							MqTopicQueueRefreshService.getInstance(this).getTopicQueueIds(topic));
					return partitionInfo;
				}
			} catch (Exception e) {
				log.error("getPartitionId_error", e);
			}
			return null;
		}

		private void publishAsyn() {
			if (asynFlag.compareAndSet(false, true)) {
				executor.submit(new Runnable() {
					@Override
					public void run() {
						PublishMessageRequest preRequest = null;
						PublishMessageRequest request = null;
						while (true) {
							if (!hasInit()) {
								Util.sleep(50);
							}
							try {
								request = msgsAsyn.poll();
								// System.out.println("t-"+msgsAsyn.size());
							} catch (Exception e) {

							}
							try {
								if (request != null) {
									if (preRequest != null
											&& request.getTopicName().equals(preRequest.getTopicName())) {
										request.getMsgs().addAll(preRequest.getMsgs());
										checkBody(request.getMsgs());
										mqContext.getMqResource().publish(request,
												mqContext.getConfig().getPbRetryTimes());
										preRequest = null;
									} else if (preRequest != null) {
										checkBody(preRequest.getMsgs());
										checkBody(request.getMsgs());
										mqContext.getMqResource().publish(preRequest,
												mqContext.getConfig().getPbRetryTimes());
										mqContext.getMqResource().publish(request,
												mqContext.getConfig().getPbRetryTimes());
										preRequest = null;
									} else {
										preRequest = request;
									}
								} else if (preRequest != null) {
									checkBody(preRequest.getMsgs());
									mqContext.getMqResource().publish(preRequest,
											mqContext.getConfig().getPbRetryTimes());
									preRequest = null;
								} else {
									Util.sleep(10);
								}
							} catch (Exception e) {

							}
						}
					}
				});
			}

		}

		private void checkBody(List<ProducerDataDto> msgs) {
			for (ProducerDataDto t1 : msgs) {
				checkBody(t1);
			}
		}

		@Override
		public void publishAsyn(final String topic, final String token, ProducerDataDto message) {
			if (message != null) {
				try {
					publishAsyn(topic, token, Arrays.asList(message), null);
				} catch (Exception e) {

				}
			}
		}

		@Override
		public void publishAsyn(final String topic, final String token, ProducerDataDto message,
				IPartitionSelector iPartitionSelector) {
			if (message != null) {
				try {
					publishAsyn(topic, token, Arrays.asList(message), iPartitionSelector);
				} catch (Exception e) {

				}
			}
		}

		@Override
		public MqContext getContext() {
			return mqContext;
		}

		@Override
		public long fetchMessageCount(String groupName, List<String> topicNames) {
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

		@Override
		// 此close表示退出消费
		public void close() {
			Transaction transaction = Tracer.newTransaction("mq-client2", "close-client");
			if (consumerPollingService != null) {
				consumerPollingService.close();
			}
			// ConsumerPollingService.getInstance().close();
			deRegister();
			if (mqBrokerUrlRefreshService != null) {
				mqBrokerUrlRefreshService.close();
			}
			// MqBrokerUrlRefreshService.getInstance().close();
			if (mqCheckService != null) {
				mqCheckService.close();
			}
			// MqCheckService.getInstance().close();
			if (mqHeartbeatService != null) {
				mqHeartbeatService.close();
			}
			// MqHeartbeatService.getInstance().close();
			MqMeticReporterService.getInstance(this).close();
			mqContext.clear();
			// initFlag.set(false);
			registerFlag.set(false);
			startFlag.set(false);
			asynFlag.set(false);
			transaction.setStatus(Transaction.SUCCESS);
			transaction.complete();
		}

		@Override
		public void stop() {
			// stop与close的区别是stop 后面可能会start
			Map<String, ConsumerGroupVo> consumerGroups = mqContext.getOrignConfig();
			close();
			mqContext.setConfigConsumerGroup(consumerGroups);
		}

		@Override
		public List<String> getTopic(String consumerGroupName) {
			if (Util.isEmpty(consumerGroupName)) {
				return new ArrayList<>();
			}
			GetTopicRequest request = new GetTopicRequest();
			request.setConsumerGroupName(consumerGroupName);
			GetTopicResponse response = this.getContext().getMqResource().getTopic(request);
			if (response == null) {
				return new ArrayList<>();
			} else {
				return response.getTopics();
			}
		}

		@Override
		public List<GroupTopicDto> getGroupTopic(List<String> consumerGroupNames) {
			if (consumerGroupNames == null || consumerGroupNames.size() == 0) {
				return new ArrayList<>();
			}
			GetGroupTopicRequest request = new GetGroupTopicRequest();
			request.setConsumerGroupNames(consumerGroupNames);
			GetGroupTopicResponse response = this.getContext().getMqResource().getGroupTopic(request);
			if (response == null) {
				return new ArrayList<>();
			} else {
				return response.getGroupTopics();
			}
		}

		@Override
		public IMqFactory getMqFactory() {
			// TODO Auto-generated method stub
			return mqFactory;
		}

		@Override
		public void checkBody(ProducerDataDto message) {
			// TODO Auto-generated method stub

		}

		

	}

	public static interface IMqClientBase {

		boolean isAsynAvailable();

		boolean start();

		boolean start(String brokerUrl);

		boolean start(MqConfig config);

		void reStart();

		void init(MqConfig config);

		boolean hasInit();

		boolean registerConsumerGroup(Map<String, ConsumerGroupVo> groups);

		boolean registerConsumerGroup(ConsumerGroupVo consumerGroup);

		void checkBody(ProducerDataDto message);

		boolean publish(final String topic, final String token, ProducerDataDto message)
				throws MqNotInitException, ContentExceed65535Exception;

		boolean publish(final String topic, final String token, List<ProducerDataDto> messages)
				throws MqNotInitException, ContentExceed65535Exception;
		
		boolean publish(final String topic, final String token, ProducerDataDto message,IPartitionSelector iPartitionSelector)
				throws MqNotInitException, ContentExceed65535Exception;

		boolean publish(final String topic, final String token, List<ProducerDataDto> messages,IPartitionSelector iPartitionSelector)
				throws MqNotInitException, ContentExceed65535Exception;

		void publishAsyn(final String topic, final String token, List<ProducerDataDto> messages,
				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception;

		void publishAsyn(final String topic, final String token, ProducerDataDto message)
				throws MqNotInitException, ContentExceed65535Exception;

		void publishAsyn(final String topic, final String token, ProducerDataDto message,
				IPartitionSelector iPartitionSelector) throws MqNotInitException, ContentExceed65535Exception;

		MqContext getContext();

		long fetchMessageCount(String groupName, List<String> topicNames);

		// 此close表示退出消费
		void stop();

		void close();

		List<String> getTopic(String consumerGroupName);

		List<GroupTopicDto> getGroupTopic(List<String> consumerGroupNames);

		IMqFactory getMqFactory();
	}

}
