package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConnectionsVo;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 16:41
 */
public class DbNodeConnectionsResponse extends BaseUiResponse<List<ConnectionsVo>> {
    public DbNodeConnectionsResponse(String code, String msg) {
        super(code, msg);
    }

    public DbNodeConnectionsResponse(Long count, List<ConnectionsVo> data) {
        super(count, data);
    }
}
