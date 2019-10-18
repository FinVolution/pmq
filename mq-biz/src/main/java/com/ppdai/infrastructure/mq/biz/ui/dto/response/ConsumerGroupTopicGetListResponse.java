package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConsumerGroupTopicVo;

import java.util.List;

public class ConsumerGroupTopicGetListResponse extends BaseUiResponse<List<ConsumerGroupTopicVo>> {
    public ConsumerGroupTopicGetListResponse(Long count, List<ConsumerGroupTopicVo> data) {
        super(count, data);
    }
}
