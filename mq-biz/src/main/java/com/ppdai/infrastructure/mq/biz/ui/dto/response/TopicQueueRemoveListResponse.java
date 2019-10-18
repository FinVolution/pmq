package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueRemoveInfoVo;

import java.util.List;

public class TopicQueueRemoveListResponse extends BaseUiResponse<List<QueueRemoveInfoVo>> {
    public TopicQueueRemoveListResponse(Long count, List<QueueRemoveInfoVo> data) {
        super(count, data);
    }
}
