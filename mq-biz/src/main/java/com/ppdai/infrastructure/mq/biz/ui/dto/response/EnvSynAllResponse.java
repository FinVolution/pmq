package com.ppdai.infrastructure.mq.biz.ui.dto.response;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;

/**
 * @Author：wanghe02
 * @Date：2019/7/18 15:32
 */
public class EnvSynAllResponse extends BaseUiResponse<Void> {
    public EnvSynAllResponse(){
        super();
    }

    public EnvSynAllResponse(String code,String msg){
        super(code,msg);
    }
}
