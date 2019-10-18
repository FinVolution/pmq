package com.ppdai.infrastructure.mq.biz.dal.meta;

import org.apache.ibatis.annotations.Mapper;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageStatEntity;
import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;

/**
 * @author dal-generator
 */
@Mapper
public interface NotifyMessageStatRepository extends BaseRepository<NotifyMessageStatEntity> {
	void updateNotifyMessageId();
}
