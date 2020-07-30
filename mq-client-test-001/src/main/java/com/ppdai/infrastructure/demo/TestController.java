package com.ppdai.infrastructure.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.mq.client.exception.ContentExceed65535Exception;
import com.ppdai.infrastructure.mq.client.exception.MqNotInitException;

import java.util.concurrent.Executors;;
@RestController
public class TestController {
	@GetMapping("/test1")
	public void test1(@RequestParam String topicName, @RequestParam int count) {
		if(Util.isEmpty(topicName))return;
		Executors.newSingleThreadExecutor().submit(new Runnable() {		
			@Override
			public void run() {
				for(int i=1;i<count;i++)
				{
					try {
						MqClient.publish(topicName, "",new ProducerDataDto(String.valueOf(i)));
					} catch (MqNotInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ContentExceed65535Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Util.sleep(10);
				}
			}
		});
	}
	
}
