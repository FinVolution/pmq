package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConsumerVo;

import java.util.List;

public class ConsumerGetListResponse extends BaseUiResponse<List<ConsumerVo>> {


    public ConsumerGetListResponse(Long count, List<ConsumerVo> data) {
        super(count, data);
    }


}
