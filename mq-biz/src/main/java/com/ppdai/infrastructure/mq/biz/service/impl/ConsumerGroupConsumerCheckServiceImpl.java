package com.ppdai.infrastructure.mq.biz.service.impl;

import com.ppdai.infrastructure.mq.biz.entity.*;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupConsumerService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.RedundanceCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：wanghe02
 * @Date：2019/7/24 16:12
 * consumer_Group_consumer检查 consumerId和consusumerGroupId是否存在
 */
@Service
public class ConsumerGroupConsumerCheckServiceImpl implements RedundanceCheckService {
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private ConsumerGroupConsumerService consumerGroupConsumerService;

    @Override
    public String checkItem() {
        String field = "ConsumerGroupConsumer下校验:" + ConsumerGroupEntity.FdId + "," + ConsumerEntity.FdId;
        return field;
    }

    @Override
    public String checkResult() {
        String result = null;
        Map<Long, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getIdCache();
        List<ConsumerEntity> consumerList =consumerService.getList();
        List<ConsumerGroupConsumerEntity> consumerGroupConsumerList=consumerGroupConsumerService.getList();
        Map<Long,ConsumerEntity> consumerMap=new HashMap<>();
        for (ConsumerEntity consumer:consumerList) {
            consumerMap.put(consumer.getId(),consumer);
        }

        result = checkConsumerGroupConsumer(consumerGroupMap, consumerMap, consumerGroupConsumerList);
        return result;
    }

    public String checkConsumerGroupConsumer(Map<Long, ConsumerGroupEntity> consumerGroupMap,Map<Long,ConsumerEntity> consumerMap,List<ConsumerGroupConsumerEntity> consumerGroupConsumerList) {
        StringBuilder resultBuilder = new StringBuilder();

        for (ConsumerGroupConsumerEntity consumerGroupConsumer:consumerGroupConsumerList) {
            if(!consumerMap.containsKey(consumerGroupConsumer.getConsumerId())){
                resultBuilder.append("consumer_group_consumer表中Id为："+consumerGroupConsumer.getId()+"对应的consumer_id不存在"+"<br/>");
            }

            if(!consumerGroupMap.containsKey(consumerGroupConsumer.getConsumerGroupId())){
                resultBuilder.append("consumer_group_consumer表中Id为："+consumerGroupConsumer.getId()+"对应的consumer_group_id不存在"+"<br/>");
            }
        }

        return resultBuilder.toString();
    }

}
