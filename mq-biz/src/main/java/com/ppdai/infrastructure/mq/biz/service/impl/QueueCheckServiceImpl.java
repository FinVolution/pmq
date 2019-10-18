package com.ppdai.infrastructure.mq.biz.service.impl;

import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.RedundanceCheckService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * queue检查：db_node_id和topicid是否存在，id和名字是否一致，node_type和db状态是否一致，最小值和最大值检查
 */

@Service("QueueCheckService")
public class QueueCheckServiceImpl implements RedundanceCheckService {
    @Autowired
    private QueueService queueService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private DbNodeService dbNodeService;

    @Override
    public String checkItem() {
        String field = "Queue下校验:" + QueueEntity.FdTopicId + "_" + QueueEntity.FdTopicName + ", "
                + QueueEntity.FdDbNodeId + "_" + QueueEntity.FdIp + "_" + QueueEntity.FdNodeType + "_"
                + QueueEntity.FdDbName;
        return field;
    }

    @Override
    public String checkResult() {
        String result = null;
        Map<String, TopicEntity> topicMap = topicService.getCache();
        Map<Long, QueueEntity> queueMap = queueService.getAllQueueMap();
        Map<Long, DbNodeEntity> dbMap = dbNodeService.getCache();
        Map<Long, Long> queueMaxIdMap = queueService.getMax();
        result = checkQueue(topicMap, queueMap, dbMap, queueMaxIdMap);
        return result;
    }

    public String checkQueue(Map<String, TopicEntity> topicMap, Map<Long, QueueEntity> queueMap, Map<Long, DbNodeEntity> dbMap, Map<Long, Long> queueMaxIdMap) {

        StringBuilder resultBuilder = new StringBuilder();
        for (long qId : queueMap.keySet()) {
            QueueEntity queueEntity = queueMap.get(qId);
            DbNodeEntity dbNodeEntity = dbMap.get(queueEntity.getDbNodeId());

            if (!dbMap.containsKey(dbNodeEntity.getId())) {
                resultBuilder.append("queue表中的queueId为：" + qId + "的queue，对应的db_node_id在dbnode表中不存在！" + "<br/>");
            }
            if (!StringUtils.isEmpty(queueEntity.getTopicName())) {
                if (topicMap.containsKey(queueEntity.getTopicName())) {
                    if (topicMap.get(queueEntity.getTopicName()).getId() != queueEntity.getTopicId()) {
                        resultBuilder.append("queue表中的queueId为：" + qId + "的queue，对应的topic：" + queueEntity.getTopicName() +
                                "名字和id不一致" + "<br/>");
                    }
                } else {
                    resultBuilder.append("queue表中的queueId为：" + qId + "的queue，对应的topic" + queueEntity.getTopicName() + "不存在,建议清除该记录中topic的相关信息" + "<br/>");
                }
            }

            if (!queueEntity.getIp().equals(dbNodeEntity.getIp())) {
                resultBuilder.append("queue表中的queueId为：" + qId + "的queue，对应的ip字段与dbnode对应ip字段不一致！应该将queue中ip字段修改为："
                        +dbNodeEntity.getIp()+ "<br/>");
            }
            if (queueEntity.getNodeType() != dbNodeEntity.getNodeType()) {
                resultBuilder.append("queue表中的queueId为：" + qId + "的queue，对应的NodeType字段与dbnode对应NodeType字段不一致！应该将queue中NodeType字段修改为："
                        +dbNodeEntity.getNodeType()+ "<br/>");
            }
            if (!queueEntity.getDbName().equals(dbNodeEntity.getDbName())) {
                resultBuilder.append("queue表中的queueId为：" + qId + "的queue，对应的DbName字段与dbnode对应DbName字段不一致！应该将queue中DbName字段修改为："
                        +dbNodeEntity.getDbName()+ "<br/>");
            }

            if (queueEntity.getMinId()>queueMaxIdMap.get(queueEntity.getId())){
                resultBuilder.append("queue表中的queueId为：" + qId + "的queue，最小Id:"+queueEntity.getMinId()+"大于最大Id:"+queueMaxIdMap.get(queueEntity.getId())+
                        "。该queue分配的topic为："+queueEntity.getTopicName()+"，物理机："+queueEntity.getIp()+",库名："+queueEntity.getDbName()
                        +",表名："+queueEntity.getTbName()+"<br/>");
            }
        }
        return resultBuilder.toString();
    }

}
