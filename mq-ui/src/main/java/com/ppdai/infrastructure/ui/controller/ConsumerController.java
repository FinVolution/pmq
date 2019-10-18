package com.ppdai.infrastructure.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.ui.dto.request.ConsumerGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerDeleteResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGetListResponse;
import com.ppdai.infrastructure.ui.service.UiConsumerService;


/**
 * @author wanghe02
 */
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    private UiConsumerService uiConsumerService;
    Logger log = LoggerFactory.getLogger(ConsumerGroupController.class);

    @RequestMapping("/list/data")
    public ConsumerGetListResponse getConsumerList(ConsumerGetListRequest consumerGetListRequest) {
        return uiConsumerService.getConsumerByPage(consumerGetListRequest);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ConsumerDeleteResponse deleteByTime(@RequestParam("consumerId") long consumerId) {
        return uiConsumerService.deleteByTime(consumerId);
    }

}

