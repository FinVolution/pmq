layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;
    var element = layui.element;

    var maxThreadSize = $("#maxThreadSize").val();
    var maxRetryCount = $("#maxRetryCount").val();
    var maxPullBatchSize = $("#maxPullBatchSize").val();
    var minPullBatchSize = $("#minPullBatchSize").val();
    var maxDelayProcessTime = $("#maxDelayProcessTime").val();
    var maxAlarmLag = $("#maxAlarmLag").val();
    var maxDelayPullTime = $("#maxDelayPullTime").val();
    var minDelayPullTime = $("#minDelayPullTime").val();

    //为consumerGroupTopicList文件中的table进行初始化
    var consumerGroupId = $("#consumerGroupId").val();
    //第三种渲染方式：转换静态表格方式
    var consumerGroupOptions = {
        url: '/consumerGroupTopic/list/data?consumerGroupId=' + consumerGroupId //请求地址
    };
    //表格初始化
    table.init('consumerGroupTopicTable', consumerGroupOptions);

    initForm();

    $("body").on("click", "#refreshList", function () {
        table.reload("consumerGroupTopicTable", {
            where: {
            }
        });

    });

    function initForm() {
        $.get("/consumerGroupTopic/initConsumerGroupTopic", 'consumerGroupId='+consumerGroupId, function (data, xhr) {
            if (xhr === 'success') {
                if (data.code == yesFlag) {
                    initFillForm(data.data);
                }
            }
        });
    }

    function initFillForm(createConsumerGroupTopicRequest) {
        var component = $("#createConsumerGroupTopicForm");
        $.each(createConsumerGroupTopicRequest, function (key, value) {
            component.find("input[name='"+ key +"']:not(:radio)").val(value);
        });
        form.render();

    }


    $('#topicNamesSelect').select2({
        ajax: {
            url: '/topic/searchTopics',
            data: function (params) {
                var query = {
                    keyword: params.term,
                    offset:0,
                    limit:20,
                    consumerGroupName:$("#consumerGroupNamesSelect").val()
                }
                return query;
            },
            processResults: function (data, params) {
                var topics = [];
                if(data.data){
                data.data.forEach(function (topic) {
                    topics.push({
                        id: topic.id,
                        text: topic.name
                    })
                });}
                return {
                    results: topics
                }

            },
            method:"post"
        }
    });

    $('body').on('click', '.logSearch', function () {
        var refId = $(this).attr("data-id");
        var tbName = 'consumer_group';
        parent.window.addTab("logSearch"+tbName + refId, '/auditLog/listPage/'+ tbName + "/" + refId, '日志查询');
    });

    form.on('submit(createSubmit)', function () {
        var component = $("#createConsumerGroupTopicForm");
        var submitArray = component.serializeArray();

        if($("#topicNamesSelect").select2('data')[0]==null){
            layer.msg("请输入您要订阅的主题")
        }else{
            submitArray.push({name:'consumerGroupId',value:$("#consumerGroupId").val()});
            submitArray.push({name:'topicName',value: $("#topicNamesSelect").select2('data')[0].text});
            submitArray.push({name:'topicId',value:$("#topicNamesSelect").val()});
            submitArray.push({name:'topicType',value:1});
            submitArray.push({name:'originTopicName',value: $("#topicNamesSelect").select2('data')[0].text})
            layer.confirm("确认添加消费者组["+ $("#consumerGroupNamesSelect").val()+"]"+",对["+$("#topicNamesSelect").find("option:selected").text()+"]的订阅？", {icon: 3, title: '不可逆操作！'}, function (index) {
                $.post("/consumerGroupTopic/create",submitArray, requestCallback);
                layer.close(index);
            });
        }

    });

    $('#threadSize').on('input change', function () {
        var component = $("#createConsumerGroupTopicForm");
        if(parseInt($('#threadSize').val())>parseInt(maxThreadSize)){
            layer.msg("线程数不能超过"+maxThreadSize,{time:700});
        }
        // var pullValue=(parseInt($('#threadSize').val())+1)*parseInt($('#consumerBatchSize').val())
        var pullValue=(parseInt($('#threadSize').val()))*2
        pullValue=compareToMaxAndMin(pullValue,maxPullBatchSize,minPullBatchSize);

        component.find("input[name='pullBatchSize']:not(:radio)").val(pullValue);
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
        zero: function(value) {
            if(parseInt(value)==0){
                return "数量不能为0";
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
        },
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


    table.on('tool(consumerGroupTopicTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        if (layEvent === 'edit') {
            editConsumerGroupTopic(data)
        } else if (layEvent === "delete") {
            deleteConsumerGroupTopic(data)
        }
    });

    function editConsumerGroupTopic(consumerGroupTopic) {
        layer.open({
            type: 2,
            title: '编辑订阅',
            shade: [0],
            area: ['900px', '750px'],
            anim: 2,
            content: ["/consumerGroup/toEditConsumerGroupTopic?consumerGroupTopicId="+consumerGroupTopic.id , 'yes'], //iframe的url，no代表不显示滚动条
        });
    }


    function deleteConsumerGroupTopic(consumerGroupTopic) {
        $.get("/queueOffset/getByConsumerGroupTopic", 'consumerGroupTopicId='+consumerGroupTopic.id, function (data, xhr) {
            if (xhr === 'success') {
                if (data.code == yesFlag) {
                    deleteAlarm(data.data,consumerGroupTopic);
                }
            }
        });
    }

    function deleteAlarm(consumerName,consumerGroupTopic) {
        if(consumerName==undefined){
            layer.confirm("该操作不可逆！确认删除消费者组["+ consumerGroupTopic.consumerGroupName+"]"+",对["+consumerGroupTopic.topicName+"]的订阅？", {icon: 3, title: '不可逆操作！'}, function (index) {
                $.post('/consumerGroupTopic/delete', 'consumerGroupTopicId='+consumerGroupTopic.id, requestCallback);
                layer.close(index);
            });
        }else{
            layer.confirm(consumerName+"正在消费。确认删除消费者组["+ consumerGroupTopic.consumerGroupName+"]"+",对["+consumerGroupTopic.topicName+"]的订阅？", {icon: 3, title: '不可逆操作！'}, function (index) {
                $.post('/consumerGroupTopic/delete', 'consumerGroupTopicId='+consumerGroupTopic.id, requestCallback);
                layer.close(index);
            });
        }

    }


    function refreshConsumerGroupTopicList() {
        initForm();
        $('#createConsumerGroupTopicForm')[0].reset();
        $("#topicNamesSelect").empty();
        table.reload("consumerGroupTopicTable", {
            where: {
            }
        });
    }
    window.refreshConsumerGroupTopicList = refreshConsumerGroupTopicList;

    function requestCallback(result, xhr) {
        $('#topicNamesSelect').select2("val", "");
        initForm();
        refreshConsumerGroupTopicList();
        parent.window.refresh1("consumerGroup/list");
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