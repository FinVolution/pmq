package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.UserInfo;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 19:47
 */
public class UserGetCurrentUserResponse extends BaseUiResponse<UserInfo> {
    public UserGetCurrentUserResponse(UserInfo data) {
        super(data);
    }
}
