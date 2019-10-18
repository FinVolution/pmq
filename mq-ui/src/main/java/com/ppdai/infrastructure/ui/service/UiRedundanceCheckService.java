package com.ppdai.infrastructure.ui.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.entity.*;
import com.ppdai.infrastructure.mq.biz.polling.RedundanceAllCheckService;
import com.ppdai.infrastructure.mq.biz.service.*;
import com.ppdai.infrastructure.mq.biz.service.impl.*;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.RedundanceCheckResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：wanghe02
 * @Date：2019/8/5 14:46
 */
@Service
public class UiRedundanceCheckService {
    @Autowired
    private SoaConfig soaConfig;
    @Autowired
    private RedundanceAllCheckService redundanceAllCheckService;
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private ConsumerGroupTopicService consumerGroupTopicService;
    @Autowired
    private QueueService queueService;
    @Autowired
    private QueueOffsetService queueOffsetService;
    @Autowired
    private DbNodeService dbNodeService;
    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private ConsumerGroupConsumerService consumerGroupConsumerService;
    @Autowired
    private ConsumerGroupCheckServiceImpl consumerGroupCheckService;
    @Autowired
    private ConsumerGroupConsumerCheckServiceImpl consumerGroupConsumerCheckService;
    @Autowired
    private ConsumerGroupTopicCheckServiceImpl consumerGroupTopicCheckService;
    @Autowired
    private QueueCheckServiceImpl queueCheckService;
    @Autowired
    private QueueOffsetCheckServiceImpl queueOffsetCheckService;
    @Autowired
    private TopicCheckServiceImpl topicCheckService;

    public RedundanceCheckResponse checkAll(){
        String result=redundanceAllCheckService.checkResult();
        result+=checkIp();
        if(StringUtils.isEmpty(result)){
            result="数据正常！";
        }
        return new RedundanceCheckResponse(result);
    }

    /**
     * 检测topic下的队列是否分布到同一台物理机上
     * @return
     */
    private String checkIp(){
        StringBuilder checkIpResult1=new StringBuilder();
        StringBuilder checkIpResult2=new StringBuilder();
        Map<String, List<QueueEntity>> topicQueueMap=queueService.getAllLocatedTopicQueue();
        for (String topicName:topicQueueMap.keySet()) {
            String ip=topicQueueMap.get(topicName).get(0).getIp();
            boolean rs=false;
            for (QueueEntity queue:topicQueueMap.get(topicName)) {
                if(!ip.equals(queue.getIp())){
                    rs=true;
                    break;
                }
            }
            if(!rs){
                if(topicQueueMap.get(topicName).get(0).getNodeType()==1){
                    checkIpResult1.append("topic : "+topicName+" 的队列分布在一台物理机上(物理机ip:"+ip+"),为了防止单点故障，请扩容。"+"<br/>");
                }else{
                    checkIpResult2.append("topic : "+topicName+" 的队列分布在一台物理机上(物理机ip:"+ip+"),为了防止单点故障，请扩容。"+"<br/>");
                }

            }
        }

        return checkIpResult1.toString()+"----------------------<br/>"+checkIpResult2.toString();

    }

