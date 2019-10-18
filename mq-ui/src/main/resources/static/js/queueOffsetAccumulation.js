layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;
    var element = layui.element;

    var cookie = {
        // 设置cookie方法
        set:function(key, val, time){
            // 获取当前时间
            var date = new Date();
            // 将date设置为n天以后的时间
            var expiresDays = time;
            // 格式化为cookie识别的时间timestamp
            date.setTime(date.getTime() + expiresDays * 24 * 3600 * 1000);
            document.cookie = key + "=" + val + ";expires=" + date.toGMTString();
        },

        // 获取cookie方法
        get:function(key){
            var getCookie = document.cookie.replace(/[ ]/g, "");
            var arrCookie = getCookie.split(";");
            var value;

            for(var i = 0; i < arrCookie.length; i++){
                var arr = arrCookie[i].split("=");
                if(key == arr[0]){
                    value = arr[1];
                    break;
                }
            }

            return value;
        },

        // 删除cookie方法
        delete:function(key){
            // 获取当前时间
            var date = new Date();
            // 将date设置为过去的时间
            date.setTime(date.getTime() - 10000);

            document.cookie = key + "=v; expires =" + date.toGMTString();
        }
    };

    var userRole=$('#userRole').val();

    initOwnerIdsSelect2();
    accumulationAlert();

    //如果是生产环境，则每天提示一次系统检查的结果
    $(function() {
        if($("#proEnv").val()==1){
            dataCheckAlert();
        }
    });

    function initOwnerIdsSelect2(){

        $('#ownerIds').select2({
            ajax: {
                url: '/user/search',
                dataType: 'json',
                delay: 0,
                data: function (params) {
                    return {
                        keyword: params.term ? params.term : '',
                        limit: 100,
                        offset: 0
                    }
                },
                processResults: function (data, params) {
                    var users = [{id: 0, text: '请选择'},{id: $("#userId").val(), text: $("#userId").val()+ "|" +$("#userName").val()}];
                    if (data.count > 0) {
                        $.each(data.data, function (k, v) {
                            users.push({
                                id: v.userId,
                                text: v.userId + "|" + v.name
                            })
                        });
                    }

                    return {
                        results: users
                    }

                },
                cache: true,
                minimumInputLength: 5
            }
        });

        initTopic();

        initConsumerGroup();
    }

    function getOwnerName() {
        var ownerId=$('#ownerIds').select2('data')[0];
        if(ownerId!=undefined){
            return ownerId.text.split('|')[1];
        }else{
            return '';
        }
    }

    $(function () {
        getQueueOffsetList($("#consumerGroupName").val(), $("#topicName").val(),1,$("#queueOffsetId").val(),getOwnerName(),$("#onlineType").val());
    });


    $("body").on("click", "#queueOffsetSearch_btn", function () {
        getQueueOffsetList($("#consumerGroupName").val(), $("#topicName").val(),1,$("#queueOffsetId").val(),getOwnerName(),$("#onlineType").val())
    });


    function getQueueOffsetList(consumerGroupName, topicName,page,queueOffsetId,ownerNames,onlineType) {
        table.reload("queueOffsetAccumulationTable", {
            url:'/queueOffset/accumulation/data',
            where: {
                consumerGroupName: consumerGroupName,
                topicName:topicName,
                id:queueOffsetId,
                ownerNames:ownerNames,
                onlineType:onlineType
            },
            page:page
        });
    }

    function initTopic(){
        $('#topicName').select2({
            ajax: {
                url: '/topic/getTopicNames',
                dataType: 'json',
                delay: 0,
                data: function (params) {
                    return {
                        keyword: params.term ? params.term : '',
                        limit: 100,
                        offset: 0
                    }
                },
                processResults: function (data, params) {
                    var topicNames = [{id: 0, text: '请选择'}];
                    if (data.count > 0) {
                        $.each(data.data, function (k, v) {
                            topicNames.push({
                                id: v,
                                text: v
                            })
                        });
                    }

                    return {
                        results: topicNames
                    }

                },
                cache: true,
                minimumInputLength: 5
            }
        });
    }

    function initConsumerGroup(){
        $('#consumerGroupName').select2({
            ajax: {
                url: '/consumerGroup/getConsumerGpNames',
                dataType: 'json',
                delay: 0,
                data: function (params) {
                    return {
                        keyword: params.term ? params.term : '',
                        limit: 100,
                        offset: 0
                    }
                },
                processResults: function (data, params) {
                    var consumerGroups = [{id: 0, text: '请选择'}];
                    if (data.count > 0) {
                        $.each(data.data, function (k, v) {
                            consumerGroups.push({
                                id: v,
                                text: v
                            })
                        });
                    }

                    return {
                        results: consumerGroups
                    }

                },
                cache: true,
                minimumInputLength: 5
            }
        });
    }

    function dataCheckAlert(){
        if(userRole==0){
            var userCookie=cookie.get($('#userName').val());
            if(userCookie==null||userCookie==''){
                $.post("/redundance/checkAll",dataCheckRequestCallback);
                cookie.set($('#userName').val(), 1, 1);
            }
        }

    }


    function accumulationAlert(){
        if(userRole!=0&&new Date()%2==1){
            $.post("/queueOffset/accumulationAlert",'ownerName='+$('#userName').val(), alertCallback);
        }
    }

    function alertCallback(result, xhr){
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                successBox(result.msg)
            }
        }
    }

    function dataCheckRequestCallback(result, xhr){
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                layer.open({
                    type : 1,
                    title : '系统检查',
                    shade : [ 0 ],
                    area : [ '1000px', '600px' ],
                    anim : 2,
                    content :result.data,
                });
            }
        }
    }


    function successBox(msg) {
        layer.alert(msg);
    }





});