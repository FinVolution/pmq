package com.ppdai.infrastructure.mq.biz.polling;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;


/*
 * 定时truncate queue
 */
@Component
public class TruncateQueueService extends AbstractTimerService {
	private Logger log = LoggerFactory.getLogger(TruncateQueueService.class);
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private QueueService queueService;

	@PostConstruct
	private void init() {
		super.init("mq_queue_truncate_sk", soaConfig.getTruncateMessageInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getTruncateMessageInterval();

			@Override
			public void run() {
				if (soaConfig.getTruncateMessageInterval() != interval) {
					interval = soaConfig.getTruncateMessageInterval();
					updateInterval(interval);
				}

			}
		});
	}

	@Override
	public void doStart() {
		Map<String, Object> conditionMap = new HashMap<>();
		conditionMap.put(QueueEntity.FdTopicName, TopicService.NEED_DELETED_TOPIC_NANE);
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -3);
		conditionMap.put(QueueEntity.FdUpdateTime+"End", Util.formateDate(calendar.getTime()));
		List<QueueEntity> allNeedTruncated = queueService.getList(conditionMap);
		if (allNeedTruncated != null) {
			for (QueueEntity temp : allNeedTruncated) {
				if (isMaster()) {
					if (getSkipTime() == 0) {
						try {
							queueService.truncate(temp);
							log.info("truncate queue "+temp.getId()+","+temp.getDbName()+"-"+temp.getTbName());
						} catch (Exception e) {
							log.error("truncate error "+temp.getId()+","+temp.getDbName()+"-"+temp.getTbName(),e);
						}
						
					}
					Util.sleep(10000);
				} else {
					break;
				}
			}
		}
	}

	private long getSkipTime() {
		List<SoaConfig.TimeRange> ranges = soaConfig.getSkipTime();
		if (CollectionUtils.isEmpty(ranges)) {
			return 0L;
		}
		Calendar calendar = Calendar.getInstance();
		int hourMinute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		for (SoaConfig.TimeRange range : ranges) {
			if (range.start <= hourMinute && range.end >= hourMinute) {
				return (range.end - hourMinute) * 60000L;
			}
		}
		return 0L;
	}

	@PreDestroy
	public void stopPortal() {
		super.stopPortal();
	}
}
