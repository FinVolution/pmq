package com.ppdai.infrastructure.mq.biz.dto;

import java.util.List;

import com.ppdai.infrastructure.mq.biz.dto.response.UiResponse;
/**
 * UiResponseHelper
 *公共静态类：设置参数
 * @author wanghe
 * @date 2018/03/21
 */

public class UiResponseHelper {
   public static UiResponse buildSuccessUiResp(Long count, List list){
       UiResponse uiResponse = new UiResponse();
       uiResponse.setCode(Constants.SUCCESS_CODE);
       uiResponse.setMsg("");
       uiResponse.setCount(count);
       uiResponse.setData(list);
       return uiResponse;
   }

    public static UiResponse buildSuccessUiResp(){
        UiResponse uiResponse = new UiResponse();
        uiResponse.setCode(Constants.SUCCESS_CODE);
        uiResponse.setMsg("请求成功");
        return uiResponse;
    }

   public static UiResponse buildFailUiResp(String errorCode ,String msg) {
       UiResponse uiResponse = new UiResponse();
       uiResponse.setCode(errorCode);
       uiResponse.setMsg(msg);
       return uiResponse;
   }

}
