package com.ppdai.infrastructure.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicGetListRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupTopicCreateResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupTopicDeleteResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupTopicService;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupTopicEditResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupTopicGetByIdResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupTopicGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupTopicInitResponse;
import com.ppdai.infrastructure.ui.service.UiConsumerGroupTopicService;

/**
 * @author wanghe
 * @date 2018/05/08
 */
@RestController
@RequestMapping("/consumerGroupTopic")
public class ConsumerGroupTopicController {
    @Autowired
    private UiConsumerGroupTopicService uiConsumerGroupTopicService;
    @Autowired
    private ConsumerGroupTopicService consumerGroupTopicService;
    Logger log = LoggerFactory.getLogger(ConsumerGroupTopicController.class);

    @RequestMapping("/list/data")
    public ConsumerGroupTopicGetListResponse findBy(ConsumerGroupTopicGetListRequest consumerGroupTopicGetListRequest) {
        return uiConsumerGroupTopicService.findBy(consumerGroupTopicGetListRequest);
    }

    @RequestMapping("/create")
    public ConsumerGroupTopicCreateResponse createConsumerGroupTopicAndFailTopic(ConsumerGroupTopicCreateRequest consumerGroupTopicCreateRequest) {
        return consumerGroupTopicService.subscribe(consumerGroupTopicCreateRequest);
    }

    @RequestMapping("/edit")
    public ConsumerGroupTopicEditResponse editConsumerGroupTopic(ConsumerGroupTopicEntity consumerGroupTopicEntity) {
        return uiConsumerGroupTopicService.editConsumerGroupTopic(consumerGroupTopicEntity);
    }

    /**
     * 取消订阅
     *
     * @param consumerGroupTopicId
     * @return
     */
    @RequestMapping("/delete")
    public ConsumerGroupTopicDeleteResponse deleteConsumerGroupTopic(Long consumerGroupTopicId) {
        return consumerGroupTopicService.deleteConsumerGroupTopic(consumerGroupTopicId);
    }

    @GetMapping("/getById")
    public ConsumerGroupTopicGetByIdResponse getById(Long consumerGroupTopicId) {
        return new ConsumerGroupTopicGetByIdResponse(uiConsumerGroupTopicService.findById(consumerGroupTopicId));
    }

    @GetMapping("/initConsumerGroupTopic")
    public ConsumerGroupTopicInitResponse initConsumerGroupTopic(Long consumerGroupId) {
        return new ConsumerGroupTopicInitResponse(uiConsumerGroupTopicService.initConsumerGroupTopic(consumerGroupId));
    }


}

