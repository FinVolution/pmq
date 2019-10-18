package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.Message01Entity;

public class MessageGetByTopicResponse extends BaseUiResponse<List<Message01Entity>>{

    public MessageGetByTopicResponse(List<Message01Entity> data){
        super(data);
    }

    public MessageGetByTopicResponse(String code, String msg){super(code,msg);}
}
