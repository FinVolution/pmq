package com.ppdai.infrastructure.mq.biz.common.util;

import java.io.IOException;

public interface IHttpClient {
	boolean check(String url);
	String post(String url, Object reqObj) throws IOException,BrokerException;
	<T> T post(String url, Object request, Class<T> class1) throws IOException,BrokerException;
	<T> T get(String url, Class<T> class1) throws IOException;
	String get(String url) throws IOException;
}
