package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueOffsetVo;

import java.util.List;

public class QueueOffsetGetListResponse extends BaseUiResponse<List<QueueOffsetVo>> {
    public QueueOffsetGetListResponse(Long count, List<QueueOffsetVo> data) {
        super(count, data);
    }
}
