package com.ppdai.infrastructure.ui.controller;

import javax.servlet.http.HttpServletRequest;

import com.ppdai.infrastructure.mq.biz.ui.dto.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.QueueOffsetAccumulationRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.QueueOffsetGetListRequest;
import com.ppdai.infrastructure.ui.service.UiQueueOffsetService;

/**
 * @author wanghe
 * @date 2018/05/09
 */
@RestController
@RequestMapping("/queueOffset")
public class QueueOffsetController {
    @Autowired
    private UiQueueOffsetService uiQueueOffsetService;
    Logger log = LoggerFactory.getLogger(ConsumerGroupController.class);

    @RequestMapping("/list/data")
    public QueueOffsetGetListResponse findBy(QueueOffsetGetListRequest queueOffsetGetListRequest) {
        return uiQueueOffsetService.findBy(queueOffsetGetListRequest);
    }

    @RequestMapping("/accumulation/data")
    public BaseUiResponse findAccumulation(QueueOffsetAccumulationRequest queueOffsetAccumulationRequest) {
        return uiQueueOffsetService.findAccumulation(queueOffsetAccumulationRequest);
    }

    @RequestMapping("/accumulationAlert")
    public BaseUiResponse accumulationAlert(String ownerName){
        return uiQueueOffsetService.accumulationAlert(ownerName);
    }

    @RequestMapping("/list/data-nopage")
    public QueueOffsetGetListResponse findByNoPage(HttpServletRequest request,
                                                   @RequestParam(name = "topicId", defaultValue = "") Long topicId) {
        return uiQueueOffsetService.findAllBy(topicId);
    }

    @RequestMapping("/updateQueueOffset")
    public QueueOffsetUpdateResponse updateQueueOffset(long id, long offset) {
        return uiQueueOffsetService.updateQueueOffset(id,offset);
    }

    @RequestMapping("/updateStopFlag")
    public QueueOffsetUpdateStopFlagResponse updateStopFlag(long id, int stopFlag) {
        return uiQueueOffsetService.updateStopFlag(id,stopFlag);
    }

    @GetMapping("/getById")
    public QueueOffsetgetByIdResponse getById(Long queueOffsetId) {
        return new QueueOffsetgetByIdResponse(uiQueueOffsetService.findById(queueOffsetId));
    }

    @GetMapping("/getByConsumerGroupTopic")
    public QueueOffsetgetConsumerGroupTopicResponse getByConsumerGroupTopic(Long consumerGroupTopicId) {
        return new QueueOffsetgetConsumerGroupTopicResponse(uiQueueOffsetService.getByConsumerGroupTopic(consumerGroupTopicId));
    }

    @RequestMapping("/intelligentDetection")
    public QueueOffsetIntelligentDetectionResponse intelligentDetection(long queueOffsetId) {
        return uiQueueOffsetService.intelligentDetection(queueOffsetId);
    }

}
