package com.ppdai.infrastructure.mq.biz.dal.meta;

import org.apache.ibatis.annotations.Mapper;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.dal.common.BaseRepository;



/**
 * @author dal-generator
 */
@Mapper
public interface AuditLogRepository extends BaseRepository<AuditLogEntity> {
    Long getMinId();

    void deleteBy(Long minId);
}
    