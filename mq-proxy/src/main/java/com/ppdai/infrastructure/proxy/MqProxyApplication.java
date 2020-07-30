package com.ppdai.infrastructure.proxy;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyRequest;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyResponse;

@SpringBootApplication
@RestController
public class MqProxyApplication {

	@GetMapping("/hs")
	public String hs() {
		return "OK";
	}

	@RequestMapping("/proxy")
	public ProxyResponse proxy(@RequestBody ProxyRequest request) {
		System.out.println(JsonUtil.toJson(request));
		return new ProxyResponse();
	}


	public static void main(String[] args) {
		
		SpringApplication.run(MqProxyApplication.class, args);
	}

	@GetMapping("/proxySend")
	public void proxySend() {
		
	}
}