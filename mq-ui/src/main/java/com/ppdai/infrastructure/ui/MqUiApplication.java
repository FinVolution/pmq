package com.ppdai.infrastructure.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * MqUiApplication
 *
 * @author wanghe
 * @date 2018/03/21
 */
@SpringBootApplication
@ServletComponentScan
public class MqUiApplication {
	public static void main(String[] args) {
		SpringApplication.run(MqUiApplication.class, args);
	}
}