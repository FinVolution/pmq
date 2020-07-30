package com.ppdai.infrastructure.mq.biz.polling;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.PortalTimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.service.MqLockService;
import com.ppdai.infrastructure.mq.biz.service.impl.MqLockServiceImpl;

public abstract class AbstractTimerService implements PortalTimerService {
	private Logger log = LoggerFactory.getLogger(AbstractTimerService.class);
	private MqLockService mqLockService = null;
	private boolean isRunning = false;
	private boolean isMaster = false;	
	private int interval = 0;
	private Object lockObj = new Object();
	private ThreadPoolExecutor executor = null;
	private SoaConfig soaConfig;
	private String key = "";
	private TraceMessage traceMessage = null;
	//注意interval为master判断间隔时间，当强行删除mqlock数据中的某条记录时，如果应用都启动了，必须等待一个interval周期才会开始新的master选举过程，如果有新的应用产生则进行新的选举，选择采取先到先得原则
	public void init(String key, int interval, SoaConfig soaConfig) {
		this.key = key;
		mqLockService = new MqLockServiceImpl(key);
		executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10),
				SoaThreadFactory.create(key, true), new ThreadPoolExecutor.DiscardOldestPolicy());
		this.interval = interval;
		this.soaConfig = soaConfig;
		this.traceMessage = TraceFactory.getInstance(key);
	}

	public void updateInterval(int interval1){
		this.interval = interval1;
	}
	@Override
	public String info() {
		return String.format("timer key [%s],  master status is %s,enableTimer status is %s", key, isMaster,
				soaConfig.enableTimer());
	}

	@Override
	public void startPortal() {
		if (!isRunning) {
			synchronized (lockObj) {
				isRunning = true;
				executor.execute(new Runnable() {
					@Override
					public void run() {
						work();
					}
				});
				executor.execute(new Runnable() {
					@Override
					public void run() {
						doHeartbeat();
					}
				});
			}
		}
	}

	public boolean isMaster() {
		return mqLockService.isMaster();
	}

	private void work() {
		while (isRunning) {
			isMaster = mqLockService.isMaster();
			TraceMessageItem item = new TraceMessageItem();
			item.status = "isMaster-" + isMaster;
			item.msg = "enableTimer-" + soaConfig.enableTimer();
			if (soaConfig.enableTimer() && isMaster) {
				Transaction transaction = Tracer.newTransaction("Broker-Timer", key);
				try {
					log.info(key + "_work_start");
					doStart();
					log.info(key + "_work_end");
					transaction.setStatus(Transaction.SUCCESS);
				} catch (Exception e) {
					transaction.setStatus(e);
					log.error(key + "_work_error", e);
				} finally {
					transaction.complete();
				}
			}
			traceMessage.add(item);		
			Util.sleep(interval * 1000);
		}
	}

	public abstract void doStart();

	private void doHeartbeat() {
		while (isRunning) {
			try {
				//根据心跳判断是否还占有锁
				isMaster = mqLockService.updateHeatTime();
			} catch (Exception e) {

			}
			Util.sleep(soaConfig.getMqLockHeartBeatTime() * 1000);
		}
	}

	@Override
	public void stopPortal() {
		isRunning = false;
	}
}
