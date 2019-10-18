package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

public class ConsumerGroupGetByIdResponse extends BaseUiResponse<ConsumerGroupEntity> {
    public ConsumerGroupGetByIdResponse(ConsumerGroupEntity data) {
        super(data);
    }
}
