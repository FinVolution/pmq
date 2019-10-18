package com.ppdai.infrastructure.ui.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dal.meta.MqLockRepository;
import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;
import com.ppdai.infrastructure.mq.biz.entity.MqLockEntity;
import com.ppdai.infrastructure.mq.biz.service.MqLockService;
import com.ppdai.infrastructure.mq.biz.service.impl.MqLockServiceImpl;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MqLockDeleteResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MqLockGetListResponse;

/**
 * @author wanghe
 * @date 2018/05/04
 */
@Service
public class UiMqLockService {
    @Autowired
    private MqLockRepository mqLockRepository;
    MqLockService mqLockService = null;

    @PostConstruct
    private  void init(){
        mqLockService=new MqLockServiceImpl(mqLockRepository);
    }
    public MqLockGetListResponse findBy(BaseUiRequst baseUiRequst) {
    	
        Map<String, Object> parameterMap = new HashMap<>();
        long count = mqLockService.count(parameterMap);
        List<MqLockEntity> consumerGroupList = mqLockService.getList(parameterMap,
                Long.valueOf(baseUiRequst.getPage()), Long.valueOf(baseUiRequst.getLimit()));

        return new MqLockGetListResponse(count,consumerGroupList);
    }

    public MqLockDeleteResponse delete(String lockId){
        mqLockService.delete(Long.parseLong(lockId));
        return new MqLockDeleteResponse();
    }
}
