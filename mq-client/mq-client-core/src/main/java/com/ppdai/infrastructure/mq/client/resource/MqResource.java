package com.ppdai.infrastructure.mq.client.resource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.ppdai.infrastructure.mq.biz.dto.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.BrokerException;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.IHttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.BaseResponse;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.client.metric.MetricSingleton;

public class MqResource implements IMqResource {
	private final Logger logger = LoggerFactory.getLogger(MqResource.class);
	private IHttpClient httpClient = null;
	private AtomicReference<List<String>> urlsG1 = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<List<String>> urlsG2 = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<List<String>> urlsOrigin = new AtomicReference<>(new ArrayList<>());
	// private AtomicInteger couter = new AtomicInteger(0);
	private Map<String, Long> failUrlG1 = new ConcurrentHashMap<>();
	private Map<String, Long> failUrlG2 = new ConcurrentHashMap<>();
	private ThreadPoolExecutor executor = null, executor1 = null;
	private AtomicLong counterG1 = new AtomicLong(0);
	private AtomicLong counterG2 = new AtomicLong(0);

	public MqResource(String url, long connectionTimeOut, long readTimeOut) {
		// this.httpClient = new HttpClient(connectionTimeOut, readTimeOut);
		this(new HttpClient(connectionTimeOut, readTimeOut), url);
	}

