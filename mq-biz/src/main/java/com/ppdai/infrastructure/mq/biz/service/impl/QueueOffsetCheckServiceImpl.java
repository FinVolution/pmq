package com.ppdai.infrastructure.mq.biz.service.impl;

import com.ppdai.infrastructure.mq.biz.entity.*;
import com.ppdai.infrastructure.mq.biz.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * queueOffset检查：每一条数据对应的topic和consumerGroup都存在，广播的是否存在原始组，db_info是否正确，topic_type是否正确、id和name是否对应，queueOffset和consumerGroupTopic的对应。
 */
@Service
public class QueueOffsetCheckServiceImpl implements RedundanceCheckService {
    @Autowired
    private QueueOffsetService queueOffsetService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
    private ConsumerGroupTopicService consumerGroupTopicService;
    @Autowired
    private QueueService queueService;

    @Override
    public String checkItem() {
        String field = "QueueOffset下校验:" + QueueOffsetEntity.FdConsumerName + "," + QueueOffsetEntity.FdConsumerId +
                "," + QueueOffsetEntity.FdOriginTopicName;
        return field;
    }

    @Override
    public String checkResult() {
        String result = null;
        List<QueueOffsetEntity> queueOffList = queueOffsetService.getCacheData();
        Map<String, TopicEntity> topicMap = topicService.getCache();
        Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
        Map<String, ConsumerGroupTopicEntity> groupTopicMap = consumerGroupTopicService.getGroupTopic();
        Map<Long, QueueEntity> queueMap = queueService.getAllQueueMap();
        result = checkQueueOffset(queueOffList, topicMap, consumerGroupMap, groupTopicMap, queueMap);
        return result;
    }

    public String checkQueueOffset(List<QueueOffsetEntity> queueOffList, Map<String, TopicEntity> topicMap,
                                   Map<String, ConsumerGroupEntity> consumerGroupMap, Map<String, ConsumerGroupTopicEntity> groupTopicMap,
                                   Map<Long, QueueEntity> queueMap) {

        StringBuilder resultBuilder = new StringBuilder();
        for (QueueOffsetEntity queueOffsetEntity : queueOffList) {
            TopicEntity topicEntity=topicMap.get(queueOffsetEntity.getTopicName());
            if (StringUtils.isEmpty(queueOffsetEntity.getOriginTopicName())) {
                resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，对应的origin_topic_name字段为空。应该修改为："+
                        topicEntity.getOriginName()+"<br/>");
            }

            if(topicEntity!=null){
                if (!(queueOffsetEntity.getOriginTopicName()).equals(topicEntity.getOriginName())) {
                    resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，对应的origin_topic_name:"
                            + queueOffsetEntity.getOriginTopicName() + "与topic表中的name为：[" + queueOffsetEntity.getTopicName()
                            + "的行，对应的origin_name:" + topicMap.get(queueOffsetEntity.getTopicName()).getOriginName() + "字段不一致"+
                            "应该将queue_offset表中该条数据的origin_topic_name改为："+topicEntity.getOriginName()+"<br/>");
                }
            }

            if (StringUtils.isEmpty(queueOffsetEntity.getConsumerName()) && queueOffsetEntity.getConsumerId() != 0) {
                resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，ConsumerId不为0但是ConsumerName字段为空。"+"<br/>");
            }
            if (!StringUtils.isEmpty(queueOffsetEntity.getConsumerName()) && queueOffsetEntity.getConsumerId() == 0) {
                resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，ConsumerId字段为0，但是ConsumerName不为空！。"+"<br/>");
            }

