package com.ppdai.infrastructure.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.infrastructure.mq.biz.common.util.JsonUtil;
import com.ppdai.infrastructure.mq.biz.dto.client.PublishMessageResponse;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.TopicEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.TopicService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.MessageConditionRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.MessageGetByTopicRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.MessageGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.MessageToolRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageConditionResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageGetByTopicResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.MessageGetListResponse;
import com.ppdai.infrastructure.mq.client.MqClient;
import com.ppdai.infrastructure.ui.service.UiMessageService;

/**
 * @author liujianjun02
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    private Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private UiMessageService uiMessageService;
    @Autowired
    private Environment env;
    @Autowired
    private AuditLogService uiAuditLogService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private UserInfoHolder userInfoHolder;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SoaConfig soaConfig;

    @RequestMapping("/list/data")
    public MessageGetListResponse getMessageByPage(MessageGetListRequest messageGetListRequest) {
        return uiMessageService.getMessageByPage(messageGetListRequest);
    }

    @RequestMapping("/list/condition")
    public MessageConditionResponse searchCondition(MessageConditionRequest messageConditionRequest) {
        return uiMessageService.getMessageRange(messageConditionRequest);
    }

    @RequestMapping("/list/topicQueueIds")
    @ResponseBody
    public List<QueueEntity> getQueueEntity(@RequestParam(name = "topicName") String topicName) {
        List<QueueEntity> queueEntities = uiMessageService.getQueueByTopicName(topicName);
        return queueEntities;
    }

    @RequestMapping("/queue/slave")
    @ResponseBody
    public int checkQueueSlave(@RequestParam(name = "queueId") long queueId) {
        int queueSlave = uiMessageService.checkQueueSlave(queueId);
        return queueSlave;
    }

    @RequestMapping("/retry/failMessage")
    @ResponseBody
    public PublishMessageResponse sendMessage(long queueId, long messageId) {
        List<Long>ids=new ArrayList<>();
        ids.add(messageId);
        return uiMessageService.sendAllFailMessage(queueId, ids);
    }

    @RequestMapping("/tool/sendMessage")
    @ResponseBody
    public PublishMessageResponse sendMessageByTool(@RequestBody MessageToolRequest messageToolRequest)
            throws Exception {
        PublishMessageResponse publishMessageResponse = new PublishMessageResponse();
        Map<String,TopicEntity> topicMap=topicService.getCache();
        TopicEntity topicEntity=topicMap.get(messageToolRequest.getTopicName());
        int userRole=roleService.getRole(userInfoHolder.getUserId(),topicEntity.getOwnerIds());
        boolean isPro = soaConfig.isPro();
        if(isPro){
            //生产环境中，如果用户不是系统管理员，也不是topic负责人，则不能往该topic发送消息
            if(userRole!=0&&userRole!=1){
                publishMessageResponse.setCode("1");
                publishMessageResponse.setMsg("你不是topic负责人，不能发送消息");
                return publishMessageResponse;
            }
        }
        boolean result = false;
        if (messageToolRequest != null) {
            if (!StringUtils.isEmpty(messageToolRequest.getTopicName())
                    && !StringUtils.isEmpty(messageToolRequest.getMessage().getBody())) {
                    result = MqClient.publish(messageToolRequest.getTopicName(), env.getProperty("test-token", ""),
                            messageToolRequest.getMessage());
                }
       }

        //发送成功，添加审计日志
        if (result) {
            uiAuditLogService.recordAudit(TopicEntity.TABLE_NAME, topicEntity.getId(), "用户" + userInfoHolder.getUserId() + "通过管理界面往topic" +
                    topicEntity.getName() + "中发送了消息：" + JsonUtil.toJson(messageToolRequest.getMessage()));
            publishMessageResponse.setCode("0");
            publishMessageResponse.setMsg("发送成功！");
        }else{
            publishMessageResponse.setCode("1");
            publishMessageResponse.setMsg("发送失败！");
        }
        return publishMessageResponse;
    }


    @RequestMapping("/retryAll/failMessage")
    @ResponseBody
    public PublishMessageResponse retryAllFailMessage(@RequestParam("messageIds") String messageIds,long queueId) {
        List<String> consumerIdList = JSONObject.parseArray(messageIds, String.class);
        List<Long>ids=new ArrayList<>();
        for (String id:consumerIdList) {
            ids.add(Long.parseLong(id));
        }
        return uiMessageService.sendAllFailMessage(queueId, ids);
    }

    @RequestMapping("/getByTopic")
    public MessageGetByTopicResponse getMessageByTopic(@RequestBody MessageGetByTopicRequest messageGetByTopicRequest){
        return uiMessageService.getMessageByTopic(messageGetByTopicRequest);
    }

}
