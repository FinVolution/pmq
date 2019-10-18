package com.ppdai.infrastructure.mq.biz.dto.response;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 14:36
 */
public class ConsumerGroupDeleteResponse extends BaseUiResponse<Void> {
    public ConsumerGroupDeleteResponse() {
        super();
    }

    public ConsumerGroupDeleteResponse(String code, String msg) {
        super(code, msg);
    }

}
