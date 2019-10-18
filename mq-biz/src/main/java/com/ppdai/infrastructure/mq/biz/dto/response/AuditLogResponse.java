package com.ppdai.infrastructure.mq.biz.dto.response;

import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

public class AuditLogResponse extends BaseUiResponse<List<AuditLogEntity>> {

    public AuditLogResponse(Long count, List<AuditLogEntity> data) {
        super(count, data);
    }
}
