package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

public class ConsumerGroupGetNamesResponse extends BaseUiResponse<List<String>>{


    public ConsumerGroupGetNamesResponse(Long count, List<String> data) {
        super(count, data);
    }
}
