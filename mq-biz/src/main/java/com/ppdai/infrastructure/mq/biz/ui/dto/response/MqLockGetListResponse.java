package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.MqLockEntity;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 17:36
 */
public class MqLockGetListResponse extends BaseUiResponse<List<MqLockEntity>> {
    public MqLockGetListResponse(Long count, List<MqLockEntity> data) {
        super(count, data);
    }
}
