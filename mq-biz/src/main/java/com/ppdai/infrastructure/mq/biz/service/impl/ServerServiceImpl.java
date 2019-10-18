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
import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.ServerRepository;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;
import com.ppdai.infrastructure.mq.biz.service.ServerService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;

/**
 * @author dal-generator
 */
@Service
public class ServerServiceImpl extends AbstractBaseService<ServerEntity> implements ServerService, BrokerTimerService {
	private Logger log = LoggerFactory.getLogger(ServerServiceImpl.class);
	private volatile boolean isRunning = true;
	private volatile long id = 0;
	private volatile boolean isBroker=false;
	@Autowired
	private Environment env;

	@Autowired
	private SoaConfig soaConfig;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private ThreadPoolExecutor executor = null;

	private AtomicReference<List<String>> cacheBrokerDataMap = new AtomicReference<>(new ArrayList<>());
	private AtomicReference<List<String>> cachePortalDataMap = new AtomicReference<>(new ArrayList<>());
	@Autowired
	private ServerRepository serverRepository;

	@PostConstruct
	private void init() {
		super.setBaseRepository(serverRepository);
	}

	@Override
	public void startBroker() {
		if (startFlag.compareAndSet(false, true)) {
			isBroker=true;
			executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(50),
					SoaThreadFactory.create("ServerServiceImpl", true), new ThreadPoolExecutor.DiscardOldestPolicy());
			updateHeartBeat();
			executor.execute(() -> {
				while (isRunning) {
					try {
						updateHeartBeat();
					} catch (Exception e) {
						log.error("ServerServiceImpl_updateHeartBeat_error", e);
					}
					Util.sleep(soaConfig.getServerHeartbeat() * 1000);
				}
			});
		}

	}

	private void updateHeartBeat() {
		if (serverRepository.updateHeartTimeById(id) <= 0) {
			id = 0;
			doInitData();
		}
		try {
			serverRepository.deleteOld(getExpireTime());
		} catch (Exception e) {
		}
		updateCache();
	}

	private void updateCache() {
		List<String> brokerIps = new ArrayList<>();
		List<String> portalIps = new ArrayList<>();
		List<ServerEntity> lst = getList();
		lst.forEach(t1 -> {
			if (t1.getServerType() == 1 && t1.getStatusFlag() == 1) {
				brokerIps.add("http://" + t1.getIp() + ":" + t1.getPort());
			} else if (t1.getServerType() == 0) {
				portalIps.add("http://" + t1.getIp() + ":" + t1.getPort());
			}
		});
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
				serverEntity.setServerType(isBroker ? 1 : 0);
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
						int serverType = serverEntity.getServerType();
						serverEntity.setServerType(isBroker ? 1 : 0);
						// 如果当前应用类型发生改变了，则表示，当前应用的角色发生了改变，需要将状态改为初始状态
						if (serverType != serverEntity.getServerType()) {
							serverEntity.setStatusFlag(0);
						}
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
		} catch (Exception e) {
		}

	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private int getExpireTime() {
		// TODO Auto-generated method stub
		return soaConfig.getServerHeartbeat() + 20;
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
