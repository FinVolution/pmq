package com.ppdai.infrastructure.mq.client.kafka;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import mq.org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class MqKafkaProducerHelper<Key, Value> {
    private Logger log = LoggerFactory.getLogger(MqKafkaProducerHelper.class);
    private volatile Producer<Key, Value> producer;
    protected Map<String, Object> producerConfig;

    public MqKafkaProducerHelper(MqKafkaProducerConfig kafkaConfig) {
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
        producerConfig.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,"1000000");
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
        } catch (Throwable e) {
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

    public Future<RecordMetadata> send(String topic, Key key, Value value) {
        return send(topic,key,value,null);
    }
    public Future<RecordMetadata> send(String topic, Key key, Value value,Callback callback) {
        ProducerRecord<Key, Value> record = new ProducerRecord<>(topic, key, value);
        return get().send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (e != null) {
                    Transaction transaction = Tracer.newTransaction("Kafka-fail", topic);
                    transaction.setStatus(Transaction.SUCCESS);
                    transaction.addData("e", e.getMessage());
                    transaction.complete();
                }
                if(callback!=null){
                    callback.onCompletion(recordMetadata,e);
                }
            }
        });
    }
}
