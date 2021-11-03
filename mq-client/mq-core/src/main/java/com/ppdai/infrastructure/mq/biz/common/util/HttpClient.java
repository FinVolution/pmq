package com.ppdai.infrastructure.mq.biz.common.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.ppdai.infrastructure.mq.biz.common.trace.CatContext;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

import okhttp3.*;

public class HttpClient implements IHttpClient{
	//private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
	private static final MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");

	private OkHttpClient client;

	public HttpClient(long connTimeout, long readTimeout) {
		ConnectionPool connectionPool = new ConnectionPool(100, 10, TimeUnit.SECONDS);
		client = new OkHttpClient.Builder().connectionPool(connectionPool)
				.connectTimeout(connTimeout, TimeUnit.MILLISECONDS).readTimeout(readTimeout, TimeUnit.MILLISECONDS)
				.build();

	}

	public HttpClient() {
		this(32000L, 32000L);
	}
	
	public boolean check(String url) {		
		Transaction transaction = Tracer.newTransaction("mq-http-doubleCheck", url);
		Response response = null;
		try {
			Request request = new Request.Builder().url(url).get().build();
		    response = client.newCall(request).execute();	
		    transaction.setStatus(Transaction.SUCCESS);
			return response.isSuccessful();
		} catch (Throwable e) {
			transaction.setStatus(e);			
			return false;
		} finally {			
			transaction.complete();
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {

			}
		}
	}


	public String post(String url, Object reqObj) throws IOException,BrokerException {
		String json = "";
		if (reqObj != null) { 
			json = JsonUtil.toJsonNull(reqObj);
		}
		Response response = null;
		Transaction transaction = Tracer.newTransaction("mq-http", url);
		try {
			RequestBody body = RequestBody.create(JSONTYPE, json);
			Request.Builder requestbuilder = new Request.Builder().url(url).post(body);
			CatContext catContext=Tracer.logRemoteCallClient();
			if(catContext!=null){
				requestbuilder.addHeader(CatContext.CHILD, catContext.getProperty(CatContext.CHILD));
				requestbuilder.addHeader(CatContext.PARENT, catContext.getProperty(CatContext.PARENT));
				requestbuilder.addHeader(CatContext.ROOT, catContext.getProperty(CatContext.ROOT));
			}
			Request request=requestbuilder.build();
			response = client.newCall(request).execute();		
			transaction.setStatus(Transaction.SUCCESS);			
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				BrokerException exception = new BrokerException(
						response.code() + " error,and message is " + response.message()+",json is "+json);
				throw exception;
			}
		} catch (IOException e) {
			transaction.setStatus(e);
			throw e;
		}catch (Throwable e) {
			transaction.setStatus(e);
			throw e;
		}
		finally {
			transaction.complete();
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {

			}
		}
	}
	

	public <T> T post(String url, Object request, Class<T> class1) throws IOException,BrokerException {
		String rs = post(url, request);
		if (rs == null || rs.length() == 0 || rs.trim().length() == 0) {
			return null;
		} else {
			return JsonUtil.parseJson(rs, class1);
		}
	}
	
	
	public <T> T get(String url, Class<T> class1) throws IOException {
		String rs = get(url);
		if (rs == null || rs.length() == 0 || rs.trim().length() == 0) {
			return null;
		} else {
			return JsonUtil.parseJson(rs, class1);
		}
	}

	public void postAsyn(String url, Object reqObj, Callback callback) {
		String json = "";
		if (reqObj != null) {
			json = JsonUtil.toJsonNull(reqObj);
		}

		try {
			RequestBody body = RequestBody.create(JSONTYPE, json);
			okhttp3.Request.Builder requestbuilder = (new okhttp3.Request.Builder()).url(url).post(body);
			Request request = requestbuilder.build();
			if (callback != null) {
				this.client.newCall(request).enqueue(callback);
			} else {
				this.client.newCall(request).enqueue(new Callback() {
					public void onFailure(Call call, IOException e) {
					}

					public void onResponse(Call call, Response response) throws IOException {
						try {
							response.close();
						} catch (Exception var4) {
							;
						}

					}
				});
			}
		} catch (Exception var8) {
			;
		}

	}

	@Override
	public void getAsyn(String url, Callback callback) {
		try {
			Request.Builder requestbuilder = new Request.Builder().url(url).get();
			Request request = requestbuilder.build();
			if (callback != null) {
				client.newCall(request).enqueue(callback);
			} else {
				client.newCall(request).enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {

					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						try {
							response.close();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});

			}
		} catch (Exception e) {

		}

	}
	public String get(String url) throws IOException {		
		Response response = null; 
		Transaction transaction = null;
		transaction = Tracer.newTransaction("mq-http", url);
		try {		
			Request request = new Request.Builder().url(url).get().build();
			response = client.newCall(request).execute();
			if (transaction != null) {
				transaction.setStatus(Transaction.SUCCESS);
			}
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				RuntimeException exception = new RuntimeException(
						response.code() + " error,and message is " + response.message());				
				throw exception;
			}
		} catch (Throwable e) {
			transaction.setStatus(e);
			throw new RuntimeException(e);
		} finally {
			transaction.complete();
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {

			}
		}
	}

}
