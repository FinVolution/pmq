package com.ppdai.infrastructure.mq.biz.polling;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.metric.MetricSingleton;
import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.EmailService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.impl.TopicServiceImpl;

/**
 * 消息堆积告警定时检查服务
 *
 */
@Service
public class MessageLagNotifyService extends AbstractTimerService {
	private static final Logger logger = LoggerFactory.getLogger(MessageLagNotifyService.class);
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	@Autowired
	private ConsumerGroupTopicService consumerGroupTopicService;
	@Autowired
	private TopicServiceImpl topicService;
	@Autowired
	private QueueService queueService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private SoaConfig soaConfig;

	@PostConstruct
	private void init() {
		super.init(Constants.MESSAGE_LANGN, soaConfig.getMessageLagNotifyCheckInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getMessageLagNotifyCheckInterval();
			@Override
			public void run() {
				if (soaConfig.getMessageLagNotifyCheckInterval() != interval) {
					interval = soaConfig.getMessageLagNotifyCheckInterval();
					updateInterval(interval);
				}

			}
		});
	}

	@Override
	public void doStart() {
		try {
			// 获取缓存数据
			Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
			Map<String, TopicEntity> topicMap = topicService.getCache();
			Map<Long, Map<String, ConsumerGroupTopicEntity>> consumerGroupTopicMap = consumerGroupTopicService
					.getCache();
			List<QueueOffsetEntity> queueOffsetList = queueOffsetService.getCacheData();
			Map<Long, QueueEntity> queueMap = queueService.getAllQueueMap();
			Map<Long, Long> queueMaxIdMap = queueService.getMax();
			logger.info("message_lag_check_begin");
			int count = 0;
			int tcount = queueOffsetList.size();
			for (QueueOffsetEntity queueOffset : queueOffsetList) {
				try {
					count++;
					String qlog = queueOffset.getConsumerGroupName() + "," + queueOffset.getTopicName() + ","
							+ queueOffset.getQueueId() + ",queueOffset," + count + " of " + tcount;
					logger.info("message_lag_check_" + qlog);
					ConsumerGroupEntity consumerGroupEntity = consumerGroupMap.get(queueOffset.getConsumerGroupName());
					if(consumerGroupEntity.getMode()==2&&consumerGroupEntity.getName().equals(consumerGroupEntity.getOriginName())) {						
						continue;
					}
					// consumerGroupEntity不存在或者该consumerGroupEntity不允许告警
					if (consumerGroupEntity == null || consumerGroupEntity.getAlarmFlag() != 1) {
						logger.info("message_lag_check_ConsumerGroup_null," + qlog);
						continue;
					}
					if (!queueMap.containsKey(queueOffset.getQueueId())) {
						logger.info("message_lag_check_queue_null," + qlog);
						continue;
					}
					QueueEntity queueEntity = queueMap.get(queueOffset.getQueueId());
					if (!queueMaxIdMap.containsKey(queueOffset.getQueueId())) {
						logger.info("message_lag_check_queueMaxId_null," + qlog);
						continue;
					}
					long maxId = queueMaxIdMap.get(queueEntity.getId());
					if (maxId <= 0) {
						logger.info("message_lag_check_MaxI_null," + qlog);
						continue;
					}
					if (!topicMap.containsKey(queueOffset.getTopicName())) {
						logger.info("message_lag_check_topicName_null," + qlog);
						continue;
					}
					TopicEntity topicEntity = topicMap.get(queueOffset.getTopicName());
					if (!(consumerGroupTopicMap.containsKey(consumerGroupEntity.getId()) && consumerGroupTopicMap
							.get(consumerGroupEntity.getId()).containsKey(topicEntity.getName()))) {
						logger.info("message_lag_check_group_topic_null," + qlog);
						continue;
					}
					ConsumerGroupTopicEntity consumerGroupTopicEntity = consumerGroupTopicMap
							.get(consumerGroupEntity.getId()).get(topicEntity.getName());

					long massageLagCount = maxId - queueOffset.getOffset() - 1;// 最大id-当前读到的id=延迟读的消息数量(id是自增长)
					if ((consumerGroupTopicEntity.getMaxLag() > 0
							&& massageLagCount >= consumerGroupTopicEntity.getMaxLag())
							|| (consumerGroupTopicEntity.getMaxLag() <= 0
									&& massageLagCount >= topicEntity.getMaxLag())) {
						String body = String.format(
								"ConsumerGroup:%s下的Consumer:%s订阅的Topic:%s下的队列:%s待处理的消息已经达到:%s条,请尽快处理!",
								queueOffset.getConsumerGroupName(), queueOffset.getConsumerId(),
								queueOffset.getTopicName(), queueOffset.getQueueId(), massageLagCount);

						logger.info("message_lag_check_alarm," + qlog + "," + body);
						SendMailRequest sendMailRequest=new SendMailRequest();
						sendMailRequest.setConsumerGroupName(queueOffset.getConsumerGroupName());
						sendMailRequest.setTopicName(queueOffset.getTopicName());
						sendMailRequest.setContent(body);
						sendMailRequest.setSubject("消息堆积告警");
						sendMailRequest.setType(1);
						sendMailRequest.setKey("消息堆积告警,"+queueOffset.getConsumerGroupName()+"-"+queueOffset.getTopicName());
						emailService.sendConsumerMail(sendMailRequest);
						//emailUtil.sendWarnMail("消息堆积告警", body, alarmEmails + "," + topicEntity.getEmails());
						// emailUtil.sendInfoMail("消息堆积告警", body,
						// topicEntity.getEmails());
						try {
							MetricSingleton.getMetricRegistry()
									.histogram(queueOffset.getConsumerGroupName() + "." + queueOffset.getTopicName())
									.update(massageLagCount);
						} catch (Exception e) {
							// TODO: handle exception
						}
					} else {
						logger.info("message_lag_check_condition_null," + consumerGroupTopicEntity.getMaxLag() + ","
								+ massageLagCount + "," + topicEntity.getMaxLag() + "");
					}
				} catch (Exception e) {
					String message = String.format("MessageLagNotifyService1异常,异常信息:%s", e.getMessage());
					logger.error(message, e);
					//emailUtil.sendErrorMail("MessageLagNotifyService1异常", message);
				}
			}
		} catch (Exception ex) {
			String message = String.format("MessageLagNotifyService异常,异常信息:%s", ex.getMessage());
			logger.error(message, ex);
			//emailUtil.sendErrorMail("MessageLagNotifyService异常", message);
		}
	}

	@PreDestroy
	public void stopPortal() {
		super.stopPortal();
	}
}
