package com.ppdai.infrastructure.mq.biz.dto;

public class Constants {
    public static final int MAX_NUMS_OF_DISTRIBUTE_SUCCESS_QUEUE = 5;

    public static final int MAX_MSG_NUMS = 500;

    public static final int MSG_NUMS_OF_ONE_QUEUE = 100 * 10000;

    public static final int DEFAULT_NUMS_OF_QUEUE = 2;

    public static final int NUMS_OF_DISTRIBUTE_FAIL_QUEUE = 2;

    public static final String SUCCESS_CODE = "0";

    public static final String CHECK_FAIL_ERROR_CODE = "02";

    public static final String AUTH_FAIL_ERROR_CODE = "03";

    public static final String UNKNOWN_ERROR_CODE = "99";

    //每个队列每天的最大消息量
    public static final int NUMS_OF_MESSAGE_PER_QUEUE_ONEDAY = 100;

    //重平衡和缓存更新请求消息的定时清理服务
    public static final String NOTIFY_CLEAN_LOCK="mq_notify_clean_sk";
    //冗余检查服务
    public static final String REDUNDANCE_CHECK="mq_redundance_check_sk";
    //重平衡检查服务
    public static final String RB="mq_rb_sk";
    //未订阅检查服务
    public static final String NO_SUBSCRIBE="mq_noSubscribe_sk";
    //消息堆积检查服务
    public static final String MESSAGE_LANGN="mq_messageLagN_sk";
    //消息清理服务
    public static final String MESSAGE_CLEAN="mq_message_clean_sk";
    //队列空间检查服务
    public static final String QUEUE_EXPANSION="mq_queueExpansion_sk";
    //操作日志清理服务
    public static final String AUDITLOG_CLEAN="mq_auditlog_clean_sk";
    //服务端心跳异常检测服务
    public static final String NOACTIVE_CONSUMER="mq_NoActiveConsumer_sk";
}
