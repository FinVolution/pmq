package com.ppdai.infrastructure.mq.biz.service.impl;

import com.ppdai.infrastructure.mq.biz.dal.meta.NotifyMessageRepository;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageEntity;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;
import com.ppdai.infrastructure.mq.biz.service.common.MessageType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author dal-generator
 */
@Service
public class NotifyMessageServiceImpl extends AbstractBaseService<NotifyMessageEntity> implements NotifyMessageService {	
	
	@Autowired
	private NotifyMessageRepository notifyMessageRepository;

	@PostConstruct
	private void init() {
		super.setBaseRepository(notifyMessageRepository);
	}

	@Override
	public long getDataMaxId(long maxId) {
		Long maxId1 = notifyMessageRepository.getMaxId(maxId, MessageType.Meta);
		if (maxId1 == null) {
			return 0;
		}
		return maxId1;
	}

	@Override
	public long getDataMaxId() {
		Long maxId1 = notifyMessageRepository.getMaxId1(MessageType.Meta);
		if (maxId1 == null) {
			return 0;
		}
		return maxId1;
	}

	@Override
	public long getDataMinId() {
		Long minId = notifyMessageRepository.getMinId(MessageType.Meta);
		if (minId == null) {
			return 0;
		}
		return minId;
	}

	@Override
	public long getRbMaxId(long maxId) {
		Long maxId1 = notifyMessageRepository.getMaxId(maxId, MessageType.Rb);
		if (maxId1 == null) {
			return 0;
		}
		return maxId1;
	}
	
	
	@Override
	public long getRbMaxId() {
		Long maxId1 = notifyMessageRepository.getMaxId1(MessageType.Rb);
		if (maxId1 == null) {
			return 0;
		}
		return maxId1;
	}

	@Override
	public long getRbMinId() {
		Long minId = notifyMessageRepository.getMinId(MessageType.Rb);
		if (minId == null) {
			return 0;
		}
		return minId;
	}	

	@Override
	public long getMinId() {
		Long minId=notifyMessageRepository.getMinId1();
		if(minId==null)
		{
			return 0;
		}
		return minId;
	}

	@Override
	public int clearOld(long clearOldTime, long maxId) {
		return notifyMessageRepository.clearOld(clearOldTime, maxId);
	}

}
