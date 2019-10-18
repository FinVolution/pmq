package com.ppdai.infrastructure.ui.util;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.AuthFailException;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;

import org.slf4j.Logger;


public abstract class AbstractHandlerInvoker<T> {
    public abstract BaseUiResponse<T> doInvoke();

    public BaseUiResponse<T> invoke(String path, Logger logger) {
        try {
            return doInvoke();
        } catch (CheckFailException e) {
            logger.warn("request path: [" + path + "]," + e.getMessage());
            return new BaseUiResponse<>(Constants.CHECK_FAIL_ERROR_CODE, e.getMessage());
        } catch (AuthFailException e) {
            logger.warn("request path: [" + path + "]," + e.getMessage());
            return new BaseUiResponse<>(Constants.AUTH_FAIL_ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            logger.error("request path: [" + path + "]," + e.getMessage(), e);
            return new BaseUiResponse<>(Constants.UNKNOWN_ERROR_CODE, e.getClass().getName() + "; " + e.getMessage());
        }
    }

}
