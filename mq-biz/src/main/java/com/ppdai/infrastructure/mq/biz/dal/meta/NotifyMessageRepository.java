package com.ppdai.infrastructure.mq.biz.dal.meta;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface NotifyMessageRepository extends BaseRepository<NotifyMessageEntity> {
	Long getMaxId(@Param("maxId1") long maxId1,  @Param("message_type") int messageType);

	Long getMaxId1(@Param("message_type") int messageType);

	int clearOld(@Param("clearOldTime") Long clearOldTime, @Param("id") long id);

	Long getMinId(@Param("message_type") int messageType);

	Long getMinId1();
}
