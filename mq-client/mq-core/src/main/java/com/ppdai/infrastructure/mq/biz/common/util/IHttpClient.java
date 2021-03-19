package com.ppdai.infrastructure.mq.biz.common.util;

import okhttp3.Callback;

import java.io.IOException;

public interface IHttpClient {
	boolean check(String url);
	String post(String url, Object reqObj) throws IOException,BrokerException;
	<T> T post(String url, Object request, Class<T> class1) throws IOException,BrokerException;
	<T> T get(String url, Class<T> class1) throws IOException;
	void postAsyn(String var1, Object var2, Callback var3);
	void getAsyn(String url, Callback callback);
	String get(String url) throws IOException;
}
