package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueVo;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 17:48
 */
public class QueueReportResponse extends BaseUiResponse<List<QueueVo>> {
    public QueueReportResponse(Long count, List<QueueVo> data) {
        super(count, data);
    }
}
