package com.ppdai.infrastructure.mq.biz.service.impl;

import com.ppdai.infrastructure.mq.biz.dal.meta.DicRepository;
import com.ppdai.infrastructure.mq.biz.entity.DicEntity;
import com.ppdai.infrastructure.mq.biz.service.DicService;
import com.ppdai.infrastructure.mq.biz.service.common.AbstractBaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author dal-generator
 */
@Service
public class DicServiceImpl extends AbstractBaseService<DicEntity> implements DicService {
    @Autowired
    private DicRepository dicRepository;

    @PostConstruct
    private void init() {
        super.setBaseRepository(dicRepository);
    }
}
