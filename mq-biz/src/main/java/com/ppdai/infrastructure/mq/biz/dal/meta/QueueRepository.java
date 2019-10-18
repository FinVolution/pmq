package com.ppdai.infrastructure.mq.biz.dal.meta;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;
import com.ppdai.infrastructure.mq.biz.dto.AnalyseDto;
import com.ppdai.infrastructure.mq.biz.entity.LastUpdateEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;


/**
 * @author dal-generator
 */
@Mapper
public interface QueueRepository extends BaseRepository<QueueEntity> {
    List<QueueEntity> getAllLocated();    

    List<QueueEntity> getTopUndistributedNodes(Map<String, Object> queryMap);

    List<Long> getTopDistributedNodes(Map<String, Object> queryMap);

    List<QueueEntity> getDistributedList(@Param("nodeIds") List<Long> nodeIds, @Param("topicId") Long topicId);

    List<QueueEntity> getUndistributedListByNodeIds(@Param("nodeIds") List<Long> nodeIds, @Param("nodeType") int nodeType);

    int updateWithLock(QueueEntity queueEntity);

    void updateForDbNodeChange(@Param("ip") String ip, @Param("dbName") String dbName, @Param("oldIp") String oldIp, @Param("oldDbName") String oldDbName);

    List<AnalyseDto> countTopicByNodeId(@Param("id") Long id, @Param("start") Long start, @Param("offset") Long offset);

    List<AnalyseDto> getDistributedNodes(@Param("dbNodeId") Long dbNodeId);

    List<AnalyseDto> getQueueQuantity();
    
    int updateMinId(@Param("id") Long id, @Param("minId") Long minId);
    //获取基本的字段
    List<QueueEntity> getAllBasic();
    
    LastUpdateEntity getLastUpdate();
}
    