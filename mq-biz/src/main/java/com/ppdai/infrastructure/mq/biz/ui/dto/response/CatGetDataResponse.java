package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;
import java.util.Map;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 14:01
 */
public class CatGetDataResponse extends BaseUiResponse<List<Map<String, Object>>> {

    public CatGetDataResponse(Long count, List<Map<String, Object>> data) {
        super(count, data);
    }


}
