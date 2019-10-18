package com.ppdai.infrastructure.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.request.AuditLogRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.AuditLogResponse;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;

@RestController
@RequestMapping("/auditLog")
public class AuditLogController {

    Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    AuditLogService uiAuditLogService;

    @GetMapping("/list")
    public AuditLogResponse auditLogList(AuditLogRequest auditLogRequest) {
        return uiAuditLogService.logList(auditLogRequest);
    }
}
