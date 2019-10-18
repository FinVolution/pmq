package com.ppdai.infrastructure.ui.controller;

import com.ppdai.infrastructure.mq.biz.ui.dto.response.DepartmentReportResponse;
import com.ppdai.infrastructure.ui.service.UiDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    UiDepartmentService uiDepartmentService;

    @RequestMapping("/report")
    public DepartmentReportResponse getReport(){
        return uiDepartmentService.getDepartmentReport();
    }

}
