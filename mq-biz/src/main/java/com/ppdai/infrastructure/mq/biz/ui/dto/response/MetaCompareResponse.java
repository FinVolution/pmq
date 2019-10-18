package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.MetaCompareVo;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 17:30
 */
public class MetaCompareResponse extends BaseUiResponse<List<MetaCompareVo>> {

    public MetaCompareResponse(Long count, List<MetaCompareVo> data) {
        super(count, data);
    }
}
