package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.request.TopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.EnvSynAllResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.EnvSynGenerateResponse;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;

/**
 * @Author：wanghe02
 * @Date：2019/7/18 13:51
 */
@Service
public class EnvSynService {
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
    private ConsumerGroupTopicService consumerGroupTopicService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private UiTopicService uiTopicService;

    private static Logger log = LoggerFactory.getLogger(EnvSynService.class);

    public EnvSynGenerateResponse generateAll(String synType) {
        String jsonMesage = "";
        if (StringUtils.isEmpty(synType)) {
            throw new CheckFailException("请选择同步类型");
        }

        if ("1".equals(synType)) {

            List<TopicEntity> topicList = topicService.getList().stream()
                    .filter(t1 -> t1.getTopicType() == 1)
                    .collect(Collectors.toList());

            jsonMesage = JsonUtil.toJson(topicList);
        } else if ("2".equals(synType)) {
            List<ConsumerGroupEntity> consumerGroupList = consumerGroupService.getList();
            jsonMesage = JsonUtil.toJson(consumerGroupList);
        } else if ("3".equals(synType)) {
            List<ConsumerGroupTopicEntity> consumerGroupTopicList = consumerGroupTopicService.getList().stream()
                    .filter(t1 -> t1.getTopicType() == 1).collect(Collectors.toList());

            jsonMesage = JsonUtil.toJson(consumerGroupTopicList);
        }
        EnvSynGenerateResponse envSynGenerateResponse = new EnvSynGenerateResponse(jsonMesage);

        return envSynGenerateResponse;
    }


