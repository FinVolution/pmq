package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 14:59
 */
public class ConsumerGroupSelectResponse extends BaseUiResponse<List<String>> {

    public ConsumerGroupSelectResponse(Long count, List<String> data) {
        super(count, data);
    }

}
