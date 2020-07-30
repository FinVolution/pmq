package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface ServerRepository extends BaseRepository<ServerEntity> {
	int deleteOld(@Param("heartTime") int heartTime);

	List<ServerEntity> getNoramlServer(@Param("heartTime") int heartTime);

	int updateHeartTimeById(@Param("id") long id);

	int insert1(ServerEntity entity);

	void batchUpdate(@Param("ids") List<Long> serverIds,@Param("statusFlag") int serverStatus);
}
