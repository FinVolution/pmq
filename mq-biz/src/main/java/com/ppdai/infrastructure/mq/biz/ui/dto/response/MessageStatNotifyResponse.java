package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageStatEntity;

import java.util.List;

public class MessageStatNotifyResponse extends BaseUiResponse<List<NotifyMessageStatEntity>> {
    public MessageStatNotifyResponse(Long count, List<NotifyMessageStatEntity> data) {
        super(count, data);
    }
}
