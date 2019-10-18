package com.ppdai.infrastructure.mq.biz.common.exception;

import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;

public class SoaException extends RuntimeException{
	private BaseResponse response;
	private Exception exception;
	public SoaException(BaseResponse response,Exception e){
		this.response=response;
		this.exception=e;
	}
	public BaseResponse getResponse() {
		return response;
	}
	
	public Exception getException() {
		return exception;
	}
	
}
