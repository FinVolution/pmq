package com.ppdai.infrastructure.mq.client.server;

import java.util.concurrent.atomic.AtomicBoolean;


import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.util.HttpClient;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.client.MqClient;


public class StatService {
    private static final Logger logger = LoggerFactory.getLogger(StatService.class);
    private static Thread thread;
    private static AtomicBoolean startFlag = new AtomicBoolean(false);
    private static HttpClient httpClient = new HttpClient(1000, 1000);

    private static MqHandler mqHandler;
    private  static Server server = null;

    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                close();
            }
        });
    }

    public static void start() {
        if (startFlag.compareAndSet(false, true)) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Util.sleep(60_1000L);
                    checkStat();

                }
            });
            thread.start();
        }
    }

    protected  static void checkStat() {
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
                    logger.warn(port + "端口启动成功");
                    server.join();
                    break;
                } catch (Throwable e) {
                    logger.warn(e.getMessage());
                    port++;
                    count++;
                    Util.sleep(1000);
                }
            }
        } catch (Throwable e) {
            // TODO: handle exception
        }
    }

    public static void close() {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Throwable e) {
            // TODO: handle exception
        }
    }
}
