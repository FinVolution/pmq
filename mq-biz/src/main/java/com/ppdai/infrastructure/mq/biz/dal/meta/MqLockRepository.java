package com.ppdai.infrastructure.mq.biz.dal.meta;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.MqLockEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface MqLockRepository extends BaseRepository<MqLockEntity> {
	int updateHeartTimeByKey1(@Param("ip") String ip, @Param("key1") String key1,
			@Param("lockInterval") int lockInterval);

	int updateHeartTimeByIdAndIp(@Param("id") long id,@Param("ip") String ip);

	int deleteOld(@Param("key1") String key1, @Param("lockInterval") int lockInterval);

	long insert1(MqLockEntity t);
}
