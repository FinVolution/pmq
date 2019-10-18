package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface DbNodeRepository extends BaseRepository<DbNodeEntity> {
	LastUpdateEntity getLastUpdate();

	//List<DbNodeEntity> getUpdateData(Date lastDate);

	List<DbNodeEntity> findConnStr();

}
