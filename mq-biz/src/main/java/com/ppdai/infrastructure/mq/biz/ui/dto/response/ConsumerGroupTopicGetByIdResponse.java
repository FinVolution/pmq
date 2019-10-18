package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupTopicEntity;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 16:03
 */
public class ConsumerGroupTopicGetByIdResponse extends BaseUiResponse<ConsumerGroupTopicEntity> {
    public ConsumerGroupTopicGetByIdResponse(ConsumerGroupTopicEntity data) {
        super(data);
    }
}
