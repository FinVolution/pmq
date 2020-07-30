package com.ppdai.infrastructure.mq.biz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Gauge;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.metric.MetricSingleton;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessage;
import com.ppdai.infrastructure.mq.biz.common.trace.TraceMessageItem;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.SpringUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dal.meta.MqLockRepository;
import com.ppdai.infrastructure.mq.biz.entity.MqLockEntity;
import com.ppdai.infrastructure.mq.biz.service.MqLockService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;
import com.ppdai.infrastructure.mq.biz.service.common.DbService;

/**
 * @author dal-generator
 */
public class MqLockServiceImpl extends AbstractBaseService<MqLockEntity> implements MqLockService {

    private Logger log = LoggerFactory.getLogger(MqLockServiceImpl.class);
    private String ip;
    private String key = "soa_clean_sk";
    private volatile boolean flag = false;
    private volatile long id = 0;
    // 对象初始化完成
    private volatile boolean objInit = false;
    private volatile Object lockObj = new Object();
    private volatile boolean isMaster = false;
    // @Autowired
    private SoaConfig soaConfig;
    private MqLockRepository mqLockRepository;
    private DbService dbService;
    private EmailUtil emailUtil;
    private HeartbeatProperty heartbeatProperty;
//    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<Runnable>(200), SoaThreadFactory.create("MqLockService", true),
//            new ThreadPoolExecutor.DiscardOldestPolicy());
    private TraceMessage traceMessage = null;
    

    public MqLockServiceImpl(MqLockRepository repository) {
        this.mqLockRepository = repository;
        setBaseRepository(repository);
    }

    public MqLockServiceImpl(String key) {
        this.key = key;
        this.emailUtil = SpringUtil.getBean(EmailUtil.class);
        this.traceMessage = TraceFactory.getInstance("lock-" + key);
        this.heartbeatProperty = new HeartbeatProperty() {
            @Override
            public int getValue() {
                return soaConfig.getMqLockHeartBeatTime();
            }
        };
    }

    private EmailUtil getEmail() {
        if (emailUtil == null) {
            emailUtil = SpringUtil.getBean(EmailUtil.class);
        }
        return emailUtil;
    }

    public MqLockServiceImpl(String key, int soaLockHeartTime) {
        this.key = key;
        this.heartbeatProperty = new HeartbeatProperty() {
            @Override
            public int getValue() {
                if (soaLockHeartTime < 5) {
                    return 5;
                }
                return soaLockHeartTime;
            }
        };
    }

    public MqLockServiceImpl(String key, HeartbeatProperty heartbeatProperty) {
        this.key = key;
        this.heartbeatProperty = heartbeatProperty;
    }

    // 检查SoaLockRepository和SoaConfig是否注入
    private boolean isLoad() {
        if (objInit) {
            return true;
        }
        if (mqLockRepository == null) {
            mqLockRepository = SpringUtil.getBean(MqLockRepository.class);
            super.setBaseRepository(mqLockRepository);
        }
        if (soaConfig == null) {
            soaConfig = SpringUtil.getBean(SoaConfig.class);
        }
        if (dbService == null) {
            dbService = SpringUtil.getBean(DbService.class);
        }
        if (emailUtil == null) {
            emailUtil = SpringUtil.getBean(EmailUtil.class);
        }
        objInit = (mqLockRepository != null && soaConfig != null && dbService != null && emailUtil != null);
        return objInit;
    }

    private void init() {
        if (!flag) {
            synchronized (lockObj) {
                if (!flag) {
                    flag = true;
                    ip = String.format("%s_%s_%s", IPUtil.getLocalIP().replaceAll("\\.", "_"), Util.getProcessId(),
                            System.currentTimeMillis() % 10000);
                    if (!clearOld()) {
                        Util.sleep(getExpired() * 1000);
                    }
                    clearAndInit();
                    //initHeartBeat();
                    initMetric();
                }
            }
        }
    }

    private void initMetric() {
        MetricSingleton.getMetricRegistry().register(key + ".Count", new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return isMaster ? 1 : 0;
            }
        });
    }

