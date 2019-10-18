package com.ppdai.infrastructure.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ppdai.infrastructure.mq.biz.ui.dto.request.CatGetDataRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.CatGetDataResponse;
import com.ppdai.infrastructure.ui.service.CatDataService;

@Controller
@RequestMapping("/cat")
public class CatDataController {
    @Autowired
    private CatDataService catDataService;

    @RequestMapping("/data")
    @ResponseBody
    public CatGetDataResponse getCatData(CatGetDataRequest catGetDataRequest) {
        CatGetDataResponse catDataResponse = catDataService.getCatData(catGetDataRequest);
        return catDataResponse;
    }
}
