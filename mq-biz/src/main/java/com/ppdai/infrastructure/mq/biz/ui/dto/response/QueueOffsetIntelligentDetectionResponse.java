package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

/**
 * @Author：wanghe02
 * @Date：2019/10/21 14:42
 */
public class QueueOffsetIntelligentDetectionResponse extends BaseUiResponse<String> {

    public QueueOffsetIntelligentDetectionResponse(String code, String msg){
        super(code,msg);
    }

}
