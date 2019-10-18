package com.ppdai.infrastructure.ui.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageEntity;
import com.ppdai.infrastructure.mq.biz.entity.NotifyMessageStatEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageService;
import com.ppdai.infrastructure.mq.biz.service.NotifyMessageStatService;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageNotifyResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageStatNotifyResponse;

@Service
public class UiNotifyService{

    @Autowired
    private NotifyMessageService notifyMessageService;

    @Autowired
    private NotifyMessageStatService notifyMessageStatService;

    @Autowired
    private AuditLogService uiAuditLogService;

    public MessageNotifyResponse getNotifyMessageByPage(long page, long limit){
        Map<String,Object> parameter=new HashMap<>();
        Long count=notifyMessageService.count(parameter);
        List<NotifyMessageEntity> notifyMessageEntities=notifyMessageService.getList(parameter,page,limit);
        return new MessageNotifyResponse(count,notifyMessageEntities);
    }

    public MessageStatNotifyResponse getNotifyKey(long page, long limit){
        Map<String,Object> parameter=new HashMap<>();
        Long count=notifyMessageStatService.count(parameter);
        List<NotifyMessageStatEntity> notifyMessageStatEntities=notifyMessageStatService.getList(parameter,page,limit);
        return new MessageStatNotifyResponse(count,notifyMessageStatEntities);
    }

    public void updateNotifyMessageStat(long id,long notityMessageId){
        NotifyMessageStatEntity notifyMessageStatEntity=notifyMessageStatService.get(id);
        notifyMessageStatEntity.setNotifyMessageId(notityMessageId);
        notifyMessageStatService.update(notifyMessageStatEntity);
        uiAuditLogService.recordAudit(NotifyMessageStatEntity.TABLE_NAME, notifyMessageStatEntity.getId(), "更改notifyMessageStatEntity");
    }
}
