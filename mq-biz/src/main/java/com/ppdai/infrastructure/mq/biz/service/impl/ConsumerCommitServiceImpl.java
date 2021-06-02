package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.BrokerTimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.MqConstanst;
import com.ppdai.infrastructure.mq.biz.dto.base.ConsumerQueueVersionDto;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetRequest;
import com.ppdai.infrastructure.mq.biz.dto.client.CommitOffsetResponse;
import com.ppdai.infrastructure.mq.biz.entity.OffsetVersionEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerCommitService;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;

@Service
public class ConsumerCommitServiceImpl implements ConsumerCommitService, BrokerTimerService {
	private static final Logger log = LoggerFactory.getLogger(ConsumerCommitService.class);
	protected final AtomicReference<Map<Long, ConsumerQueueVersionDto>> mapAppPolling = new AtomicReference<>(
			new ConcurrentHashMap<>(4000));
	protected final Map<Long, ConsumerQueueVersionDto> failMapAppPolling = new ConcurrentHashMap<>(100);
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private QueueOffsetService queueOffsetService;
	@Autowired
	private ConsumerGroupService consumerGroupService;
	TraceMessage traceMessageCommit = TraceFactory.getInstance("mq-commit");
	// TraceMessage traceMessageCommit1 =
	// TraceFactory.getInstance("mq-commit-data");
	private volatile boolean isRunning = true;
	private ThreadPoolExecutor executorRun = null;
	private volatile int commitThreadSize = 5;
	private volatile int commitUpdateThreadSize = 20;
	private final ReentrantLock reentrantLock = new ReentrantLock();
	private ThreadPoolExecutor executorCommit = null;

	@Override
	public void startBroker() {
		commitThreadSize = soaConfig.getCommitThreadSize();
		executorRun = new ThreadPoolExecutor(commitThreadSize + 1, commitThreadSize + 1, 10L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(200), SoaThreadFactory.create("commit-run", Thread.MAX_PRIORITY - 1, true),
				new ThreadPoolExecutor.CallerRunsPolicy());
		executorCommit = new ThreadPoolExecutor(2, commitUpdateThreadSize, 10L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(500), SoaThreadFactory.create("commit-update", Thread.MAX_PRIORITY - 1, true),
				new ThreadPoolExecutor.CallerRunsPolicy());
		soaConfig.registerChanged(new Runnable() {
			@Override
			public void run() {
				if (commitThreadSize != soaConfig.getCommitThreadSize()) {
					commitThreadSize = soaConfig.getCommitThreadSize();
					executorRun.setCorePoolSize(commitThreadSize + 1);
					executorRun.setMaximumPoolSize(commitThreadSize + 1);
				}

				if (commitUpdateThreadSize != soaConfig.getCommitUpdateThreadSize()) {
					commitUpdateThreadSize = soaConfig.getCommitUpdateThreadSize();
					executorCommit.setMaximumPoolSize(commitUpdateThreadSize);
				}

			}
		});
		executorRun.execute(() -> {
			commitOffset();
		});

	}

	protected void commitOffset() {
		log.info("doSubmitOffset");
		while (isRunning) {
			doCommit();
			// 通过随机的方式来避免数据库的洪峰压力
			Util.sleep(soaConfig.getCommitSleepTime());
		}
	}

	protected void doCommit() {
		Map<Long, OffsetVersionEntity> offsetVersionMap = queueOffsetService.getOffsetVersion();
		Map<Long, ConsumerQueueVersionDto> map = new HashMap<>(mapAppPolling.get());
		if (map.size() > 0) {
			final int size = map.size();
			Transaction transaction = Tracer.newTransaction("Timer-service", "commit");
			try {
				int countSize = map.size() < commitThreadSize ? map.size() : commitThreadSize;
				if (countSize == 1) {
					for (Map.Entry<Long, ConsumerQueueVersionDto> entry : map.entrySet()) {
						doCommitOffset(entry.getValue(), 0, offsetVersionMap, size);
					}
				} else {
					CountDownLatch countDownLatch = new CountDownLatch(countSize);
					for (Map.Entry<Long, ConsumerQueueVersionDto> entry : map.entrySet()) {
						executorRun.execute(new Runnable() {
							@Override
							public void run() {
								doCommitOffset(entry.getValue(), 0, offsetVersionMap, size);
								countDownLatch.countDown();
							}
						});
					}
					countDownLatch.await();
				}
				transaction.setStatus(Transaction.SUCCESS);
			} catch (Throwable e) {
				transaction.setStatus(e);
			} finally {
				transaction.complete();
			}
		}
	}

