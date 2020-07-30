package com.ppdai.infrastructure.mq.biz.polling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerUtil;
import com.ppdai.infrastructure.mq.biz.common.util.ConsumerUtil.ConsumerVo;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.Constants;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.common.DbService;

/**
 * 定时清理无心跳服务
 *
 */
@Service
public class NoActiveConsumerService extends AbstractTimerService {
	private static final Logger logger = LoggerFactory.getLogger(NoActiveConsumerService.class);
	private HttpClient httpClient = new HttpClient(1500, 1500);
	@Autowired
	private ConsumerService consumerService;
	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private EmailUtil emailUtil;
	@Autowired
	private DbService dbService;
	@Autowired
	private Environment env;

	@PostConstruct
	private void init() {
		super.init(Constants.NOACTIVE_CONSUMER, soaConfig.getConsumerCheckInterval(), soaConfig);
		soaConfig.registerChanged(new Runnable() {
			private volatile int interval = soaConfig.getConsumerCheckInterval();

			@Override
			public void run() {
				if (soaConfig.getConsumerCheckInterval() != interval) {
					interval = soaConfig.getConsumerCheckInterval();
					updateInterval(interval);
				}

			}
		});
	}

	@Override
	public void doStart() {
		Transaction transaction = Tracer.newTransaction("Polling", "NoActiveConsumerService");
		try {
			// 查找过期的consumer，大于心跳间隔时间 还没发送心跳
			List<ConsumerEntity> consumerList = consumerService
					.findByHeartTimeInterval(soaConfig.getConsumerInactivityTime());
			Date dbTime = dbService.getDbTime();
			if (consumerList == null || consumerList.size() < 1) {
				transaction.setStatus(Transaction.SUCCESS);
				return;
			}
			consumerList = doubleCheck(consumerList, dbTime);
			List<List<ConsumerEntity>> list = Util.split(consumerList, 10);
			for (List<ConsumerEntity> consumers : list) {
				if (isMaster()) {
					consumerService.deleteByConsumers(consumers);
				}
			}
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception ex) {
			transaction.setStatus(ex);
			String message = String.format("NoActiveConsumerService执行异常,异常信息:%s", ex.getMessage() + ex.getStackTrace());
			logger.error("NoActiveConsumerService", ex);
			emailUtil.sendErrorMail("NoActiveConsumerService", message);
		} finally {
			transaction.complete();
		}

	}

	private List<ConsumerEntity> doubleCheck(List<ConsumerEntity> consumerList, Date dbTime) {
		List<ConsumerEntity> rs = new ArrayList<>();
		consumerList.forEach(t1 -> {
			ConsumerVo consumerVo = ConsumerUtil.parseConsumerId(t1.getName());
			if (!soaConfig.isPro()) {
				rs.add(t1);
			} else {
				// consumerid老版本没有端口号或者有的不是web无端口号或者心跳时间超过5分钟没有发送心跳就认为此实例已经down了
				if (Util.isEmpty(consumerVo.port)
						|| (dbTime.getTime() - t1.getHeartTime().getTime()) > soaConfig.getMaxConsumerNoActiveTime()) {
					rs.add(t1);
				} else {
					String url = String.format("http://%s:%s/mq/client/hs", consumerVo.ip, consumerVo.port);
					if (!httpClient.check(url)) {
						rs.add(t1);
						String message = String.format("Consumer心跳异常，但是健康检查没有问题，请注意。url is %s,最后心跳时间为%s,当前时间为%s", url,
								Util.formateDate(t1.getHeartTime()), Util.formateDate(dbTime));
						emailUtil.sendWarnMail("NoActiveConsumerService", message);
					}
				}
			}
		});
		return rs;
	}

	@PreDestroy
	@Override
	public void stopPortal() {
		super.stopPortal();
	}
}