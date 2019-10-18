package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.PhysicalMachineReportVo;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/15 19:19
 */
public class PhysicalMachineReportResponse extends BaseUiResponse<List<PhysicalMachineReportVo>> {
    public PhysicalMachineReportResponse(Long count, List<PhysicalMachineReportVo> data) {
        super(count, data);
    }
}
