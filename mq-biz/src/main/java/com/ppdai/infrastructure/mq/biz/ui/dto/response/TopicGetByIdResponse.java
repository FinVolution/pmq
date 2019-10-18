package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

public class TopicGetByIdResponse extends BaseUiResponse<TopicEntity> {
    public TopicGetByIdResponse(TopicEntity data) {
        super(data);
    }
}
