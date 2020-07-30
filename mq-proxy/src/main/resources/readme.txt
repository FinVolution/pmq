消费接口入参
{"msgs":[{
long id;
   String topicName;
   String consumerGroupName;
   //可能为空
   String bizId;
   //可能为空
   Map<String, String> head;
   String body;
   //可能为空
   String traceId;
   String sendIp;
   // yyyy-MM-dd HH:mm:ss:SSS
   Date insertTime;},{
long id;
   String topicName;
   String consumerGroupName;
   //可能为空
   String bizId;
   //可能为空
   Map<String, String> head;
   String body;
   //可能为空
   String traceId;
   String sendIp;
   // yyyy-MM-dd HH:mm:ss:SSS
   Date insertTime;}]}

   
 出参
 {
    boolean isSuc;
   String code;
   String msg;
   List<Long> failIds;
        //代码执行时间，毫秒
       Long time;
}