            if (!StringUtils.isEmpty(queueOffsetEntity.getTopicName()) && !topicMap.containsKey(queueOffsetEntity.getTopicName())) {
                resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，对应的topic_name:" + queueOffsetEntity.getTopicName()
                        + "在topic表中不存在,建议删除该条记录"+"<br/>");
            }
            
            if (!queueMap.containsKey(queueOffsetEntity.getQueueId())) {
                resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，对应的queue_id:" + queueOffsetEntity.getQueueId() +
                        "在queue表中不存在,请注意,建议删除该条记录!" +"<br/>");
            }
            else if (!StringUtils.isEmpty(queueOffsetEntity.getTopicName())&&!queueOffsetEntity.getTopicName().equalsIgnoreCase(queueMap.get(queueOffsetEntity.getQueueId()).getTopicName())) {
                resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，对应的queue_id:" + queueOffsetEntity.getQueueId() +
                        "在queue表中对应的topic_name不存在或者queue表中的topicName("+queueMap.get(queueOffsetEntity.getQueueId()).getTopicName()+")与queueoffset表中的topicName("+queueOffsetEntity.getTopicName()+")不相等,请注意!" +"<br/>");
            }

            if (!StringUtils.isEmpty(queueOffsetEntity.getConsumerGroupName()) && !consumerGroupMap.containsKey(queueOffsetEntity.getConsumerGroupName())) {
                resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，对应的consumer_group_name:" + queueOffsetEntity.getConsumerGroupName()
                        + "在consumer_group表中不存在,建议删除该条记录"+"<br/>");
            }

            ConsumerGroupEntity consumerGroupEntity=consumerGroupMap.get(queueOffsetEntity.getConsumerGroupName());

            if(!StringUtils.isEmpty(queueOffsetEntity.getConsumerGroupName())&&consumerGroupEntity.getMode()==1){
                if(!queueOffsetEntity.getConsumerGroupName().equals(queueOffsetEntity.getOriginConsumerGroupName())){
                    resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行的origin_consumer_group_name字段，与对应的consumer_group_name:" +
                            queueOffsetEntity.getConsumerGroupName() +
                            "不一致"+"<br/>");
                }
            }

            if(consumerGroupEntity!=null){
                if (!StringUtils.isEmpty(queueOffsetEntity.getConsumerGroupName()) &&consumerGroupEntity.getMode() == 2) {
                    if (!consumerGroupMap.containsKey(consumerGroupEntity.getOriginName())) {
                        resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，对应的consumer_group_name是广播消费者组：" + consumerGroupEntity.getName() +
                                "对应的原始组不存在,建议删除！"+"<br/>");
                    }
                }
            }

            if (!StringUtils.isEmpty(queueOffsetEntity.getTopicName()) && topicMap.containsKey(queueOffsetEntity.getTopicName())) {
                if (topicMap.get(queueOffsetEntity.getTopicName()).getTopicType() != queueOffsetEntity.getTopicType()) {
                    resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "行的topic_type字段与的topic表中name为：" + queueOffsetEntity.getTopicName() +
                            "的行对应的topic_type不一致"+"建议把queue_offset中该条记录的topic_type改为："
                            +topicMap.get(queueOffsetEntity.getTopicName()).getTopicType()+"<br/>");
                }
                if (topicMap.get(queueOffsetEntity.getTopicName()).getId() != queueOffsetEntity.getTopicId()) {
                    if (topicMap.get(queueOffsetEntity.getTopicName()).getTopicType() != queueOffsetEntity.getTopicType()) {
                        resultBuilder.append("queueOffset表中Id为：" + queueOffsetEntity.getId() + "的topicId与它对应的topic：" + queueOffsetEntity.getTopicName() +
                                "在topic表中的id不一致"+"<br/>");
                    }
                }


                if (!groupTopicMap.containsKey(queueOffsetEntity.getConsumerGroupName() + "_" + queueOffsetEntity.getTopicName())) {
                    resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行对应的ConsumerGroupTopic：" +
                            queueOffsetEntity.getConsumerGroupName() + "_" + queueOffsetEntity.getTopicName() + "不存在，建议删除！"+"<br/>");
                }

                QueueEntity queueEntity = queueMap.get(queueOffsetEntity.getQueueId());
                String db=queueEntity.getIp() + " | " + queueEntity.getDbName() + " | " + queueEntity.getTbName();
                if(!db.equals(queueOffsetEntity.getDbInfo())){
                    resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，DbInfo与queue表中不一致，建议修改为："
                            +db+"<br/>");
                }

            }

            if(queueOffsetEntity.getConsumerGroupMode()==2&&!queueOffsetEntity.getConsumerGroupName().equals(queueOffsetEntity.getOriginConsumerGroupName())){
                //如果是镜像消费者组，则对应的消费者字段不能不为空
                if(StringUtils.isEmpty(queueOffsetEntity.getConsumerName())){
                    resultBuilder.append("queue_offset表中Id为：" + queueOffsetEntity.getId() + "的行，镜像消费者组："+queueOffsetEntity.getConsumerGroupName()+
                            "对应的consumer_name字段不能不为空,建议删除！"+"<br/>");
                }
            }


        }
        return resultBuilder.toString();
    }


}
