package com.ppdai.infrastructure.mq.biz.common;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import com.ppdai.infrastructure.mq.biz.common.trace.ICatSkip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.PropUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;

@Component
public class SoaConfig {

	private static Logger log = LoggerFactory.getLogger(SoaConfig.class);
	@Autowired
	private Environment env;
	private Map<Runnable, Boolean> changed = new ConcurrentHashMap<>();
	private Runnable proMonitor = null;
	private static final String SERVER_PROPERTIES_LINUX = "/opt/settings/server.properties";
	private static final String SERVER_PROPERTIES_WINDOWS = "C:/opt/settings/server.properties";
	private String envName = "";
	private List<Method> methods = new ArrayList<>();
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("SoaConfig-scan", true),
			new ThreadPoolExecutor.DiscardPolicy());

	public SoaConfig() {
		init();
	}

	private void init() {
		// 初始化环境
		initEnv();
		// 初始化monitor
		initMonitor();
		// 启动monitor
		startMonitor();
	}

	@PostConstruct
	private void setCat() {
		Tracer.setCatSkip(new ICatSkip() {
			@Override
			public boolean isSkip(String type, String name) {
				if (getTransSkipNames().containsKey(type) || getTransSkipNames().containsKey(type + "." + name)) {
					return true;
				}
				return false;
			}
		});
	}

	private void startMonitor() {
		executor.execute(() -> {
			try {
				while (true) {
					if (proMonitor != null && env != null) {
						proMonitor.run();
					}
					Util.sleep(1000);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		});
	}

	private void initMonitor() {
		Method[] declaredMethods = this.getClass().getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.getModifiers() == 1 && method.getParameterCount() == 0) {
				methods.add(method);
			}
		}
		SoaConfig pThis = this;
		proMonitor = new Runnable() {
			@Override
			public void run() {
				methods.forEach(t1 -> {
					try {
						t1.invoke(pThis);
						// System.out.println(t1.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

			}
		};
	}

	private void onChange() {
		executor.execute(() -> {
			for (Runnable runnable : changed.keySet()) {
				try {
					runnable.run();
				} catch (Exception e) {
					log.error("onchange-error", e);
				}
			}
		});
	}

	// 获取当前系统的环境
	public static final String env_getEnvName_key = "mq.env";
	private final String env_getEnvName_defaultValue = envName;
	private final String env_getEnvName_des = "当前系统环境";

	public String getEnvName() {
		if (!Util.isEmpty(envName)) {
			return envName;
		} else {
			return env.getProperty("mq.env", envName);
		}
	}

	public boolean isPro() {
		return "pro".equalsIgnoreCase(getEnvName());
	}

	// 获取当前环境
	private void initEnv() {
		envName = System.getProperty("env");
		if (Util.isEmpty(envName)) {
			envName = System.getenv("ENV");
		}
		if (Util.isEmpty(envName)) {
			FileInputStream in = null;
			try {
				File file = new File(SERVER_PROPERTIES_LINUX);
				if (!file.exists()) {
					file = new File(SERVER_PROPERTIES_WINDOWS);
				}
				Properties properties = new Properties();
				if (file.canRead()) {
					try {
						in = new FileInputStream(file);
						properties.load(in);
						envName = properties.getProperty("env", "").toLowerCase();

					} catch (Exception e) {
						// TODO: handle exception
					} finally {
						try {
							if (in != null) {
								in.close();
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}

			} catch (Exception e) {
				log.error("initEnv_SoaConfig_error", e);
			}
		}
		// isProFlag = "pro".equalsIgnoreCase(envName);
	}

	public void registerChanged(Runnable runnable) {
		changed.put(runnable, true);
	}

	private volatile String _heartBeatTime = "";
	private volatile int heartBeatTime = 0;
	public final String env_getConsumerHeartBeatTime_key = "mq.consumer.heartbeat.time";
	private final String env_getConsumerHeartBeatTime_defaultValue = "5";
	private final String env_getConsumerHeartBeatTime_des = "consumer发送心跳时间";

	/*
	 * consumer发送心跳时间
	 */
	public int getConsumerHeartBeatTime() {
		try {
			if (!_heartBeatTime.equals(
					env.getProperty(env_getConsumerHeartBeatTime_key, env_getConsumerHeartBeatTime_defaultValue))) {
				_heartBeatTime = env.getProperty(env_getConsumerHeartBeatTime_key,
						env_getConsumerHeartBeatTime_defaultValue);
				heartBeatTime = Integer.parseInt(
						env.getProperty(env_getConsumerHeartBeatTime_key, env_getConsumerHeartBeatTime_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			heartBeatTime = 5;
			onChange();
			log.error("getHeartBeatTime_SoaConfig_error", e);
		}
		return heartBeatTime;
	}

	private volatile String _getDbFailWaitTime = "";
	private volatile int dbFailWait = 0;
	private final String env_getDbFailWaitTime_key = "mq.dbFail.waitTime";
	private final String env_getDbFailWaitTime_defaultValue = "15";
	private final String env_getDbFailWaitTime_des = "数据库失败等待时间";

	// 数据库失败等待时间
	public int getDbFailWaitTime() {
		try {
			if (!_getDbFailWaitTime
					.equals(env.getProperty(env_getDbFailWaitTime_key, env_getDbFailWaitTime_defaultValue))) {
				_getDbFailWaitTime = env.getProperty(env_getDbFailWaitTime_key, env_getDbFailWaitTime_defaultValue);
				dbFailWait = Integer
						.parseInt(env.getProperty(env_getDbFailWaitTime_key, env_getDbFailWaitTime_defaultValue));
				if (dbFailWait < 15) {
					dbFailWait = 15;
				}
				onChange();
			}
		} catch (Exception e) {
			dbFailWait = 15;
			onChange();
			log.error("getgetDbFailWaitTime_SoaConfig_error", e);
		}
		return dbFailWait;
	}

	// consumer 定时检查周期
	private volatile String _getRbCheckInterval = "";
	private volatile int getRbCheckInterval = 0;
	private final String env_getRbCheckInterval_key = "mq.rb.interval";
	private final String env_getRbCheckInterval_defaultValue = "5";
	private final String env_getRbCheckInterval_des = "重平衡检测时间";

	// 重平衡检测时间
	public int getRbCheckInterval() {
		try {
			if (!_getRbCheckInterval
					.equals(env.getProperty(env_getRbCheckInterval_key, env_getRbCheckInterval_defaultValue))) {
				_getRbCheckInterval = env.getProperty(env_getRbCheckInterval_key, env_getRbCheckInterval_defaultValue);
				getRbCheckInterval = Integer
						.parseInt(env.getProperty(env_getRbCheckInterval_key, env_getRbCheckInterval_defaultValue));
				if (getRbCheckInterval < 2) {
					getRbCheckInterval = 2;
				}
				onChange();
			}
		} catch (Exception e) {
			getRbCheckInterval = 2;
			onChange();
			log.error("getgetRbCheckInterval_SoaConfig_error", e);
		}
		return getRbCheckInterval;
	}

	private volatile String _getCatSql = "";
	private volatile int getCatSql = 0;
	private final String env_getCatSql_key = "mq.cat.sql";
	private final String env_getCatSql_defaultValue = "0";
	private final String env_getCatSql_des = "是否记录cat sql";

	// 是否记录cat sql
	public int getCatSql() {
		try {
			if (!_getCatSql.equals(env.getProperty(env_getCatSql_key, env_getCatSql_defaultValue))) {
				_getCatSql = env.getProperty(env_getCatSql_key, env_getCatSql_defaultValue);
				getCatSql = Integer.parseInt(env.getProperty(env_getCatSql_key, env_getCatSql_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			getCatSql = 0;
			onChange();
			log.error("getgetCatSql_SoaConfig_error", e);
		}
		return getCatSql;
	}

	private final String env_getCatSqlKey_key = "mq.cat.sql.key";
	private final String env_getCatSqlKey_defaultValue = "0";
	private final String env_getCatSqlKey_des = "打印某个方法的sql";

	// 是否记录cat sql
	public String getCatSqlKey() {
		return env.getProperty(env_getCatSqlKey_key, env_getCatSqlKey_defaultValue);
	}

	private volatile String _getCleanInterval = "";
	private volatile int getCleanInterval = 0;
	private final String env_getCleanInterval_key = "mq.notifymessage.clean.interval";
	private final String env_getCleanInterval_defaultValue = "3600";
	private final String env_getCleanInterval_des = "消息定时清理时间";

	// 消息定时清理时间
	public int getCleanInterval() {
		try {
			if (!_getCleanInterval
					.equals(env.getProperty(env_getCleanInterval_key, env_getCleanInterval_defaultValue))) {
				_getCleanInterval = env.getProperty(env_getCleanInterval_key, env_getCleanInterval_defaultValue);
				getCleanInterval = Integer
						.parseInt(env.getProperty(env_getCleanInterval_key, env_getCleanInterval_defaultValue));
				if (getCleanInterval < 3600) {
					getCleanInterval = 3600;
				}
				onChange();
			}
		} catch (Exception e) {
			getCleanInterval = 3600;
			onChange();
			log.error("getCleanInterval_SoaConfig_error", e);
		}
		return getCleanInterval;
	}

	private volatile String _getAuditLogCleanInterval = "";
	private volatile int getAuditLogCleanInterval = 0;
	private final String env_getAuditLogCleanInterval_key = "mq.auditlog.clean.interval";
	private final String env_getAuditLogCleanInterval_defaultValue = "86400";
	private final String env_getAuditLogCleanInterval_des = "审计日志和历史消息定时清理时间";

	// 审计日志和历史消息定时清理时间
	public int getAuditLogCleanInterval() {
		try {
			if (!_getAuditLogCleanInterval.equals(
					env.getProperty(env_getAuditLogCleanInterval_key, env_getAuditLogCleanInterval_defaultValue))) {
				_getAuditLogCleanInterval = env.getProperty(env_getAuditLogCleanInterval_key,
						env_getAuditLogCleanInterval_defaultValue);
				getAuditLogCleanInterval = Integer.parseInt(
						env.getProperty(env_getAuditLogCleanInterval_key, env_getAuditLogCleanInterval_defaultValue));
				if (getAuditLogCleanInterval < 86400) {
					getAuditLogCleanInterval = 86400;
				}
				onChange();
			}
		} catch (Exception e) {
			getAuditLogCleanInterval = 86400;
			onChange();
			log.error("getgetAuditLogCleanInterval_SoaConfig_error", e);
		}
		return getAuditLogCleanInterval;
	}

	private volatile String _getCleanMessageInterval = "";
	private volatile int getCleanMessageInterval = 0;
	private final String env_getCleanMessageInterval_key = "mq.msg.clean.interval";
	private final String env_getCleanMessageInterval_defaultValue = "86400";
	private final String env_getCleanMessageInterval_des = "审计日志和历史消息定时清理时间";

	// 审计日志和历史消息定时清理时间
	public int getCleanMessageInterval() {
		try {
			if (!_getCleanMessageInterval.equals(
					env.getProperty(env_getCleanMessageInterval_key, env_getCleanMessageInterval_defaultValue))) {
				_getCleanMessageInterval = env.getProperty(env_getCleanMessageInterval_key,
						env_getCleanMessageInterval_defaultValue);
				getCleanMessageInterval = Integer.parseInt(
						env.getProperty(env_getCleanMessageInterval_key, env_getCleanMessageInterval_defaultValue));
				if (getCleanMessageInterval < 86400) {
					getCleanMessageInterval = 86400;
				}
				onChange();
			}
		} catch (Exception e) {
			getCleanMessageInterval = 86400;
			onChange();
			log.error("getgetCleanMessageInterval_SoaConfig_error", e);
		}
		return getCleanMessageInterval;
	}

	private volatile String _cleanBatchSize = "";
	private volatile int cleanBatchSize = 2000;
	private final String env_getCleanBatchSize_key = "mq.message.clean.batch.size";
	private final String env_getCleanBatchSize_defaultValue = "2000";
	private final String env_getCleanBatchSize_des = "历史消息一次清理条数";

	// 历史消息一次清理条数
	public int getCleanBatchSize() {
		try {
			if (!_cleanBatchSize
					.equals(env.getProperty(env_getCleanBatchSize_key, env_getCleanBatchSize_defaultValue))) {
				_cleanBatchSize = env.getProperty(env_getCleanBatchSize_key, env_getCleanBatchSize_defaultValue);
				cleanBatchSize = Integer
						.parseInt(env.getProperty(env_getCleanBatchSize_key, env_getCleanBatchSize_defaultValue));
				if (cleanBatchSize < 500) {
					cleanBatchSize = 500;
				}
				onChange();
			}
		} catch (Exception e) {
			cleanBatchSize = 500;
			onChange();
			log.error("getCleanBatchSize_SoaConfig_error", e);
		}
		return cleanBatchSize;
	}


	private volatile String _authorizedUsers = "";
	private volatile List<String> authorizedUsers = new ArrayList<>();
	private String env_getAuthorizedUsers_key = "mq.messageTool.filter.user.name";
	private String env_getAuthorizedUsers_defaultValue = "[]";
	private final String env_getAuthorizedUsers_des = "生产环境，界面发送工具授权用户列表";

	// 生产环境，界面发送工具授权用户列表
	public List<String> getAuthorizedUsers() {
		try {
			if (!_authorizedUsers.equals(env.getProperty(env_getAuthorizedUsers_key, env_getAuthorizedUsers_defaultValue))) {
				_authorizedUsers = env.getProperty(env_getAuthorizedUsers_key, env_getAuthorizedUsers_defaultValue);
				authorizedUsers = JsonUtil.parseJson(
						env.getProperty(env_getAuthorizedUsers_key, env_getAuthorizedUsers_defaultValue),
						new TypeReference<List<String>>() {
						});
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getAuthorizedUsers_SoaConfig_error", e);
		}
		return authorizedUsers;
	}


	private volatile String _getCleanSleepTime = "";
	private volatile int getCleanSleepTime = 100;
	private final String env_getCleanSleepTime_key = "mq.message.clean.sleep.time";
	private final String env_getCleanSleepTime_defaultValue = "100";
	private final String env_getCleanSleepTime_des = "历史消息清理间隔时间";

	// 历史消息清理间隔时间
	public int getCleanSleepTime() {
		try {
			if (!_getCleanSleepTime
					.equals(env.getProperty(env_getCleanSleepTime_key, env_getCleanSleepTime_defaultValue))) {
				_getCleanSleepTime = env.getProperty(env_getCleanSleepTime_key, env_getCleanSleepTime_defaultValue);
				getCleanSleepTime = Integer
						.parseInt(env.getProperty(env_getCleanSleepTime_key, env_getCleanSleepTime_defaultValue));
				if (getCleanSleepTime < 50) {
					getCleanSleepTime = 50;
				}
				onChange();
			}
		} catch (Exception e) {
			getCleanSleepTime = 100;
			onChange();
			log.error("getgetCleanSleepTime_SoaConfig_error", e);
		}
		return getCleanSleepTime;
	}

	private volatile String _getMqLockHeartBeatTime = "";
	private volatile int getMqLockHeartBeatTime = 0;
	private final String env_getMqLockHeartBeatTime_key = "mq.lock.heartbeat.time";
	private final String env_getMqLockHeartBeatTime_defaultValue = "15";
	private final String env_getMqLockHeartBeatTime_des = "锁心跳发送时间间隔";

	// 锁心跳发送时间间隔
	public int getMqLockHeartBeatTime() {
		try {
			if (!_getMqLockHeartBeatTime
					.equals(env.getProperty(env_getMqLockHeartBeatTime_key, env_getMqLockHeartBeatTime_defaultValue))) {
				_getMqLockHeartBeatTime = env.getProperty(env_getMqLockHeartBeatTime_key,
						env_getMqLockHeartBeatTime_defaultValue);
				getMqLockHeartBeatTime = Integer.parseInt(
						env.getProperty(env_getMqLockHeartBeatTime_key, env_getMqLockHeartBeatTime_defaultValue));
				if (getMqLockHeartBeatTime < 15) {
					getMqLockHeartBeatTime = 15;
				}
				onChange();
			}
		} catch (Exception e) {
			getMqLockHeartBeatTime = 15;
			onChange();
			log.error("getgetMqLockHeartBeatTime_SoaConfig_error", e);
		}
		return getMqLockHeartBeatTime;

	}

	private volatile String _isEnableRb = "";
	private volatile boolean isEnableRb = true;
	private final String env_isEnableRb_key = "mq.rb.enable";
	private final String env_isEnableRb_defaultValue = "true";
	private final String env_isEnableRb_des = "是否开启重平衡功能";

	// 是否开启重平衡功能
	public boolean isEnableRb() {
		try {
			if (!_isEnableRb.equals(env.getProperty(env_isEnableRb_key, env_isEnableRb_defaultValue))) {
				_isEnableRb = env.getProperty(env_isEnableRb_key, env_isEnableRb_defaultValue);
				isEnableRb = Boolean.parseBoolean(env.getProperty(env_isEnableRb_key, env_isEnableRb_defaultValue));
				onChange();
			}
			return isEnableRb;
		} catch (Exception e) {
			isEnableRb = true;
			onChange();
			log.error("isisEnableRb_SoaConfig_error", e);
			return true;
		}
	}

	private volatile String _isEnableRebuild = "";
	private volatile boolean isEnableRebuild = true;
	private final String env_isEnableRebuild_key = "mq.rebuild.enable";
	private final String env_isEnableRebuild_defaultValue = "true";
	private final String env_isEnableRebuild_des = "是否开启全量同步功能,尽量设置为true，否则删除操作会出现数据不同步";

	// 是否开启全量同步功能,尽量设置为true，否则删除操作会出现数据不同步
	public boolean isEnableRebuild() {
		try {
			if (!_isEnableRebuild.equals(env.getProperty(env_isEnableRebuild_key, env_isEnableRebuild_defaultValue))) {
				_isEnableRebuild = env.getProperty(env_isEnableRebuild_key, env_isEnableRebuild_defaultValue);
				isEnableRebuild = Boolean
						.parseBoolean(env.getProperty(env_isEnableRebuild_key, env_isEnableRebuild_defaultValue));
				onChange();
			}
			return isEnableRebuild;
		} catch (Exception e) {
			isEnableRb = true;
			onChange();
			log.error("isisEnableRebuild_SoaConfig_error", e);
			return true;
		}
	}

	private volatile String _getReinitInterval = "";
	private volatile int getReinitInterval = 0;
	private final String env_getReinitInterval_key = "mq.reinit.interval";
	private final String env_getReinitInterval_defaultValue = "30";
	private final String env_getReinitInterval_des = "定时全量同步间隔时间";

	// 定时全量同步间隔时间
	public int getReinitInterval() {
		try {
			if (!_getReinitInterval
					.equals(env.getProperty(env_getReinitInterval_key, env_getReinitInterval_defaultValue))) {
				_getReinitInterval = env.getProperty(env_getReinitInterval_key, env_getReinitInterval_defaultValue);
				getReinitInterval = Integer
						.parseInt(env.getProperty(env_getReinitInterval_key, env_getReinitInterval_defaultValue));
				if (getReinitInterval < 30) {
					getReinitInterval = 30;
				}
				onChange();
			}
		} catch (Exception e) {
			getReinitInterval = 30;
			onChange();
			log.error("getReinitInterval_SoaConfig_error", e);
		}
		return getReinitInterval;
		// return Integer.parseInt(env.getProperty("getReinitInterval", "30"));
	}

	private volatile String _getCheckPollingDataInterval = "";
	private volatile int getCheckPollingDataInterval = 0;
	private final String env_getCheckPollingDataInterval_key = "mq.polling.cache.interval";
	private final String env_getCheckPollingDataInterval_defaultValue = "1000";
	private final String env_getCheckPollingDataInterval_des = "元数据信息缓存同步间隔时间";

	// 元数据信息缓存同步间隔时间
	public int getCheckPollingDataInterval() {
		try {
			if (!_getCheckPollingDataInterval.equals(env.getProperty(env_getCheckPollingDataInterval_key, env
					.getProperty(env_getCheckPollingDataInterval_key, env_getCheckPollingDataInterval_defaultValue)))) {
				_getCheckPollingDataInterval = env.getProperty(env_getCheckPollingDataInterval_key,
						env_getCheckPollingDataInterval_defaultValue);
				getCheckPollingDataInterval = Integer.parseInt(env.getProperty(env_getCheckPollingDataInterval_key,
						env_getCheckPollingDataInterval_defaultValue));
				if (getCheckPollingDataInterval < 1000) {
					getCheckPollingDataInterval = 1000;
				}
				onChange();
			}
		} catch (Exception e) {
			getCheckPollingDataInterval = 1000;
			onChange();
			log.error("getgetCheckPollingDataInterval_SoaConfig_error", e);
		}
		return getCheckPollingDataInterval;
	}

	private volatile String _getNotifyBatchSize = "";
	private volatile int getNotifyBatchSize = 0;
	private final String env_getNotifyBatchSize_key = "mq.client.getNotifyBatchSize";
	private final String env_getNotifyBatchSize_defaultValue = "500";
	private final String env_getNotifyBatchSize_des = "批量通知条数";

	// 批量通知条数
	public int getNotifyBatchSize() {
		try {
			if (!_getNotifyBatchSize
					.equals(env.getProperty(env_getNotifyBatchSize_key, env_getNotifyBatchSize_defaultValue))) {
				_getNotifyBatchSize = env.getProperty(env_getNotifyBatchSize_key, env_getNotifyBatchSize_defaultValue);
				getNotifyBatchSize = Integer
						.parseInt(env.getProperty(env_getNotifyBatchSize_key, env_getNotifyBatchSize_defaultValue));
				if (getNotifyBatchSize < 500) {
					getNotifyBatchSize = 500;
				}
				onChange();
			}
		} catch (Exception e) {
			getNotifyBatchSize = 500;
			onChange();
			log.error("getNotifyBatchSize_SoaConfig_error", e);
		}
		return getNotifyBatchSize;
		// return Integer.parseInt(env.getProperty("getNotifyBatchSize",
		// "500"));
	}

	private volatile String _getNotifyWaitTime = "";
	private volatile int getNotifyWaitTime = 0;
	private final String env_getNotifyWaitTime_key = "mq.client.getNotifyWaitTime";
	private final String env_getNotifyWaitTime_defaultValue = "50";
	private final String env_getNotifyWaitTime_des = "当连接数过多时，等待间隔时间";

	// 当连接数过多时，等待间隔时间
	public int getNotifyWaitTime() {
		try {
			if (!_getNotifyWaitTime
					.equals(env.getProperty(env_getNotifyWaitTime_key, env_getNotifyWaitTime_defaultValue))) {
				_getNotifyWaitTime = env.getProperty(env_getNotifyWaitTime_key, env_getNotifyWaitTime_defaultValue);
				getNotifyWaitTime = Integer
						.parseInt(env.getProperty(env_getNotifyWaitTime_key, env_getNotifyWaitTime_defaultValue));
				if (getNotifyWaitTime < 50) {
					getNotifyWaitTime = 50;
				}
				onChange();
			}
		} catch (Exception e) {
			getNotifyWaitTime = 50;
			onChange();
			log.error("getgetNotifyWaitTime_SoaConfig_error", e);
		}
		return getNotifyWaitTime;
		// return Integer.parseInt(env.getProperty("getNotifyWaitTime", "50"));
	}

	private volatile String _getPollingTimeOut = "";
	private volatile int getPollingTimeOut = 0;
	private final String env_getPollingTimeOut_key = "mq.polling.timeout";
	private final String env_getPollingTimeOut_defaultValue = "10";
	private final String env_getPollingTimeOut_des = "长连接超时时间";

	// 长连接超时时间
	public int getPollingTimeOut() {
		try {
			if (!_getPollingTimeOut
					.equals(env.getProperty(env_getPollingTimeOut_key, env_getPollingTimeOut_defaultValue))) {
				_getPollingTimeOut = env.getProperty(env_getPollingTimeOut_key, env_getPollingTimeOut_defaultValue);
				getPollingTimeOut = Integer.parseInt(_getPollingTimeOut);
				if (getPollingTimeOut < 5) {
					getPollingTimeOut = 5;
				}
				onChange();
			}
		} catch (Exception e) {
			getPollingTimeOut = 10;
			onChange();
			log.error("getPollingTimeOut_SoaConfig_error", e);
		}
		return getPollingTimeOut;
		// return Integer.parseInt(env.getProperty("polling.size", "5000"));
	}

	private volatile String _getPollingSize = "";
	private volatile int getPollingSize = 0;
	private final String env_getPollingSize_key = "mq.polling.size";
	private final String env_getPollingSize_defaultValue = getTomcatAcceptCount() - 200 + "";
	private final String env_getPollingSize_des = "最多允许长连接个数";

	// 最多允许长连接个数
	public int getPollingSize() {
		try {
			if (!_getPollingSize.equals(env.getProperty(env_getPollingSize_key, env_getPollingSize_defaultValue))) {
				_getPollingSize = env.getProperty(env_getPollingSize_key, (env_getPollingSize_defaultValue));
				getPollingSize = Integer.parseInt(_getPollingSize);
				if (getPollingSize < 500) {
					getPollingSize = 500;
				}
				onChange();
			}
		} catch (Exception e) {
			getPollingSize = 500;
			onChange();
			log.error("getgetPollingSize_SoaConfig_error", e);
		}
		return getPollingSize;
		// return Integer.parseInt(env.getProperty("polling.size", "5000"));
	}

	private volatile String _getMqQueueCacheInterval = "";
	private volatile int getMqQueueCacheInterval = 0;
	private final String env_getMqQueueCacheInterval_key = "mq.queue.cache.interval";
	private final String env_getMqQueueCacheInterval_defaultValue = "5000";
	private final String env_getMqQueueCacheInterval_des = "Queue 缓存更新时间";

	// queue重建间隔时间
	public int getMqQueueCacheInterval() {
		try {
			if (!_getMqQueueCacheInterval.equals(
					env.getProperty(env_getMqQueueCacheInterval_key, env_getMqQueueCacheInterval_defaultValue))) {
				_getMqQueueCacheInterval = env.getProperty(env_getMqQueueCacheInterval_key,
						env_getMqQueueCacheInterval_defaultValue);
				getMqQueueCacheInterval = Integer.parseInt(
						env.getProperty(env_getMqQueueCacheInterval_key, env_getMqQueueCacheInterval_defaultValue));
				onChange();
				if (getMqQueueCacheInterval < 3000) {
					getMqQueueCacheInterval = 3000;
				}
			}
		} catch (Exception e) {
			getMqQueueCacheInterval = 5000;
			onChange();
			log.error("getgetMqQueueCacheInterval_SoaConfig_error", e);
		}
		return getMqQueueCacheInterval;
	}

	private volatile String _mqTopicCacheInterval = "";
	private volatile int mqTopicCacheInterval = 0;
	private final String env_getMqTopicCacheInterval_key = "mq.topic.cache.interval";
	private final String env_getMqTopicCacheInterval_defaultValue = "3000";
	private final String env_getMqTopicCacheInterval_des = "topic 缓存更新时间";

	// queue重建间隔时间
	public int getMqTopicCacheInterval() {
		try {
			if (!_mqTopicCacheInterval.equals(
					env.getProperty(env_getMqTopicCacheInterval_key, env_getMqTopicCacheInterval_defaultValue))) {
				_mqTopicCacheInterval = env.getProperty(env_getMqTopicCacheInterval_key,
						env_getMqTopicCacheInterval_defaultValue);
				mqTopicCacheInterval = Integer.parseInt(
						env.getProperty(env_getMqTopicCacheInterval_key, env_getMqTopicCacheInterval_defaultValue));
				onChange();
				if (mqTopicCacheInterval < 3000) {
					mqTopicCacheInterval = 3000;
				}
			}
		} catch (Exception e) {
			mqTopicCacheInterval = 3000;
			onChange();
			log.error("getMqTopicCacheInterval_SoaConfig_error", e);
		}
		return mqTopicCacheInterval;
	}

	private volatile String _getMqConsumerGroupCacheInterval = "";
	private volatile int getMqConsumerGroupCacheInterval = 0;
	private final String env_getMqConsumerGroupCacheInterval_key = "mq.ConsumerGroup.cache.interval";
	private final String env_getMqConsumerGroupCacheInterval_defaultValue = "3000";
	private final String env_getMqConsumerGroupCacheInterval_des = "ConsumerGroup 缓存更新时间";

	// queue重建间隔时间
	public int getMqConsumerGroupCacheInterval() {
		try {
			if (!_getMqConsumerGroupCacheInterval.equals(env.getProperty(env_getMqConsumerGroupCacheInterval_key,
					env_getMqConsumerGroupCacheInterval_defaultValue))) {
				_getMqConsumerGroupCacheInterval = env.getProperty(env_getMqConsumerGroupCacheInterval_key,
						env_getMqConsumerGroupCacheInterval_defaultValue);
				getMqConsumerGroupCacheInterval = Integer.parseInt(env.getProperty(
						env_getMqConsumerGroupCacheInterval_key, env_getMqConsumerGroupCacheInterval_defaultValue));
				onChange();
				if (getMqConsumerGroupCacheInterval < 3000) {
					getMqConsumerGroupCacheInterval = 3000;
				}
			}
		} catch (Exception e) {
			getMqConsumerGroupCacheInterval = 3000;
			onChange();
			log.error("getgetMqConsumerGroupCacheInterval_SoaConfig_error", e);
		}
		return getMqConsumerGroupCacheInterval;
	}

	private volatile String _getMqConsumerGroupTopicCacheInterval = "";
	private volatile int getMqConsumerGroupTopicCacheInterval = 0;
	private final String env_getMqConsumerGroupTopicCacheInterval_key = "mq.ConsumerGroupTopic.cache.interval";
	private final String env_getMqConsumerGroupTopicCacheInterval_defaultValue = "3000";
	private final String env_getMqConsumerGroupTopicCacheInterval_des = "ConsumerGroupTopic 缓存更新时间";

	// queue重建间隔时间
	public int getMqConsumerGroupTopicCacheInterval() {
		try {
			if (!_getMqConsumerGroupTopicCacheInterval
					.equals(env.getProperty(env_getMqConsumerGroupTopicCacheInterval_key,
							env_getMqConsumerGroupTopicCacheInterval_defaultValue))) {
				_getMqConsumerGroupTopicCacheInterval = env.getProperty(env_getMqConsumerGroupTopicCacheInterval_key,
						env_getMqConsumerGroupTopicCacheInterval_defaultValue);
				getMqConsumerGroupTopicCacheInterval = Integer
						.parseInt(env.getProperty(env_getMqConsumerGroupTopicCacheInterval_key,
								env_getMqConsumerGroupTopicCacheInterval_defaultValue));
				onChange();
				if (getMqConsumerGroupTopicCacheInterval < 3000) {
					getMqConsumerGroupTopicCacheInterval = 3000;
				}
			}
		} catch (Exception e) {
			getMqConsumerGroupTopicCacheInterval = 3000;
			onChange();
			log.error("getgetMqConsumerGroupTopicCacheInterval_SoaConfig_error", e);
		}
		return getMqConsumerGroupTopicCacheInterval;
	}

	private volatile String _getMqQueueOffsetCacheInterval = "";
	private volatile int getMqQueueOffsetCacheInterval = 0;
	private final String env_getMqQueueOffsetCacheInterval_key = "mq.queueoffset.cache.interval";
	private final String env_getMqQueueOffsetCacheInterval_defaultValue = "5000";
	private final String env_getMqQueueOffsetCacheInterval_des = "QueueOffset 缓存更新时间";

	// queue重建间隔时间
	public int getMqQueueOffsetCacheInterval() {
		try {
			if (!_getMqQueueOffsetCacheInterval.equals(env.getProperty(env_getMqQueueOffsetCacheInterval_key,
					env_getMqQueueOffsetCacheInterval_defaultValue))) {
				_getMqQueueOffsetCacheInterval = env.getProperty(env_getMqQueueOffsetCacheInterval_key,
						env_getMqQueueOffsetCacheInterval_defaultValue);
				getMqQueueOffsetCacheInterval = Integer.parseInt(env.getProperty(env_getMqQueueOffsetCacheInterval_key,
						env_getMqQueueOffsetCacheInterval_defaultValue));
				onChange();
				if (getMqQueueOffsetCacheInterval < 3000) {
					getMqQueueOffsetCacheInterval = 3000;
				}
			}
		} catch (Exception e) {
			getMqQueueOffsetCacheInterval = 3000;
			onChange();
			log.error("getMqQueueOffsetCacheInterval_SoaConfig_error", e);
		}
		return getMqQueueOffsetCacheInterval;
	}

	private volatile String _getMqDbNodeCacheInterval = "";
	private volatile int getMqDbNodeCacheInterval = 0;
	private final String env_getMqDbNodeCacheInterval_key = "mq.dbnode.cache.interval";
	private final String env_getMqDbNodeCacheInterval_defaultValue = "15000";
	private final String env_getMqDbNodeCacheInterval_des = "dbNode 缓存更新时间";

	// queue重建间隔时间
	public int getMqDbNodeCacheInterval() {
		try {
			if (!_getMqDbNodeCacheInterval.equals(
					env.getProperty(env_getMqDbNodeCacheInterval_key, env_getMqDbNodeCacheInterval_defaultValue))) {
				_getMqDbNodeCacheInterval = env.getProperty(env_getMqDbNodeCacheInterval_key,
						env_getMqDbNodeCacheInterval_defaultValue);
				getMqDbNodeCacheInterval = Integer.parseInt(
						env.getProperty(env_getMqDbNodeCacheInterval_key, env_getMqDbNodeCacheInterval_defaultValue));
				onChange();
				if (getMqDbNodeCacheInterval < 5000) {
					getMqDbNodeCacheInterval = 5000;
				}
			}
		} catch (Exception e) {
			getMqDbNodeCacheInterval = 5000;
			onChange();
			log.error("getgetMqDbNodeCacheInterval_SoaConfig_error", e);
		}
		return getMqDbNodeCacheInterval;
	}

	private volatile String _getMqReportInterval = "";
	private volatile int getMqReportInterval = 0;
	private final String env_getMqReportInterval_key = "mq.report.refresh.interval";
	private final String env_getMqReportInterval_defaultValue = "10000";
	private final String env_getMqReportInterval_des = "队列报表,queueOffset报表,topic报表 刷新间隔";

	// 队列报表，queueOffset报表，topic报表 刷新间隔
	public int getMqReportInterval() {
		try {
			if (!_getMqReportInterval
					.equals(env.getProperty(env_getMqReportInterval_key, env_getMqReportInterval_defaultValue))) {
				_getMqReportInterval = env.getProperty(env_getMqReportInterval_key,
						env_getMqReportInterval_defaultValue);
				getMqReportInterval = Integer
						.parseInt(env.getProperty(env_getMqReportInterval_key, env_getMqReportInterval_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			getMqReportInterval = 10000;
			onChange();
			log.error("getgetMqReportInterval_SoaConfig_error", e);
		}
		return getMqReportInterval;
	}

	private volatile String _getMqQueueMaxRebuildInterval = "";
	private volatile int getMqQueueMaxRebuildInterval = 5000;
	private final String env_getMqQueueMaxRebuildInterval_key = "mq.queue.max.rebuild.interval";
	private final String env_getMqQueueMaxRebuildInterval_defaultValue = "5000";
	private final String env_getMqQueueMaxRebuildInterval_des = "queue最大值定时刷新间隔时间";

	// queue最大值定时刷新间隔时间
	public int getMqQueueMaxRebuildInterval() {
		try {
			if (!_getMqQueueMaxRebuildInterval.equals(env.getProperty(env_getMqQueueMaxRebuildInterval_key,
					env_getMqQueueMaxRebuildInterval_defaultValue))) {
				_getMqQueueMaxRebuildInterval = env.getProperty(env_getMqQueueMaxRebuildInterval_key,
						env_getMqQueueMaxRebuildInterval_defaultValue);
				getMqQueueMaxRebuildInterval = Integer.parseInt(env.getProperty(env_getMqQueueMaxRebuildInterval_key,
						env_getMqQueueMaxRebuildInterval_defaultValue));
				if (getMqQueueMaxRebuildInterval < 5000) {
					getMqQueueMaxRebuildInterval = 5000;
				}
				onChange();
			}
		} catch (Exception e) {
			getMqQueueMaxRebuildInterval = 5000;
			onChange();
			log.error("getgetMqQueueMaxRebuildInterval_SoaConfig_error", e);
		}
		return getMqQueueMaxRebuildInterval;
	}

	private volatile String _getHeartbeatSleepTime = "";
	private volatile int getHeartbeatSleepTime = 0;
	private final String env_getHeartbeatSleepTime_key = "mq.heartbeat.sleepTime";
	private final String env_getHeartbeatSleepTime_defaultValue = "2000";
	private final String env_getHeartbeatSleepTime_des = "心跳处理等待时间";

	// 最多允许长连接个数
	public int getHeartbeatSleepTime() {
		try {
			if (!_getHeartbeatSleepTime
					.equals(env.getProperty(env_getHeartbeatSleepTime_key, env_getHeartbeatSleepTime_defaultValue))) {
				_getHeartbeatSleepTime = env.getProperty(env_getHeartbeatSleepTime_key,
						env_getHeartbeatSleepTime_defaultValue);
				getHeartbeatSleepTime = Integer.parseInt(
						env.getProperty(env_getHeartbeatSleepTime_key, env_getHeartbeatSleepTime_defaultValue));
				if (getHeartbeatSleepTime < 1000 || getHeartbeatSleepTime > 5000) {
					getHeartbeatSleepTime = 2000;
				}
				onChange();
			}
		} catch (Exception e) {
			getHeartbeatSleepTime = 1000;
			onChange();
			log.error("getgetHeartbeatSleepTime_SoaConfig_error", e);
		}
		return getHeartbeatSleepTime;
	}

	private volatile String _commitSleepTime = "";
	private volatile int commitSleepTime = 0;
	private final String env_getCommitSleepTime_key = "mq.commit.sleepTime";
	private final String env_getCommitSleepTime_defaultValue = "5000";
	private final String env_getCommitSleepTime_des = "偏移处理等待时间";

	public int getCommitSleepTime() {
		try {
			if (!_commitSleepTime
					.equals(env.getProperty(env_getCommitSleepTime_key, env_getCommitSleepTime_defaultValue))) {
				_commitSleepTime = env.getProperty(env_getCommitSleepTime_key, env_getCommitSleepTime_defaultValue);
				commitSleepTime = Integer
						.parseInt(env.getProperty(env_getCommitSleepTime_key, env_getCommitSleepTime_defaultValue));
				if (commitSleepTime < 1000 || commitSleepTime > 5000) {
					commitSleepTime = 2000;
				}
				onChange();
			}
		} catch (Exception e) {
			commitSleepTime = 3000;
			onChange();
			log.error("getCommitSleepTime_SoaConfig_error", e);
		}
		return commitSleepTime;
	}

	private volatile String _getHeartbeatBatchSize = "";
	private volatile int getHeartbeatBatchSize = 0;
	private final String env_getHeartbeatBatchSize_key = "mq.heartbeat.batch.size";
	private final String env_getHeartbeatBatchSize_defaultValue = "5";
	private final String env_getHeartbeatBatchSize_des = "允许同时更新心跳的个数";

	// 最多允许长连接个数
	public int getHeartbeatBatchSize() {
		try {
			if (!_getHeartbeatBatchSize
					.equals(env.getProperty(env_getHeartbeatBatchSize_key, env_getHeartbeatBatchSize_defaultValue))) {
				_getHeartbeatBatchSize = env.getProperty(env_getHeartbeatBatchSize_key,
						env_getHeartbeatBatchSize_defaultValue);
				getHeartbeatBatchSize = Integer.parseInt(
						env.getProperty(env_getHeartbeatBatchSize_key, env_getHeartbeatBatchSize_defaultValue));
				if (getHeartbeatBatchSize < 5 && heartBeatTime > 20) {
					getHeartbeatBatchSize = 5;
				}
				onChange();
			}
		} catch (Exception e) {
			getHeartbeatBatchSize = 5;
			onChange();
			log.error("getgetHeartbeatBatchSize_SoaConfig_error", e);
		}
		return getHeartbeatBatchSize;
	}

	private final String env_isFullLog_key = "mq.log.full";
	private final String env_isFullLog_defaultValue = "0";
	private final String env_isFullLog_des = "";

	public boolean isFullLog() {
		return "1".equals(env.getProperty(env_isFullLog_key, env_isFullLog_defaultValue));
	}

	private final String env_enableTimer_key = "mq.enableTimer";
	private final String env_enableTimer_defaultValue = "1";
	private final String env_enableTimer_des = "";

	public boolean enableTimer() {
		return "1".equals(env.getProperty(env_enableTimer_key, env_enableTimer_defaultValue));
	}

	private volatile String _getInitDbCount = "";
	private volatile int getInitDbCount = 10;
	private final String env_getInitDbCount_key = "mq.db.initCount";
	private final String env_getInitDbCount_defaultValue = "10";
	private final String env_getInitDbCount_des = "消息数据库初始链接个数";

	// 数据库初始链接个数
	public int getInitDbCount() {
		try {
			if (!_getInitDbCount.equals(env.getProperty(env_getInitDbCount_key, env_getInitDbCount_defaultValue))) {
				_getInitDbCount = env.getProperty(env_getInitDbCount_key, env_getInitDbCount_defaultValue);
				getInitDbCount = Integer
						.parseInt(env.getProperty(env_getInitDbCount_key, env_getInitDbCount_defaultValue));
				if (getInitDbCount < 2) {
					getInitDbCount = 2;
				}
				onChange();
			}
		} catch (Exception e) {
			getInitDbCount = 2;
			onChange();
			log.error("getgetInitDbCount_SoaConfig_error", e);
		}
		return getInitDbCount;
	}

	private volatile String _getMaxDbCount = "";
	private volatile int getMaxDbCount = 100;
	private final String env_getMaxDbCount_key = "mq.db.maxCount";
	private final String env_getMaxDbCount_defaultValue = "100";
	private final String env_getMaxDbCount_des = "消息数据库初始链接个数";

	// 数据库初始链接个数
	public int getMaxDbCount() {
		try {
			if (!_getMaxDbCount.equals(env.getProperty(env_getMaxDbCount_key, env_getMaxDbCount_defaultValue))) {
				_getMaxDbCount = env.getProperty(env_getMaxDbCount_key, env_getMaxDbCount_defaultValue);
				getMaxDbCount = Integer
						.parseInt(env.getProperty(env_getMaxDbCount_key, env_getMaxDbCount_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			getMaxDbCount = 5;
			onChange();
			log.error("getgetMaxDbCount_SoaConfig_error", e);
		}
		return getMaxDbCount;
	}

	private volatile String getSoaLogLevel = "";
	private final String env_getSoaLogLevel_key = "mq.log.level";
	private final String env_getSoaLogLevel_defaultValue = "";
	private final String env_getSoaLogLevel_des = "";

	public String getSoaLogLevel() {
		if (!getSoaLogLevel.equals(env.getProperty(env_getSoaLogLevel_key, env_getSoaLogLevel_defaultValue))) {
			getSoaLogLevel = env.getProperty(env_getSoaLogLevel_key, env_getSoaLogLevel_defaultValue);
			onChange();
		}
		return getSoaLogLevel;
	}

	private volatile String _getTomcatAcceptCount = "";
	private volatile int getTomcatAcceptCount = 0;
	private final String env_getTomcatAcceptCount_key = "server.tomcat.accept-count";
	private final String env_getTomcatAcceptCount_defaultValue = "500";
	private final String env_getTomcatAcceptCount_des = "最多允许长连接个数";

	// 最多允许长连接个数
	public int getTomcatAcceptCount() {
		try {
			if (env != null && !_getTomcatAcceptCount
					.equals(env.getProperty(env_getTomcatAcceptCount_key, env_getTomcatAcceptCount_defaultValue))) {
				_getTomcatAcceptCount = env.getProperty(env_getTomcatAcceptCount_key,
						env_getTomcatAcceptCount_defaultValue);
				getTomcatAcceptCount = Integer
						.parseInt(env.getProperty(env_getTomcatAcceptCount_key, env_getTomcatAcceptCount_defaultValue));
				if (getTomcatAcceptCount < 50) {
					getTomcatAcceptCount = 50;
				}
				onChange();
			}
		} catch (Exception e) {
			getTomcatAcceptCount = 100;
			onChange();
			log.error("getgetTomcatAcceptCount_SoaConfig_error", e);
		}
		return getTomcatAcceptCount;
	}

	private volatile String _getTomcatMaxThreads = "";
	private volatile int getTomcatMaxThreads = 0;
	private final String env_getTomcatMaxThreads_key = "server.tomcat.max-threads";
	private final String env_getTomcatMaxThreads_defaultValue = "200";
	private final String env_getTomcatMaxThreads_des = "tomcat最大线程数";

	// 最大线程数
	public int getTomcatMaxThreads() {
		try {
			if (env != null && !_getTomcatMaxThreads
					.equals(env.getProperty(env_getTomcatMaxThreads_key, env_getTomcatMaxThreads_defaultValue))) {
				_getTomcatMaxThreads = env.getProperty(env_getTomcatMaxThreads_key,
						env_getTomcatMaxThreads_defaultValue);
				getTomcatMaxThreads = Integer
						.parseInt(env.getProperty(env_getTomcatMaxThreads_key, env_getTomcatMaxThreads_defaultValue));
				if (getTomcatMaxThreads < 100) {
					getTomcatMaxThreads = 100;
				}
				onChange();
			}
		} catch (Exception e) {
			getTomcatMaxThreads = 200;
			onChange();
			log.error("getgetTomcatMaxThreads_SoaConfig_error", e);
		}
		return getTomcatMaxThreads;
	}

	private volatile String _getTomcatMinThreads = "";
	private volatile int getTomcatMinThreads = 0;
	private final String env_getTomcatMinThreads_key = "server.tomcat.min-threads";
	private final String env_getTomcatMinThreads_defaultValue = "10";
	private final String env_getTomcatMinThreads_des = "tomcat最小线程数";

	// 最大线程数
	public int getTomcatMinThreads() {
		try {
			if (env != null && !_getTomcatMinThreads
					.equals(env.getProperty(env_getTomcatMinThreads_key, env_getTomcatMinThreads_defaultValue))) {
				_getTomcatMinThreads = env.getProperty(env_getTomcatMinThreads_key,
						env_getTomcatMinThreads_defaultValue);
				getTomcatMinThreads = Integer
						.parseInt(env.getProperty(env_getTomcatMinThreads_key, env_getTomcatMinThreads_defaultValue));
				if (getTomcatMinThreads < 10) {
					getTomcatMinThreads = 10;
				}
				onChange();
			}
		} catch (Exception e) {
			getTomcatMinThreads = 10;
			onChange();
			log.error("getgetTomcatMinThreads_SoaConfig_error", e);
		}
		return getTomcatMinThreads;
	}

	private final String env_getEmailHost_key = "email.host";
	private final String env_getEmailHost_defaultValue = "";
	private final String env_getEmailHost_des = "邮件服务器地址";

	public String getEmailHost() {
		return env.getProperty(env_getEmailHost_key, env_getEmailHost_defaultValue);
	}

	private final String env_enableMailAuth_key = "email.enableAuth";
	private final String env_enableMailAuth_defaultValue = "false";
	private final String env_enableMailAuth_des = "邮件发送是否开启认证";

	public boolean enableMailAuth() {
		return "true".equals(env.getProperty(env_enableMailAuth_key, env_enableMailAuth_defaultValue));
	}

	private volatile String _emailPort = "";
	private volatile int emailPort = 0;
	private final String env_getEmailPort_key = "email.port";
	private final String env_getEmailPort_defaultValue = "25";
	private final String env_getEmailPort_des = "";

	public int getEmailPort() {
		try {
			if (!_emailPort.equals(env.getProperty(env_getEmailPort_key, env_getEmailPort_defaultValue))) {
				_emailPort = env.getProperty(env_getEmailPort_key, env_getEmailPort_defaultValue);
				emailPort = Integer.parseInt(_emailPort);
			}
		} catch (Exception e) {
			emailPort = 0;
		}
		return emailPort;
	}

	private final String env_getEmailAuName_key = "email.auName";
	private final String env_getEmailAuName_defaultValue = "";
	private final String env_getEmailAuName_des = "";

	public String getEmailAuName() {
		return env.getProperty(env_getEmailAuName_key, env_getEmailAuName_defaultValue);
	}

	private final String env_getEmailAuPass_key = "email.auPass";
	private final String env_getEmailAuPass_defaultValue = "";
	private final String env_getEmailAuPass_des = "";

	public String getEmailAuPass() {
		return env.getProperty(env_getEmailAuPass_key, env_getEmailAuPass_defaultValue);
	}

	private final String env_getAdminEmail_key = "admin.email";
	private final String env_getAdminEmail_defaultValue = "";
	private final String env_getAdminEmail_des = "";

	public String getAdminEmail() {
		return env.getProperty(env_getAdminEmail_key, env_getAdminEmail_defaultValue);
	}

	// private final String env_getAdminName_key = "admin.username";
	// private final String env_getAdminName_defaultValue = "";
	// private final String env_getAdminName_des = "";
	//
	// public String getAdminName() {
	// return env.getProperty(env_getAdminName_key,
	// env_getAdminName_defaultValue);
	// }

	private final String env_isEmailEnable_key = "email.enable";
	private final String env_isEmailEnable_defaultValue = "false";
	private final String env_isEmailEnable_des = "";

	public boolean isEmailEnable() {
		return "true".equals(env.getProperty(env_isEmailEnable_key, env_isEmailEnable_defaultValue));
	}

	private final String env_isOnlyEmailAdmin_key = "email.admin.only";
	private final String env_isOnlyEmailAdmin_defaultValue = "false";
	private final String env_isOnlyEmailAdmin_des = "";

	public boolean isOnlyEmailAdmin() {
		return "true".equals(env.getProperty(env_isOnlyEmailAdmin_key, env_isOnlyEmailAdmin_defaultValue));
	}

	private final String env_getConsumerInactivityTime_key = "mq.consumer.inactivity.check.interval";
	private final String env_getConsumerInactivityTime_defaultValue = "60";
	private final String env_getConsumerInactivityTime_des = "一分钟无心跳，默认消费者下线";

	public int getConsumerInactivityTime() {
		// 默认一分钟
		return Integer.parseInt(
				env.getProperty(env_getConsumerInactivityTime_key, env_getConsumerInactivityTime_defaultValue));
	}

	// 默认2小时
	private volatile String _getMessageLagNotifyCheckInterval = "";
	private volatile int getMessageLagNotifyCheckInterval = 600;
	private final String env_getMessageLagNotifyCheckInterval_key = "mq.message.lag.check.interval";
	private final String env_getMessageLagNotifyCheckInterval_defaultValue = "600";
	private final String env_getMessageLagNotifyCheckInterval_des = "消息堆积告警检查间隔，默认10分钟";

	// //检查可用队列周期
	public int getMessageLagNotifyCheckInterval() {
		try {
			if (!_getMessageLagNotifyCheckInterval.equals(env.getProperty(env_getMessageLagNotifyCheckInterval_key,
					env_getMessageLagNotifyCheckInterval_defaultValue))) {
				_getMessageLagNotifyCheckInterval = env.getProperty(env_getMessageLagNotifyCheckInterval_key,
						env_getMessageLagNotifyCheckInterval_defaultValue);
				getMessageLagNotifyCheckInterval = Integer.parseInt(env.getProperty(
						env_getMessageLagNotifyCheckInterval_key, env_getMessageLagNotifyCheckInterval_defaultValue));

				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getgetMessageLagNotifyCheckInterval_SoaConfig_error", e);
		}
		return getMessageLagNotifyCheckInterval;
	}

	// 失效consumer检查周期
	private final String env_getConsumerCheckInterval_key = "mq.consumer.check.interval";
	private final String env_getConsumerCheckInterval_defaultValue = "60";
	private final String env_getConsumerCheckInterval_des = "失效consumer检查周期";

	public int getConsumerCheckInterval() {
		return Integer
				.parseInt(env.getProperty(env_getConsumerCheckInterval_key, env_getConsumerCheckInterval_defaultValue));
	}

	// 失效consumer检查周期
	private final String env_getNoSubscribeInterval_key = "mq.consumer.noSubscribe.interval";
	private final String env_getNoSubscribeInterval_defaultValue = "60";
	private final String env_getNoSubscribeInterval_des = "consumer 未订阅检查周期";

	public int getNoSubscribeInterval() {
		return Integer
				.parseInt(env.getProperty(env_getNoSubscribeInterval_key, env_getNoSubscribeInterval_defaultValue));
	}

	private volatile String _getQueueExpansionCheckInterval = "";
	private volatile int getQueueExpansionCheckInterval = 21600;
	private final String env_getQueueExpansionCheckInterval_key = "mq.queue.expansion.check.interval";
	private final String env_getQueueExpansionCheckInterval_defaultValue = "21600";
	private final String env_getQueueExpansionCheckInterval_des = "检查可用队列周期";

	// //检查可用队列周期
	public int getQueueExpansionCheckInterval() {
		try {
			if (!_getQueueExpansionCheckInterval.equals(env.getProperty(env_getQueueExpansionCheckInterval_key,
					env_getQueueExpansionCheckInterval_defaultValue))) {
				_getQueueExpansionCheckInterval = env.getProperty(env_getQueueExpansionCheckInterval_key,
						env_getQueueExpansionCheckInterval_defaultValue);
				getQueueExpansionCheckInterval = Integer.parseInt(env.getProperty(
						env_getQueueExpansionCheckInterval_key, env_getQueueExpansionCheckInterval_defaultValue));

				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getQueueExpansionCheckInterval_SoaConfig_error", e);
		}
		return getQueueExpansionCheckInterval;
	}

	// 可用队列个数告警
	private final String env_getAvailableQueuesNum_key = "mq.available.queues.num";
	private final String env_getAvailableQueuesNum_defaultValue = "100";
	private final String env_getAvailableQueuesNum_des = "可用队列个数告警";

	public int getAvailableQueuesNum() {
		return Integer.parseInt(env.getProperty(env_getAvailableQueuesNum_key, env_getAvailableQueuesNum_defaultValue));
	}

	private volatile String _getFailTopicSaveDayNum = "";
	private volatile int getFailTopicSaveDayNum = 0;
	private final String env_getFailTopicSaveDayNum_key = "mq.fail.topic.saveDayNum";
	private final String env_getFailTopicSaveDayNum_defaultValue = "2";
	private final String env_getFailTopicSaveDayNum_des = "失败topic消息保存天数";

	// 失败topic消息保存天数
	public int getFailTopicSaveDayNum() {
		try {
			if (!_getFailTopicSaveDayNum
					.equals(env.getProperty(env_getFailTopicSaveDayNum_key, env_getFailTopicSaveDayNum_defaultValue))) {
				_getFailTopicSaveDayNum = env.getProperty(env_getFailTopicSaveDayNum_key,
						env_getFailTopicSaveDayNum_defaultValue);
				getFailTopicSaveDayNum = Integer.parseInt(
						env.getProperty(env_getFailTopicSaveDayNum_key, env_getFailTopicSaveDayNum_defaultValue));

				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getgetFailTopicSaveDayNum_SoaConfig_error", e);
		}
		return getFailTopicSaveDayNum;
	}

	private volatile String _getFailTopicDelayProcessTime = "";
	private volatile int getFailTopicDelayProcessTime = 0;
	private final String env_getFailTopicDelayProcessTime_key = "mq.fail.topic.delayProcessTime";
	private final String env_getFailTopicDelayProcessTime_defaultValue = "60";
	private final String env_getFailTopicDelayProcessTime_des = "失败topic的默认延迟处理时间";

	// 失败topic的默认延迟处理时间
	public int getFailTopicDelayProcessTime() {
		try {
			if (!_getFailTopicDelayProcessTime.equals(env.getProperty(env_getFailTopicDelayProcessTime_key,
					env_getFailTopicDelayProcessTime_defaultValue))) {
				_getFailTopicDelayProcessTime = env.getProperty(env_getFailTopicDelayProcessTime_key,
						env_getFailTopicDelayProcessTime_defaultValue);
				getFailTopicDelayProcessTime = Integer.parseInt(env.getProperty(env_getFailTopicDelayProcessTime_key,
						env_getFailTopicDelayProcessTime_defaultValue));

				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getgetFailTopicDelayProcessTime_SoaConfig_error", e);
		}
		return getFailTopicDelayProcessTime;
	}

	private volatile String _getFailTopicThreadSize = "";
	private volatile int getFailTopicThreadSize = 0;
	private final String env_getFailTopicThreadSize_key = "mq.fail.topic.threadsize";
	private final String env_getFailTopicThreadSize_defaultValue = "1";
	private final String env_getFailTopicThreadSize_des = "失败topic的处理线程数";

	// 失败topic的处理线程数
	public int getFailTopicThreadSize() {
		try {
			if (!_getFailTopicThreadSize
					.equals(env.getProperty(env_getFailTopicThreadSize_key, env_getFailTopicThreadSize_defaultValue))) {
				_getFailTopicThreadSize = env.getProperty(env_getFailTopicThreadSize_key,
						env_getFailTopicThreadSize_defaultValue);
				getFailTopicThreadSize = Integer.parseInt(
						env.getProperty(env_getFailTopicThreadSize_key, env_getFailTopicThreadSize_defaultValue));

				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getFailTopicThreadSize_SoaConfig_error", e);
		}
		return getFailTopicThreadSize;
	}

	private volatile String _topicThreadSize = "";
	private volatile int topicThreadSize = 0;
	public static final String env_getConsumerGroupTopicThreadSize_key = "mq.consumergroup.topic.threadsize";
	private final String env_getConsumerGroupTopicThreadSize_defaultValue = "5";
	private final String env_getConsumerGroupTopicThreadSize_des = "topic的处理线程数,默认值为5";

	// topic的处理线程数,默认值为5
	public int getConsumerGroupTopicThreadSize() {
		try {
			if (!_topicThreadSize.equals(env.getProperty(env_getConsumerGroupTopicThreadSize_key,
					env_getConsumerGroupTopicThreadSize_defaultValue))) {
				_topicThreadSize = env.getProperty(env_getConsumerGroupTopicThreadSize_key,
						env_getConsumerGroupTopicThreadSize_defaultValue);
				topicThreadSize = Integer.parseInt(env.getProperty(env_getConsumerGroupTopicThreadSize_key,
						env_getConsumerGroupTopicThreadSize_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getConsumerGroupTopicThreadSize_SoaConfig_error", e);
		}
		return topicThreadSize;
	}

	private volatile String _maxTopicThreadSize = "";
	private volatile int maxTopicThreadSize = 0;
	public static final String env_getConsumerGroupTopicMaxThreadSize_key = "mq.consumer.group.topic.max.threadsize";
	private final String env_getConsumerGroupTopicMaxThreadSize_defaultValue = "50";
	private final String env_getConsumerGroupTopicMaxThreadSize_des = "topic的最大处理线程数,最大值为50";

	// topic的最大处理线程数,最大值为50
	public int getConsumerGroupTopicMaxThreadSize() {
		try {
			if (!_maxTopicThreadSize.equals(env.getProperty(env_getConsumerGroupTopicMaxThreadSize_key,
					env_getConsumerGroupTopicMaxThreadSize_defaultValue))) {
				_maxTopicThreadSize = env.getProperty(env_getConsumerGroupTopicMaxThreadSize_key,
						env_getConsumerGroupTopicMaxThreadSize_defaultValue);
				maxTopicThreadSize = Integer.parseInt(env.getProperty(env_getConsumerGroupTopicMaxThreadSize_key,
						env_getConsumerGroupTopicMaxThreadSize_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getConsumerGroupTopicMaxThreadSize_SoaConfig_error", e);
		}
		return maxTopicThreadSize;
	}

	private volatile String _isDbStatusLog = "";
	private volatile boolean isDbStatusLog = false;
	private final String env_isDbStatusLog_key = "mq.consumer.isDbStatusLog";
	private final String env_isDbStatusLog_defaultValue = "false";
	private final String env_isDbStatusLog_des = "是否开启数据状态日志";

	// 是否开启数据状态日志
	public boolean isDbStatusLog() {
		try {
			if (!_isDbStatusLog.equals(env.getProperty(env_isDbStatusLog_key, env_isDbStatusLog_defaultValue))) {
				_isDbStatusLog = env.getProperty(env_isDbStatusLog_key, env_isDbStatusLog_defaultValue);
				isDbStatusLog = Boolean
						.parseBoolean(env.getProperty(env_isDbStatusLog_key, env_isDbStatusLog_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			isDbStatusLog = false;
			log.error("isDbStatusLog_SoaConfig_error", e);
		}
		return isDbStatusLog;
	}

	private volatile String _topicRetryCount = "";
	private volatile int topicRetryCount = 0;
	public static final String env_getConsumerGroupTopicRetryCount_key = "mq.consumer.group.topic.retrycount";
	private final String env_getConsumerGroupTopicRetryCount_defaultValue = "3";
	private final String env_getConsumerGroupTopicRetryCount_des = "topic的重试次数,默认为3";

	// topic的重试次数,默认为1
	public int getConsumerGroupTopicRetryCount() {
		try {
			if (!_topicRetryCount.equals(env.getProperty(env_getConsumerGroupTopicRetryCount_key,
					env_getConsumerGroupTopicRetryCount_defaultValue))) {
				_topicRetryCount = env.getProperty(env_getConsumerGroupTopicRetryCount_key,
						env_getConsumerGroupTopicRetryCount_defaultValue);
				topicRetryCount = Integer.parseInt(env.getProperty(env_getConsumerGroupTopicRetryCount_key,
						env_getConsumerGroupTopicRetryCount_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getConsumerGroupTopicRetryCount_SoaConfig_error", e);
		}
		return topicRetryCount;
	}

	private volatile String _topicMaxRetryCount = "";
	private volatile int topicMaxRetryCount = 0;
	public static final String env_getConsumerGroupTopicMaxRetryCount_key = "mq.consumer.group.topic.max.retrycount";
	private final String env_getConsumerGroupTopicMaxRetryCount_defaultValue = "10";
	private final String env_getConsumerGroupTopicMaxRetryCount_des = "topic的最大重试次数,最大值为10";

	// topic的最大重试次数,最大值为10
	public int getConsumerGroupTopicMaxRetryCount() {
		try {
			if (!_topicMaxRetryCount.equals(env.getProperty(env_getConsumerGroupTopicMaxRetryCount_key,
					env_getConsumerGroupTopicMaxRetryCount_defaultValue))) {
				_topicMaxRetryCount = env.getProperty(env_getConsumerGroupTopicMaxRetryCount_key,
						env_getConsumerGroupTopicMaxRetryCount_defaultValue);
				topicMaxRetryCount = Integer.parseInt(env.getProperty(env_getConsumerGroupTopicMaxRetryCount_key,
						env_getConsumerGroupTopicMaxRetryCount_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getConsumerGroupTopicMaxRetryCount_SoaConfig_error", e);
		}
		return topicMaxRetryCount;
	}

	private volatile String _topicLag = "";
	private volatile int topicLag = 0;
	public static final String env_getConsumerGroupTopicLag_key = "mq.consumer.group.topic.lag";
	private final String env_getConsumerGroupTopicLag_defaultValue = "10000";
	private final String env_getConsumerGroupTopicLag_des = "topic的告警阈值，默认为10000";

	// topic的告警阈值，默认为10000
	public int getConsumerGroupTopicLag() {
		try {
			if (!_topicLag.equals(
					env.getProperty(env_getConsumerGroupTopicLag_key, env_getConsumerGroupTopicLag_defaultValue))) {
				_topicLag = env.getProperty(env_getConsumerGroupTopicLag_key,
						env_getConsumerGroupTopicLag_defaultValue);
				topicLag = Integer.parseInt(
						env.getProperty(env_getConsumerGroupTopicLag_key, env_getConsumerGroupTopicLag_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getConsumerGroupTopicLag_SoaConfig_error", e);
		}
		return topicLag;
	}

	private volatile String _topicMaxLag = "";
	private volatile int topicMaxLag = 0;
	public static final String env_getConsumerGroupTopicMaxLag_key = "mq.consumer.group.topic.maxlag";
	private final String env_getConsumerGroupTopicMaxLag_defaultValue = "100000";
	private final String env_getConsumerGroupTopicMaxLag_des = "topic的最大告警阈值，最大为100000";

	// topic的最大告警阈值，最大为100000
	public int getConsumerGroupTopicMaxLag() {
		try {
			if (!_topicMaxLag.equals(env.getProperty(env_getConsumerGroupTopicMaxLag_key,
					env_getConsumerGroupTopicMaxLag_defaultValue))) {
				_topicMaxLag = env.getProperty(env_getConsumerGroupTopicMaxLag_key,
						env_getConsumerGroupTopicMaxLag_defaultValue);
				topicMaxLag = Integer.parseInt(env.getProperty(env_getConsumerGroupTopicMaxLag_key,
						env_getConsumerGroupTopicMaxLag_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getConsumerGroupTopicMaxLag_SoaConfig_error", e);
		}
		return topicMaxLag;
	}

	private volatile String _topicDelayProcessTime = "";
	private volatile int topicDelayProcessTime = 0;
	public static final String env_getDelayProcessTime_key = "mq.consumer.group.topic.delay.processtime";
	private final String env_getDelayProcessTime_defaultValue = "0";
	private final String env_getDelayProcessTime_des = "topic的默认延迟处理时间,默认为0";

	// topic的默认延迟处理时间,默认为0
	public int getDelayProcessTime() {
		try {
			if (!_topicDelayProcessTime
					.equals(env.getProperty(env_getDelayProcessTime_key, env_getDelayProcessTime_defaultValue))) {
				_topicDelayProcessTime = env.getProperty(env_getDelayProcessTime_key,
						env_getDelayProcessTime_defaultValue);
				topicDelayProcessTime = Integer
						.parseInt(env.getProperty(env_getDelayProcessTime_key, env_getDelayProcessTime_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getDelayProcessTime_SoaConfig_error", e);
		}
		return topicDelayProcessTime;
	}

	private volatile String _topicMaxDelayProcessTime = "";
	private volatile int topicMaxDelayProcessTime = 0;
	public static final String env_getMaxDelayProcessTime_key = "mq.consumer.group.topic.max.delay.processtime";
	private final String env_getMaxDelayProcessTime_defaultValue = "43200000";
	private final String env_getMaxDelayProcessTime_des = "topic的最大延迟处理时间,最大值为0.5天，单位是毫秒。";

	// topic的最大延迟处理时间,最大值为0.5天，单位是毫秒。
	public int getMaxDelayProcessTime() {
		try {
			if (!_topicMaxDelayProcessTime
					.equals(env.getProperty(env_getMaxDelayProcessTime_key, env_getMaxDelayProcessTime_defaultValue))) {
				_topicMaxDelayProcessTime = env.getProperty(env_getMaxDelayProcessTime_key,
						env_getMaxDelayProcessTime_defaultValue);
				topicMaxDelayProcessTime = Integer.parseInt(
						env.getProperty(env_getMaxDelayProcessTime_key, env_getMaxDelayProcessTime_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMaxDelayProcessTime_SoaConfig_error", e);
		}
		return topicMaxDelayProcessTime;
	}

	private volatile String _topicMaxDelayPullTime = "";
	private volatile int topicMaxDelayPullTime = 0;
	public static final String env_getMaxDelayPullTime_key = "mq.consumer.group.topic.max.delay.pulltime";
	private final String env_getMaxDelayPullTime_defaultValue = "5";
	private final String env_getMaxDelayPullTime_des = "consumerGroupTopic的最大拉取等待时间,最大值和默认值都为5秒，单位是毫秒。";

	// consumerGroupTopic的最大拉取等待时间,最大值和默认值都为5秒，单位是秒。
	public int getMaxDelayPullTime() {
		try {
			if (!_topicMaxDelayPullTime
					.equals(env.getProperty(env_getMaxDelayPullTime_key, env_getMaxDelayPullTime_defaultValue))) {
				_topicMaxDelayPullTime = env.getProperty(env_getMaxDelayPullTime_key,
						env_getMaxDelayPullTime_defaultValue);
				topicMaxDelayPullTime = Integer
						.parseInt(env.getProperty(env_getMaxDelayPullTime_key, env_getMaxDelayPullTime_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMaxDelayPullTime_SoaConfig_error", e);
		}
		return topicMaxDelayPullTime;
	}

	private volatile String _topicgetMinDelayPullTime = "";
	private volatile int topicgetMinDelayPullTime = 0;
	public static final String env_getMinDelayPullTime_key = "mq.consumer.group.topic.min.delay.pulltime";
	private final String env_getMinDelayPullTime_defaultValue = "1";
	private final String env_getMinDelayPullTime_des = "consumerGroupTopic的最小拉取等待时间,最小值为1秒，单位是秒。";

	// consumerGroupTopic的最大拉取等待时间,最大值和默认值都为5秒，单位是毫秒。
	public int getMinDelayPullTime() {
		try {
			if (!_topicgetMinDelayPullTime
					.equals(env.getProperty(env_getMinDelayPullTime_key, env_getMinDelayPullTime_defaultValue))) {
				_topicgetMinDelayPullTime = env.getProperty(env_getMinDelayPullTime_key,
						env_getMinDelayPullTime_defaultValue);
				topicgetMinDelayPullTime = Integer
						.parseInt(env.getProperty(env_getMinDelayPullTime_key, env_getMinDelayPullTime_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMinDelayPullTime_SoaConfig_error", e);
		}
		return topicgetMinDelayPullTime;
	}

	private volatile String _pullBatchSize = "";
	private volatile int pullBatchSize = 0;
	public static final String env_getPullBatchSize_key = "mq.consumer.group.pull.batchsize";
	private final String env_getPullBatchSize_defaultValue = "50";
	private final String env_getPullBatchSize_des = "topic的批量拉取条数,默认值为50";

	// topic的批量拉取条数,默认值为50
	public int getPullBatchSize() {
		try {
			if (!_pullBatchSize.equals(env.getProperty(env_getPullBatchSize_key, env_getPullBatchSize_defaultValue))) {
				_pullBatchSize = env.getProperty(env_getPullBatchSize_key, env_getPullBatchSize_defaultValue);
				pullBatchSize = Integer
						.parseInt(env.getProperty(env_getPullBatchSize_key, env_getPullBatchSize_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getPullBatchSize_SoaConfig_error", e);
		}
		return pullBatchSize;
	}

	private volatile String _consumerBatchSize = "";
	private volatile int consumerBatchSize = 0;
	public static final String env_getConsumerBatchSize_key = "mq.consumer.batch.size";
	private final String env_getConsumerBatchSize_defaultValue = "1";
	private final String env_getConsumerBatchSize_des = "topic的批量消费条数,默认值为1";

	// topic的批量消费条数,默认值为1
	public int getConsumerBatchSize() {
		try {
			if (!_consumerBatchSize
					.equals(env.getProperty(env_getConsumerBatchSize_key, env_getConsumerBatchSize_defaultValue))) {
				_consumerBatchSize = env.getProperty(env_getConsumerBatchSize_key,
						env_getConsumerBatchSize_defaultValue);
				consumerBatchSize = Integer
						.parseInt(env.getProperty(env_getConsumerBatchSize_key, env_getConsumerBatchSize_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getConsumerBatchSize_SoaConfig_error", e);
		}
		return consumerBatchSize;
	}

	private volatile String _getMaxConsumerBatchSize = "";
	private volatile int getMaxConsumerBatchSize = 0;
	public static final String env_getMaxConsumerBatchSize_key = "mq.max.consumer.batch.size";
	private final String env_getMaxConsumerBatchSize_defaultValue = "50";
	private final String env_getMaxConsumerBatchSize_des = "topic的最大批量消费条数,默认值为50";

	// topic的最大批量消费条数,默认值为50
	public int getMaxConsumerBatchSize() {
		try {
			if (!_getMaxConsumerBatchSize.equals(
					env.getProperty(env_getMaxConsumerBatchSize_key, env_getMaxConsumerBatchSize_defaultValue))) {
				_getMaxConsumerBatchSize = env.getProperty(env_getMaxConsumerBatchSize_key,
						env_getMaxConsumerBatchSize_defaultValue);
				getMaxConsumerBatchSize = Integer.parseInt(
						env.getProperty(env_getMaxConsumerBatchSize_key, env_getMaxConsumerBatchSize_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getgetMaxConsumerBatchSize_SoaConfig_error", e);
		}
		return getMaxConsumerBatchSize;
	}

	private volatile String _maxPullBatchSize = "";
	private volatile int maxPullBatchSize = 0;
	public static final String env_getMaxPullBatchSize_key = "mq.consumer.group.max.pull.batchsize";
	private final String env_getMaxPullBatchSize_defaultValue = "200";
	private final String env_getMaxPullBatchSize_des = "topic的批量拉取条数,最大值为200";

	// topic的批量拉取条数,最大值为200
	public int getMaxPullBatchSize() {
		try {
			if (!_maxPullBatchSize
					.equals(env.getProperty(env_getMaxPullBatchSize_key, env_getMaxPullBatchSize_defaultValue))) {
				_maxPullBatchSize = env.getProperty(env_getMaxPullBatchSize_key, env_getMaxPullBatchSize_defaultValue);
				maxPullBatchSize = Integer
						.parseInt(env.getProperty(env_getMaxPullBatchSize_key, env_getMaxPullBatchSize_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMaxPullBatchSize_SoaConfig_error", e);
		}
		return maxPullBatchSize;
	}

	private volatile String _minPullBatchSize = "";
	private volatile int minPullBatchSize = 0;
	public static final String env_getMinPullBatchSize_key = "mq.consumer.group.min.pull.batchsize";
	private final String env_getMinPullBatchSize_defaultValue = "50";
	private final String env_getMinPullBatchSize_des = "topic的批量拉取条数,最小值为50";

	// topic的批量拉取条数,最小值为50
	public int getMinPullBatchSize() {
		try {
			if (!_minPullBatchSize
					.equals(env.getProperty(env_getMinPullBatchSize_key, env_getMinPullBatchSize_defaultValue))) {
				_minPullBatchSize = env.getProperty(env_getMinPullBatchSize_key, env_getMinPullBatchSize_defaultValue);
				minPullBatchSize = Integer
						.parseInt(env.getProperty(env_getMinPullBatchSize_key, env_getMinPullBatchSize_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMinPullBatchSize_SoaConfig_error", e);
		}
		return minPullBatchSize;
	}

	private volatile String _expectDayCount = "";
	private volatile int expectDayCount = 0;
	public static final String env_getExpectDayCount_key = "fail.topic.expect.day.count";
	private final String env_getExpectDayCount_defaultValue = "200";
	private final String env_getExpectDayCount_des = "失败topic的每天预计消息量";

	// 失败topic的每天预计消息量
	public int getExpectDayCount() {
		try {
			if (!_expectDayCount
					.equals(env.getProperty(env_getExpectDayCount_key, env_getExpectDayCount_defaultValue))) {
				_expectDayCount = env.getProperty(env_getExpectDayCount_key, env_getExpectDayCount_defaultValue);
				expectDayCount = Integer
						.parseInt(env.getProperty(env_getExpectDayCount_key, env_getExpectDayCount_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getExpectDayCount_SoaConfig_error", e);
		}
		return expectDayCount;
	}

	private volatile String _adminUserIds = "";
	private volatile List<String> adminUserIds = new ArrayList<>();
	private String env_getAdminUserIds_key = "mq.admin.user.ids";
	private String env_getAdminUserIds_defaultValue = "[]";
	private final String env_getAdminUserIds_des = "管理员userid列表";

	// 管理员userid列表
	public List<String> getAdminUserIds() {
		try {
			if (!_adminUserIds.equals(env.getProperty(env_getAdminUserIds_key, env_getAdminUserIds_defaultValue))) {
				_adminUserIds = env.getProperty(env_getAdminUserIds_key, env_getAdminUserIds_defaultValue);
				adminUserIds = JsonUtil.parseJson(
						env.getProperty(env_getAdminUserIds_key, env_getAdminUserIds_defaultValue),
						new TypeReference<List<String>>() {
						});
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getAdminUserIds_SoaConfig_error", e);
		}
		return adminUserIds;
	}

	private volatile String _bizType = "";
	private volatile List<String> bizType = new ArrayList<>();
	private String env_getBizTypes_key = "mq.biz.types";
	private String env_getBizTypes_defaultValue = "[]";
	private final String env_getBizTypes_des = "业务线类型";

	// 业务线类型
	public List<String> getBizTypes() {
		try {
			if (!_bizType.equals(env.getProperty(env_getBizTypes_key, env_getBizTypes_defaultValue))) {
				_bizType = env.getProperty(env_getBizTypes_key, env_getBizTypes_defaultValue);
				bizType = JsonUtil.parseJson(env.getProperty(env_getBizTypes_key, env_getBizTypes_defaultValue),
						new TypeReference<List<String>>() {
						});
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getBizTypes_SoaConfig_error", e);
		}
		return bizType;
	}

	private volatile String _maxTableMessage = "";
	private volatile int maxTableMessage = 0;
	private String env_getMaxTableMessage_key = "mq.max.table.message";
	private String env_getMaxTableMessage_defaultValue = "7000000";
	private final String env_getMaxTableMessage_des = "单表最大消息量";

	// 单表最大消息量
	public int getMaxTableMessage() {
		try {
			if (!_maxTableMessage
					.equals(env.getProperty(env_getMaxTableMessage_key, env_getMaxTableMessage_defaultValue))) {
				_maxTableMessage = env.getProperty(env_getMaxTableMessage_key, env_getMaxTableMessage_defaultValue);
				maxTableMessage = Integer
						.parseInt(env.getProperty(env_getMaxTableMessage_key, env_getMaxTableMessage_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMaxTableMessage_SoaConfig_error", e);
		}
		return maxTableMessage;
	}

	private volatile String _maxTableMessageSwitch = "";
	private String env_getMaxTableMessageSwitch_key = "mq.max.table.message.switch";
	private String env_getMaxTableMessageSwitch_defaultValue = "on";
	private volatile boolean maxTableMessageSwitch = false;
	private final String env_getMaxTableMessageSwitch_des = "是否开启扩容时最大值校验，默认开启，如果没有达到最大值不允许扩容，超级管理员可以扩容";

	// 是否开启扩容时最大值校验，默认开启，如果没有达到最大值不允许扩容，超级管理员可以扩容
	public boolean getMaxTableMessageSwitch() {
		try {
			if (!_maxTableMessageSwitch.equals(
					env.getProperty(env_getMaxTableMessageSwitch_key, env_getMaxTableMessageSwitch_defaultValue))) {
				_maxTableMessageSwitch = env.getProperty(env_getMaxTableMessageSwitch_key,
						env_getMaxTableMessageSwitch_defaultValue);
				maxTableMessageSwitch = _maxTableMessageSwitch.equals("on");
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMaxTableMessageSwitch_SoaConfig_error", e);
		}
		return maxTableMessageSwitch;
	}

	private volatile String _maxQueuePerTopic = "";
	private volatile int maxQueuePerTopic = 0;
	private String env_getMaxQueuePerTopic_key = "mq.max.queue.per.topic";
	private String env_getMaxQueuePerTopic_defaultValue = "3";
	private final String env_getMaxQueuePerTopic_des = "普通用户每个topic分配的队列的数量上限是3";

	// 普通用户每个topic分配的队列的数量上限是3
	public int getMaxQueuePerTopic() {
		try {
			if (!_maxQueuePerTopic
					.equals(env.getProperty(env_getMaxQueuePerTopic_key, env_getMaxQueuePerTopic_defaultValue))) {
				_maxQueuePerTopic = env.getProperty(env_getMaxQueuePerTopic_key, env_getMaxQueuePerTopic_defaultValue);
				maxQueuePerTopic = Integer
						.parseInt(env.getProperty(env_getMaxQueuePerTopic_key, env_getMaxQueuePerTopic_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMaxQueuePerTopic_SoaConfig_error", e);
		}
		return maxQueuePerTopic;
	}

	private volatile String _expectDayCountOptions = "";
	private volatile List<Integer> expectDayCountOptions = new ArrayList<>();
	private String env_getExpectDayCountOptions_key = "mq.expect.day.count.select.option";
	private String env_getExpectDayCountOptions_defaultValue = "[200,300,400,500]";
	private final String env_getExpectDayCountOptions_des = "默认每个topic每天数量下拉列表";

	// 默认每个topic每天数量下拉列表
	public List<Integer> getExpectDayCountOptions() {
		try {
			if (!_expectDayCountOptions.equals(
					env.getProperty(env_getExpectDayCountOptions_key, env_getExpectDayCountOptions_defaultValue))) {
				_expectDayCountOptions = env.getProperty(env_getExpectDayCountOptions_key,
						env_getExpectDayCountOptions_defaultValue);
				expectDayCountOptions = JsonUtil.parseJson(
						env.getProperty(env_getExpectDayCountOptions_key, env_getExpectDayCountOptions_defaultValue),
						new TypeReference<List<Integer>>() {
						});
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMaxQueuePerTopic_SoaConfig_error", e);
		}
		return expectDayCountOptions;
	}

	private volatile String _logSaveDayNum = "";
	private volatile int logSaveDayNum = 0;
	private String env_getLogSaveDayNum_key = "mq.log.save.day.num";
	private String env_getLogSaveDayNum_defaultValue = "30";
	private final String env_getLogSaveDayNum_des = "审计日志保留天数";

	// 审计日志保留天数
	public int getLogSaveDayNum() {
		try {
			if (!_logSaveDayNum.equals(env.getProperty(env_getLogSaveDayNum_key, env_getLogSaveDayNum_defaultValue))) {
				_logSaveDayNum = env.getProperty(env_getLogSaveDayNum_key, env_getLogSaveDayNum_defaultValue);
				logSaveDayNum = Integer
						.parseInt(env.getProperty(env_getLogSaveDayNum_key, env_getLogSaveDayNum_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getLogSaveDayNum_SoaConfig_error", e);
		}
		return logSaveDayNum;
	}

	// 是否开启发送失败消息告警
	private String env_enableSendFailTopicMail_key = "mq.topic.sendFailMail";
	private String env_enableSendFailTopicMail_defaultValue = "1";
	private final String env_enableSendFailTopicMail_des = "是否开启发送失败消息告警";

	public boolean enableSendFailTopicMail(String topic) {
		if (env.getProperty(env_enableSendFailTopicMail_key, env_enableSendFailTopicMail_defaultValue).equals("1")) {
			return env.getProperty("mq.topic." + topic + ".sendFailMail", "1").equals("1");
		}
		return false;
	}

	private volatile String _msgSaveDayNumOptions = "";
	private volatile List<Integer> msgSaveDayNumOptions = new ArrayList<>();
	private String env_getMsgSaveDayNum_key = "mq.msg.save.day.num.options";
	private String env_getMsgSaveDayNum_defaultValue = "[7,6,5,4,3,2,1]";
	private final String env_getMsgSaveDayNum_des = "消息保留默认天数";

	// 消息保留默认天数
	public List<Integer> getMsgSaveDayNum() {
		try {
			if (!_msgSaveDayNumOptions
					.equals(env.getProperty(env_getMsgSaveDayNum_key, env_getMsgSaveDayNum_defaultValue))) {
				_msgSaveDayNumOptions = env.getProperty(env_getMsgSaveDayNum_key, env_getMsgSaveDayNum_defaultValue);
				msgSaveDayNumOptions = JsonUtil.parseJson(_msgSaveDayNumOptions, new TypeReference<List<Integer>>() {
				});
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getMsgSaveDayNum_SoaConfig_error", e);
		}
		return msgSaveDayNumOptions;
	}

	private volatile String _adminMsgSaveDayNumOptions = "";
	private volatile List<Integer> adminMsgSaveDayNumOptions = new ArrayList<>();
	private String env_getAdminMsgSaveDayNum_key = "mq.admin.msg.save.day.num.options";
	private String env_getAdminMsgSaveDayNum_defaultValue = "[13,12,11,10,9,8,7,6,5,4,3,2,1]";
	private final String env_getAdminMsgSaveDayNum_des = "超级管理员，操作消息的配置天数";

	// 超级管理员专用的 消息保留默认天数
	public List<Integer> getAdminMsgSaveDayNum() {
		try {
			if (!_adminMsgSaveDayNumOptions
					.equals(env.getProperty(env_getAdminMsgSaveDayNum_key, env_getAdminMsgSaveDayNum_defaultValue))) {
				_adminMsgSaveDayNumOptions = env.getProperty(env_getAdminMsgSaveDayNum_key,
						env_getAdminMsgSaveDayNum_defaultValue);
				adminMsgSaveDayNumOptions = JsonUtil.parseJson(_adminMsgSaveDayNumOptions,
						new TypeReference<List<Integer>>() {
						});
				onChange();
			}
		} catch (Exception e) {
			onChange();
			log.error("getAdminMsgSaveDayNum_SoaConfig_error", e);
		}
		return adminMsgSaveDayNumOptions;
	}

	private volatile String _getCheckExpiredTime = "";
	private volatile int getCheckExpiredTime = 0;
	private String env_getCheckExpiredTime_key = "mq.db.checkTime";
	private String env_getCheckExpiredTime_defaultValue = "120";
	private final String env_getCheckExpiredTime_des = "冗余字段检查间隔时间，单位秒";

	// 单位是秒
	public int getCheckExpiredTime() {
		try {
			if (!_getCheckExpiredTime
					.equals(env.getProperty(env_getCheckExpiredTime_key, env_getCheckExpiredTime_defaultValue))) {
				_getCheckExpiredTime = env.getProperty(env_getCheckExpiredTime_key,
						env_getCheckExpiredTime_defaultValue);
				getCheckExpiredTime = Integer
						.parseInt(env.getProperty(env_getCheckExpiredTime_key, env_getCheckExpiredTime_defaultValue));
				onChange();
			}
		} catch (Exception e) {
			// getCheckExpiredTime = getCheckExpiredTime() * 2 + 3;
			onChange();
			log.error("getExpiredTime_SoaConfig_error", e);
		}
		return getCheckExpiredTime;
	}

	private volatile String _heartBeatThreadSize = "5";
	private volatile int heartBeatThreadSize = 5;

	private final String env_getHeartBeatThreadSize_key = "mq.heartbeat.thread.size";
	private final String env_getHeartBeatThreadSize_defaultValue = "3";
	private final String env_getHeartBeatThreadSize_des = "批量执行心跳线程数";

	public int getHeartBeatThreadSize() {
		try {
			if (!_heartBeatThreadSize
					.equals(env.getProperty(env_getHeartBeatThreadSize_key, env_getHeartBeatThreadSize_defaultValue))) {
				_heartBeatThreadSize = env.getProperty(env_getHeartBeatThreadSize_key,
						env_getHeartBeatThreadSize_defaultValue);
				heartBeatThreadSize = Integer.parseInt(_heartBeatThreadSize);
				onChange();
			}
		} catch (Exception e) {
			heartBeatThreadSize = 3;
			onChange();
		}
		return heartBeatThreadSize;
	}

	private volatile String _commitThreadSize = "5";
	private volatile int commitThreadSize = 5;

	private final String env_getCommitThreadSize_key = "mq.commit.thread.size";
	private final String env_getCommitThreadSize_defaultValue = "5";
	private final String env_getCommitThreadSize_des = "批量执行提交偏移线程数";

	public int getCommitThreadSize() {
		try {
			if (!_commitThreadSize
					.equals(env.getProperty(env_getCommitThreadSize_key, env_getCommitThreadSize_defaultValue))) {
				_commitThreadSize = env.getProperty(env_getCommitThreadSize_key, env_getCommitThreadSize_defaultValue);
				commitThreadSize = Integer.parseInt(_commitThreadSize);
				onChange();
			}
		} catch (Exception e) {
			commitThreadSize = 5;
			onChange();
		}
		return commitThreadSize;
	}

	private final String env_getMqLdapUser_key = "mq.ldap.user";
	private final String env_getMqLdapUser_defaultValue = "";
	private final String env_getMqLdapUser_des = "访问ldap的用户名";

	public String getMqLdapUser() {
		return env.getProperty(env_getMqLdapUser_key, env_getMqLdapUser_defaultValue);
	}

	private final String env_getMqLdapPass_key = "mq.ldap.pass";
	private final String env_getMqLdapPass_defaultValue = "";
	private final String env_getMqLdapPass_des = "访问ldap的密码";

	public String getMqLdapPass() {
		return env.getProperty(env_getMqLdapPass_key, env_getMqLdapPass_defaultValue);
	}

	private final String env_getMqAdminUser_key = "mq.adminUser";
	private final String env_getMqAdminUser_defaultValue = "mqadmin";
	private final String env_getMqAdminUser_des = "默认管理员账号";

	public String getMqAdminUser() {
		return env.getProperty(env_getMqAdminUser_key, env_getMqAdminUser_defaultValue);
	}

	private final String env_getMqAdminPass_key = "mq.adminPass";
	private final String env_getMqAdminPass_defaultValue = "mqadmin";
	private final String env_getMqAdminPass_des = "默认管理员密码";

	public String getMqAdminPass() {
		return env.getProperty(env_getMqAdminPass_key, env_getMqAdminPass_defaultValue);
	}

	private final String env_getTopicFlag_key = "mq.topic.%s.flag";
	private final String env_getTopicFlag_defaultValue = "1";
	private final String env_getTopicFlag_des = "是否允许topic发送,如果禁止了表示彻底不接收某个topic的消息";

	public String getTopicFlag(String topic) {
		return env.getProperty(String.format(env_getTopicFlag_key, topic), env_getTopicFlag_defaultValue);
	}

	private final String env_getTopicHostMax_key = "mq.topic.host.max";
	private final String env_getTopicHostMax_defaultValue = "0";
	private final String env_getTopicHostMax_des = "设置topic的最大并发数，0表示不限制，最小500，一旦开启超过最大并发数，需要限速";

	private volatile String _getTopicHostMax = "500";
	private volatile int getTopicHostMax = 500;

	public int getTopicHostMax() {
		try {
			if (!_getTopicHostMax.equals(env.getProperty(env_getTopicHostMax_key, env_getTopicHostMax_defaultValue))) {
				_getTopicHostMax = env.getProperty(env_getTopicHostMax_key, env_getTopicHostMax_defaultValue);
				getTopicHostMax = Integer.parseInt(_getTopicHostMax);
				if (getTopicHostMax > 0 && getTopicHostMax < getTopicHostMin()) {
					getTopicHostMax = getTopicHostMin();
				}
				onChange();
			}
		} catch (Exception e) {
			getTopicHostMax = 0;
			if (getTopicHostMax > 0 && getTopicHostMax < getTopicHostMin()) {
				getTopicHostMax = getTopicHostMin();
			}
			onChange();
		}

		return getTopicHostMax;
	}

	private final String env_getTopicPerMax_key = "mq.topic.%s.host.max";
	private final String env_getTopicPerMax_defaultValue = "0";
	private final String env_getTopicPerMax_des = "设置单个topic的最大并发数，超过会限速";

	private volatile String _getTopicPerMax = "500";
	private volatile int getTopicPerMax = 500;

	public int getTopicPerMax(String topic) {
		try {
			if (!_getTopicPerMax.equals(
					env.getProperty(String.format(env_getTopicPerMax_key, topic), env_getTopicPerMax_defaultValue))) {
				_getTopicPerMax = env.getProperty(String.format(env_getTopicPerMax_key, topic),
						env_getTopicPerMax_defaultValue);
				getTopicPerMax = Integer.parseInt(_getTopicPerMax);
				onChange();
			}
		} catch (Exception e) {
			getTopicPerMax = 0;
			onChange();
		}
		return getTopicPerMax;
	}

	private final String env_getTopicHostMin_key = "mq.topic.host.total";
	private final String env_getTopicHostMin_defaultValue = "0";
	private final String env_getTopicHostMin_des = "如果设置了topic的最大并发数，则此值需要大于最小值，防止出错";

	private volatile String _getTopicHostMin = "500";
	private volatile int getTopicHostMin = 500;

	public int getTopicHostMin() {
		try {
			if (!_getTopicHostMin.equals(env.getProperty(env_getTopicHostMin_key, env_getTopicHostMin_defaultValue))) {
				_getTopicHostMin = env.getProperty(env_getTopicHostMin_key, env_getTopicHostMin_defaultValue);
				getTopicHostMin = Integer.parseInt(_getTopicHostMin);
				onChange();
			}
		} catch (Exception e) {
			getTopicHostMin = 500;
			onChange();
		}
		return getTopicHostMin;
	}

	private final String env_getBrokerMetaMode_key = "mq.broker.metaMode";
	private final String env_getBrokerMetaMode_defaultValue = "0";
	private final String env_getBrokerMetaMode_des = "1表示强制meta模式（meta模式表示由客户端进行lb负载均衡），-1表示类似强制nginx负载均衡，0表示由客户端决定";

	private volatile String _getBrokerMetaMode = "0";
	private volatile int getBrokerMetaMode = 0;

	public int getBrokerMetaMode() {
		try {
			if (!_getBrokerMetaMode
					.equals(env.getProperty(env_getBrokerMetaMode_key, env_getBrokerMetaMode_defaultValue))) {
				_getBrokerMetaMode = env.getProperty(env_getBrokerMetaMode_key, env_getBrokerMetaMode_defaultValue);
				getBrokerMetaMode = Integer.parseInt(_getBrokerMetaMode);
				if (getBrokerMetaMode > 1 && getBrokerMetaMode < -1) {
					getBrokerMetaMode = 0;
				}
				onChange();
			}
		} catch (Exception e) {
			getBrokerMetaMode = 0;
			onChange();
		}
		return getBrokerMetaMode;
	}

	private final String env_getServerHeartbeat_key = "mq.broker.server.heartbeat";
	private final String env_getServerHeartbeat_defaultValue = "0";
	private final String env_getServerHeartbeat_des = "broker server 报心跳的时间";

	private volatile String _getServerHeartbeat = "10";
	private volatile int getServerHeartbeat = 10;

	public int getServerHeartbeat() {
		try {
			if (!_getServerHeartbeat
					.equals(env.getProperty(env_getServerHeartbeat_key, env_getServerHeartbeat_defaultValue))) {
				_getServerHeartbeat = env.getProperty(env_getServerHeartbeat_key, env_getServerHeartbeat_defaultValue);
				getServerHeartbeat = Integer.parseInt(_getServerHeartbeat);
				if (getServerHeartbeat < 10) {
					getServerHeartbeat = 10;
				}
				onChange();
			}
		} catch (Exception e) {
			getServerHeartbeat = 10;
			onChange();
		}
		return getServerHeartbeat;
	}

	private final String env_isEnbaleAuditLogClean_key = "mq.audit.log.clean";
	private final String env_isEnbaleAuditLogClean_defaultValue = "1";
	private final String env_isEnbaleAuditLogClean_des = "是否开启AuditLogClean,0表示关闭，1表示开启";

	public Boolean isEnbaleAuditLogClean() {
		return "1".equals(env.getProperty(env_isEnbaleAuditLogClean_key, env_isEnbaleAuditLogClean_defaultValue));
	}

	private final String env_isEnbaleNotifyMessageClean_key = "mq.notify.message.clean";
	private final String env_isEnbaleNotifyMessageClean_defaultValue = "1";
	private final String env_isEnbaleNotifyMessageClean_des = "是否开启NotifyMessageClean,0表示关闭，1表示开启";

	public Boolean isEnbaleNotifyMessageClean() {
		return "1".equals(
				env.getProperty(env_isEnbaleNotifyMessageClean_key, env_isEnbaleNotifyMessageClean_defaultValue));
	}

	private final String env_isEnbaleMessageClean_key = "mq.message.clean";
	private final String env_isEnbaleMessageClean_defaultValue = "1";
	private final String env_isEnbaleMessageClean_des = "是否开启MessageClean,0表示关闭，1表示开启";

	public Boolean isEnbaleMessageClean() {
		return "1".equals(env.getProperty(env_isEnbaleMessageClean_key, env_isEnbaleMessageClean_defaultValue));
	}

	private final String env_getClearTopics_key = "mq.clear.topics";
	private final String env_getClearTopics_defaultValue = "[]";
	private final String env_getClearTopics_des = "获取定时清理topic列表";

	private volatile String _getClearTopics = "[]";
	private volatile List<String> getClearTopics = new ArrayList<>();

	public List<String> getClearTopics() {
		try {
			if (!_getClearTopics.equals(env.getProperty(env_getClearTopics_key, env_getClearTopics_defaultValue))) {
				_getClearTopics = env.getProperty(env_getClearTopics_key, env_getClearTopics_defaultValue);
				getClearTopics = JsonUtil.parseJson(_getClearTopics, new TypeReference<List<String>>() {
				});
				onChange();
			}
		} catch (Exception e) {
			getClearTopics = new ArrayList<>();
			onChange();
		}
		return getClearTopics;
	}

	private final String env_isEnbaleNoSubsribe_key = "mq.consumer.nosubscribe";
	private final String env_isEnbaleNoSubsribe_defaultValue = "0";
	private final String env_isEnbaleNoSubsribe_des = "是否开启未订阅告警,0表示关闭，1表示开启";

	public Boolean isEnbaleNoSubsribe() {
		return "1".equals(env.getProperty(env_isEnbaleNoSubsribe_key, env_isEnbaleNoSubsribe_defaultValue));
	}

	private final String env_getServerGroupCount_key = "mq.broker.group.count";
	private final String env_getServerGroupCount_defaultValue = "4";
	private final String env_getServerGroupCount_des = "broker server 非重要功能分配的ip集群分组数量";

	private volatile String _getServerGroupCount = "4";
	private volatile int getServerGroupCount = 4;

	public int getServerGroupCount() {
		try {
			if (!_getServerGroupCount
					.equals(env.getProperty(env_getServerGroupCount_key, env_getServerGroupCount_defaultValue))) {
				_getServerGroupCount = env.getProperty(env_getServerGroupCount_key,
						env_getServerGroupCount_defaultValue);
				getServerGroupCount = Integer.parseInt(_getServerGroupCount);
				if (getServerGroupCount < 2) {
					getServerGroupCount = 2;
				}
				onChange();
			}
		} catch (Exception e) {
			getServerGroupCount = 4;
			onChange();
		}
		return getServerGroupCount;
	}

	private final String env_getEnableTopicRate_key = "mq.publish.rate.enable";
	private final String env_getEnableTopicRate_defaultValue = "4";
	private final String env_getEnableTopicRate_des = "是否开启限速";

	private volatile String _getEnableTopicRate = "0";
	private volatile int getEnableTopicRate = 0;

	public int getEnableTopicRate() {
		try {
			if (!_getEnableTopicRate
					.equals(env.getProperty(env_getEnableTopicRate_key, env_getEnableTopicRate_defaultValue))) {
				_getEnableTopicRate = env.getProperty(env_getEnableTopicRate_key, env_getEnableTopicRate_defaultValue);
				getEnableTopicRate = Integer.parseInt(_getEnableTopicRate);
				if (getEnableTopicRate < 2) {
					getEnableTopicRate = 2;
				}
				onChange();
			}
		} catch (Exception e) {
			getEnableTopicRate = 0;
			onChange();
		}
		return getEnableTopicRate;
	}

	// 为了保证数据不丢失，开启非严格模式，即当数据异常时，如果有可用的队列即开始保存消息，忽略读写属性
	private final String env_getPublishMode_key = "mq.publish.mode";
	private final String env_getPublishMode_defaultValue = "1";
	private final String env_getPublishMode_des = "当数据异常时，如果有可用的队列即开始保存消息，忽略读写属性";

	public int getPublishMode() {
		try {
			return Integer.parseInt(env.getProperty(env_getPublishMode_key, env_getPublishMode_defaultValue));
		} catch (Exception e) {
			return 1;
		}
	}

	// 用来保存所有的发送失败的topic
	private final String env_getSysPubFailTopic_key = "mq.sys.publish.topic";
	private final String env_getSysPubFailTopic_defaultValue = "SysTopicFailMsg";
	private final String env_getSysPubFailTopic_des = "用来保存所有的发送失败的topic";

	public String getSysPubFailTopic() {
		return env.getProperty(env_getSysPubFailTopic_key, env_getSysPubFailTopic_defaultValue);
	}

	private final String env_getSysFailSub_key = "mq.sys.fail.sub";
	private final String env_getSysFailSub_defaultValue = "SysFailSub";
	private final String env_getSysFailSub_des = "";

	public String getSysFailSub() {
		return env.getProperty(env_getSysFailSub_key, env_getSysFailSub_defaultValue);
	}

	private final String env_getMetricUrl_key = "mq.client.metric.url";
	private final String env_getMetricUrl_defaultValue = "";
	private final String env_getMetricUrl_des = "客户端上报metric的url地址";

	public String getMetricUrl() {
		return env.getProperty(env_getMetricUrl_key, env_getMetricUrl_defaultValue);
	}

	private final String env_getCacheRebuild_key = "mq.cache.rebuild.count";
	private final String env_getCacheRebuild_defaultValue = "0";
	private final String env_getCacheRebuild_des = "cache重建计数器";

	private volatile String _getCacheRebuild = "0";
	private volatile int getCacheRebuild = 0;

	public int getCacheRebuild() {
		try {
			if (!_getCacheRebuild.equals(env.getProperty(env_getCacheRebuild_key, env_getCacheRebuild_defaultValue))) {
				_getCacheRebuild = env.getProperty(env_getCacheRebuild_key, env_getCacheRebuild_defaultValue);
				getCacheRebuild = Integer.parseInt(_getCacheRebuild);
				onChange();
			}
		} catch (Exception e) {
			getCacheRebuild = 4;
			onChange();
		}
		return getCacheRebuild;
	}

	private final String env_getLogPortalTopic_key = "mq.topic.check.log";
	private final String env_getLogPortalTopic_defaultValue = "0";
	private final String env_getLogPortalTopic_des = "";

	public String getLogPortalTopic() {
		return env.getProperty(env_getLogPortalTopic_key, env_getLogPortalTopic_defaultValue);
	}

	// private final String env_getBrokerDbMasterSlave_key =
	// "mq.broker.db.slave.open";
	// private final String env_getBrokerDbMasterSlave_defaultValue = "0";
	// private final String env_getBrokerDbMasterSlave_des =
	// "broker是否开启读写分离，默认否";
	//
	// public String getBrokerDbMasterSlave() {
	// return env.getProperty("mq.broker.db.slave.open", "0");
	// }

	private final String env_getDbMasterSlave_key = "mq.db.slave.open";
	private final String env_getDbMasterSlave_defaultValue = "1";
	private final String env_getDbMasterSlave_des = "是否开启master slave";

	public String getDbMasterSlave() {
		return env.getProperty(env_getDbMasterSlave_key, env_getDbMasterSlave_defaultValue);
	}

	private final String env_getDbIpCat_key = "mq.db.ip.cat";
	private final String env_getDbIpCat_defaultValue = "0";
	private final String env_getDbIpCat_des = "是否在cat上记录数据ip";

	public String getDbIpCat() {
		return env.getProperty(env_getDbIpCat_key, env_getDbIpCat_defaultValue);
	}

//	private final String env_getConsumerAlarmTopicName_key = "mq.consumer.alarm.topic";
//	private final String env_getConsumerAlarmTopicName_defaultValue = "Mq_Consumer_Alarm";
//	private final String env_getConsumerAlarmTopicName_des = "记录消费告警的topic名称";
//
//	public String getConsumerAlarmTopicName() {
//		return env.getProperty(env_getConsumerAlarmTopicName_key, env_getConsumerAlarmTopicName_defaultValue);
//	}

//	private final String env_getConsumerLogTopicName_key = "mq.consumer.alarm.topic";
//	private final String env_getConsumerLogTopicName_defaultValue = "Mq_Consumer_Log";
//	private final String env_getConsumerLogTopicName_des = "记录消费日志的topic名称";
//
//	public String getConsumerLogTopicName() {
//		return env.getProperty(env_getConsumerLogTopicName_key, env_getConsumerLogTopicName_defaultValue);
//	}

	private final String env_getMaxConsumerNoActiveTime_key = "mq.consumer.inactive.max.time";
	private final String env_getMaxConsumerNoActiveTime_defaultValue = "300000";
	private final String env_getMaxConsumerNoActiveTime_des = "当消费者未发送心跳时，服务端会反向调用hs接口，如果接口正常，则会最大等待5分钟(默认),如果未报心跳则删除。这种情况会出现频繁重启的时候，会出现这种情况。";

	private volatile String _getMaxConsumerNoActiveTime = "300000";
	private volatile int getMaxConsumerNoActiveTime = 300000;

	public int getMaxConsumerNoActiveTime() {
		try {
			if (!_getMaxConsumerNoActiveTime.equals(
					env.getProperty(env_getMaxConsumerNoActiveTime_key, env_getMaxConsumerNoActiveTime_defaultValue))) {
				_getMaxConsumerNoActiveTime = env.getProperty(env_getMaxConsumerNoActiveTime_key,
						env_getMaxConsumerNoActiveTime_defaultValue);
				getMaxConsumerNoActiveTime = Integer.parseInt(_getMaxConsumerNoActiveTime);
				if (getMaxConsumerNoActiveTime < 2) {
					getMaxConsumerNoActiveTime = 2;
				}
				onChange();
			}
		} catch (Exception e) {
			getMaxConsumerNoActiveTime = 4;
			onChange();
		}
		return getMaxConsumerNoActiveTime;
	}

	private volatile String _getMqMetaRebuildMaxInterval = "";
	private volatile int getMqMetaRebuildMaxInterval = 120000;
	private final String env_getMqMetaRebuildMaxInterval_key = "mq.meta.rebuild.max.interval";
	private final String env_getMqMetaRebuildMaxInterval_defaultValue = "120000";
	private final String env_getMqMetaRebuildMaxInterval_des = "queue最大值定时刷新间隔时间";

	// 元数据强制重构刷新时间间隔
	public int getMqMetaRebuildMaxInterval() {
		try {
			if (!_getMqMetaRebuildMaxInterval.equals(env.getProperty(env_getMqMetaRebuildMaxInterval_key,
					env_getMqMetaRebuildMaxInterval_defaultValue))) {
				_getMqMetaRebuildMaxInterval = env.getProperty(env_getMqMetaRebuildMaxInterval_key,
						env_getMqMetaRebuildMaxInterval_defaultValue);
				getMqMetaRebuildMaxInterval = Integer.parseInt(env.getProperty(env_getMqMetaRebuildMaxInterval_key,
						env_getMqMetaRebuildMaxInterval_defaultValue));
				if (getMqMetaRebuildMaxInterval < 5000) {
					getMqMetaRebuildMaxInterval = 5000;
				}
				onChange();
			}
		} catch (Exception e) {
			getMqMetaRebuildMaxInterval = 5000;
			onChange();
			log.error("getgetMqMetaRebuildMaxInterval_SoaConfig_error", e);
		}
		return getMqMetaRebuildMaxInterval;
	}

	private final String env_isUseDruid_key = "mq.druid.enable";
	private final String env_isUseDruid_defaultValue = "1";
	private final String env_isUseDruid_des = "连接池是否采用druid";

	public boolean isUseDruid() {
		return "1".equals(env.getProperty(env_isUseDruid_key, env_isUseDruid_defaultValue));
	}

	private final String env_getToolTopicFilterFlag_key = "mq.tool.topic.filter.flag";
	private final String env_getToolTopicFilterFlag_defaultValue = "0";
	private final String env_getToolTopicFilterFlag_des = "值为0表示普通用户可以通过界面往所有topic推送消息。值为1普通用户只能往自己的topic推送消息";

	public String getToolTopicFilterFlag() {
		return env.getProperty(env_getToolTopicFilterFlag_key, env_getToolTopicFilterFlag_defaultValue);
	}

	private final String env_getSdkVersion_key = "mq.client.version";
	private final String env_getSdkVersion_defaultValue = PropUtil.getSdkVersion();
	private final String env_getSdkVersion_des = "客户端推荐版本号";

	public String getSdkVersion() {
		return env.getProperty(env_getSdkVersion_key, env_getSdkVersion_defaultValue);
	}

	private final String env_getSkipTime_key = "mq.clean.skip.time";
	private final String env_getSkipTime_defaultValue = "";
	private final String env_getSkipTime_des = "消息清理跳过时间格式为, 00:00-1:00,2:00-3:00";

	private volatile String _getSkipTime = "";
	private AtomicReference<List<TimeRange>> getSkipTime = new AtomicReference<List<TimeRange>>(new ArrayList<>());

	public List<TimeRange> getSkipTime() {
		try {
			if (!_getSkipTime.equals(env.getProperty(env_getSkipTime_key, env_getSkipTime_defaultValue))) {
				_getSkipTime = env.getProperty(env_getSkipTime_key, env_getSkipTime_defaultValue);
				String[] arr = _getSkipTime.split(",");
				List<TimeRange> getSkipTime1 = new ArrayList<>();
				for (String temp : arr) {
					if (!com.ppdai.infrastructure.mq.biz.common.util.Util.isEmpty(temp)) {
						String[] arr1 = temp.split("-");
						if (arr1.length == 2) {
							TimeRange timeRange = new TimeRange();
							try {
								initTimeRange(getSkipTime1, arr1, timeRange);
							} catch (Exception e) {
							}
						}
					}
				}
				getSkipTime.set(getSkipTime1);
				onChange();
			}
		} catch (Exception e) {
			// getSkipTime = new ArrayList<>();
			onChange();
		}
		return getSkipTime.get();
	}

	private void initTimeRange(List<TimeRange> getSkipTime1, String[] arr1, TimeRange timeRange) {
		String[] arr2 = arr1[0].split(":");
		String[] arr3 = arr1[1].split(":");

		int start = getMinute(arr2);
		int end = getMinute(arr3);

		if (start != -1 && end != -1) {
			if (start <= end) {
				timeRange.start = start;
				timeRange.end = end;
				getSkipTime1.add(timeRange);
			} else {
				timeRange.start = start;
				timeRange.end = 24 * 60;
				getSkipTime1.add(timeRange);

				timeRange = new TimeRange();
				timeRange.start = 0;
				timeRange.end = end;
				getSkipTime1.add(timeRange);
			}
		}
	}

	private int getMinute(String[] arr2) {
		if (Integer.parseInt(arr2[0]) >= 0 && Integer.parseInt(arr2[0]) < 24 && Integer.parseInt(arr2[1]) >= 0
				&& Integer.parseInt(arr2[1]) < 60) {
			return Integer.parseInt(arr2[0]) * 60 + Integer.parseInt(arr2[1]);
		}
		return -1;
	}

	public class TimeRange {
		public int start;
		public int end;
	}

	private final String env_getTopicDeleteLimitCount_key = "mq.topic.delete.limit";
	private final String env_getTopicDeleteLimitCount_defaultValue = "100";
	private final String env_getTopicDeleteLimitCount_des = "topic删除限制值，默认100万，单位为万，超过此值，不能直接删除,生产环境生效！";

	private volatile String _getTopicDeleteLimitCount = "0";
	private volatile int getTopicDeleteLimitCount = 0;

	public int getTopicDeleteLimitCount() {
		try {
			if (!_getTopicDeleteLimitCount.equals(
					env.getProperty(env_getTopicDeleteLimitCount_key, env_getTopicDeleteLimitCount_defaultValue))) {
				_getTopicDeleteLimitCount = env.getProperty(env_getTopicDeleteLimitCount_key,
						env_getTopicDeleteLimitCount_defaultValue);
				getTopicDeleteLimitCount = Integer.parseInt(_getTopicDeleteLimitCount);
				onChange();
			}
		} catch (Exception e) {
			getTopicDeleteLimitCount = 100;
			onChange();
		}
		return getTopicDeleteLimitCount;
	}

	private final String env_getMqLdapUrl_key = "mq.ldap.url";
	private final String env_getMqLdapUrl_defaultValue = "";
	private final String env_getMqLdapUrl_des = "域控地址";

	public String getMqLdapUrl() {
		return env.getProperty(env_getMqLdapUrl_key, env_getMqLdapUrl_defaultValue);
	}

	private final String env_getMqLdapPath_key = "mq.ldap.path";
	private final String env_getMqLdapPath_defaultValue = "";
	private final String env_getMqLdapPath_des = "域控路径，以 | 隔开,如OU=***,OU=***,DC=corp,DC=***,DC=com";

	public String getMqLdapPath() {
		return env.getProperty(env_getMqLdapPath_key, env_getMqLdapPath_defaultValue);
	}

	private final String env_getTransSkipNames_key = "mq.cat.skip.names";
	private final String env_getTransSkipNames_defaultValue = "";
	private final String env_getTransSkipNames_des = "忽略cat埋点的名称";

	private volatile String _getTransSkipNames = "5";
	private volatile Map<String, String> getTransSkipNameMap = new HashMap<>();

	public Map<String, String> getTransSkipNames() {
		try {
			if (!_getTransSkipNames
					.equals(env.getProperty(env_getTransSkipNames_key, env_getTransSkipNames_defaultValue))) {
				_getTransSkipNames = env.getProperty(env_getTransSkipNames_key, env_getTransSkipNames_defaultValue);
				String[] arr = _getTransSkipNames.split(",");
				Map<String, String> getTransSkipNameMap1 = new HashMap<>();
				if (arr != null && arr.length > 0) {
					for (int i = 0; i < arr.length; i++) {
						if (!com.ppdai.infrastructure.mq.biz.common.util.Util.isEmpty(arr[i])) {
							getTransSkipNameMap1.put(arr[i], "1");
						}
					}
				}
				getTransSkipNameMap = getTransSkipNameMap1;
				onChange();
			}
		} catch (Exception e) {
			getTransSkipNameMap = new HashMap<>();
			onChange();
		}
		return getTransSkipNameMap;
	}
}
