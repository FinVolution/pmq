package com.ppdai.infrastructure.rest.mq.controller.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.ppdai.infrastructure.mq.biz.cache.ConsumerGroupCacheService;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.ConsumerGroupChangedListener;
import com.ppdai.infrastructure.mq.biz.common.metric.MetricSingleton;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerGroupDto;
import com.ppdai.infrastructure.mq.biz.dto.client.ConsumerGroupOneDto;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.GetConsumerGroupResponse;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;

@RestController
@RequestMapping(MqConstanst.CONSUMERPRE)
public class ConsumerGroupNotifyController implements ConsumerGroupChangedListener {

	// private static final Logger log =
	// LoggerFactory.getLogger(ConsumerGroupNotifyController.class);

	private final Map<GetConsumerGroupRequest, DeferredResult<GetConsumerGroupResponse>> mapAppPolling = new ConcurrentHashMap<>();

	@Autowired
	private ConsumerGroupCacheService consumerGroupCacheService;
	@Autowired
	private ConsumerService consumerService;
	private static AtomicLong longPollingCounter = new AtomicLong(0);
	@Autowired
	private SoaConfig soaConfig;
	private TraceMessage traceMessage3 = TraceFactory.getInstance("ConsumerGroupPollingCount");

	@PostConstruct
	private void init() {
		MetricSingleton.getMetricRegistry().register(MetricRegistry.name("data.ConsumerGroupPollingCount"),
				new Gauge<Long>() {
					@Override
					public Long getValue() {
						return longPollingCounter.get();
					}
				});
	}

	@GetMapping("/getConsumerGroupPollingCount")
	public long getServicePollingCount() {
		return longPollingCounter.get();
	}

	@PostMapping("/getConsumerGroupPolling")
	public DeferredResult<GetConsumerGroupResponse> getConsumerGroupPolling(
			@RequestBody GetConsumerGroupRequest request) {
		GetConsumerGroupResponse response = new GetConsumerGroupResponse();
		response.setSuc(true);
		response.setSleepTime(RandomUtils.nextInt(50, 2000));
		response.setBrokerMetaMode(soaConfig.getBrokerMetaMode());
		DeferredResult<GetConsumerGroupResponse> deferredResult = new DeferredResult<>(
				soaConfig.getPollingTimeOut() * 1000L, response);
		GetConsumerGroupResponse getApplicationResponse = doCheckConsumerGroupPolling(request);
		if (getApplicationResponse != null) {
			deferredResult.setResult(getApplicationResponse);
		} else {
			mapAppPolling.put(request, deferredResult);
			long count = longPollingCounter.incrementAndGet();
			TraceMessageItem traceMessageItem = new TraceMessageItem();
			traceMessageItem.status = count + "";
			if (count > soaConfig.getPollingSize()) {
				response.setSleepTime(RandomUtils.nextInt(50, 2000));
				deferredResult.setResult(response);
				longPollingCounter.decrementAndGet();
			} else {
				deferredResult.onTimeout(() -> {
					getFollowMsg(request, "getConsumerGroupPolling time out notify");
				});
				deferredResult.onCompletion(() -> {
					Transaction transaction = Tracer.newTransaction("Service", "getConsumerGroupPolling");
					try {
						if (mapAppPolling.remove(request) != null) {
							long count1 = longPollingCounter.decrementAndGet();
							traceMessageItem.msg = count + "_" + count1;
							traceMessage3.add(traceMessageItem);
						}
						transaction.setStatus(Transaction.SUCCESS);
					} catch (Exception e) {
						transaction.setStatus(e);
					} finally {
						transaction.complete();
					}
				});
			}
		}
		return deferredResult;
	}

	private void getFollowMsg(GetConsumerGroupRequest request, String action) {
		// if (soaConfig.isFullLog()) {
		// if (request != null && request.getConsumerGroupVersion() != null) {
		// for (String key : request.getConsumerGroupVersion().keySet()) {
		// log.info("app_{}_is_{},and request version is {}", key,
		// action.replaceAll(" ", "_"),
		// request.getConsumerGroupVersion().get(key));
		// }
		// }
		// }
	}

	private void notifyMessage() {
		int notifyBatchSize = 0;
		Map<GetConsumerGroupRequest, DeferredResult<GetConsumerGroupResponse>> mapTemp = new HashMap<>(mapAppPolling);
		for (GetConsumerGroupRequest request : mapTemp.keySet()) {
			try {
				notifyBatchSize++;
				GetConsumerGroupResponse response = doCheckConsumerGroupPolling(request);
				if (response != null && mapAppPolling.containsKey(request)) {
					mapTemp.get(request).setResult(response);
				}
			} catch (Exception e) {

			}
			if (soaConfig.getNotifyWaitTime() > 0 && notifyBatchSize > soaConfig.getNotifyBatchSize()) {
				Util.sleep(soaConfig.getNotifyWaitTime());
			}
		}
	}

	private GetConsumerGroupResponse doCheckConsumerGroupPolling(GetConsumerGroupRequest request) {
		Map<String, ConsumerGroupDto> consumerGroupMap = consumerGroupCacheService.getCache();
		GetConsumerGroupResponse response = new GetConsumerGroupResponse();
		response.setSuc(true);
		response.setConsumerDeleted(0);
		response.setBrokerMetaMode(soaConfig.getBrokerMetaMode());
		if (consumerService.get(request.getConsumerId()) == null) {
			response.setConsumerDeleted(1);
			return response;
		} else {
			Map<String, ConsumerGroupOneDto> dataRs = new HashMap<>();
			//t1,key 为consumergroupname,value为consumergroup对应的版本号
			for (Map.Entry<String, Long> t1 : request.getConsumerGroupVersion().entrySet()) {
				if (consumerGroupMap.containsKey(t1.getKey()) && t1.getValue() < consumerGroupMap.get(t1.getKey()).getMeta().getVersion()) {
					ConsumerGroupOneDto consumerGroupOneDto = new ConsumerGroupOneDto();
					consumerGroupOneDto.setMeta(consumerGroupMap.get(t1.getKey()).getMeta());					
					consumerGroupOneDto.setQueues(new HashMap<>());
					if (consumerGroupMap.get(t1.getKey()).getConsumers() != null
							&& consumerGroupMap.get(t1.getKey()).getConsumers().containsKey(request.getConsumerId())) {
						consumerGroupOneDto
								.setQueues(consumerGroupMap.get(t1.getKey()).getConsumers().get(request.getConsumerId()));
					}
					dataRs.put(t1.getKey(), consumerGroupOneDto);
				}
			}
			if (dataRs.size() > 0) {
				response.setConsumerGroups(dataRs);
				return response;
			} else {
				return null;
			}
		}
	}

	@Override
	public void onChanged() {
		notifyMessage();

	}
}
