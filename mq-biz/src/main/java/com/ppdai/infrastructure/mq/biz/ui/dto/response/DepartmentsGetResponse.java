package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

public class DepartmentsGetResponse extends BaseUiResponse<List<String>> {
    public DepartmentsGetResponse(Long count, List<String> data) {
        super(count, data);
    }
}
