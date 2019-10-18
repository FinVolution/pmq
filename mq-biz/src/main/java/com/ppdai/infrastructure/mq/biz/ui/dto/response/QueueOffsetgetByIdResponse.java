package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueOffsetVo;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 18:58
 */
public class QueueOffsetgetByIdResponse extends BaseUiResponse<QueueOffsetVo> {
    public QueueOffsetgetByIdResponse(QueueOffsetVo data) {
        super(data);
    }
}
