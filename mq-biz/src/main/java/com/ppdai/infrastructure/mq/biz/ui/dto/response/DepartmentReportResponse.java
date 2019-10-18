package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.DepartmentVo;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/9/2 13:45
 */
public class DepartmentReportResponse extends BaseUiResponse<List<DepartmentVo>> {
    public DepartmentReportResponse(Long count, List<DepartmentVo> data) {
        super(count, data);
    }
}
