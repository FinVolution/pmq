package com.ppdai.infrastructure.ui.util;

import com.ppdai.infrastructure.mq.biz.dto.UiResponseHelper;
import com.ppdai.infrastructure.mq.biz.dto.Constants;
import com.ppdai.infrastructure.mq.biz.dto.response.UiResponse;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;

import org.slf4j.Logger;


public abstract class ExceptionHandlerInvoker {
    public abstract UiResponse doInvoke();

    public UiResponse invoke(String path, Logger logger) {
        try {
            logger.info("receive request; Request path: " + path);
            return doInvoke();
        } catch (CheckFailException e) {
            logger.warn("request path: [" + path + "]," + e.getMessage(), e);
            return  UiResponseHelper.buildFailUiResp(Constants.CHECK_FAIL_ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            logger.error("request path: [" + path + "]," + e.getMessage(), e);
            return  UiResponseHelper.buildFailUiResp(Constants.UNKNOWN_ERROR_CODE,e.getClass().getName() + "。" + e.getMessage());
        }
    }

}
