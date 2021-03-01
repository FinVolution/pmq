package com.ppdai.infrastructure.kafka;

import com.ppdai.infrastructure.radar.biz.common.trace.Tracer;
import com.ppdai.infrastructure.radar.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.radar.biz.common.util.IPUtil;
import com.ppdai.infrastructure.radar.biz.common.util.SoaThreadFactory;
import com.ppdai.infrastructure.radar.biz.common.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.bind.PropertySourcesBinder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class KafkaProducerService implements BeanFactoryPostProcessor, PriorityOrdered, EnvironmentAware {
    private static Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    static Map<String, KafkaStringProducerHelper> kafkaMapper = new ConcurrentHashMap<>();
    static Map<String, String> kafkaBroker = new ConcurrentHashMap<>();
    private static ConfigurableEnvironment env;
    public static final int BOUND = 100;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(BOUND), SoaThreadFactory.create("mq-KafkaProducerService-" + IPUtil.getLocalIP(), true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    private static AtomicBoolean flag = new AtomicBoolean(false);

    private static void monitor() {
        if (flag.compareAndSet(false, true)) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    while (flag.get()) {
                        if (kafkaBroker.size() > 0) {
                            List<String> keys = new ArrayList<>(kafkaBroker.keySet());
                            keys.forEach(t -> {
                                if (!kafkaBroker.get(t).equalsIgnoreCase(getBootstrapServers(t))) {
                                    KafkaStringProducerHelper kafkaStringProducerHelper = kafkaMapper.get(t);
                                    kafkaMapper.put(t, create(t));
                                    kafkaBroker.put(t, getBootstrapServers(t));
                                    if (kafkaStringProducerHelper != null) {
                                        Util.sleep(5_000L);
                                        kafkaStringProducerHelper.destory();
                                    }
                                }
                            });
                            Util.sleep(10_000L);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        try {
            if (environment instanceof ConfigurableEnvironment) {
                env = (ConfigurableEnvironment) environment;
            }
        } catch (Throwable e) {

        }
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private static Random random = new Random();

    public static boolean sample(String key, String defKey) {
        try {
            int rate = getRate(key,defKey);
            if(rate<=0){
                return false;
            }
            if (rate == BOUND) {
                return true;
            }
            if (rate >= random.nextInt(BOUND)) {
                return true;
            }
            return false;
        } catch (Throwable e) {
            log.warn("sample", e);
            return true;
        }
    }

    private static int getRate(String key, String defKey) {
        try {
            String rateTemp = env.getProperty(key);
            if (Util.isEmpty(rateTemp)) {
                rateTemp = env.getProperty(defKey, "100");
            }
            Integer rate = Integer.parseInt(rateTemp);
            if (rate > BOUND) {
                return BOUND;
            }
            return rate;

        } catch (Throwable e) {

        }
        return BOUND;
    }

    public static KafkaStringProducerHelper getKafka(String module) {
        KafkaStringProducerHelper util = kafkaMapper.get(module);
        if (util == null) {
            synchronized (KafkaProducerService.class) {
                util = kafkaMapper.get(module);
                if (util == null) {
                    util = create(module);
                    kafkaBroker.put(module, getBootstrapServers(module));
                    kafkaMapper.put(module, util);
                    monitor();
                }
            }
        }
        return util;
    }

    private static String getBootstrapServers(String moudle) {
        return env.getProperty(moudle + ".bootstrapServers");
    }

    private static String getTopicFlag(String module, String topic) {
        return env.getProperty(module + "." + topic + ".flag", "1");
    }

    private static KafkaStringProducerHelper create(String moudle) {
        KafkaProducerConfig kafkaProducerConfig = new KafkaProducerConfig();
        PropertySourcesBinder propertySourcesBinder = new PropertySourcesBinder(env);
        propertySourcesBinder.bindTo(moudle, kafkaProducerConfig);
        kafkaProducerConfig.setClientId(moudle + "-producer-" + IPUtil.getLocalIP());
        return new KafkaStringProducerHelper(kafkaProducerConfig);
    }

    public static void send(String module, String topic, String key, String value) {
        Transaction transaction = Tracer.newTransaction("kafka-send-syn", module + ":" + topic);
        try {
            if ("1".equalsIgnoreCase(getTopicFlag(module, topic))) {
                if (sample(module + "." + topic + ".rate", module + ".rate")) {
                    KafkaStringProducerHelper helper = getKafka(module);
                    if (helper != null) {
                        helper.send(topic, key, value);
                    }
                }
            }
            transaction.setStatus(Transaction.SUCCESS);
        } catch (Throwable e) {
            log.warn("", e);
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
    }
    private static ThreadPoolExecutor kafkaexecutor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(300), SoaThreadFactory.create("kafkaexecutor-"+ IPUtil.getLocalIP(), true),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    public static void sendAysn(String module, String topic, String key, String value) {
        kafkaexecutor.submit(new Runnable() {
            @Override
            public void run() {
                Transaction transaction = Tracer.newTransaction("kafka-send-asyn", module + ":" + topic);
                try {
                    if ("1".equalsIgnoreCase(getTopicFlag(module, topic))) {
                        if (sample(module, topic)) {
                            KafkaStringProducerHelper helper = getKafka(module);
                            if (helper != null) {
                                helper.send(topic, key, value);
                            }
                        }
                    }
                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Throwable e) {
                    log.warn("", e);
                    transaction.setStatus(e);
                } finally {
                    transaction.complete();
                }
            }
        });
    }
    public static void remove(String module) {
        KafkaStringProducerHelper rm = kafkaMapper.remove(module);
        if (rm != null) {
            Util.sleep(5000);
            rm.destory();
            kafkaBroker.remove(module);
        }
    }

    @PreDestroy
    private void destroy() {
        flag.set(false);
        try {
            kafkaMapper.values().forEach(t -> {
                t.destory();
            });
            executor.shutdownNow();
        } catch (Throwable e) {

        }
    }
}
