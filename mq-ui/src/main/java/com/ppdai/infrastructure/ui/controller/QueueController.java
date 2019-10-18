package com.ppdai.infrastructure.ui.controller;

import javax.servlet.http.HttpServletRequest;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.ui.dto.request.QueueGetListRequest;
import com.ppdai.infrastructure.ui.service.UiQueueService;
import com.ppdai.infrastructure.ui.util.CookieUtil;


@RestController
@RequestMapping("/queue")
public class QueueController {

    Logger log = LoggerFactory.getLogger(QueueController.class);

    @Autowired
    UiQueueService uiQueueService;

    @GetMapping("/countByType")
    public QueueCountResponse countByType(int nodeType) {
        return uiQueueService.count(nodeType);
    }

    @RequestMapping("/list/data")
    public QueueGetListResponse queueListData(HttpServletRequest request, QueueGetListRequest queueGetListRequest) {
        QueueGetListResponse queueGetListResponse =uiQueueService.queryByPage(queueGetListRequest);
        return queueGetListResponse;
    }

    @RequestMapping("/report/data")
    public QueueReportResponse queueReportData(HttpServletRequest request, QueueGetListRequest queueGetListRequest) {
        String userId= CookieUtil.getUserName(request);
        QueueReportResponse baseUiResponse=uiQueueService.getQueueForReport(queueGetListRequest,userId);
        return baseUiResponse;
    }

    @PostMapping("/readOnly")
    public QueueReadOnlyResponse readOnly(@RequestParam("id") Long queueId,
                                          @RequestParam("isReadOnly") int isReadOnly) {
        return uiQueueService.readOnly(queueId, isReadOnly);
    }

    @PostMapping("/updateMinId")
    public QueueUpdateMinIdResponse updateMinId(@RequestParam("id") Long queueId,
                                                @RequestParam("minId") Long minId) {
        return uiQueueService.updateMinId(queueId, minId);
    }

    @PostMapping("/getQueueMinId")
    public BaseUiResponse<String> getQueueMinId(@RequestParam("queueId") Long queueId) {
        return uiQueueService.getQueueMinId(queueId);
    }

    @RequestMapping("/abnormal/minId")
    public QueueGetListResponse getAbnormalMinId(HttpServletRequest request, QueueGetListRequest queueGetListRequest) {
        QueueGetListResponse queueGetListResponse =uiQueueService.getAbnormalMinId(queueGetListRequest);
        return queueGetListResponse;
    }

}
