package com.ppdai.infrastructure.mq.client;

import java.util.ArrayList;
import java.util.List;

import com.ppdai.infrastructure.mq.biz.common.trace.CatContext;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.util.Util;
import com.ppdai.infrastructure.mq.biz.dto.base.MessageDto;
import com.ppdai.infrastructure.mq.biz.dto.base.ProducerDataDto;

public class MessageUtil {

	public static void addCatChain(List<MessageDto> dtos) {
		try {
			MessageDto temp = dtos.get(dtos.size() - 1);
			if (temp.getHead() != null && !Util.isEmpty(temp.getHead().get(CatContext.ROOT))) {
				CatContext propertyContext = new CatContext();
				propertyContext.addProperty(CatContext.ROOT, temp.getHead().get(CatContext.ROOT));
				propertyContext.addProperty(CatContext.PARENT, temp.getHead().get(CatContext.PARENT));
				propertyContext.addProperty(CatContext.CHILD, temp.getHead().get(CatContext.CHILD));
				Tracer.logRemoteCallServer(propertyContext);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public static boolean checkMessageExceed65535(String value) {
		if (Util.isEmpty(value)) {
			return false;
		}
		return value.getBytes().length > 65535;
	}
	
	public static List<ProducerDataDto> getData(List<MessageDto> msgs){
		List<ProducerDataDto> dataDtos=new ArrayList<ProducerDataDto>(msgs.size());
		msgs.forEach(t1->{
			ProducerDataDto producerDataDto=new ProducerDataDto();
			producerDataDto.setId(t1.getId());
			producerDataDto.setBizId(t1.getBizId());
			producerDataDto.setBody(t1.getBody());
			producerDataDto.setHead(t1.getHead());
			producerDataDto.setTag(t1.getTag());
			producerDataDto.setTraceId(t1.getTraceId());
			producerDataDto.setRetryCount(t1.getRetryCount());			
			dataDtos.add(producerDataDto);
		});
		return dataDtos;
	} 
}
