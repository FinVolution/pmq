package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConsumerGroupVo;

import java.util.List;

public class ConsumerGroupGetListResponse extends BaseUiResponse<List<ConsumerGroupVo>> {

    public ConsumerGroupGetListResponse(Long count, List<ConsumerGroupVo> data) {
        super(count, data);
    }

}
