package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.dto.UserInfo;

import java.util.List;

public class UserSearchResponse extends BaseUiResponse<List<UserInfo>> {
    public UserSearchResponse(Long count, List<UserInfo> data) {
        super(count, data);
    }
}
