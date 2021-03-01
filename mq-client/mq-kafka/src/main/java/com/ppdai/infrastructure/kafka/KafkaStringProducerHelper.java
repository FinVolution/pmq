package com.ppdai.infrastructure.kafka;


import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaStringProducerHelper extends KafkaProducerHelper<String, String> {
    public KafkaStringProducerHelper(KafkaProducerConfig kafkaConfig) {
        super(kafkaConfig);
        setKeySerializer(StringSerializer.class.getName());
        setValueSerializer(StringSerializer.class.getName());


    }
}
