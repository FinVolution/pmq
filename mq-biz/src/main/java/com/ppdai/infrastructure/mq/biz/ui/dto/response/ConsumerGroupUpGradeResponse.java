package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConsumerGroupVo;

import java.util.List;

/**
 * @Author：wanghe02
 * @Date：2019/7/5 15:01
 */
public class ConsumerGroupUpGradeResponse extends BaseUiResponse<List<ConsumerGroupVo>> {
    public ConsumerGroupUpGradeResponse(Long count, List<ConsumerGroupVo> data) {
        super(count, data);
    }
}