	public MqResource(IHttpClient httpClient, String url) {
		this.urlsG1.set(Arrays.asList(url.trim().split(",")));
		this.urlsG2.set(Arrays.asList(url.trim().split(",")));
		this.urlsOrigin.get().addAll(this.urlsG1.get());
		this.httpClient = httpClient;
		executor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(500),
				SoaThreadFactory.create("MqResource-heartbeat", true), new ThreadPoolExecutor.DiscardOldestPolicy());
		executor1 = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(500),
				SoaThreadFactory.create("MqResource-mail", true), new ThreadPoolExecutor.DiscardOldestPolicy());

	}

	public void setUrls(List<String> urlsTempG1, List<String> urlsTempG2) {
		if (urlsTempG1 == null) {
			urlsTempG1 = new ArrayList<>();
		}
		if (urlsTempG2 == null) {
			urlsTempG2 = new ArrayList<>();
		}
		urlsG1.set(urlsTempG1);
		urlsG2.set(urlsTempG2);
		if (urlsTempG1.size() > 0) {
			int count = ((new BigDecimal(Math.random())).multiply(new BigDecimal(urlsTempG1.size()))).intValue();
			counterG1.set(count);
		}
		if (urlsTempG2.size() > 0) {
			int count = ((new BigDecimal(Math.random())).multiply(new BigDecimal(urlsTempG2.size()))).intValue();
			counterG2.set(count);
		}
		failUrlG1.clear();
		failUrlG2.clear();
	}

	protected String getHost(boolean isImportant) {
		List<String> urLst = (isImportant ? urlsG1.get() : urlsG2.get());
		int urlSize = urLst.size();
		if (urLst.size() == 0) {
			urLst = urlsOrigin.get();
			urlSize = urLst.size();
		}
		if (urlSize == 1)
			return urLst.get(0);

		int count = 0;
		int counter1 = 0;
		if (isImportant) {
			counter1 = (int) (counterG1.incrementAndGet() % urlSize);
		} else {
			counter1 = (int) (counterG2.incrementAndGet() % urlSize);
		}
		while (count < urlSize) {
			String url = doGetHost(urLst, counter1, isImportant);
			counter1 = (counter1 + 1) % urlSize;
			if (!Util.isEmpty(url)) {
				return url;
			}
			count++;
		}
		return urLst.get(0);
	}

	protected String doGetHost(List<String> urLst, int count, boolean isImportant) {
		String temp = urLst.get(count);
		Long t = 0L;
		if (isImportant) {
			t = failUrlG1.get(temp);
		} else {
			t = failUrlG2.get(temp);
		}
		if (t != null) {
			long currentTime = System.currentTimeMillis();
			if (t > currentTime - 10 * 1000) {
				return "";
			}
		}
		return temp;
	}

	public long register(ConsumerRegisterRequest request) {
		if (request == null) {
			return 0;
		}
		String url = MqConstanst.CONSUMERPRE + "/register";
		ConsumerRegisterResponse response = null;
		try {
			response = post(request, url, 10, ConsumerRegisterResponse.class, true);
			if (response != null && !Util.isEmpty(response.getMsg())) {
				logger.warn(response.getMsg());
			}
		} catch (Exception e) {
			CatRequest request2 = new CatRequest();
			request2.setMethod("register");
			request2.setJson(JsonUtil.toJson(request));
			request2.setMsg(e.getMessage());
			addCat(request2);
			throw new RuntimeException(request.getName() + "注册失败," + e.getMessage() + "！");
		}
		return response.getId();
	}

	public void publishAndUpdateResultFailMsg(FailMsgPublishAndUpdateResultRequest request) {
		if (request == null) {
			return;
		}
		String url = MqConstanst.CONSUMERPRE + "/publishAndUpdateResultFailMsg";
		try {
			post(request, url, 2, FailMsgPublishAndUpdateResultResponse.class, true);
		} catch (Exception e) {
			CatRequest request2 = new CatRequest();
			request2.setMethod("register");
			request2.setJson(JsonUtil.toJson(request));
			request2.setMsg(e.getMessage());
			addCat(request2);
		}
	}

	public void deRegister(ConsumerDeRegisterRequest request) {
		if (request == null) {
			return;
		}
		String url = MqConstanst.CONSUMERPRE + "/deRegister";
		post(request, url, 10, ConsumerDeRegisterResponse.class, true);
	}

	public GetMetaGroupResponse getMetaGroup(GetMetaGroupRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.METAPRE + "/getMetaGroup";
		GetMetaGroupResponse response = post(request, url, 10, GetMetaGroupResponse.class, false);
		return response;
	}

	public GetTopicResponse getTopic(GetTopicRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.METAPRE + "/getTopic";
		GetTopicResponse response = post(request, url, 2, GetTopicResponse.class, false);
		return response;
	}

	public GetGroupTopicResponse getGroupTopic(GetGroupTopicRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.METAPRE + "/getGroupTopic";
		GetGroupTopicResponse response = post(request, url, 2, GetGroupTopicResponse.class, false);
		return response;
	}

	public void addCat(CatRequest request) {
		if (request == null) {
			return;
		}
		executor1.submit(new Runnable() {
			@Override
			public void run() {
				String url = MqConstanst.TOOLPRE + "/addCat";
				try {
					smPost(request, url, 1, CatResponse.class, false);
				} catch (Throwable e) {
					// TODO: handle exception
				}
			}
		});
	}

	@Override
	public void updateMeta(UpdateMetaRequest request) {
		if (request == null) {
			return;
		}
		executor1.submit(new Runnable() {
			@Override
			public void run() {
				String url = MqConstanst.TOOLPRE + "/updateMeta";
				try {
					smPost(request, url, 1, UpdateMetaResponse.class, false);
				} catch (Throwable e) {
					// TODO: handle exception
				}
			}
		});
	}

	protected <T> void smPost(Object request, String path, int tryCount, Class<T> class1, boolean isImportant) {
		T response = null;
		String url = null;
		String host = getHost(isImportant);
		url = host + path;
		try {
			response = httpClient.post(url, request, class1);
		} catch (Throwable e) {

		}
	}
	public boolean publish(PublishMessageRequest request) {
		return publish(request, 10);
	}

	public boolean publish(PublishMessageRequest request, int retryTimes) {
		if (request == null) {
			return true;
		}
		Transaction transaction = Tracer.newTransaction("mq-client-publish", request.getTopicName());
		Timer.Context timer1 = MetricSingleton.getMetricRegistry()
				.timer("mq.client.publish.time?topic=" + request.getTopicName()).time();
		try {
			String url = MqConstanst.CONSUMERPRE + "/publish";
			long start = System.nanoTime();
			PublishMessageResponse response = post(request, url, retryTimes, PublishMessageResponse.class, true);
			long end = System.nanoTime();
			if (response.getTime() > 0) {
				long t = end - start - response.getTime();
				t = (t - t % 1000000) / 1000000;
				MetricSingleton.getMetricRegistry()
						.histogram("mq.client.publish.network.time?topic=" + request.getTopicName()).update(t);
			}
			transaction.setStatus(Transaction.SUCCESS);
			if (!response.isSuc()) {
				String json = JsonUtil.toJson(request);
				logger.error(response.getMsg());
				CatRequest request2 = new CatRequest();
				request2.setMethod("publish_fail");
				request2.setJson(json);
				request2.setMsg(response.getMsg());
				addCat(request2);
			}
			return response.isSuc();
		} catch (Throwable e) {
			MetricSingleton.getMetricRegistry().counter("mq.client.publish.fail.count?topic=" + request.getTopicName())
					.inc();
			logger.error("publish_error", e);
			String json = JsonUtil.toJson(request);
			transaction.setStatus(e);
			CatRequest request2 = new CatRequest();
			request2.setMethod("publish");
			request2.setJson(json);
			request2.setMsg(e.getMessage());
			addCat(request2);

			SendMailRequest mailRequest = new SendMailRequest();
			mailRequest.setSubject("消息发送失败,客户端：" + request.getClientIp() + ",Topic:" + request.getTopicName());
			mailRequest.setContent("消息发送异常，" + ",消息体是：" + json + ",异常原因是：" + e.getMessage());
			mailRequest.setType(2);
			mailRequest.setTopicName(request.getTopicName());
			sendMail(mailRequest);
			throw new RuntimeException(e);
		} finally {
			transaction.complete();
			timer1.stop();
		}
	}

	public void commitOffset(CommitOffsetRequest request) {
		if (request == null) {
			return;
		}
		String url = MqConstanst.CONSUMERPRE + "/commitOffset";
		try {
			post(request, url, 10, CommitOffsetResponse.class, false);
		} catch (Exception e) {
			CatRequest request2 = new CatRequest();
			request2.setMethod("commitOffset");
			request2.setJson(JsonUtil.toJson(request));
			request2.setMsg(e.getMessage());
			addCat(request2);
		}
	}

	public ConsumerGroupRegisterResponse registerConsumerGroup(ConsumerGroupRegisterRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.CONSUMERPRE + "/registerConsumerGroup";
		try {
			ConsumerGroupRegisterResponse response = post(request, url, 2, ConsumerGroupRegisterResponse.class, true);
			boolean flag = response != null && response.isSuc();
			if (!flag && response != null) {
				logger.error("registerConsumerGroup_error," + response.getMsg());
			}
			return response;
		} catch (Exception e) {
			CatRequest request2 = new CatRequest();
			request2.setMethod("registerConsumerGroup");
			request2.setJson(JsonUtil.toJson(request));
			request2.setMsg(e.getMessage());
			addCat(request2);
			logger.error("registerConsumerGroup_error", e);
			throw e;
		}

	}

	public HeartbeatResponse heartbeat(HeartbeatRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.CONSUMERPRE + "/heartbeat";
		try {
			return post(request, url, 3, HeartbeatResponse.class, false);
		} catch (Throwable e) {
			// TODO: handle exception
		}
		return null;
	}

	public GetConsumerGroupResponse getConsumerGroup(GetConsumerGroupRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.CONSUMERPRE + "/getConsumerGroupPolling";
		return post(request, url, 10, GetConsumerGroupResponse.class, true);
	}

	public GetMessageCountResponse getMessageCount(GetMessageCountRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.CONSUMERPRE + "/getMessageCount";
		return post(request, url, 2, GetMessageCountResponse.class, true);
	}

	public PullDataResponse pullData(PullDataRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.CONSUMERPRE + "/pullData";
		try {
			return post(request, url, 2, PullDataResponse.class, true);
		} catch (Exception e) {
			CatRequest request2 = new CatRequest();
			request2.setMethod("pullData");
			request2.setJson(JsonUtil.toJson(request));
			request2.setMsg(e.getMessage());
			addCat(request2);
			throw e;
		}
	}

	public GetTopicQueueIdsResponse getTopicQueueIds(GetTopicQueueIdsRequest request) {
		if (request == null) {
			return null;
		}
		String url = MqConstanst.METAPRE + "/getTopicQueueIds";
		try {
			return post(request, url, 2, GetTopicQueueIdsResponse.class, false);
		} catch (Exception e) {
			CatRequest request2 = new CatRequest();
			request2.setMethod("getTopicQueueIds");
			request2.setJson(JsonUtil.toJson(request));
			request2.setMsg(e.getMessage());
			addCat(request2);
			throw e;
		}
	}

	public void addLog(LogRequest request) {
		if (request == null) {
			return;
		}
		executor.execute(new Runnable() {
			@Override
			public void run() {
				String url = MqConstanst.TOOLPRE + "/addLog";
				try {
					smPost(request, url, 1, LogResponse.class, false);
				} catch (Throwable e) {

				}
			}
		});

	}

	public void addOpLog(OpLogRequest request) {
		if (request == null) {
			return;
		}
		executor1.submit(new Runnable() {
			@Override
			public void run() {
				String url = MqConstanst.TOOLPRE + "/addOpLog";
				try {
					smPost(request, url, 5, OpLogResponse.class, false);
				} catch (Throwable e) {
					// TODO: handle exception
				}

			}
		});

	}

	public void sendMail(SendMailRequest request) {
		if (request == null) {
			return;
		}
		executor1.submit(new Runnable() {
			@Override
			public void run() {
				String url = MqConstanst.TOOLPRE + "/sendMail";
				try {
					smPost(request, url, 1, SendMailResponse.class, false);
				} catch (Throwable e) {
					// TODO: handle exception
				}
			}
		});
	}

	protected <T> T post(Object request, String path, int tryCount, Class<T> class1, boolean isImportant) {
		T response = null;
		int count = 0;
		Exception last = null;
		String url = null;
		while (response == null && count < tryCount) {
			String host = getHost(isImportant);
			url = host + path;
			try {
				response = httpClient.post(url, request, class1);
				last = null;
			} catch (IOException e) {
				if (!(url.indexOf(MqConstanst.CONSUMERPRE + "/heartbeat") != -1
						|| url.indexOf(MqConstanst.CONSUMERPRE + "/getMetaGroup") != -1)) {
					logger.error("访问" + url + "异常,access_error", e);
				}
				addErrorCat(e, request, count, tryCount);
				last = e;
			} catch (BrokerException e) {
				last = e;
				addErrorCat(e, request, count, tryCount);
			} catch (Exception e) {
				last = e;
				addErrorCat(e, request, count, tryCount);
			} finally {
				if (response != null) {
					if (isImportant) {
						failUrlG1.put(host, System.currentTimeMillis() - 10 * 1000);
					} else {
						failUrlG2.put(host, System.currentTimeMillis() - 10 * 1000);
					}
					if (response instanceof PublishMessageResponse) {
						PublishMessageResponse response2 = ((PublishMessageResponse) response);
						if (response2.getSleepTime() > 0) {
							response = null;
							logger.info(response2.getMsg());
							Util.sleep(response2.getSleepTime());
							// 这个不算重试，只是降速
							count--;
						}
					} else {
						BaseResponse baseResponse = (BaseResponse) response;
						if (!baseResponse.isSuc() && baseResponse.getCode() == MqConstanst.NO) {
							response = null;
							Util.sleep(1000);
						} else {
							if (!baseResponse.isSuc()) {
								logger.error(baseResponse.getMsg());
							}
						}
					}
				} else {
					// response 等于null 说明接口调用失败了。此时需要将url 放入失败接口中。
					if (isImportant) {
						failUrlG1.put(host, System.currentTimeMillis());
					} else {
						failUrlG2.put(host, System.currentTimeMillis());
					}
					Util.sleep(500);
				}
			}
			count++;
		}
		if (last != null) {			
			throw new RuntimeException(last);
		}
		return response;
	}
	private void addErrorCat(Exception e, Object request, int count, int tryCount) {
		try {
			if (request instanceof PublishMessageRequest && count < tryCount) {
				CatRequest request2 = new CatRequest();
				request2.setJson(JsonUtil.toJson(request) + ",try count " + count + " of " + tryCount);
				request2.setMethod("publish_try_" + ((PublishMessageRequest) request).getTopicName());
				request2.setMsg(e.getMessage());
				addCat(request2);
			}
		} catch (Exception ee) {
			// TODO: handle exception
		}

	}
	@Override
	public String getBrokerIp() {
		String url = getHost(false)+MqConstanst.TOOLPRE + "/getIp";
		try {
			return httpClient.get(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}
}