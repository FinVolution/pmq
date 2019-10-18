layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = parent.layer === undefined ? layui.layer : parent.layer;
    var $ = layui.$;
    var element = layui.element;

    var maxThreadSize = $("#maxThreadSize").val();
    var maxRetryCount = $("#maxRetryCount").val();
    var maxPullBatchSize = $("#maxPullBatchSize").val();
    var minPullBatchSize = $("#minPullBatchSize").val();
    var maxDelayProcessTime = $("#maxDelayProcessTime").val();
    var maxAlarmLag = $("#maxAlarmLag").val();
    var maxConsumerBatchSize=$("#maxConsumerBatchSize").val();
    var maxDelayPullTime = $("#maxDelayPullTime").val();
    var minDelayPullTime = $("#minDelayPullTime").val();

    form.render();

    initForm();

    form.on('submit(editSubmit)', function () {
        var component = $("#toEditConsumerGroupTopicForm");
        var submitArray = component.serializeArray();
        $.post("/consumerGroupTopic/edit",submitArray, requestCallback);
        return false;
    });

    function initForm() {
        var consumerGroupTopicId=$("#consumerGroupTopicId").val();
        $.get("/consumerGroupTopic/getById", 'consumerGroupTopicId='+consumerGroupTopicId, function (data, xhr) {
            if (xhr === 'success') {
                if (data.code == 0) {
                    initFillForm(data.data);
                }
            }
        });
    }

    function initFillForm(consumerGroupTopic) {
        var component = $("#toEditConsumerGroupTopicForm");
        $.each(consumerGroupTopic, function (key, value) {
            component.find("input[name='"+ key +"']:not(:radio)").val(value);
        });
        form.render();

    }

    $('#threadSize').on('input change', function () {
        var component = $("#toEditConsumerGroupTopicForm");
        if(parseInt($('#threadSize').val())>parseInt(maxThreadSize)){
            layer.msg("线程数不能超过"+maxThreadSize,{time:700});
        }
        // var pullValue=(parseInt($('#threadSize').val())+1)*parseInt($('#consumerBatchSize').val())
        var pullValue=(parseInt($('#threadSize').val()))*2
        pullValue=compareToMaxAndMin(pullValue,maxPullBatchSize,minPullBatchSize);

        component.find("input[name='pullBatchSize']:not(:radio)").val(pullValue);
    });


    $('#consumerBatchSize').on('input change', function () {
        var component = $("#toEditConsumerGroupTopicForm");

        if(parseInt($('#consumerBatchSize').val())>parseInt(maxConsumerBatchSize)){
            layer.msg("批量处理条数不能超过"+maxConsumerBatchSize,{time:700})
        }

        var pullValue=(parseInt($('#threadSize').val())+1)*parseInt($('#consumerBatchSize').val())

        var finalValue=compareToMaxAndMin(pullValue,maxPullBatchSize,minPullBatchSize);
        component.find("input[name='pullBatchSize']:not(:radio)").val(finalValue);
    });

    function compareToMaxAndMin(pullValue,maxPullBatchSize,minPullBatchSize) {
             if(pullValue>maxPullBatchSize){
                 return maxPullBatchSize;
             }else if (pullValue<minPullBatchSize){
                 return minPullBatchSize;
             }else{
                 return pullValue;
             }
    }

    form.verify({
        emailList: [
            /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+(,([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+)*$/,
            "邮箱格式不正确！英文逗号间隔多个，无空格。"
        ],
        maxConsumerSize: function(value){
            if(parseInt(value)>parseInt(maxConsumerBatchSize)){
                return "批量处理条数最多"+maxConsumerBatchSize;
            }
        },
        minConsumerBatchSize: function(value) {
            if(parseInt(value)<1){
                return "批量处理条数不能小于1";
            }
        },
        negative: function(value) {
            if(parseInt(value)<0){
                return "不能输入负数";
            }
        },
        thread: function(value) {
            if(parseInt(value)>parseInt(maxThreadSize)){
                return "线程数最多"+maxThreadSize;
            }
        },
        batch: function(value) {
            if(parseInt(value)>parseInt(maxPullBatchSize)){
                return "批量拉取条数最多"+maxPullBatchSize;
            }
        },
        minBatch: function(value){
            if(parseInt(value)<parseInt(minPullBatchSize)){
                return "批量拉取条数不能小于"+minPullBatchSize;
            }
        },
        zero: function(value) {
            if(parseInt(value)==0){
                return "线程数不能为0";
            }
        },
        retry:function(value){
            if(parseInt(value) >parseInt(maxRetryCount)){
                return "重试次数最多："+maxRetryCount;
            }
        },
        maxLag:function(value){
            if(parseInt(value)>parseInt(maxAlarmLag)){
                return "告警阈值最多："+maxAlarmLag;
            }
        },
        maxDelay:function(value){
            if(parseInt(value)>parseInt(maxDelayProcessTime)){
                return "延迟处理时间最多："+maxDelayProcessTime;
            }
        }
        ,
        maxPullDelay:function(value){
            if(parseInt(value)>parseInt(maxDelayPullTime)){
                return "等待拉取时间最多："+maxDelayPullTime;
            }
        },
        minPullDelay:function(value){
            if(parseInt(value)<parseInt(minDelayPullTime)){
                return "等待拉取时间最少："+minDelayPullTime;
            }
        }

    });

    function requestCallback(result, xhr) {
        layer.closeAll();
        parent.window.refreshConsumerGroupTopicList();

        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                successBox(result.msg);
            } else {
                failBox(result.msg);
            }
        } else {
            failBox("网络异常！"+xhr);
        }
    }

    function successBox(msg) {

        layer.msg(msg, {icon: 1})
    }

    function failBox(msg) {

        layer.alert(msg, {icon: 2})
    }
});