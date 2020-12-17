package com.ppdai.infrastructure.ui.job;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.util.EmailUtil;
import com.ppdai.infrastructure.mq.biz.polling.AbstractTimerService;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.QueueGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.QueueReportResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueVo;
import com.ppdai.infrastructure.ui.service.UiQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.*;

@Component
public class QueueWarningInfoService extends AbstractTimerService {
    private final Logger logger = LoggerFactory.getLogger(QueueWarningInfoService.class);

    @Autowired
    UiQueueService uiQueueService;
    @Autowired
    Environment environment;
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private SoaConfig soaConfig;

    @PostConstruct
    private void init() {
        super.init("mq_queue_warning_sk", 3600, soaConfig);
    }

    @Override
    public void doStart() {
        int dataSize = Integer.parseInt(environment.getProperty("queue.warning.data.size", "5"));
        int tableRows = Integer.parseInt(environment.getProperty("queue.warning.table.row", "10000000"));
        // 每天8点发送
        Date date = new Date();
        int hours = date.getHours();
        Integer mailSendTime = Integer.parseInt(environment.getProperty("queue.warning.mail.time", "8"));
        // 提前触发,
        if (hours == (mailSendTime - 2) || hours == (mailSendTime - 1)) {
            getReport();
        } else if (hours == mailSendTime) {
            doChceck(dataSize, tableRows);
        }
    }


    private void doChceck(int dataSize, int tableRows) {
        List<QueueVo> queueDataList = uiQueueService.getQueueListAvg();
        List<QueueVo> queueForReportDataList = new ArrayList<QueueVo>();
        List<QueueVo> QueuesWithExpiredMessageList = new ArrayList<QueueVo>();

        for (QueueVo queueVo : queueDataList) {
            // 返回单表大小大于5g, 行数大于1千万的 队列
            if ((queueVo.getDataSize() > dataSize) || (queueVo.getMsgCount() > tableRows)) {
                if (queueVo != null) {
                    queueForReportDataList.add(queueVo);
                }
            }
            // 找出消息过期的队列
            if (queueVo.getIsException() == 1) {
                QueuesWithExpiredMessageList.add(queueVo);
            }
        }

        Context context = new Context();
        // 如果消息库中最早的一条消息的插入日期，加上消息的保存天数，比今天的日期大，则为异常。
        context.setVariable("totalNumber", queueForReportDataList.size());
        context.setVariable("QueuesWithExpiredMessagesNumber", QueuesWithExpiredMessageList.size());
        context.setVariable("queueForReportDataList", queueForReportDataList);
        context.setVariable("QueuesWithExpiredMessageList", QueuesWithExpiredMessageList);
        logger.info(soaConfig.getEnvName() + "- MQ单表容量告警邮件开始发送");
        StringWriter content = new StringWriter();
        templateEngine.process("queue/queueReportEmail", context, content);

        String revConfig = environment.getProperty("queue.report.receivers");
        Set<String> receivers = StringUtils.commaDelimitedListToSet(revConfig);
        EmailUtil.EmailVo email = new EmailUtil.EmailVo();
        email.setContent(content.toString());
        email.setRev(new ArrayList<>(receivers));
        email.setTitle(soaConfig.getEnvName()  + "- MQ单表容量告警");
        email.setSenderName("MQ队列告警信息");
        boolean result = emailUtil.sendImmediately(email, 3);

        if (!result) {
            logger.info(soaConfig.getEnvName()  + "- MQ单表容量告警, 发送告警失败!!");
        }
        logger.info(soaConfig.getEnvName()  + "- MQ单表容量告警, 邮件发送成功");
    }

    private void getReport() {
        QueueGetListRequest queueGetListRequest = new QueueGetListRequest();
        queueGetListRequest.setPage("1");
        queueGetListRequest.setLimit("20");
        uiQueueService.getQueueForReport(queueGetListRequest, "");
    }

}