    public RedundanceCheckResponse imitate(){
        StringBuilder resultBuilder = new StringBuilder();

        Map<String, ConsumerGroupEntity> consumerGroupMap =JsonUtil.parseJson(JsonUtil.toJson(consumerGroupService.getCache()),
                new TypeReference<Map<String, ConsumerGroupEntity>>() {});
        Map<String, ConsumerGroupTopicEntity> consumerGroupTopicMap = JsonUtil.parseJson(JsonUtil.toJson(consumerGroupTopicService.getGroupTopic()),
                new TypeReference<Map<String, ConsumerGroupTopicEntity>>() {});
        Map<String,TopicEntity> topicMap=JsonUtil.parseJson(JsonUtil.toJson(topicService.getCache()),
                new TypeReference<Map<String,TopicEntity>>() {});

        Map<Long, ConsumerGroupEntity> groupIdMap =JsonUtil.parseJson(JsonUtil.toJson(consumerGroupService.getIdCache()),
                new TypeReference<Map<Long, ConsumerGroupEntity>>() {});
        List<ConsumerEntity> consumerList =JsonUtil.parseJson(JsonUtil.toJson(consumerService.getList()),
                new TypeReference<List<ConsumerEntity>>() {});
        List<ConsumerGroupConsumerEntity> consumerGroupConsumerList=JsonUtil.parseJson(JsonUtil.toJson(consumerGroupConsumerService.getList()),
                new TypeReference<List<ConsumerGroupConsumerEntity>>() {});

        Map<String,QueueOffsetEntity> unQueueOffset=JsonUtil.parseJson(JsonUtil.toJson(queueOffsetService.getUqCache()),
                new TypeReference<Map<String,QueueOffsetEntity>>() {});


        Map<Long, QueueEntity> queueMap = JsonUtil.parseJson(JsonUtil.toJson(queueService.getAllQueueMap()),
                new TypeReference<Map<Long, QueueEntity>>() {});
        Map<Long, DbNodeEntity> dbMap =JsonUtil.parseJson(JsonUtil.toJson(dbNodeService.getCache()),
                new TypeReference<Map<Long, DbNodeEntity>>() {});
        Map<Long, Long> queueMaxIdMap = JsonUtil.parseJson(JsonUtil.toJson(queueService.getMax()),
                new TypeReference<Map<Long, Long>>() {});

        List<QueueOffsetEntity> queueOffList = JsonUtil.parseJson(JsonUtil.toJson(queueOffsetService.getCacheData()),
                new TypeReference<List<QueueOffsetEntity>>() {});


        //consumerGroup脏数据测试
        int consumerGroupCount=0;
        for (String groupName:consumerGroupMap.keySet()) {
            ConsumerGroupEntity consumerGroupEntity=consumerGroupMap.get(groupName);
            if(consumerGroupCount==0){
                if(consumerGroupEntity.getMode()==1){
                    consumerGroupEntity.setIpWhiteList("127.0.0.1");
                    consumerGroupEntity.setIpBlackList("127.0.0.1");
                    consumerGroupEntity.setOriginName("hahaahahahaha");
                    consumerGroupEntity.setConsumerQuality(-1);
                    consumerGroupEntity.setTopicNames(consumerGroupEntity.getTopicNames()+",tototottotto");
                    consumerGroupCount++;
                    continue;
                }

            }else if (consumerGroupCount==1){
                if(consumerGroupEntity.getMode()==2){
                    consumerGroupEntity.setOriginName("ororororrorororor");
                    consumerGroupCount++;
                    continue;
                }

            }else{
                break;
            }

        }

        resultBuilder.append(consumerGroupCheckService.checkConsumerGroup(consumerGroupMap,consumerGroupTopicMap,topicMap));


        //consumerGroupConsumer脏数据测试
        for (ConsumerGroupConsumerEntity groupConsumer:consumerGroupConsumerList) {
            groupConsumer.setConsumerId(Long.MAX_VALUE);
            groupConsumer.setConsumerGroupId(Long.MAX_VALUE);
            break;
        }
        Map<Long,ConsumerEntity> consumerMap=new HashMap<>();
        for (ConsumerEntity consumer:consumerList) {
            consumerMap.put(consumer.getId(),consumer);
        }
        resultBuilder.append(consumerGroupConsumerCheckService.checkConsumerGroupConsumer(groupIdMap,consumerMap,consumerGroupConsumerList));

        //consumerGroupTopic脏数据测试
        int consumerGroupTopicCount=0;
        for (String groupTopicName:consumerGroupTopicMap.keySet()) {
            ConsumerGroupTopicEntity groupTopicEntity=consumerGroupTopicMap.get(groupTopicName);
            if(consumerGroupTopicCount==0){
                groupTopicEntity.setConsumerGroupName("grgrgrgrgrrgrgrgr");
                groupTopicEntity.setTopicName("toptoptotpotptotptotp");
                groupTopicEntity.setRetryCount(-1);
                groupTopicEntity.setThreadSize(-1);
                groupTopicEntity.setPullBatchSize(soaConfig.getMinPullBatchSize()-1);
                groupTopicEntity.setDelayProcessTime(-1);
                groupTopicEntity.setMaxPullTime(soaConfig.getMinDelayPullTime()-1);
                groupTopicEntity.setTimeOut(-1);
                consumerGroupTopicCount++;
                continue;
            }
            if(consumerGroupTopicCount==1){
                groupTopicEntity.setRetryCount(soaConfig.getConsumerGroupTopicMaxRetryCount()+1);
                groupTopicEntity.setThreadSize(soaConfig.getConsumerGroupTopicMaxThreadSize()+1);
                groupTopicEntity.setPullBatchSize(soaConfig.getMaxPullBatchSize()+1);
                groupTopicEntity.setDelayProcessTime(soaConfig.getMaxDelayProcessTime()+1);
                groupTopicEntity.setMaxPullTime(soaConfig.getMaxDelayPullTime()+1);
                consumerGroupTopicCount++;
                continue;
            }
            if(consumerGroupTopicCount==2){
                break;
            }


        }
        consumerGroupTopicMap.put("GFFHJFDHGHFFDHD_VJFFHGFDXNB",new ConsumerGroupTopicEntity());
        resultBuilder.append(consumerGroupTopicCheckService.checkConsumerGroupTopic(consumerGroupTopicMap,consumerGroupMap,topicMap,
                unQueueOffset));

        //queue脏数据测试
        for (long qId : queueMap.keySet()) {
            QueueEntity queueEntity = queueMap.get(qId);
            if(!StringUtils.isEmpty(queueEntity.getTopicName())){
                DbNodeEntity dbNodeEntity = dbMap.get(queueEntity.getDbNodeId());
                dbNodeEntity.setId(Long.MAX_VALUE);
                queueEntity.setTopicName("fhhfhfhdhsshdsfhskj");
                queueEntity.setIp("127.0.0.1");
                queueEntity.setNodeType(10);
                queueEntity.setDbName(queueEntity.getDbName()+"jfdvffdskjgfdsgjkfds");
                queueEntity.setMinId(Long.MAX_VALUE);
                break;
            }
        }
        resultBuilder.append(queueCheckService.checkQueue(topicMap,queueMap,dbMap,queueMaxIdMap));
         int queueOffsetCount=0;
        //queueOffset脏数据测试
        for (QueueOffsetEntity queueOffsetEntity : queueOffList) {
            if(!queueOffsetEntity.getConsumerGroupName().equals(queueOffsetEntity.getOriginConsumerGroupName())){
                queueOffsetEntity.setOriginConsumerGroupName("jhdsjhfdsjgkjdsgjdsj");
            }

            if(queueOffsetCount==0&&queueOffsetEntity.getConsumerId()!=0){
                queueOffsetEntity.setOriginTopicName("");
                queueOffsetEntity.setConsumerName("");
                queueOffsetEntity.setTopicType(10);
                queueOffsetCount++;
                continue;
            }else if(queueOffsetCount==1){
                if(queueOffsetEntity.getConsumerId()==0){
                    queueOffsetEntity.setOriginTopicName("jhfdgjhfdlkgkfds");
                    queueOffsetEntity.setConsumerName("jhfdjhjd");
                    queueOffsetEntity.setTopicName("fdkjhsglkjfdsgfskj");
                    queueOffsetEntity.setConsumerGroupName("dhgjhfdgfdskjhgklfdlkh");
                    queueOffsetCount++;
                    continue;
                }
            }
            else if(queueOffsetCount==2){
             queueOffsetEntity.setTopicId(Long.MAX_VALUE);
             queueOffsetCount++;
             continue;
            }
            else if(queueOffsetCount==3){
                queueOffsetEntity.setTopicName("fdhgdslkgk");
                queueOffsetCount++;
                continue;
            }else if(queueOffsetCount==4){
                queueOffsetEntity.setDbInfo("FGJG|fdgkjg|jfdgdkg");
                queueOffsetCount++;
                continue;
            }

            if(queueOffsetCount>4){
                break;
            }
        }

        resultBuilder.append(queueOffsetCheckService.checkQueueOffset(queueOffList, topicMap, consumerGroupMap, consumerGroupTopicMap, queueMap));

        //topic脏数据测试
        int topicCount=0;
        for (String topicName:topicMap.keySet()) {
            TopicEntity topicEntity=topicMap.get(topicName);
            if(topicCount==0&&topicEntity.getTopicType()==1){
                topicEntity.setName(topicName+"_fail");
                topicEntity.setOriginName(topicName+"fdf");
                topicCount++;
                continue;
            }
            if(topicCount==1&&topicEntity.getTopicType()==2){
                topicEntity.setOriginName("jhgfdsjhdjhs");
                topicCount++;
                continue;
            }

            if(topicCount>=2){
                break;
            }
        }
        resultBuilder.append(topicCheckService.checkTopic(topicMap));

        return new RedundanceCheckResponse(resultBuilder.toString());
    }
}
