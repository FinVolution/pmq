package com.ppdai.infrastructure.mq.client.exception;

@SuppressWarnings("serial")
public class ContentExceed65535Exception extends Exception {
	public ContentExceed65535Exception() {
		super("字符串对应的byte长度超过65535,注意一个中文字符占3个byte。");
	}
}
