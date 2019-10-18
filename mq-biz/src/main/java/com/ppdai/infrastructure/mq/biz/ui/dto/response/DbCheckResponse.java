package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.Map;

/**
 * @author: liujianjun02
 * @date: 2019/3/4
 */

public class DbCheckResponse extends BaseUiResponse<Map<String,Object>> {

    public DbCheckResponse() {
    }

    public DbCheckResponse(Map<String, Object> data) {
        super(data);
    }

}
