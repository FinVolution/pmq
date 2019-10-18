package com.ppdai.infrastructure.mq.biz.common.trace.internals.cat;


public interface CatNames {
  String CAT_CLASS = "com.dianping.cat.Cat";
  String LOG_ERROR_METHOD = "logError";
  String LOG_EVENT_METHOD = "logEvent";
  String NEW_TRANSACTION_METHOD = "newTransaction";
  String GET_MANAGER_METHOD = "getManager";
  String GET_DOMAIN_METHOD = "getDomain";
  String CAT_TRANSACTION_CLASS = "com.dianping.cat.message.Transaction";
  String DEFAULT_DEFAULTTRANSACTION="com.dianping.cat.message.internal.DefaultTransaction";
  
  String CAT_MESSAGEMANAGER_CLASS="com.dianping.cat.message.spi.MessageManager";
  String GET_MESSAGEMANAGER_METHOD="getManager";
  String SET_STATUS_METHOD = "setStatus";
  String GET_STATUS_METHOD = "getStatus";
  
  String GET_TYPE_METHOD = "getType";
  
  String GET_NAME_METHOD = "getName";
  
  String RESET_METHOD="reset";
  String ADD_DATA_METHOD = "addData";
  String COMPLETE_METHOD = "complete";
  
  String CAT_CONTEXT_CLASS = "com.dianping.cat.Cat$Context";
  String LOG_REMOTE_CALL_CLIENT = "logRemoteCallClient";
  String LOG_REMOTE_CALL_SERVER = "logRemoteCallServer";
}
