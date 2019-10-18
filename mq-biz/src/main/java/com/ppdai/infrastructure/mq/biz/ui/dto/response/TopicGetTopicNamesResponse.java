package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 19:30
 */
public class TopicGetTopicNamesResponse extends BaseUiResponse<List<String>> {
    public TopicGetTopicNamesResponse(Long count, List<String> data) {
        super(count, data);
    }
}