    public EnvSynAllResponse synAll(String synType, String synMessage) {
        StringBuilder sucMsg=new StringBuilder();
        StringBuilder failMsg=new StringBuilder();
        int sucCount=0;
        int failCount=0;
        if (StringUtils.isEmpty(synType)) {
            throw new CheckFailException("请选择同步类型");
        }
        Map<String,TopicEntity> topicMap=topicService.getCache();
        Map<String,ConsumerGroupEntity> consumerGroupMap=consumerGroupService.getCache();

        if ("1".equals(synType)) {
            //当前数据库中的数据(排除掉失败topic)
            List<TopicEntity> originTopicList = topicService.getList().stream().filter(t1 -> t1.getTopicType() == 1).collect(Collectors.toList());
            //从其他环境copy的数据
            List<TopicEntity> topicList = JsonUtil.parseJson(synMessage, new TypeReference<List<TopicEntity>>() {
            });

            //对比当前数据库中的数据和从其他环境copy的数据，把当前环境不存在的数据存在synTopicList中，然后同步到当前数据库
            List<TopicEntity> synTopicList = new ArrayList<>();
            Map<String, TopicEntity> originTopicMap = new HashMap<>();
            for (TopicEntity origin : originTopicList) {
                originTopicMap.put(origin.getName(), origin);
            }

            for (TopicEntity syn : topicList) {
                //数据库中不存在则添加到待同步列表中
                if (!originTopicMap.containsKey(syn.getName())) {
                    synTopicList.add(syn);
                }
            }

            for (TopicEntity topic : synTopicList) {
                if (topic.getTopicType() == 1) {
                    TopicCreateRequest topicCreateRequest = new TopicCreateRequest();
                    topicCreateRequest.setName(topic.getName());
                    topicCreateRequest.setOwnerIds(topic.getOwnerIds());
                    topicCreateRequest.setOwnerNames(topic.getOwnerNames());
                    topicCreateRequest.setExpectDayCount(topic.getExpectDayCount());
                    topicCreateRequest.setEmails(topic.getEmails());
                    topicCreateRequest.setBusinessType(topic.getBusinessType());
                    topicCreateRequest.setMaxLag(topic.getMaxLag());
                    topicCreateRequest.setRemark(topic.getRemark());
                    topicCreateRequest.setDptName(topic.getDptName());
                    topicCreateRequest.setNormalFlag(topic.getNormalFlag());
                    topicCreateRequest.setSaveDayNum(topic.getSaveDayNum());
                    topicCreateRequest.setTels(topic.getTels());
                    topicCreateRequest.setInsertBy(topic.getInsertBy());
                    topicCreateRequest.setTopicType(topic.getTopicType());
                    topicCreateRequest.setConsumerFlag(topic.getConsumerFlag());
                    topicCreateRequest.setConsumerGroupList(topic.getConsumerGroupNames());
                    topicCreateRequest.setAppId(topic.getAppId());
                    try {
                        uiTopicService.createOrUpdateTopic(topicCreateRequest);
                        sucCount++;
                        sucMsg.append(sucCount+":"+topicCreateRequest.getName()+"的topic同步成功"+"\n");
                    } catch (Exception e) {
                        failCount++;
                        failMsg.append(failCount+":"+topicCreateRequest.getName()+"的topic同步失败，异常信息为："+e.getMessage()+"\n");
                    }
                }
            }


        } else if ("2".equals(synType)) {
            //当前数据库中的数据
            List<ConsumerGroupEntity> originConsumerGroupList = consumerGroupService.getList();
            //从其他环境copy的数据
            List<ConsumerGroupEntity> consumerGroupList = JsonUtil.parseJson(synMessage, new TypeReference<List<ConsumerGroupEntity>>() {
            });

            //对比当前数据库中的数据和从其他环境copy的数据，把当前环境不存在的数据存在synConsumerGroupList中，然后同步到当前数据库
            List<ConsumerGroupEntity> synConsumerGroupList = new ArrayList<>();
            Map<String, ConsumerGroupEntity> originConsumerGroupMap = new HashMap<>();
            for (ConsumerGroupEntity origin : originConsumerGroupList) {
                originConsumerGroupMap.put(origin.getName(), origin);
            }

            for (ConsumerGroupEntity syn : consumerGroupList) {
                if (!originConsumerGroupMap.containsKey(syn.getName())) {
                    synConsumerGroupList.add(syn);
                }
            }

            for (ConsumerGroupEntity consumerGroup : synConsumerGroupList) {
                //不同步镜像组
                if(consumerGroup.getMode()==2&&!consumerGroup.getName().equals(consumerGroup.getOriginName())){
                    continue;
                }
                ConsumerGroupCreateRequest consumerGroupCreateRequest = new ConsumerGroupCreateRequest();
                consumerGroupCreateRequest.setName(consumerGroup.getName());
                consumerGroupCreateRequest.setOwnerIds(consumerGroup.getOwnerIds());
                consumerGroupCreateRequest.setOwnerNames(consumerGroup.getOwnerNames());
                consumerGroupCreateRequest.setAlarmFlag(consumerGroup.getAlarmFlag());
                consumerGroupCreateRequest.setTraceFlag(consumerGroup.getTraceFlag());
                consumerGroupCreateRequest.setAlarmEmails(consumerGroup.getAlarmEmails());
                consumerGroupCreateRequest.setTels(consumerGroup.getTels());
                consumerGroupCreateRequest.setDptName(consumerGroup.getDptName());
                consumerGroupCreateRequest.setRemark(consumerGroup.getRemark());
                consumerGroupCreateRequest.setAppId(consumerGroup.getAppId());
                consumerGroupCreateRequest.setMode(consumerGroup.getMode());
                try {
                    consumerGroupService.createConsumerGroup(consumerGroupCreateRequest);
                    sucCount++;
                    sucMsg.append(sucCount+":"+consumerGroupCreateRequest.getName()+"的consumerGroup同步成功"+"\n");
                } catch (Exception e) {
                    failCount++;
                    failMsg.append(failCount+":"+consumerGroupCreateRequest.getName()+"的consumerGroup同步失败,异常信息为："+e.getMessage()+"\n");
                    log.error(e.getMessage());
                }
            }
        } else if ("3".equals(synType)) {
            //当前数据库中的数据(排除掉失败topic)
            List<ConsumerGroupTopicEntity> originConsumerGroupTopicList = consumerGroupTopicService.getList().stream()
                    .filter(t1 -> t1.getTopicType() == 1).collect(Collectors.toList());
            //从其他环境copy的数据
            List<ConsumerGroupTopicEntity> consumerGroupTopicList = JsonUtil.parseJson(synMessage, new TypeReference<List<ConsumerGroupTopicEntity>>() {
            });

            //对比当前数据库中的数据和从其他环境copy的数据，把当前环境不存在的数据存在synConsumerGroupTopicList中，然后同步到当前数据库
            List<ConsumerGroupTopicEntity> synConsumerGroupTopicList = new ArrayList<>();
            Map<String, ConsumerGroupTopicEntity> originConsumerGroupTopicMap = new HashMap<>();
            for (ConsumerGroupTopicEntity origin : originConsumerGroupTopicList) {
                originConsumerGroupTopicMap.put(origin.getConsumerGroupName() + "_" + origin.getTopicName(), origin);
            }

            for (ConsumerGroupTopicEntity syn : consumerGroupTopicList) {
                //把当前数据库中不存在的数据加入到synConsumerGroupTopicList中
                if (!originConsumerGroupTopicMap.containsKey(syn.getConsumerGroupName() + "_" + syn.getTopicName())) {
                    synConsumerGroupTopicList.add(syn);
                }
            }

            for (ConsumerGroupTopicEntity consumerGroupTopic : synConsumerGroupTopicList) {
                if (consumerGroupTopic.getTopicType() == 1) {
                    ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest = new ConsumerGroupTopicCreateRequest();
                    consumerGroupTopicCreateRequest.setConsumerGroupName(consumerGroupTopic.getConsumerGroupName());
                    ConsumerGroupEntity consumerGroupEntity=consumerGroupMap.get(consumerGroupTopic.getConsumerGroupName());
                    TopicEntity topicEntity=topicMap.get(consumerGroupTopic.getTopicName());
                    if(consumerGroupEntity==null){
                        failCount++;
                        failMsg.append(failCount+":"+consumerGroupTopic.getConsumerGroupName()+"订阅"+consumerGroupTopic.getTopicName()
                                +",订阅关系同步失败。失败原因：当前缓存中不存在消费者组："+consumerGroupTopic.getConsumerGroupName()+"\n");
                        continue;

                    }
                    if(topicEntity==null){
                        failCount++;
                        failMsg.append(failCount+":"+consumerGroupTopic.getConsumerGroupName()+"订阅"+consumerGroupTopic.getTopicName()
                                +",订阅关系同步失败。失败原因：当前缓存中不存在topic："+consumerGroupTopic.getTopicName()+"\n");
                        continue;

                    }

                    consumerGroupTopicCreateRequest.setConsumerGroupId(consumerGroupEntity.getId());
                    consumerGroupTopicCreateRequest.setTopicId(topicEntity.getId());

                    consumerGroupTopicCreateRequest.setTopicName(consumerGroupTopic.getTopicName());
                    consumerGroupTopicCreateRequest.setOriginTopicName(consumerGroupTopic.getOriginTopicName());
                    consumerGroupTopicCreateRequest.setTopicType(consumerGroupTopic.getTopicType());
                    consumerGroupTopicCreateRequest.setRetryCount(consumerGroupTopic.getRetryCount());
                    consumerGroupTopicCreateRequest.setThreadSize(consumerGroupTopic.getThreadSize());
                    consumerGroupTopicCreateRequest.setMaxLag(consumerGroupTopic.getMaxLag());
                    consumerGroupTopicCreateRequest.setTag(consumerGroupTopic.getTag());
                    consumerGroupTopicCreateRequest.setDelayProcessTime(consumerGroupTopic.getDelayProcessTime());
                    consumerGroupTopicCreateRequest.setPullBatchSize(consumerGroupTopic.getPullBatchSize());
                    consumerGroupTopicCreateRequest.setAlarmEmails(consumerGroupTopic.getAlarmEmails());
                    consumerGroupTopicCreateRequest.setDelayPullTime(consumerGroupTopic.getMaxPullTime());
                    consumerGroupTopicCreateRequest.setConsumerBatchSize(consumerGroupTopic.getConsumerBatchSize());
                    consumerGroupTopicCreateRequest.setTimeOut(consumerGroupTopic.getTimeOut());
                    try {
                        consumerGroupTopicService.subscribe(consumerGroupTopicCreateRequest);
                        sucCount++;
                        sucMsg.append(sucCount+":"+consumerGroupTopicCreateRequest.getConsumerGroupName()+"订阅"+consumerGroupTopicCreateRequest.getTopicName()
                                +",订阅关系同步成功。"+"\n");
                    } catch (Exception e) {
                        failCount++;
                        failMsg.append(failCount+":"+consumerGroupTopicCreateRequest.getConsumerGroupName()+"订阅"+consumerGroupTopicCreateRequest.getTopicName()
                                +",订阅关系同步失败。异常信息为："+e.getMessage()+"\n");
                        log.error(e.getMessage());
                    }
                }

            }

        }

        int allNumber=sucCount+failCount;
        return new EnvSynAllResponse("0","一共"+allNumber+"条\n"+failMsg.toString()+"\n"+sucMsg.toString());

    }

}
