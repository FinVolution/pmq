package com.ppdai.infrastructure.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.ui.dto.response.EnvSynAllResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.EnvSynGenerateResponse;
import com.ppdai.infrastructure.ui.service.EnvSynService;

/**
 * @Author：wanghe02
 * @Date：2019/7/18 13:33
 */
@RestController
@RequestMapping("/envSyn")
public class EnvSynController {
    @Autowired
    private EnvSynService envSynService;
    @RequestMapping("/generateAll")
    public EnvSynGenerateResponse generateAll(@RequestParam(name = "synType") String synType) {
        return envSynService.generateAll(synType);
    }

    @RequestMapping("/synAll")
    public EnvSynAllResponse synAll(@RequestParam(name = "synType") String synType, @RequestParam(name = "synMessage") String synMessage) {
        return envSynService.synAll(synType,synMessage);
    }

}
