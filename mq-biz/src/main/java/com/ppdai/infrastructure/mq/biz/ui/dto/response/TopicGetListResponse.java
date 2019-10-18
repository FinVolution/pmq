package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.TopicVo;

import java.util.List;

public class TopicGetListResponse extends BaseUiResponse<List<TopicVo>> {
    public TopicGetListResponse(Long count, List<TopicVo> data) {
        super(count, data);
    }
}
