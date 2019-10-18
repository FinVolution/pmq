package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.Map;

public class QueueCountResponse extends BaseUiResponse<Map<String, Long>> {
    public QueueCountResponse(Map<String, Long> data) {
        super(data);
    }
}
