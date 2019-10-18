package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.PanelNodeVo;

import java.util.List;

public class PanelNodeGetListResponse extends BaseUiResponse<List<PanelNodeVo>> {

    public PanelNodeGetListResponse(Long count, List<PanelNodeVo> data) {
        super(count, data);
    }
}
