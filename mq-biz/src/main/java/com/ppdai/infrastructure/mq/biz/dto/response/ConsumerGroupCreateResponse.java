package com.ppdai.infrastructure.mq.biz.dto.response;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 14:36
 */
public class ConsumerGroupCreateResponse extends BaseUiResponse<Void> {
    public ConsumerGroupCreateResponse() {
        super();
    }

    public ConsumerGroupCreateResponse(String code, String msg) {
        super(code,msg);
    }

}
