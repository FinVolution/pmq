package com.ppdai.infrastructure.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.ConfigDto;
import com.ppdai.infrastructure.ui.service.ConfigService;

@Controller
public class ConfigController {
    @Autowired
    ConfigService configService;

    /**
     * 显示内部配置项的页面
     *
     * @param model
     * @return
     */
    @GetMapping("/config/soaConfig")
    public String getConfig(Model model) {
        return "config/soaConfig";
    }


    @RequestMapping("/config/soaConfig/data")
    @ResponseBody
    public BaseUiResponse<List<ConfigDto>> getConfigData(){
        return configService.getConfigData();
    }


}
