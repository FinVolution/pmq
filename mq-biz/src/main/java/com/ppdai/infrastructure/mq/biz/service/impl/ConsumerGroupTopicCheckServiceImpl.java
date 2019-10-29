package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.*;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.RedundanceCheckService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import org.springframework.util.StringUtils;

/**
 * @Author：wanghe02
 * @Date：2019/7/24 16:12
 */
@Service
public class ConsumerGroupTopicCheckServiceImpl implements RedundanceCheckService {
    @Autowired
    private ConsumerGroupTopicService consumerGroupTopicService;
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private QueueOffsetService queueOffsetService;
    @Autowired
    private SoaConfig soaConfig;

    @Override
    public String checkItem() {
        String field = "ConsumerGroupTopic下校验:" + ConsumerGroupTopicEntity.FdConsumerGroupName + "," + ConsumerGroupTopicEntity.FdTopicName;
        return field;
    }

    @Override
    public String checkResult() {
        String result = null;
        Map<String,ConsumerGroupTopicEntity> consumerGroupTopicMap = consumerGroupTopicService.getGroupTopic();
        Map<String,ConsumerGroupEntity> consumerGroupMap=consumerGroupService.getCache();
        Map<String, TopicEntity> topicMap = topicService.getCache();
        Map<String,QueueOffsetEntity> unQueueOffset=queueOffsetService.getUqCache();
        result = checkConsumerGroupTopic(consumerGroupTopicMap,consumerGroupMap, topicMap,unQueueOffset);
        return result;
    }

    public String checkConsumerGroupTopic(Map<String,ConsumerGroupTopicEntity> consumerGroupTopicMap, Map<String,ConsumerGroupEntity> consumerGroupMap,
                                          Map<String, TopicEntity> topicMap,Map<String,QueueOffsetEntity> unQueueOffset) {
        StringBuilder resultBuilder=new StringBuilder();

        Set<String> queueOffsetSet=new HashSet<String>();
        for (String unKey:unQueueOffset.keySet()) {
            queueOffsetSet.add(unKey.substring(0,unKey.lastIndexOf("_")));
        }


        for (Map.Entry<String, ConsumerGroupTopicEntity> consumerGroupTopic:consumerGroupTopicMap.entrySet()) {
            ConsumerGroupTopicEntity consumerGroupTopicEntity = consumerGroupTopic.getValue();
            String consumerGroupTopicName=consumerGroupTopic.getKey();

            if(!consumerGroupMap.containsKey(consumerGroupTopicEntity.getConsumerGroupName())){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()
                        +")对应的consumerGroup不存在,建议删除！"+"<br/>");
            }

            if(!topicMap.containsKey(consumerGroupTopicEntity.getTopicName())){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")对应的topic不存在,建议删除！"+"<br/>");
            }

            if(!queueOffsetSet.contains(consumerGroupTopicName)){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")对应的queueOffset不存在,建议删除！"+"<br/>");
            }

            ConsumerGroupEntity consumerGroupEntity=consumerGroupMap.get(consumerGroupTopicEntity.getConsumerGroupName());
            if(consumerGroupEntity!=null){
                if(!StringUtils.isEmpty(consumerGroupEntity.getTopicNames())){
                    List<String> subTopics= Arrays.asList(consumerGroupEntity.getTopicNames().split(","));
                    if(consumerGroupTopicEntity.getTopicType()==1&&!subTopics.contains(consumerGroupTopicEntity.getTopicName())){
                        resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                                "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                                +consumerGroupTopicEntity.getTopicName()+"),consumerGroup订阅的topicNames字段中不存在该topic"+"<br/>");
                    }
                }
            }


            if(consumerGroupTopicEntity.getTopicType()==1&&!topicMap.containsKey(consumerGroupTopicName+"_fail")){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")对应的失败topic："+consumerGroupTopicName+"_fail"+"不存在"+"<br/>");
            }
            if(consumerGroupTopicEntity.getRetryCount()<0){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的重试次数为："+consumerGroupTopicEntity.getRetryCount()+"，不能小于零"+"<br/>");
            }

            if(consumerGroupTopicEntity.getRetryCount()>soaConfig.getConsumerGroupTopicMaxRetryCount()){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的重试次数为："+consumerGroupTopicEntity.getRetryCount()+"，不能大于最大值："+soaConfig.getConsumerGroupTopicMaxRetryCount()+"<br/>");
            }

            if(consumerGroupTopicEntity.getThreadSize()<=0){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的处理线程数为："+consumerGroupTopicEntity.getThreadSize()+"，不能小于等于零"+"<br/>");
            }

            if(consumerGroupTopicEntity.getThreadSize()>soaConfig.getConsumerGroupTopicMaxThreadSize()){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的处理线程数为："+consumerGroupTopicEntity.getThreadSize()+"，不能大于"+soaConfig.getConsumerGroupTopicMaxThreadSize()+"<br/>");
            }

            if(consumerGroupTopicEntity.getPullBatchSize()<soaConfig.getMinPullBatchSize()){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的批量拉取条数为："+consumerGroupTopicEntity.getPullBatchSize()+"，不能小于"+soaConfig.getMinPullBatchSize()+"<br/>");
            }

            if(consumerGroupTopicEntity.getPullBatchSize()>soaConfig.getMaxPullBatchSize()){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的批量拉取条数为："+consumerGroupTopicEntity.getPullBatchSize()+"，不能大于"+soaConfig.getMaxPullBatchSize()+"<br/>");
            }

            if(consumerGroupTopicEntity.getDelayProcessTime()<0){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的延迟处理时间为："+consumerGroupTopicEntity.getDelayProcessTime()+"，不能小于零"+"<br/>");
            }
            if(consumerGroupTopicEntity.getDelayProcessTime()>soaConfig.getMaxDelayProcessTime()){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的延迟处理时间为："+consumerGroupTopicEntity.getDelayProcessTime()+"，不能大于"+soaConfig.getMaxDelayProcessTime()+"<br/>");
            }

            if(consumerGroupTopicEntity.getMaxPullTime()<soaConfig.getMinDelayPullTime()){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的拉取等待时间为："+consumerGroupTopicEntity.getMaxPullTime()+"，不能小于"+soaConfig.getMinDelayPullTime()+"<br/>");
            }
            if(consumerGroupTopicEntity.getMaxPullTime()>soaConfig.getMaxDelayPullTime()){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的拉取等待时间为："+consumerGroupTopicEntity.getMaxPullTime()+"，不能大于"+soaConfig.getMaxDelayPullTime()+"<br/>");
            }
            if(consumerGroupTopicEntity.getTimeOut()<0){
                resultBuilder.append("consumer_group_topic表中的id为："+consumerGroupTopicEntity.getId()+"的记录"+
                        "(consumerGroup为:"+consumerGroupTopicEntity.getConsumerGroupName()+",topic为:"
                        +consumerGroupTopicEntity.getTopicName()+")的消费熔断时间为："+consumerGroupTopicEntity.getTimeOut()+"，不能小于零"+"<br/>");
            }

        }

        return resultBuilder.toString();
    }
}
