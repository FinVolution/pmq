package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.MessageVo;

import java.util.List;

public class MessageGetListResponse extends BaseUiResponse<List<MessageVo>>{

    public MessageGetListResponse(Long count, List<MessageVo> data){
        super(count,data);
    }
}
