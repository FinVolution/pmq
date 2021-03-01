package com.ppdai.infrastructure.kafka;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { MqKafkaConfiguration.class })
public class MqKafkaConfiguration {

}
