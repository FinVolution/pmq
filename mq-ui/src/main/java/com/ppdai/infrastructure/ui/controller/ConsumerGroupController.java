package com.ppdai.infrastructure.ui.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupCreateResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupDeleteResponse;
import com.ppdai.infrastructure.mq.biz.dto.response.ConsumerGroupEditResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.ConsumerGroupGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupGetByIdResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupGetNamesResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupRebalenceResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupRefreshMetaResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupSelectResponse;
import com.ppdai.infrastructure.ui.service.UiConsumerGroupService;

/**
 * @author wanghe
 * @date 2018/05/04
 */
@RestController
@RequestMapping("/consumerGroup")
public class ConsumerGroupController {
    @Autowired
    private UiConsumerGroupService uiConsumerGroupService;
    @Autowired
    private ConsumerGroupService consumerGroupService;
    Logger log = LoggerFactory.getLogger(ConsumerGroupController.class);

    @RequestMapping("/list/data")
    public ConsumerGroupGetListResponse findBy(ConsumerGroupGetListRequest consumerGroupGetListRequest) {
        return uiConsumerGroupService.findBy(consumerGroupGetListRequest);
    }

    @RequestMapping("/createAndUpdate")
    public ConsumerGroupCreateResponse createConsumerGroup(ConsumerGroupCreateRequest consumerGroupCreateRequest) {
        return consumerGroupService.createConsumerGroup(consumerGroupCreateRequest);
    }

    @RequestMapping("/edit")
    public ConsumerGroupEditResponse editConsumerGroup(@RequestParam("ConsumerGroupEntity") ConsumerGroupEntity consumerGroupEntity) {
        return consumerGroupService.editConsumerGroup(consumerGroupEntity);
    }

    @RequestMapping("/delete")
    public ConsumerGroupDeleteResponse deleteConsumerGroup(long consumerGroupId) {
        return consumerGroupService.deleteConsumerGroup(consumerGroupId,true);
    }

    @GetMapping("/getById")
    public ConsumerGroupGetByIdResponse getById(Long id) {
        return uiConsumerGroupService.getById(id);
    }

    @RequestMapping("/refreshMeta")
    public ConsumerGroupRefreshMetaResponse refreshMeta(long consumerGroupId) {
           consumerGroupService.notifyMeta(consumerGroupId);
           return new ConsumerGroupRefreshMetaResponse();
    }

    @RequestMapping("/rebalence")
    public ConsumerGroupRebalenceResponse rebalence(long consumerGroupId) {
        consumerGroupService.notifyRb(consumerGroupId);
        return new ConsumerGroupRebalenceResponse();
    }

    @RequestMapping("/getConsumerGpNames")
    public ConsumerGroupGetNamesResponse getConsumerGpNames(String keyword, int offset, int limit){
        if (StringUtils.isEmpty(keyword)) {
            return new ConsumerGroupGetNamesResponse(0L, null);
        }
        return uiConsumerGroupService.getConsumerGpNames(keyword, offset,limit);
    }


    @RequestMapping("/consumerGroupSelect")
    public ConsumerGroupSelectResponse searchConsumerGroups(String keyword, int offset, int limit) {
        return uiConsumerGroupService.searchConsumerGroups(keyword,offset,limit);
    }

}
