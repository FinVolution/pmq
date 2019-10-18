package com.ppdai.infrastructure.mq.biz.service;

import com.ppdai.infrastructure.mq.biz.dto.request.AuditLogRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.AuditLogResponse;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.service.common.BaseService;

/**
 * @author dal-generator
 */
public interface AuditLogService extends BaseService<AuditLogEntity> {
	long getMindId();
    void deleteBy(Long minId);
    void recordAudit(String tbName, long refId, String content);
    AuditLogResponse logList(AuditLogRequest auditLogRequest);
}
