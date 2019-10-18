package com.ppdai.infrastructure.mq.biz.common.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;

@Component
public class LogLevelConfig {
	
	@Autowired
	private SoaConfig soaConfig;
	private LoggingSystem loggingSystem = LoggingSystem.get(this.getClass().getClassLoader());
	private String logLevel = "";
	private Map<String, String> logMap = new ConcurrentHashMap<>();
	private LogLevel rootLevel=LogLevel.INFO;
	@PostConstruct
	private void init() {
		soaConfig.registerChanged(new Runnable() {
			@Override
			public void run() {
				setLogLevel();
			}
		});
		rootLevel=loggingSystem.getLoggerConfiguration("root").getEffectiveLevel();
		setLogLevel();
	}

	private void setLogLevel() {
		if (!(logLevel + "").equals(soaConfig.getSoaLogLevel())) {
			logLevel = soaConfig.getSoaLogLevel();
			try {
				Map<String, String> map = JsonUtil.parseJson(logLevel,
						new TypeReference<Map<String, String>>() {
						});
				if (map != null) {
					//loggingSystem.
					map.entrySet().forEach(t1 -> {
						changeLogLevel(t1.getKey(), t1.getValue());
					});
					for (String key : logMap.keySet()) {
						if (!map.containsKey(key)) {
							//因为默认没有删除loger接口，所以只能将级别设置成跟logger级别
							changeLogLevel(key, rootLevel);
						}
					}
					logMap = map;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void changeLogLevel(String loggerName, String level) {
		try {
			loggingSystem.setLogLevel(loggerName, coerceLogLevel(level));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void changeLogLevel(String loggerName, LogLevel level) {
		try {
			loggingSystem.setLogLevel(loggerName, level);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private LogLevel coerceLogLevel(String level) {
		if ("false".equalsIgnoreCase(level)) {
			return LogLevel.OFF;
		}
		return LogLevel.valueOf(level.toUpperCase());
	}

}
