package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

/**
 * @Author：wanghe02
 * @Date：2019/8/5 14:41
 */
public class RedundanceCheckResponse extends BaseUiResponse<String> {

    public RedundanceCheckResponse() {
        super();
    }


    public RedundanceCheckResponse(String data) {
        super(data);
    }
}
