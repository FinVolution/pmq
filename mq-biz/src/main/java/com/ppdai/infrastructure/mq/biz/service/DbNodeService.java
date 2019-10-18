package com.ppdai.infrastructure.mq.biz.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */

public interface DbNodeService extends BaseService<DbNodeEntity> {	
	Map<Long, DbNodeEntity> getCache();
	Map<String,List<DbNodeEntity>> getCacheByIp();
	void updateCache();
	void createDataSource(DbNodeEntity t1);
	void checkDataSource(DbNodeEntity dbNodeEntity);
	DataSource getDataSource(long id,boolean isMaster);
	Map<String, DataSource> getDataSource();
	//String getConKey(long dbNodeId);
	//String getIpFromKey(String key);
	//long getLastVersion();
	boolean hasSlave(DbNodeEntity dbNodeEntity);
}
