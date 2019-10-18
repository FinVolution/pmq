1，需要配置proxy.xml

<?xml version="1.0" encoding="UTF-8" ?>
<messageQueue>
     <consumer groupName="****" alarmEmails="fandong@ppdai.com">
        <topics> 
            <topic name="****"  receiverType="*****.ProxySub"></topic>
            <topic name="*****"  receiverType="*****.ProxySub"></topic>
        </topics>
    </consumer>
</messageQueue>

2，需要配置健康检查地址  mq.client.proxy.hs.url=http://*********/**


3，需要配置消费地址 mq.client.proxy.exe.url=http://*********/**


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

