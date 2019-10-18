package com.ppdai.infrastructure.proxy.boot;


import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.proxy.HsCheckService;
import com.ppdai.infrastructure.proxy.ProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;


@Component
public class PPMqBootstrapListener implements ApplicationListener<ContextRefreshedEvent>, Ordered {
    private static final Logger log = LoggerFactory.getLogger(PPMqBootstrapListener.class);
    private static boolean isInit = false;
    @Autowired
    private ReportService reportService;
    @Autowired
    private HsCheckService hsCheckService;

    @Autowired
    private ProxyService proxyService;

    @Override
    public int getOrder() {
        // TODO Auto-generated method stub
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!isInit) {
            try {
                reportService.registerReport();
                MqClient.registerPreHandleEvent(hsCheckService);
                proxyService.start();
                isInit = true;
                log.info("mq初始化成功！");
            } catch (Exception e) {
                log.error("mq初始化异常", e);
                throw e;
            }
        }

    }

}
