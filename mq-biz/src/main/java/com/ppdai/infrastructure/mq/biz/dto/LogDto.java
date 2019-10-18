package com.ppdai.infrastructure.mq.biz.dto;

import com.ppdai.infrastructure.mq.biz.dto.client.LogRequest;

public class LogDto extends LogRequest {
	private Throwable throwable;
	
	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
