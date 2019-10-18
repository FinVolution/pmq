package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

public class UserGetBizTypesResponse extends BaseUiResponse<List<String>> {
    public UserGetBizTypesResponse(Long count, List<String> data) {
        super(count, data);
    }
}
