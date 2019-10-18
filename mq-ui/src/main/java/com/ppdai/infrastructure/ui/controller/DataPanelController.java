package com.ppdai.infrastructure.ui.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.ui.dto.response.PanelNodeGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.PanelNodeVo;
import com.ppdai.infrastructure.ui.service.UiPanelService;

@RestController
@RequestMapping("/dataPanel")
public class DataPanelController {
    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private UiPanelService uiPanelService;

    @GetMapping("/node")
    public PanelNodeGetListResponse getNodePanel() {
        List<PanelNodeVo> panelNodeVoList = uiPanelService.getNodePanel();
        if (CollectionUtils.isEmpty(panelNodeVoList)) {
            return new PanelNodeGetListResponse(0L, null);
        } else {
            return new PanelNodeGetListResponse((long) panelNodeVoList.size(), panelNodeVoList);
        }

    }
}
