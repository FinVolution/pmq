package com.ppdai.infrastructure.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.util.CollectionUtils;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MetaHelper {
	// private static final MediaType JSONTYPE =
	// MediaType.parse("application/json; charset=utf-8");
	private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
	private String url = "http://localhost:8089";
	private OkHttpClient httpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
		@Override
		public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
			cookieStore.put(httpUrl.host(), list);
		}

		@Override
		public List<Cookie> loadForRequest(HttpUrl httpUrl) {
			List<Cookie> cookies = cookieStore.get(httpUrl.host());
			if (CollectionUtils.isEmpty(cookies)) {
				try {
					Cookie cookie = new Cookie.Builder().domain(httpUrl.host()).name("userSessionId")
							.value("2DAA024C41F462A6").build();
					cookies=new ArrayList<>();
					cookies.add(cookie);
				} catch (Exception e) {

				}

			}
			return cookies != null ? cookies : new ArrayList<Cookie>();
		}
	}).build();

	public MetaHelper(String url) {
		this.url = url;
	}

	public Response buildTopic(String topicName) {
		RequestBody formBody = new FormBody.Builder().add("topicName", topicName).build();
		return execute(url + "/test/buildTopic", formBody);
	}

	public Response deleteTopic(String topicName) {
		RequestBody formBody = new FormBody.Builder().add("topicName", topicName).build();
		return execute(url + "/test/deleteTopic", formBody);
	}

	public Response buildConsumerGroup(String groupName) {
		RequestBody formBody = new FormBody.Builder().add("consumerGroupName", groupName).build();
		return execute(url + "/test/buildConsumerGroup", formBody);
	}

	public Response deleteConsumerGroup(String groupName) {
		RequestBody formBody = new FormBody.Builder().add("consumerGroupName", groupName).build();
		return execute(url + "/test/deleteConsumerGroup", formBody);
	}

	public Response subscribe(String groupName, String topicName) {
		RequestBody formBody = new FormBody.Builder().add("consumerGroupName", groupName).add("topicName", topicName)
				.build();
		return execute(url + "/test/subscribe", formBody);
	}

	public Response unSubscribe(String groupName, String topicName) {
		RequestBody formBody = new FormBody.Builder().add("consumerGroupName", groupName).add("topicName", topicName)
				.build();
		return execute(url + "/test/unSubscribe", formBody);
	}

	private Response execute(String url, RequestBody formBody) {		
		Response response = null;		
		final Request request = new Request.Builder().url(url).post(formBody).build();
		try {
			response = httpClient.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
