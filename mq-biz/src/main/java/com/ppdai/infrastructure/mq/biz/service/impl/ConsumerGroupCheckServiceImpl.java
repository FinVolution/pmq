package com.ppdai.infrastructure.mq.biz.service.impl;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.RedundanceCheckService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：wanghe02
 * @Date：2019/7/29 17:59
 * consumerGroup检查：黑白名单只能存在一个，originName是否和自己一致，consumer_quality不能小于0，topicNames订阅检查（数量和值）， 是否只存在虚拟消费者组，不存在原始组。
 */
@Service
public class ConsumerGroupCheckServiceImpl implements RedundanceCheckService {
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
    private ConsumerGroupTopicService consumerGroupTopicService;
    @Autowired
    private TopicService topicService;

    @Override
    public String checkItem() {
        String field = "ConsumerGroup下校验:" + ConsumerGroupEntity.FdName + "," + ConsumerGroupEntity.FdOriginName + "," +
                ConsumerGroupEntity.FdIpWhiteList + "," + ConsumerGroupEntity.FdIpBlackList + "," + ConsumerGroupEntity.FdTopicNames;
        return field; 
    }

    @Override
    public String checkResult() {
        String result = null;
        Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
        Map<String, ConsumerGroupTopicEntity> consumerGroupTopicMap = consumerGroupTopicService.getGroupTopic();
        Map<String,TopicEntity> topicMap=topicService.getCache();
        result = checkConsumerGroup(consumerGroupMap, consumerGroupTopicMap,topicMap);
        return result;
    }

    public String checkConsumerGroup(Map<String, ConsumerGroupEntity> consumerGroupMap, Map<String, ConsumerGroupTopicEntity> consumerGroupTopicMap,
                                     Map<String,TopicEntity> topicMap) {
        StringBuilder resultBuilder = new StringBuilder();
        Map<String,String> consumerGroupCheck=new HashMap<>(consumerGroupMap.size());

        for (String groupName : consumerGroupMap.keySet()) {
            if(!StringUtils.isEmpty(consumerGroupMap.get(groupName).getTopicNames())){
                List<String> subTopicNames = Arrays.asList(consumerGroupMap.get(groupName).getTopicNames().split(","));
                for (String topicName : subTopicNames) {
                    if (!consumerGroupTopicMap.containsKey(groupName + "_" + topicName)) {
                        resultBuilder.append("consumer_group表中的：" + groupName + "订阅的topic：" + topicName + "在consumer_group_topic中不存在，建议修复consumer_group表中的："
                                + groupName+"的topic_names字段"+ "<br/>");
                    }
                }

                for (String topicName:subTopicNames) {
                    if(!topicMap.containsKey(topicName)){
                        resultBuilder.append("consumer_group表中的：" + groupName + "订阅的topic：" + topicName + "在Topic表中不存在建议修复consumer_group表中的："
                                +groupName+"的topic_names字段" + "<br/>");
                    }
                }
            }

            if (!StringUtils.isEmpty(consumerGroupMap.get(groupName).getIpWhiteList()) && !StringUtils.isEmpty(consumerGroupMap.get(groupName).getIpBlackList())) {
                resultBuilder.append("consumer_group表中的：" + groupName + "同时存在黑白名单" + "<br/>");
            }
            if (consumerGroupMap.get(groupName).getMode() == 1&&!groupName.equals(consumerGroupMap.get(groupName).getOriginName())) {
                resultBuilder.append("consumer_group表中的：" + groupName + "与它的origin_name：" + consumerGroupMap.get(groupName).getOriginName()
                        + "不一致" + "<br/>");
            }

            if (consumerGroupMap.get(groupName).getConsumerQuality() < 0) {
                resultBuilder.append("consumer_group表中的：" + groupName + "consumer_quality不能小于0" + "<br/>");
            }


            if (consumerGroupMap.get(groupName).getMode() == 2 && !groupName.equals(consumerGroupMap.get(groupName).getOriginName())) {
                //对于镜像消费者组
                if (!consumerGroupMap.containsKey(consumerGroupMap.get(groupName).getOriginName())) {
                    resultBuilder.append("consumer_group表中的镜像组：" + groupName + "的原始组：" + consumerGroupMap.get(groupName).getOriginName()
                            + "不存在，建议删除！" + "<br/>");
                }
            }

            if(consumerGroupCheck.containsKey(groupName.toLowerCase())){
                resultBuilder.append("consumer_group表中的：" + groupName + "重复（名称不区分大小写）" + "<br/>");
            }else{
                consumerGroupCheck.put(groupName.toLowerCase(),"");
            }

        }

        return resultBuilder.toString();
    }
}
