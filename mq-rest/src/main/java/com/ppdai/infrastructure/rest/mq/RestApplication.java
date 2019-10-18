package com.ppdai.infrastructure.rest.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ServletComponentScan
@RestController
public class RestApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}
}
