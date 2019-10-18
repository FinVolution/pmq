package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface ConsumerGroupRepository extends BaseRepository<ConsumerGroupEntity> {

	List<ConsumerGroupEntity> getLastConsumerGroup(@Param("minMessageId") long minMessageId,
			@Param("maxMessageId") long maxMessageId, @Param("messageType") int messageType);

	void updateRbVersion(@Param("ids") List<Long> ids);

	void updateMetaVersion(@Param("ids") List<Long> ids);

	List<ConsumerGroupEntity> getByNames(@Param("names") List<String> names);

	List<ConsumerGroupEntity> getByOwnerNames(Map<String, Object> parameterMap);

	long countByOwnerNames(Map<String, Object> parameterMap);

	LastUpdateEntity getLastUpdate();

	void updateByOriginName(ConsumerGroupEntity consumerGroupEntity);

}
