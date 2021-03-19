package com.ppdai.infrastructure.mq.client.kafka;


import mq.org.apache.kafka.common.serialization.StringSerializer;

public class MqKafkaStringProducerHelper extends MqKafkaProducerHelper<String, String> {
    public MqKafkaStringProducerHelper(MqKafkaProducerConfig kafkaConfig) {
        super(kafkaConfig);
        setKeySerializer(StringSerializer.class.getName());
        setValueSerializer(StringSerializer.class.getName());
    }
}
