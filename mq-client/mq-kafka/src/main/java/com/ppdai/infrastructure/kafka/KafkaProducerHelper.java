package com.ppdai.infrastructure.kafka;


import com.ppdai.infrastructure.radar.biz.common.trace.Tracer;
import com.ppdai.infrastructure.radar.biz.common.trace.spi.Transaction;
import mq.org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerHelper<Key, Value> {
    private Logger log = LoggerFactory.getLogger(KafkaProducerHelper.class);
    private volatile Producer<Key, Value> producer;
    protected Map<String, Object> producerConfig;

    public KafkaProducerHelper(KafkaProducerConfig kafkaConfig) {
        producerConfig = new HashMap<>();
        producerConfig.put("bootstrap.servers", kafkaConfig.getBootstrapServers().trim());
        producerConfig.put("key.serializer", kafkaConfig.getKeySerializer());
        producerConfig.put("value.serializer", kafkaConfig.getValueSerializer());
        producerConfig.put("retries", kafkaConfig.getRetries());
        producerConfig.put("max.block.ms", kafkaConfig.getMaxBlockMs());
        producerConfig.put("acks", kafkaConfig.getAcks());
        producerConfig.put("batch.size", kafkaConfig.getBatchSize());
        producerConfig.put("linger.ms", kafkaConfig.getLingerMs());
        producerConfig.put("buffer.memory", kafkaConfig.getBufferMemory());
        producerConfig.put("client.id", kafkaConfig.getClientId());
        producerConfig.put("producer.type", kafkaConfig.getProducerType());
        producerConfig.put("compression.type", kafkaConfig.getCompressionType());
    }

    protected void setKeySerializer(String keySerializer) {
        producerConfig.put("key.serializer", keySerializer);
    }

    protected void setValueSerializer(String valueSerializer) {
        producerConfig.put("value.serializer", valueSerializer);
    }

    public Producer<Key, Value> get() {
        if (producer == null) {
            synchronized (this) {
                if (producer == null) {
                    this.producer = this.initialize();
                }
            }
        }
        return producer;
    }

    protected Producer<Key, Value> initialize() {
        try {
            Producer<Key, Value> producer = new KafkaProducer<>(new HashMap<>(producerConfig));
            return producer;
        }catch (Throwable e){
            return null;
        }

    }

    public void destory() {
        if (isInitialized()) {
            this.producer.flush();
            this.producer.close();
            this.producer = null;
        }
    }

    public boolean isInitialized() {
        return producer != null;
    }

    public boolean send(String topic, Key key, Value value) {
        try {
            ProducerRecord<Key, Value> record = new ProducerRecord<>(topic, key, value);
            get().send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(e!=null){
                      Transaction transaction= Tracer.newTransaction("Kafka-fail",topic);
                      transaction.setStatus(Transaction.SUCCESS);
                      transaction.addData("e",e.getMessage());
                      transaction.complete();
                    }
                }
            });
        } catch (Throwable e) {
            log.error("[radar] send access log data to kafka failed." + e.getMessage());
            return false;
        }
        return true;
    }
}
