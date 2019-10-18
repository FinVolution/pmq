package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.Map;

public class MessageConditionResponse extends BaseUiResponse<Map<String,Object>>{

    public MessageConditionResponse(Map<String,Object> data) {
        super(data);
    }
}
