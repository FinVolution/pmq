package com.ppdai.infrastructure.mq.biz;

public class MqConst {
	// 1 表示error，2，warn，3，info，4，debug
	public static final int ERROR = 1;
	public static final int WARN = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;

	public static final int MAX_LENGTH = 40000;
	public static String DEFAULT_SUBENV = "default";

	public final static String MQ_SUB_ENV_KEY = "mq_sub_env";
	// public static final String HD_MQ_QUEUEID="mq-queueId";
}
