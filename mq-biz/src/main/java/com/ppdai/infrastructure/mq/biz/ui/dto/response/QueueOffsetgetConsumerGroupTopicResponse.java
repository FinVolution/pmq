package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 18:58
 */
public class QueueOffsetgetConsumerGroupTopicResponse extends BaseUiResponse<String> {
    public QueueOffsetgetConsumerGroupTopicResponse(String data) {
        super(data);
    }
}
