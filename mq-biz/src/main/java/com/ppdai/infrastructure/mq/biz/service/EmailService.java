package com.ppdai.infrastructure.mq.biz.service;

import com.ppdai.infrastructure.mq.biz.dto.client.SendMailRequest;

public interface EmailService {	
	void sendConsumerMail(SendMailRequest request);
	void sendProduceMail(SendMailRequest request);
}
