package com.ppdai.infrastructure.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.ui.dto.request.MqLockGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MqLockDeleteResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MqLockGetListResponse;
import com.ppdai.infrastructure.ui.service.UiMqLockService;

/**
 * @author wanghe
 * @date 2018/05/04
 */
@RestController
@RequestMapping("/lock")
public class MqLockController {
    @Autowired
    private UiMqLockService uiMqLockService;
    Logger log = LoggerFactory.getLogger(ConsumerGroupController.class);

    @RequestMapping("/list/data")
    public MqLockGetListResponse findBy(MqLockGetListRequest baseUiRequst) {
        return uiMqLockService.findBy(baseUiRequst);
    }

    @RequestMapping("/delete")
    public MqLockDeleteResponse deleteLock(String lockId) {
        return uiMqLockService.delete(lockId);
    }

}
