package com.ppdai.infrastructure.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.ui.dto.response.RedundanceCheckResponse;
import com.ppdai.infrastructure.ui.service.UiRedundanceCheckService;

/**
 * @Author：wanghe02
 * @Date：2019/8/5 14:37
 */
@RestController
@RequestMapping("/redundance")
public class RedundanceCheckController {
    @Autowired
    private UiRedundanceCheckService uiRedundanceCheckService;

    @RequestMapping("/checkAll")
    public RedundanceCheckResponse checkAll() {
        return uiRedundanceCheckService.checkAll();
    }

    @RequestMapping("/imitate")
    public  RedundanceCheckResponse imitate(){
        return uiRedundanceCheckService.imitate();
    }
}