	protected boolean doCommitOffset(ConsumerQueueVersionDto request, int flag,
			Map<Long, OffsetVersionEntity> offsetVersionMap, int count) {
		Transaction catTransaction = null;
		OffsetVersionEntity offsetVersionEntity = offsetVersionMap.get(request.getQueueOffsetId());
		if (checkOffsetAndVersion(request, offsetVersionEntity)) {
			try {
				catTransaction = Tracer.newTransaction("Timer-service",
						MqConstanst.CONSUMERPRE + "/commitOffset-" + flag);
				QueueOffsetEntity queueOffsetEntity = new QueueOffsetEntity();
				queueOffsetEntity.setId(request.getQueueOffsetId());
				queueOffsetEntity.setOffsetVersion(request.getOffsetVersion());
				queueOffsetEntity.setOffset(request.getOffset());	
				queueOffsetEntity.setConsumerGroupName(request.getConsumerGroupName());
				queueOffsetEntity.setTopicName(request.getTopicName());
				boolean rs = false;
				if (flag == 1) {
					rs = queueOffsetService.commitOffsetAndUpdateVersion(queueOffsetEntity) > 0 && offsetVersionEntity != null;
					if(rs){
						queueOffsetEntity.setOffsetVersion(queueOffsetEntity.getOffsetVersion()+1);
					}
				} else {
					rs = queueOffsetService.commitOffset(queueOffsetEntity) > 0 && offsetVersionEntity != null;
				}
				if (rs) {
					reentrantLock.lock();
					if (request.getOffsetVersion() == offsetVersionEntity.getOffsetVersion()
							&& request.getOffset() > offsetVersionEntity.getOffset()) {
						offsetVersionEntity.setOffset(request.getOffset());
					} else if (request.getOffsetVersion() > offsetVersionEntity.getOffsetVersion()) {
						offsetVersionEntity.setOffsetVersion(request.getOffsetVersion());
						offsetVersionEntity.setOffset(request.getOffset());
					}
					reentrantLock.unlock();
				}
				catTransaction.setStatus(Transaction.SUCCESS);

				return true;
			} catch (Throwable e) {
				failMapAppPolling.put(request.getQueueOffsetId(), request);
				log.error("doSubmitOffset失败", e);
				catTransaction.setStatus(e);
				return false;
			} finally {
				catTransaction.complete();
			}
		}
		return true;
	}

	protected boolean checkOffsetAndVersion(ConsumerQueueVersionDto request, OffsetVersionEntity offsetVersionEntity) {
		if (offsetVersionEntity == null) {
			return true;
		} else if (request.getOffsetVersion() > offsetVersionEntity.getOffsetVersion()) {
			return true;
		} else if (request.getOffset() > offsetVersionEntity.getOffset()) {
			return true;
		}
		return false;
	}

	protected long lastTime = System.currentTimeMillis();
	private Object lockObj = new Object();
	private Object lockObj1 = new Object();

	@Override
	public CommitOffsetResponse commitOffset(CommitOffsetRequest request) {
		// Transaction catTransaction = Tracer.newTransaction("Timer-service",
		// "commitOffset");
		CommitOffsetResponse response = new CommitOffsetResponse();
		response.setSuc(true);
		Map<Long, ConsumerQueueVersionDto> map = mapAppPolling.get();
		try {
			if (request != null && !CollectionUtils.isEmpty(request.getQueueOffsets())) {
				request.getQueueOffsets().forEach(t1 -> {
					ConsumerQueueVersionDto temp = map.get(t1.getQueueOffsetId());
					boolean flag1 = true;
					if (temp == null) {
						synchronized (lockObj1) {
							temp = map.get(t1.getQueueOffsetId());
							if (temp == null) {
								map.put(t1.getQueueOffsetId(), t1);
								flag1 = false;
							}
						}
					}
					if (flag1) {
						if (temp.getOffsetVersion() < t1.getOffsetVersion()) {
							clearOldData();
							map.put(t1.getQueueOffsetId(), t1);
						} else if (temp.getOffsetVersion() == t1.getOffsetVersion()
								&& temp.getOffset() < t1.getOffset()) {
							clearOldData();
							map.put(t1.getQueueOffsetId(), t1);
						}
					}
				});
				if (request.getFlag() == 1) {
					executorCommit.submit(new Runnable() {
						@Override
						public void run() {
							commitAndUpdate(request);
						}
					});
				}
			}
		} catch (Exception e) {
		}
		// catTransaction.setStatus(Transaction.SUCCESS);
		// catTransaction.complete();
		return response;
	}
	private void commitAndUpdate(CommitOffsetRequest request) {
		Transaction catTransaction = Tracer.newTransaction("Timer-service", "close-commitOffset");
		Set<String> consumerGroupNames = new HashSet<>();
		try {
			Map<Long, OffsetVersionEntity> offsetVersionMap = queueOffsetService.getOffsetVersion();
			request.getQueueOffsets().forEach(t1 -> {
				doCommitOffset(t1, 1, offsetVersionMap, 0);
				consumerGroupNames.add(t1.getConsumerGroupName());
			});
			catTransaction.setStatus(Transaction.SUCCESS);
		} catch (Throwable ee) {
			catTransaction.setStatus(ee);
			log.error("", ee);
		} finally {
			try {
				if (consumerGroupNames.size() > 0) {
					consumerGroupService.notifyMetaByNames(new ArrayList<>(consumerGroupNames));
					catTransaction.setStatus(Transaction.SUCCESS);
				}
			} catch (Throwable ee) {
				catTransaction.setStatus(ee);
				log.error("", ee);
			}
		}
		catTransaction.complete();
	}
	protected void clearOldData() {
		boolean flag = (System.currentTimeMillis() - lastTime - 10 * 60 * 1000) > 0;
		if (flag) {
			synchronized (lockObj) {
				if ((System.currentTimeMillis() - lastTime - 30 * 60 * 1000) > 0) {
					mapAppPolling.set(new ConcurrentHashMap<>(failMapAppPolling));
					failMapAppPolling.clear();
					lastTime = System.currentTimeMillis();
				}
			}
		}
	}

	@Override
	public void stopBroker() {
		try {
			isRunning = false;
			executorRun.shutdown();
			executorCommit.shutdown();
		} catch (Throwable e) {
			// TODO: handle exception
		}

	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, ConsumerQueueVersionDto> getCache() {
		// TODO Auto-generated method stub
		return mapAppPolling.get();
	}
}
