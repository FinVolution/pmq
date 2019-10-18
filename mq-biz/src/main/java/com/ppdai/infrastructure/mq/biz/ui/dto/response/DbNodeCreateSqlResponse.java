package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 16:41
 */
public class DbNodeCreateSqlResponse extends BaseUiResponse<List<String>> {
    public DbNodeCreateSqlResponse(Long count, List<String> data) {
        super(count, data);
    }
}
