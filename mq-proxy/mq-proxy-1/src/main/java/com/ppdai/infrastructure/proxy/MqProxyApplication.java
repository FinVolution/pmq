package com.ppdai.infrastructure.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyRequest;
import com.ppdai.infrastructure.mq.biz.dto.proxy.ProxyResponse;
import com.ppdai.infrastructure.mq.client.MqClient;

@SpringBootApplication
@RestController
public class MqProxyApplication {		
	
	@GetMapping("/hs")
	public String hs(){
		return "OK";
	}
	@RequestMapping("/proxy")
	public ProxyResponse proxy(@RequestBody ProxyRequest request){
		return new ProxyResponse();
	}
	
	public static void main(String[] args) {		
		SpringApplication.run(MqProxyApplication.class, args);
	}

	@GetMapping("/proxySend")
	public void proxySend() {		
		for (int j = 0; j < 1000000; j++) {
			try {
				StringBuilder sr = new StringBuilder();
				for (int i = 0; i < 2000; i++) {
					sr.append("1");
				}
				MqClient.publish("testProxy","", new ProducerDataDto("test", sr.toString()));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}