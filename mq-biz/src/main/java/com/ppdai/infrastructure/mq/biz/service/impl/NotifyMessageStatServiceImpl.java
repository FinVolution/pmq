package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dal.meta.NotifyMessageStatRepository;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageStatEntity;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageStatService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;

/**
 * @author dal-generator
 */
@Service
public class NotifyMessageStatServiceImpl extends AbstractBaseService<NotifyMessageStatEntity>
		implements NotifyMessageStatService {
	@Autowired
	private NotifyMessageStatRepository notifyMessageStatRepository;

	@PostConstruct
	private void init() {
		super.setBaseRepository(notifyMessageStatRepository);
	}

	@Override
	public NotifyMessageStatEntity initNotifyMessageStat() {
		NotifyMessageStatEntity messageStatEntity = new NotifyMessageStatEntity();
		messageStatEntity.setKey1("rb_notifyMessageStat");
		messageStatEntity.setNotifyMessageId(0);
		notifyMessageStatRepository.insert(messageStatEntity);
		return messageStatEntity;
	}

	@Override
	public NotifyMessageStatEntity get() {
		Map<String, Object> conMap = new HashMap<>();
		conMap.put(NotifyMessageStatEntity.FdKey1, "rb_notifyMessageStat");
		NotifyMessageStatEntity messageStatEntity = notifyMessageStatRepository.get(conMap);
		return messageStatEntity;
	}

	@Override
	public void updateNotifyMessageId() {
		notifyMessageStatRepository.updateNotifyMessageId();
	}

}