//    private void initHeartBeat() {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        if (isMaster) {
//                            updateHeatTime();
//                        }
//                    } catch (Exception e) {
//                        log.error("doHearBeatError", e);
//                    }
//                    Util.sleep(getHeartBeatTime() * 1000);
//                }
//            }
//        });
//    }

    private void clearAndInit() {
        try {
            Map<String, Object> mapCond = new HashMap<>();
            mapCond.put(MqLockEntity.FdKey1, key);
            MqLockEntity entity = mqLockRepository.get(mapCond);
            if (entity == null) {
                // 保证数据库中有一条记录
                insert();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private boolean clearOld() {
        return mqLockRepository.deleteOld(key, getExpired()) > 0;
    }

    private int getExpired() {
        return getHeartBeatTime() * 2 + 3;
    }

    private void insert() {
        MqLockEntity entity = new MqLockEntity();
        entity.setIp(ip);
        entity.setKey1(key);
        mqLockRepository.insert1(entity);
    }

    // @Transactional(rollbackFor = Exception.class)
    public boolean isMaster() {
        try {
            if (!isLoad()) {
                return false;
            }
            init();
            boolean temp = checkMaster();
            if (temp != isMaster) {
                isMaster = temp;
                if (temp) {
                    log.info("ip_{}_key_{} 获取到master!", ip, key);
                    EmailUtil emailUtil = getEmail();
                    if (emailUtil != null) {
                        emailUtil.sendWarnMail("锁发生变更" + key, String.format("ip_%s_key_%s 获取到master!", ip, key));
                    }
                } else {
                    log.info("ip_{}_key_{} 失去master!", ip, key);
                    EmailUtil emailUtil = getEmail();
                    if (emailUtil != null) {
                        emailUtil.sendWarnMail("锁发生变更" + key, String.format("ip_%s_key_%s 失去master!", ip, key));
                    }
                }
            }
            isMaster = temp;
            return isMaster;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInLock() {
		boolean flag=true;
		if(!Util.isEmpty(soaConfig.getLockWhiteIps(key))){
			flag=soaConfig.getLockWhiteIps(key).indexOf(IPUtil.getLocalIP()) != -1;
		}
		else if(!Util.isEmpty(soaConfig.getLockBlackIps(key))) {
			flag=soaConfig.getLockBlackIps(key).indexOf(IPUtil.getLocalIP()) == -1;
		}		
		return flag;
	}

	private boolean checkMaster() {
		if (isInLock()) {
			return doCheckMaster();
		} else {
			return false;
		}
	}

	private boolean doCheckMaster() {
        Map<String, Object> mapCond = new HashMap<>();
        mapCond.put(MqLockEntity.FdKey1, key);
        MqLockEntity entity = mqLockRepository.get(mapCond);
        if (entity == null) {
            clearAndInit();
            entity = mqLockRepository.get(mapCond);
        }
        // Date dbNow=util.getDbNow();
        Date dbNow = dbService.getDbTime();
        TraceMessageItem item = new TraceMessageItem();
        id = entity.getId();
        // 注意比较的时候，此时单位是毫秒
        if (entity.getHeartTime().getTime() < dbNow.getTime() - getExpired() * 1000) {
            // clearAndInit();
            // 根据受影响条数争夺分配锁,单位是秒
            Integer count = mqLockRepository.updateHeartTimeByKey1(ip, key, getExpired());
            boolean flag1 = count > 0;
            item.status = "master-1";
            return flag1;
        } else {
            item.status = "master-2";
            return checkMaster(entity, dbNow, item);
        }
    }

    private boolean checkMaster(MqLockEntity entity, Date dbNow, TraceMessageItem item) {
        boolean flag1 = entity.getIp().equals(ip);
        item.msg = entity.getHeartTime().getTime() + "-" + dbNow.getTime() + "-" + getExpired() * 1000 + "-" + flag1
                + "-" + ip + "-" + key;
        traceMessage.add(item);
        return flag1;
    }

    // @Transactional(rollbackFor = Exception.class)
    public boolean updateHeatTime() {
        if (!isLoad()) {
            return false;
        }
        return mqLockRepository.updateHeartTimeByIdAndIp(id, ip) > 0;
    }

    private int getHeartBeatTime() {
        try {
            return this.heartbeatProperty.getValue();
        } catch (Exception e) {
            return soaConfig.getMqLockHeartBeatTime();
        }
    }

    public interface HeartbeatProperty {
        int getValue();
    }
}
