package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.AnalyseDto;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 16:41
 */
public class DbNodeAnalyseResponse extends BaseUiResponse<List<AnalyseDto>> {
    public DbNodeAnalyseResponse(Long count, List<AnalyseDto> data) {
        super(count, data);
    }
}
