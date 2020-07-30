package com.ppdai.infrastructure.proxy.boot;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.client.metric.MetricSingleton;




@Service
public class ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

//    @Autowired
//    private KafkaTransport transport;

    public void registerReport() {
        registerMetricReport();
    }

    private void registerMetricReport() {
        try {
        	// String metricTopic =env.getProperty("metric.kafka-topic");//
            // "framework.metric";
            // 配置Kafka地址
            // String bootstrapServers
            // =env.getProperty("metric.kafka-servers");//
            // "127.0.0.1:1092,127.0.0.1:1092,127.0.0.1:1092";

            // 初始化Kafka通道
            // KafkaTransport transport = new KafkaTransport(metricTopic,
            // bootstrapServers);
            // 初始化Kairosdb Reporter
            MqMetricReporter reporter = MqMetricReporter
                    .forRegistry(MetricSingleton.getMetricRegistry())
                    .withApplicationId("mqbroker") // 设置应用ID
                    .build();
            // 每60s上报数据
            reporter.start(30, TimeUnit.SECONDS);
            log.info("registerMetricReport_suc,初始化成功！");
        } catch (Exception e) {
            log.info("registerMetricReport_error,初始化失败！");
        }
    }
}
