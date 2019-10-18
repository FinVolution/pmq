package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

public class DbNodeGetListResponse extends BaseUiResponse<List<DbNodeEntity>> {

    public DbNodeGetListResponse(Long count, List<DbNodeEntity> data) {
        super(count, data);
    }

}
