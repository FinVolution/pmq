package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface TopicRepository extends BaseRepository<TopicEntity> {
	LastUpdateEntity getLastUpdate();

	//List<TopicEntity> getUpdateData(Date lastDate);

	TopicEntity getTopicByName(@Param("topicName")String topicName);

	List<TopicEntity> getListWithUserName(Map<String, Object> conditionMap);

	Long countWithUserName(Map<String, Object> conditionMap);

}
