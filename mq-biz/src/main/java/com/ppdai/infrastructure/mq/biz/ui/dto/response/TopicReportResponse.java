package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.TopicVo;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 19:30
 */
public class TopicReportResponse extends BaseUiResponse<List<TopicVo>> {
    public TopicReportResponse(Long count, List<TopicVo> data) {
        super(count, data);
    }
}
