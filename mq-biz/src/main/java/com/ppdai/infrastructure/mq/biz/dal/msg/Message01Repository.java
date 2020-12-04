package com.ppdai.infrastructure.mq.biz.dal.msg;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;
import com.ppdai.infrastructure.mq.biz.entity.TableInfoEntity;

/**
 * @author dal-generator
 */
@Mapper
public interface Message01Repository extends BaseRepository<Message01Entity> {
	void insertBatchDy(@Param("tbName") String tbName, @Param("entityList") List<Message01Entity> entities);

	List<Message01Entity> getListDy(@Param("tbName") String tbName, @Param("start") long start, @Param("end") long end);

	List<Message01Entity> getListByPageSize(Map<String, Object> parameterMap);

	long countByPage(Map<String, Object> parameterMap);

	Message01Entity getMessageById(@Param("tbName") String tbName, @Param("id") long id);

	Long getTableMinId(@Param("tbName") String tbName);

	List<Message01Entity> getMessageByIds(@Param("tbName") String tbName, @Param("ids") List<Long> ids);

	Message01Entity getNearByMessageById(@Param("tbName") String tbName, @Param("id") long id);

	int deleteDy(@Param("tbName") String tbName, @Param("nextId") long nextId, @Param("date") String date,@Param("size") int size);

	void deleteByIds(@Param("tbName") String tbName, @Param("ids") List<Long> ids);

	Long getMaxId(@Param("dbName") String dbName, @Param("tbName") String tbName);

	// Long getMaxIdByTb(@Param("tbName") String tbName);

	List<TableInfoEntity> getMaxIdByDb();

	void truncate(@Param("tbName") String tbName);

	void createMessageTable(@Param("tbName") String tbName);

	List<Message01Entity> getListByTime(@Param("tbName") String tbName, @Param("sendTime") String sendTime);

	Map<String, String> getMaxConnectionsCount();

	Integer getConnectionsCount();

	// 通过retrycount来标识是否成功
	void updateFailMsgResult(@Param("tbName") String tbName, @Param("ids") List<Long> ids,
			@Param("retryCount") int retryCount);

	int deleteOldFailMsg(@Param("tbName") String tbName, @Param("id") long id, @Param("retryCount") int retryCount);
	Long getNextId(@Param("tbName") String tbName, @Param("id") long id, @Param("size") int size);

	Message01Entity getMinIdMsg(@Param("tbName") String tbName);
}
