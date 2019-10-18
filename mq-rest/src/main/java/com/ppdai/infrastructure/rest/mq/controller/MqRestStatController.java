package com.ppdai.infrastructure.rest.mq.controller;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.cache.ConsumerGroupCacheService;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.polling.MessageLagNotifyService;
import com.ppdai.infrastructure.mq.biz.service.CacheUpdateService;

@RestController
public class MqRestStatController {
	// @Autowired
	// private Message01Service message01Service;
	@Autowired
	private ConsumerGroupCacheService consumerGroupCacheService;

	@Autowired
	private MessageLagNotifyService MessageLagNotifyService;

	@Autowired
	private EmailUtil emailUtil;

	@GetMapping("/cacheJson")
	public String cacheJson(@RequestParam("key") String key) {
		if (Util.isEmpty(key))
			return "";
		Map<String, CacheUpdateService> cacheUpdateMap = SpringUtil.getBeans(CacheUpdateService.class);
		StringBuilder rs = new StringBuilder(10000);
		cacheUpdateMap.entrySet().forEach(t1 -> {
			if (t1.getKey().toLowerCase().indexOf(key.toLowerCase()) != -1) {
				rs.append("key:" + t1.getKey() + "\n");
				rs.append("value:" + t1.getValue().getCacheJson() + "\n");
				rs.append("----------------------------------------");
			}
		});
		return rs.toString();
	}

	@GetMapping("/mq/cache")
	public Object getCache() {
		return consumerGroupCacheService.getCache();
	}

	// @GetMapping("/dbHs")
	// public Object dbHs() {
	// return message01Service.health();
	// }

	@GetMapping("/mq/trace")
	public Object getTrace() {
		return TraceFactory.getTraces();
	}

	@GetMapping("/ip")
	public Object getIp() {
		return IPUtil.getLocalIP();
	}

	@GetMapping("/lag")
	public void tt() {
		MessageLagNotifyService.doStart();
	}

	@GetMapping("/mail")
	public void mail() {
		emailUtil.sendInfoMail("test", "test");
	}

	@GetMapping("/mq/th")
	public String th() {
		StringBuilder rs = new StringBuilder();
		Map<State, Integer> state = new HashMap<>();
		for (Map.Entry<Thread, StackTraceElement[]> t1 : Thread.getAllStackTraces().entrySet()) {
			Thread thread = t1.getKey();
			StackTraceElement[] stackTraceElements = t1.getValue();
			state.putIfAbsent(thread.getState(), 0);
			state.put(thread.getState(), state.get(thread.getState()) + 1);
			rs.append("\n<br/>线程名称：" + thread.getName() + ",线程id:" + thread.getId() + ",16进制为："
					+ Long.toHexString(thread.getId()) + ",线程优先级为：" + thread.getPriority() + "，线程状态："
					+ thread.getState() + "<br/>\n");
			for (StackTraceElement st : stackTraceElements) {
				rs.append(st.toString() + "<br/>\n");
			}
		}
		StringBuilder rs1 = new StringBuilder();
		for (Map.Entry<State, Integer> t1 : state.entrySet()) {
			rs1.append("线程状态：" + t1.getKey() + ",数量：" + t1.getValue() + "<br/>\n");
		}
		return rs1.toString() + rs.toString();
	}

	@PostConstruct
	public void report() {
		// MetricSingleton.getMetricRegistry().register(MetricRegistry.name("app.Count"),
		// new Gauge<Long>() {
		// @Override
		// public Long getValue() {
		// return appService.getCount();
		// }
		// });
		// MetricSingleton.getMetricRegistry().register(MetricRegistry.name("app.ClusterCount"),
		// new Gauge<Long>() {
		// @Override
		// public Long getValue() {
		// return appClusterService.getClusterCount();
		// }
		// });
		// MetricSingleton.getMetricRegistry().register(MetricRegistry.name("app.InstanceCount"),
		// new Gauge<Long>() {
		// @Override
		// public Long getValue() {
		// return instanceService.getCount();
		// }
		// });
	}
}
