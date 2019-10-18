package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 19:17
 */
public class ToolCompareQueueGetListResponse extends BaseUiResponse<List<QueueEntity>> {
    public ToolCompareQueueGetListResponse() {
        super();
    }
}
