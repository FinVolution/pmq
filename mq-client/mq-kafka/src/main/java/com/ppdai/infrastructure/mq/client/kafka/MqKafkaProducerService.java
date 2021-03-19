package com.ppdai.infrastructure.mq.client.kafka;

import com.ppdai.infrastructure.mq.biz.common.thread.SoaThreadFactory;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.common.util.IPUtil;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import mq.org.apache.kafka.clients.producer.RecordMetadata;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class MqKafkaProducerService implements BeanFactoryPostProcessor, PriorityOrdered, EnvironmentAware {
    public static final String FLAG1 = "1";
    public static final String S_S_RATE = "%s.%s.rate";
    public static final String S_RATE = "%s.rate";
    private static Logger log = LoggerFactory.getLogger(MqKafkaProducerService.class);
    static Map<String, MqKafkaStringProducerHelper> kafkaMapper = new ConcurrentHashMap<>();
    static Map<String, String> kafkaBroker = new ConcurrentHashMap<>();
    private static ConfigurableEnvironment env;
    public static final int BOUND = 100;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(BOUND), SoaThreadFactory.create("KafkaProducerService-" + IPUtil.getLocalIP() + System.currentTimeMillis() % 10000, true),
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
                                    MqKafkaStringProducerHelper kafkaStringProducerHelper = kafkaMapper.get(t);
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

    //private static Random random = new Random();

    public static boolean sample(String key, String defKey) {
        return sample(key, defKey, "100");
    }

    public static boolean sample(String key, String defKey, String defValue) {
        try {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int rate = getRate(key, defKey, defValue);
            if (rate <= 0) {
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

    private static int getRate(String key, String defKey, String defValue) {
        try {
            String rateTemp = env.getProperty(key);
            if (Util.isEmpty(rateTemp)) {
                rateTemp = env.getProperty(defKey, defValue);
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

    public static MqKafkaStringProducerHelper getKafka(String module) {
        MqKafkaStringProducerHelper util = kafkaMapper.get(module);
        if (util == null) {
            synchronized (MqKafkaProducerService.class) {
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

    private static MqKafkaStringProducerHelper create(String moudle) {
        MqKafkaProducerConfig kafkaProducerConfig = new MqKafkaProducerConfig();
        PropertySourcesBinder propertySourcesBinder = new PropertySourcesBinder(env);
        propertySourcesBinder.bindTo(moudle, kafkaProducerConfig);
        kafkaProducerConfig.setClientId(moudle + "-producer-" + IPUtil.getLocalIP());
        return new MqKafkaStringProducerHelper(kafkaProducerConfig);
    }

    public static Future<RecordMetadata> send(String module, String topic, String key, String value, mq.org.apache.kafka.clients.producer.Callback callable) {
        Transaction transaction = Tracer.newTransaction("kafka-send-syn", module + ":" + topic);
        Future<RecordMetadata> rs = null;
        try {
            if (FLAG1.equalsIgnoreCase(getTopicFlag(module, topic))) {
                if (sample(String.format(S_S_RATE, module, topic), String.format(S_RATE, module))) {
                    MqKafkaStringProducerHelper helper = getKafka(module);
                    if (helper != null) {
                        rs = helper.send(topic, key, value, callable);
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
        return null;
    }

    public static Future<RecordMetadata> send(String module, String topic, String key, String value) {
        return send(module, topic, key, value, null);
    }

    private static ThreadPoolExecutor kafkaexecutor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(300), SoaThreadFactory.create("kafkaexecutor-" + IPUtil.getLocalIP(), true),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void sendAysn(String module, String topic, String key, String value) {
        kafkaexecutor.submit(new Runnable() {
            @Override
            public void run() {
                Transaction transaction = Tracer.newTransaction("kafka-send-asyn", module + ":" + topic);
                try {
                    if ("1".equalsIgnoreCase(getTopicFlag(module, topic))) {
                        if (sample(module, topic)) {
                            MqKafkaStringProducerHelper helper = getKafka(module);
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
        MqKafkaStringProducerHelper rm = kafkaMapper.remove(module);
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
