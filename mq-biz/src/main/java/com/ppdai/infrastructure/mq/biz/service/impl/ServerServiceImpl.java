package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.inf.BrokerTimerService;
import com.ppdai.infrastructure.mq.biz.common.inf.PortalTimerService;
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.ServerRepository;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.ServerService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;

/**
 * @author dal-generator
 */
@Service
public class ServerServiceImpl extends AbstractBaseService<ServerEntity>
		implements ServerService, BrokerTimerService, PortalTimerService {
	private Logger log = LoggerFactory.getLogger(ServerServiceImpl.class);
	private volatile boolean isRunning = true;
	private volatile long id = 0;
	private volatile boolean isBroker = false;
	@Autowired
	private Environment env;

	@Autowired
	private SoaConfig soaConfig;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private ThreadPoolExecutor executor = null;

	private AtomicReference<List<String>> cacheBrokerDataMap = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<List<String>> cachePortalDataMap = new AtomicReference<>(new ArrayList<>());
	private volatile int onlineServerCount = 0;
	@Autowired
	private ServerRepository serverRepository;
	@Autowired
	private AuditLogService auditLogService;

	@PostConstruct
	private void init() {
		super.setBaseRepository(serverRepository);
	}	
	
	@Override
	public void startBroker() {
		isBroker = true;
		heartbeatAndUpdate();
	}
	
	private  String getServerVersion() {
		try {
			return env.getProperty("mq.broker.version",Util.formateDate(new Date(), "yyyyMMdd"));
		} catch (Exception e) {
			return Util.formateDate(new Date(), "yyyyMMdd");
		}
	}

	private void heartbeatAndUpdate() {
		if (startFlag.compareAndSet(false, true)) {
			executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(50),
					SoaThreadFactory.create("ServerServiceImpl", true), new ThreadPoolExecutor.DiscardOldestPolicy());
			updateHeartBeat();
			updateCache();
			executor.execute(() -> {
				while (isRunning) {
					try {
						updateHeartBeat();
						updateCache();
					} catch (Throwable e) {
						log.error("ServerServiceImpl_updateHeartBeat_error", e);
					}
					Util.sleep(soaConfig.getServerHeartbeat() * 1000);
				}
			});
		}
	}

	private void updateHeartBeat() {
		if (isBroker) {
			if (serverRepository.updateHeartTimeById(id) <= 0) {
				id = 0;
				doInitData();
			}
		} else {
			if (env.getProperty("server.delete", "1").equals("1")) {
				try {
					int count = serverRepository.deleteOld(soaConfig.getServerExpireTime());
					if (count > 0) {
						AuditLogEntity auditLog = new AuditLogEntity();
						auditLog.setTbName(ServerEntity.TABLE_NAME);
						auditLog.setRefId(0);
						auditLog.setInsertBy(IPUtil.getLocalIP());
						auditLog.setContent("deleted instance,count is " + count + ",expiretime config is "
								+ soaConfig.getServerExpireTime());
						auditLogService.insert(auditLog);
						log.info(auditLog.getContent());
					}
				} catch (Exception e) {
				}
			}
		}

	}

	private void updateCache() {
		List<String> brokerIps = new ArrayList<>();
		List<String> portalIps = new ArrayList<>();
		List<ServerEntity> lst = serverRepository.getNoramlServer(soaConfig.getServerHeartbeat() + 10);
		int count = 0;
		for (ServerEntity t1 : lst) {
			if (t1.getServerType() == 1 && t1.getStatusFlag() == 1) {
				brokerIps.add("http://" + t1.getIp() + ":" + t1.getPort());
				count++;
			} else if (t1.getServerType() == 0) {
				portalIps.add("http://" + t1.getIp() + ":" + t1.getPort());
			}

		}
		onlineServerCount = count;
		cacheBrokerDataMap.set(brokerIps);
		cachePortalDataMap.set(portalIps);
	}

	private void doInitData() {
		if (id <= 0) {
			ServerEntity serverEntity = new ServerEntity();
			try {
				serverEntity.setIp(IPUtil.getLocalIP(env.getProperty("mq.broker.netCard")));
				serverEntity.setPort(Integer.parseInt(env.getProperty("server.port", "8080")));
				serverEntity.setHeartTime(new Date());
				serverEntity.setServerType(isBroker?1:0);
				serverEntity.setServerVersion(getServerVersion());
				serverEntity.setStatusFlag(0);
				serverRepository.insert1(serverEntity);
				id = serverEntity.getId();
			} catch (Exception e) {
				try {
					Map<String, Object> conditionMap = new HashMap<>();
					conditionMap.put(ServerEntity.FdIp, serverEntity.getIp());
					conditionMap.put(ServerEntity.FdPort, serverEntity.getPort());
					serverEntity = serverRepository.get(conditionMap);
					if (serverEntity != null) {
						id = serverEntity.getId();
						serverEntity.setServerType(isBroker?1:0);
						serverEntity.setServerVersion(getServerVersion());
						serverRepository.update(serverEntity);
					}
				} catch (Exception e1) {

				}
			}
		}
	}

	@Override
	public void stopBroker() {
		try {
			executor.shutdown();
			isRunning = false;
			id = 0;
		} catch (Throwable e) {
		}

	}

	@Override
	public void startPortal() {
		heartbeatAndUpdate();
	}

	@Override
	public void stopPortal() {
		stopBroker();

	}

	@Override
	public int getOnlineServerNum() {
		return onlineServerCount;
	}

	public void batchUpdate(List<Long> serverIds,int serverStatus){
		serverRepository.batchUpdate(serverIds,serverStatus);
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getBrokerUrlCache() {
		return cacheBrokerDataMap.get();
	}

	@Override
	public List<String> getPortalCache() {
		// TODO Auto-generated method stub
		return cachePortalDataMap.get();
	}
}
