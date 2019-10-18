package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 16:41
 */
public class DbNodeCompareResponse extends BaseUiResponse<List<Long>> {
    public DbNodeCompareResponse(Long count, List<Long> data) {
        super(count, data);
    }
}
