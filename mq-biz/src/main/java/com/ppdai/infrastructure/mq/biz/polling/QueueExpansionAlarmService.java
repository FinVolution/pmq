package com.ppdai.infrastructure.mq.biz.polling;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.MqTypeConst;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.service.QueueService;

/**
 * 队列容量定时检查服务
 */
@Service
public class QueueExpansionAlarmService extends AbstractTimerService {
	//private static final Logger logger = LoggerFactory.getLogger(QueueExpansionAlarmService.class);
	@Autowired
	private QueueService queueService;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private EmailUtil emailUtil;

	@PostConstruct
	private void init() {
		super.init(Constants.QUEUE_EXPANSION, soaConfig.getQueueExpansionCheckInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getQueueExpansionCheckInterval();
			@Override
			public void run() {
				if (soaConfig.getQueueExpansionCheckInterval() != interval) {
					interval = soaConfig.getQueueExpansionCheckInterval();
					updateInterval(interval);
				}
			}
		});
	}

	@Override
	public void doStart() {
		int availableQueuesNum = 0;
		int availableFailQueuesNum = 0;
		try {
			List<QueueEntity> queueList = new ArrayList<>(queueService.getAllQueueMap().values());
			for (QueueEntity qu : queueList) {
				if (qu.getNodeType() == MqTypeConst.NORMAL && qu.getTopicId() == 0) {
					availableQueuesNum++;
				} else if (qu.getNodeType() == MqTypeConst.FAIL && qu.getTopicId() == 0) {
					availableFailQueuesNum++;
				}
			}
			if (availableQueuesNum > 0 && availableQueuesNum < soaConfig.getAvailableQueuesNum()) {
				emailUtil.sendWarnMail("QueueExpansionAlarmService", "正常队列的可用数量为：" + availableQueuesNum + "条，请尽快扩容");
			}
			if (availableFailQueuesNum > 0 && availableFailQueuesNum < soaConfig.getAvailableQueuesNum()) {
				emailUtil.sendWarnMail("QueueExpansionAlarmService", "失败队列的可用队列数量为：" + availableQueuesNum + "条，请尽快扩容");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@PreDestroy
	public void stopPortal() {
		super.stopPortal();
	}
}
