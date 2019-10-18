package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageEntity;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

public class MessageNotifyResponse extends BaseUiResponse<List<NotifyMessageEntity>> {
    public MessageNotifyResponse(Long count, List<NotifyMessageEntity> data) {
        super(count, data);
    }
}
