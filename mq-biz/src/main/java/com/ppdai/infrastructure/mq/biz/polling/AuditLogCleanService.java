package com.ppdai.infrastructure.mq.biz.polling;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.ppdai.infrastructure.mq.biz.dto.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.entity.AuditLogEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
/*
 * 审计日志定时清理
 */
@Component
public class AuditLogCleanService extends AbstractTimerService {
	//private Logger log = LoggerFactory.getLogger(AuditLogCleanService.class);
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private AuditLogService auditLogService;

	@PostConstruct
	private void init() {
		super.init(Constants.AUDITLOG_CLEAN, soaConfig.getAuditLogCleanInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getAuditLogCleanInterval();
			@Override
			public void run() {
				if (soaConfig.getAuditLogCleanInterval() != interval) {
					interval = soaConfig.getAuditLogCleanInterval();
					updateInterval(interval);
				}

			}
		});
	}

	@Override
	public void doStart() {
		deleteOldDate();
	}

	public void deleteOldDate(){
		long saveDayNum = soaConfig.getLogSaveDayNum();
		long minId = auditLogService.getMindId();
		while (true&&super.isMaster&&soaConfig.isEnbaleAuditLogClean()) {
			Transaction transaction = Tracer.newTransaction("mq-msg",
					"delete-log");
			transaction.setStatus(Transaction.SUCCESS);
			try {
				AuditLogEntity auditLogEntity = auditLogService.get(minId + 500);
				if (auditLogEntity != null) {
					if (auditLogEntity.getInsertTime().getTime() < System.currentTimeMillis()
							- saveDayNum * 86400000) {
						// 说明数据已经过期需要删除
						auditLogService.deleteBy(minId);
						minId = auditLogEntity.getId();
						Util.sleep(1000);
					} else {
						break;
					}
				} else {
					break;
				}

			} catch (Exception e) {
				transaction.setStatus(e);
			} finally {
				transaction.complete();
			}
		}
	}



	@Override
	@PreDestroy
	public void stopPortal() {
		super.stopPortal();
	}
}
