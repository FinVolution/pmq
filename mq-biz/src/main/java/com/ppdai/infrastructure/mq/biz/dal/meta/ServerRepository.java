package com.ppdai.infrastructure.mq.biz.dal.meta;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface ServerRepository extends BaseRepository<ServerEntity> {
	void deleteOld(@Param("lockInterval") int lockInterval);

	int updateHeartTimeById(@Param("id") long id);
	
	int insert1(ServerEntity entity);
}
