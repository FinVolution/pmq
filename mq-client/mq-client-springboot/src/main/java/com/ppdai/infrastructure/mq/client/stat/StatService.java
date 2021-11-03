package com.ppdai.infrastructure.mq.client.stat;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.client.MqClient;

@Component
public class StatService {
	private static final Logger logger = LoggerFactory.getLogger(StatService.class);
	private Thread thread;
	private AtomicBoolean startFlag = new AtomicBoolean(false);
	private HttpClient httpClient = new HttpClient(1000, 1000);
	@Autowired
	private MqHandler mqHandler;
	Server server = null;

	public void start() {
		if (startFlag.compareAndSet(false, true)) {
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					//Util.sleep(60 * 1000L);
					checkStat();

				}
			});
			thread.start();
		}
	}

	protected void checkStat() {
		try {
			String port1 = MqClient.getContext().getConfig().getServerPort();
			int port = Integer.parseInt(port1);
			String url = "http://localhost:" + port + "/mq/client/hs";
			int count = 0;
			while (count < 4) {
				if (httpClient.check(url)) {
					return;
				} else {
					count++;
					Util.sleep(1000);
				}
			}
			count = 0;
			while (count < 10) {
				try {
					server = new Server(port);
					server.setHandler(mqHandler);
					server.start();
					logger.warn(port+"端口启动成功");
					server.join();
					break;
				} catch (Exception e) {
					logger.warn(e.getMessage());
					port++;
					count++;
					Util.sleep(1000);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@PreDestroy
	public void close() {
		try {
			if (server != null) {
				server.stop();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
