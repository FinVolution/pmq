package com.ppdai.infrastructure.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageNotifyResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageStatNotifyResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageUpdateNotifyResponse;
import com.ppdai.infrastructure.ui.service.UiNotifyService;

@RestController
@RequestMapping("/notify")
public class NotifyController {
    @Autowired
    private UiNotifyService uiNotifyService;
    Logger log = LoggerFactory.getLogger(QueueController.class);

    @RequestMapping("/list/data")
    public MessageNotifyResponse getNotifyMessageByPage(long page, long limit) {
        return uiNotifyService.getNotifyMessageByPage(page, limit);

    }

    @RequestMapping("/list/key")
    public MessageStatNotifyResponse getNotifyKeyByPage(long page, long limit) {
        return uiNotifyService.getNotifyKey(page, limit);

    }

    @RequestMapping("/update/id")
    public MessageUpdateNotifyResponse updateNotifyMessage(long id, long notifyMessageId) {
        uiNotifyService.updateNotifyMessageStat(id, notifyMessageId);
        return new MessageUpdateNotifyResponse();

    }
}
