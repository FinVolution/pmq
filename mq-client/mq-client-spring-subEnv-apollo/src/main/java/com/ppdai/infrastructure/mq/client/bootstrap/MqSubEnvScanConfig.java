package com.ppdai.infrastructure.mq.client.bootstrap;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { MqSubEnvScanConfig.class })
public class MqSubEnvScanConfig {
	
}
