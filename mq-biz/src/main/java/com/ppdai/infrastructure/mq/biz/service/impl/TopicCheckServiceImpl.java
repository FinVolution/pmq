package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.RedundanceCheckService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;

/**
 * @Author：wanghe02
 * @Date：2019/7/29 17:30
 * topic检查：正常topic是不是以fail结尾，失败topic的原始topic是否存在，正常topic的原始name是否跟自己一致。
 */
@Service
public class TopicCheckServiceImpl implements RedundanceCheckService {
    @Autowired
    private TopicService topicService;
    @Override
    public String checkItem() {
        String field = "Topic下校验:" + TopicEntity.FdName + "," + TopicEntity.FdOriginName;
        return field;
    }

    @Override
    public String checkResult() {
        String result = null;
        Map<String, TopicEntity> topicMap = topicService.getCache();
        result = checkTopic(topicMap);
        return result;
    }


    public String checkTopic(Map<String, TopicEntity> topicMap) {
        StringBuilder resultBuilder = new StringBuilder();
        Map<String,String> topicCheck=new HashMap<>(topicMap.size());

        for (String topicName : topicMap.keySet()) {
            if (topicMap.get(topicName).getTopicType() == 1 && topicName.endsWith("fail")) {
                resultBuilder.append("Topic表中的：" + topicName + "是正常topic,但是却以fail结尾" + "<br/>");
            }
            if (topicMap.get(topicName).getTopicType() == 2 && !topicMap.containsKey(topicMap.get(topicName).getOriginName())) {
                resultBuilder.append("Topic表中的失败topic：" + topicName + "对应的原始topic：" + topicMap.get(topicName).getOriginName() + "不存在，建议删除！" + "<br/>");
            }
            if (topicMap.get(topicName).getTopicType() == 1 && !topicName.equals(topicMap.get(topicName).getOriginName())) {
                resultBuilder.append("Topic表中的：" + topicName + "跟对应的原始topic：" + topicMap.get(topicName).getOriginName() +
                        "不一致，建议将该条记录的origin_name字段改为：" +topicName+ "<br/>");
            }
            if(topicCheck.containsKey(topicName.toLowerCase())){
                resultBuilder.append("Topic表中的：" + topicName + "重复（名称不区分大小写）" + "<br/>");
            }else{
                topicCheck.put(topicName.toLowerCase(),"");
            }

        }
        return resultBuilder.toString();
    }
}
