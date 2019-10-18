package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 19:30
 */
public class TopicSearchResponse extends BaseUiResponse<List<TopicEntity>> {
    public TopicSearchResponse(Long count, List<TopicEntity> data) {
        super(count, data);
    }

    public TopicSearchResponse(){
        super();
    }
}
