package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 19:30
 */
public class TopicExpandResponse extends BaseUiResponse<Void> {

    public TopicExpandResponse() {
        super();
    }

    public TopicExpandResponse(String code, String msg) {
        super(code,msg);
    }


}
