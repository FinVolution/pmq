//package com.ppdai.infrastructure.ui.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
//import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
//import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
//import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
//import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
//import com.ppdai.infrastructure.mq.biz.service.TopicService;
//import com.ppdai.infrastructure.mq.client.MqClient;
//import com.ppdai.infrastructure.mq.client.MqContext;
//import com.ppdai.infrastructure.ui.service.ServiceTest;
//import com.ppdai.infrastructure.ui.service.UiTopicService;
//
//
//@RestController
//public class ServiceTestController {
//
//    @Autowired
//    ServiceTest serviceTest;
//
//    @Autowired
//    private UiTopicService uiTopicService;
//    @Autowired
//    private TopicService topicService;
//    @Autowired
//    private ConsumerGroupService consumerGroupService;
//    @Autowired
//    private ConsumerGroupTopicService consumerGroupTopicService;
//
//    @GetMapping("/testAll")
//    public String testAll() {
//        try {
//            serviceTest.allProcess();
//        }catch (Exception e) {
//            e.printStackTrace();
//            serviceTest.deleteTopicAndConsumer();            
//            return "error:"+e.getMessage();
//        }
//        return "success";
//    }
//
//    @GetMapping("/mqContext")
//    public MqContext getMqContext(){
//        return MqClient.getContext();
//    }
//
//
//    @PostMapping("/test/buildTopic")
//    public void buildTopic(@RequestParam("topicName") String topicName){
//        serviceTest.buildTopic(topicName);
//    }
//
//    @PostMapping("/test/deleteTopic")
//    public void deleteTopic(@RequestParam("topicName") String topicName){
//        TopicEntity topicEntity = topicService.getCache().get(topicName);
//        if (topicEntity != null) {
//            uiTopicService.deleteTopic(topicEntity.getId());
//        }
//    }
//
//    @PostMapping("/test/buildConsumerGroup")
//    public void buildConsumerGroup(@RequestParam("consumerGroupName") String consumerGroupName){
//        serviceTest.buildConsumerGroup(consumerGroupName);
//    }
//
//    @PostMapping("/test/deleteConsumerGroup")
//    public void deleteConsumerGroup(@RequestParam("consumerGroupName") String consumerGroupName){
//        ConsumerGroupEntity consumerGroupEntity = consumerGroupService.getCache().get(consumerGroupName);
//        consumerGroupService.deleteConsumerGroup(consumerGroupEntity.getId(),false);
//    }
//
//    @PostMapping("/test/subscribe")
//    public void subscribe(@RequestParam("consumerGroupName") String consumerGroupName,@RequestParam("topicName") String topicName){
//        // 订阅consumerGroup
//        consumerGroupTopicService.subscribe(serviceTest.createConsumerGroupTopicRequest(topicName,
//                topicService.getCache().get(topicName).getId(), consumerGroupName,consumerGroupService.getCache().get(consumerGroupName).getId()));
//    }
//
//
//    @PostMapping("/test/unSubscribe")
//    public void unSubscribe(@RequestParam("consumerGroupName") String consumerGroupName,@RequestParam("topicName") String topicName){
//        // 取消订阅consumerGroup
//        ConsumerGroupEntity consumerGroupentity = consumerGroupService.getCache().get(consumerGroupName);
//        ConsumerGroupTopicEntity groupTopic = consumerGroupTopicService.getCache().get(consumerGroupentity.getId())
//                .get(topicName);
//        if (groupTopic != null) {
//            consumerGroupTopicService.deleteConsumerGroupTopic(groupTopic.getId());
//        }
//    }
//
//
//
//
//
//
//
//}
