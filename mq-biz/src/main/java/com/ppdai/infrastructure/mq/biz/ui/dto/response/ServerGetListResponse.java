package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 19:07
 */
public class ServerGetListResponse extends BaseUiResponse<List<ServerEntity>> {
    public ServerGetListResponse(Long count, List<ServerEntity> data) {
        super(count, data);
    }
}
