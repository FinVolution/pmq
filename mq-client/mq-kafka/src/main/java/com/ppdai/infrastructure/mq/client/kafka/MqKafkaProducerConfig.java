package com.ppdai.infrastructure.mq.client.kafka;

import mq.org.apache.kafka.common.serialization.StringSerializer;

public class MqKafkaProducerConfig {
    /* config.put("bootstrap.servers", RadarConfig.getRadarKafkaBroker().trim());
             config.put("key.serializer", env.getProperty("radar.kafka.key.serializer", ByteArraySerializer .class.getName()));
             config.put("value.serializer", env.getProperty("radar.kafka.value.serializer", ByteArraySerializer.class.getName()));
             config.put("retries", env.getProperty("radar.kafka.retries", "0"));
             config.put("max.block.ms", env.getProperty("radar.kafka.max.block.ms", "3000"));
             config.put("acks", env.getProperty("radar.kafka.acks", "1"));
             config.put("batch.size", env.getProperty("radar.kafka.batch.size", "16384"));
             config.put("linger.ms", env.getProperty("radar.kafka.linger.ms", "1"));
             config.put("buffer.memory", env.getProperty("radar.kafka.buffer.memory", "33554432"));
             config.put("client.id", env.getProperty("radar.kafka.client.id", "producer-radar-" + IPUtil.getLocalIP()));
             config.put("producer.type", env.getProperty("radar.kafka.producer.type", "async"));
             config.put("compression.type", env.getProperty("radar.kafka.compression.type", "none"));*/
    private String bootstrapServers;
    private String keySerializer = StringSerializer.class.getName();
    private String valueSerializer = StringSerializer.class.getName();
    private String retries = "5";
    private String maxBlockMs = "30000";
    private String acks = "1";
    private String batchSize = "102400";
    private String lingerMs = "1000";
    private String bufferMemory = "33554432";
    private String clientId;
    private String producerType = "async";
    private String compressionType = "none";

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getMaxBlockMs() {
        return maxBlockMs;
    }

    public void setMaxBlockMs(String maxBlockMs) {
        this.maxBlockMs = maxBlockMs;
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(String lingerMs) {
        this.lingerMs = lingerMs;
    }

    public String getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(String bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getProducerType() {
        return producerType;
    }

    public void setProducerType(String producerType) {
        this.producerType = producerType;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(String compressionType) {
        this.compressionType = compressionType;
    }
}
