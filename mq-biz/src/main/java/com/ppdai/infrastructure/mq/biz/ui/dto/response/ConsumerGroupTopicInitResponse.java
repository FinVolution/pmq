package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.request.ConsumerGroupTopicCreateRequest;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 16:03
 */
public class ConsumerGroupTopicInitResponse extends BaseUiResponse<ConsumerGroupTopicCreateRequest> {
    public ConsumerGroupTopicInitResponse(ConsumerGroupTopicCreateRequest data) {
        super(data);
    }
}
