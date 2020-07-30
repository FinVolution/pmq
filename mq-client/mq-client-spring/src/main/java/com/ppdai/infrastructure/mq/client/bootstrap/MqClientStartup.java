package com.ppdai.infrastructure.mq.client.bootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.ppdai.infrastructure.mq.biz.MqConst;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.MqConfig;

public class MqClientStartup {
	private static final Logger logger = LoggerFactory.getLogger(MqClientStartup.class);
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(100), SoaThreadFactory.create("mqconfig-scan", true),
			new ThreadPoolExecutor.DiscardPolicy());

	private static Environment env;
	private static volatile boolean isRunning = true;
	protected static AtomicBoolean initFlag = new AtomicBoolean(false);
	private static AtomicBoolean startFlag = new AtomicBoolean(false);

	public static void springInitComplete() {
		MqClient.start();
		monitorConfig();
	}

	public static void init(Environment env1) {
		if (initFlag.compareAndSet(false, true)) {
			env = env1;
			initConfig();
		}
	}

	private static void initConfig() {
		MqConfig config = new MqConfig();
		String netCard = System.getProperty("mq.network.netCard", env.getProperty("mq.network.netCard", ""));		
		String url =System.getProperty("mq.broker.url", env.getProperty("mq.broker.url", ""));		
		String host = System.getProperty("mq.client.host", env.getProperty("mq.client.host", ""));
		String serverPort = System.getProperty("server.port", env.getProperty("server.port", "8080"));
		String asynCapacity = System.getProperty("mq.asyn.capacity", env.getProperty("mq.asyn.capacity", "2000"));
		String rbTimes = System.getProperty("mq.rb.times", env.getProperty("mq.rb.times", "4"));
		String pbRetryTimes = System.getProperty("mq.pb.retry.times", env.getProperty("mq.pb.retry.times", "10"));
		String readTimeOut = System.getProperty("mq.http.timeout", env.getProperty("mq.http.timeout", "10000"));
		String pullDeltaTime = System.getProperty("mq.pull.time.delta", env.getProperty("mq.pull.time.delta", "150"));
		boolean metaMode = "true"
				.equals(System.getProperty("mq.broker.metaMode", env.getProperty("mq.broker.metaMode", "true")));
		if(!Util.isEmpty(netCard)){
			logger.warn("请注意你指定了网卡名称mq.network.netCard="+netCard);
		}		
		if (Util.isEmpty(host)) {
			host = IPUtil.getLocalIP(netCard);
			logger.info("自动获取当前的ip地址是"+host);
		}else{
			logger.info("当前配置生效的机器ip地址是：mq.client.host="+host);
		}
		
		if (Util.isEmpty(url)) {
			throw new RuntimeException("没有配置broker地址。");
		}
		config.setIp(host);
		config.setMetaMode(metaMode);
		config.setServerPort(serverPort);
		config.setUrl(url);
		config.setAsynCapacity(Integer.parseInt(asynCapacity));
		config.setRbTimes(getRbTimes(rbTimes));
		config.setPbRetryTimes(getPbRetryTimes(pbRetryTimes));
		config.setPullDeltaTime(Integer.parseInt(pullDeltaTime));
		config.setReadTimeOut(Long.parseLong(readTimeOut));
		logger.info("当前生效的配置是："+JsonUtil.toJsonNull(config));
		MqClient.init(config);
		updateConfig();
	}

	private static int getPbRetryTimes(String pbRetryTimes) {
		int pbTimes1 = 2;
		try {
			pbTimes1 = Integer.parseInt(pbRetryTimes);
		} catch (Exception e) {

		}
		if (pbTimes1 < 2) {
			pbTimes1 = 2;
		}
		return pbTimes1;
	}

	private static int getRbTimes(String rbTimes) {
		int rbTimes1 = 4;
		try {
			rbTimes1 = Integer.parseInt(rbTimes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (rbTimes1 < 0) {
			rbTimes1 = 0;
		}
		return rbTimes1;
	}

	public static void close() {
		isRunning = false;
	}

	protected static String mqLogOrig = "-2";
	protected static String rbTimes = "4";
	protected static String pbTimes = "0";
	protected static String metaMode = "-2";
	protected static String asynCapacity = "-2";
	protected static String pullDeltaTime1 = "150";
	protected static String subEnvs1 = " ";
	protected static String publishAsynTimeout1="1000";
	protected static Map<String, String> properties = null;

	private static void monitorConfig() {
		if (env != null) {
			if (!startFlag.compareAndSet(false, true)) {
				return;
			}
		} else {
			return;
		}
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while (isRunning) {
					updateConfig();
					Util.sleep(2000);
				}
			}

		});
	}

	protected static void updateConfig() {
		if (properties == null) {
			properties = MqClient.getContext().getConfig().getProperties();
		}
		// setLog();

		setRbTimes();

		setPbTimes();

		setAsynCapacity();

		setMetaMode();

		setPullDeltaTime();
		
		//setAppSubEnvs();
		
		setPublishAsynTimeout();
	}

	private static void setPublishAsynTimeout() {
		String publishAsynTimeout = System.getProperty("mq.publish.asyn.timeout", env.getProperty("mq.publish.asyn.timeout", "1000"));
		if (!publishAsynTimeout.equals(publishAsynTimeout1)) {
			try{
				publishAsynTimeout1=publishAsynTimeout;
				int publishAsynTimeout2 = Integer.parseInt(publishAsynTimeout);				
				MqClient.getContext().getConfig().setPublishAsynTimeout(publishAsynTimeout2);
			}catch (Exception e) {
				logger.error("setPublishAsynTimeout_error", e);
			}
			
		}

	}

	private static void setAppSubEnvs() {
		String subEnvs = System.getProperty("mq.app.subEnvs", env.getProperty("mq.app.subEnvs", ""));
		if (!subEnvs1.equals(subEnvs)&&MqClient.getMqEnvironment()!=null) {
			List<String> subEnv2 = Arrays.asList(subEnvs.toLowerCase().split(","));
			subEnv2.remove(MqConst.DEFAULT_SUBENV);
			if(MqClient.getMqEnvironment()!=null){
				MqClient.getMqEnvironment().setAppSubEnvs(subEnv2);
			}
			subEnvs1 = subEnvs;
		}
	}

	private static void setPullDeltaTime() {
		try {
			String pullDeltaTime = System.getProperty("mq.pull.time.delta",
					env.getProperty("mq.pull.time.delta", "150"));
			properties.put("mq.pull.time.delta", pullDeltaTime);
			if (!pullDeltaTime1.equals(pullDeltaTime)) {
				pullDeltaTime1 = pullDeltaTime;
				int pullDeltaTime2 = Integer.parseInt(pullDeltaTime);
				if (MqClient.getContext() != null
						&& MqClient.getContext().getConfig().getPullDeltaTime() != pullDeltaTime2) {
					MqClient.getContext().getConfig().setPullDeltaTime(pullDeltaTime2);
				}
			}
		} catch (Exception e) {
			logger.error("setPullDeltaTime_error", e);
		}

	}

	private static void setMetaMode() {
		try {
			String metaMode1 = System.getProperty("mq.broker.metaMode", env.getProperty("mq.broker.metaMode", "true"));
			if ("true".equals(metaMode1) || "false".equals(metaMode1)) {
				properties.put("mq.broker.metaMode", metaMode1);
				if (!metaMode1.equals(metaMode)) {
					metaMode = metaMode1;
					boolean mm = Boolean.parseBoolean(metaMode1);
					if (MqClient.getContext() != null && MqClient.getContext().getConfig().isMetaMode() != mm) {
						MqClient.getContext().getConfig().setMetaMode(mm);
					}
				}
			}
		} catch (Exception e) {
			logger.error("setMetaMode_error", e);
		}
	}

	private static void setAsynCapacity() {
		try {
			String asynCapacityTemp = System.getProperty("mq.asyn.capacity",
					env.getProperty("mq.asyn.capacity", "2000"));
			properties.put("mq.asyn.capacity", asynCapacityTemp);
			if (!asynCapacity.equals(asynCapacityTemp)) {
				asynCapacity = asynCapacityTemp;
				int asynCapacity1 = Integer.parseInt(asynCapacityTemp);
				if (MqClient.getContext() != null
						&& MqClient.getContext().getConfig().getAsynCapacity() != asynCapacity1) {
					if (asynCapacity1 < 2000) {
						asynCapacity1 = 2000;
						properties.put("mq.asyn.capacity", "2000");
					}
					MqClient.getContext().getConfig().setAsynCapacity(asynCapacity1);
				}
			}
		} catch (Exception e) {
			logger.error("setAsynCapacity_error", e);
		}
	}

	private static void setRbTimes() {
		try {
			String timesTemp = System.getProperty("mq.rb.times", env.getProperty("mq.rb.times", "4"));
			properties.put("mq.rb.times", timesTemp);
			if (!timesTemp.equals(rbTimes)) {
				rbTimes = timesTemp;
				int times1 = getRbTimes(timesTemp);
				if (MqClient.getContext() != null && MqClient.getContext().getConfig().getRbTimes() != times1) {
					MqClient.getContext().getConfig().setRbTimes(times1);

				}
			}
		} catch (Exception e) {
			logger.error("setRbTimes_error", e);
		}

	}

	private static void setPbTimes() {
		try {
			String timesTemp = System.getProperty("mq.pb.retry.times", env.getProperty("mq.pb.retry.times", "5"));
			properties.put("mq.pb.retry.times", timesTemp);
			if (!timesTemp.equals(pbTimes)) {
				pbTimes = timesTemp;
				int times1 = getPbRetryTimes(timesTemp);
				if (MqClient.getContext() != null && MqClient.getContext().getConfig().getPbRetryTimes() != times1) {
					MqClient.getContext().getConfig().setPbRetryTimes(times1);

				}
			}
		} catch (Exception e) {
			logger.error("setRbTimes_error", e);
		}

	}

//	private static void setLog() {
//		String logOrig = env.getProperty("mq.log.original", "0");
//		properties.put("mq.log.original", logOrig);
//		if (!mqLogOrig.equals(logOrig)) {
//			if (logOrig.equals("1") || logOrig.equals("0")) {
//				MqClient.getContext().setLogOrigData(Integer.parseInt(logOrig));
//				mqLogOrig = logOrig;
//			}
//		}
//	}

}
