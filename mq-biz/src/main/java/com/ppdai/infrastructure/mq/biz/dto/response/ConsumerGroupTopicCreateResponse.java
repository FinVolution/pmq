package com.ppdai.infrastructure.mq.biz.dto.response;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 16:03
 */
public class ConsumerGroupTopicCreateResponse extends BaseUiResponse<Void> {
    public ConsumerGroupTopicCreateResponse() {
        super();
    }

    public ConsumerGroupTopicCreateResponse(String code, String msg) {
        super(code, msg);
    }
}
