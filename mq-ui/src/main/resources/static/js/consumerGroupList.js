layui.use(['element', 'table', 'jquery', 'layer', 'form','searchSelect'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;
    var element = layui.element;

    initOwnerIdsSelect2();


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
        getconsumerGroupList($("#consumerGroupName").val(),$("#appId").val(), getOwnerName(),$("#consumerGroupId").val(),$("#mode").val(),1);
    });

    $("body").on("click", "#consumerGroupSearchList_btn", function () {
        getconsumerGroupList($("#consumerGroupName").val(),$("#appId").val(), getOwnerName(),$("#consumerGroupId").val(),$("#mode").val(),1);

    });

    window.doSearch= function () {
        // table.reload('consumerGroupTable');
        getconsumerGroupList($("#consumerGroupName").val(),$("#appId").val(), getOwnerName(),$("#consumerGroupId").val(),$("#mode").val(),1);
    }

    $('body').on("click", "#createConsumerGroup", function () {
        createConsumerGroup();
    });

    $('body').on('click', '.logSearch', function () {
        var refId = $(this).attr("data-id");
        var tbName = 'consumer_group';
        parent.window.addTab("logSearch"+tbName + refId, '/auditLog/listPage/'+ tbName + "/" + refId, '日志查询');
    });

    table.on('tool(consumerGroupTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
         if (layEvent === 'edit') {
            editConsumerGroup(data)
        } else if (layEvent === "delete") {
            deleteConsumerGroup(data.id, data.name)
        }else if(layEvent==='subscribe'){
             subscribe(data)
         }
         else if(layEvent==='refreshMeta'){
             refreshMeta(data)
        }
        else if(layEvent==='rebalence'){
             rebalence(data)
         }
    });


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

    function rebalence(consumerGroup) {
        $.post("/consumerGroup/rebalence",'consumerGroupId='+consumerGroup.id, requestCallback);

    }

    function refreshMeta(consumerGroup){
        $.post("/consumerGroup/refreshMeta",'consumerGroupId='+consumerGroup.id, requestCallback);
    }
    
    function subscribe(consumerGroup) {
        var url = "consumerGroupTopic/list?consumerGroupId="+consumerGroup.id;
        parent.window.addTab(
            "dingyueguanli"+consumerGroup.id, url,consumerGroup.name+"订阅管理");
    }

    function editConsumerGroup(consumerGroup) {
        createConsumerGroup(consumerGroup);
    }


    function deleteConsumerGroup(consumerGroupId, consumerGroupName) {
        var url="consumerGroup/toDelete/?consumerGroupId="+consumerGroupId;
        parent.window.addTab("shanchu"+consumerGroupId,url,"删除"+consumerGroupName);
    }


    function getconsumerGroupList(consumerGroupName,appId, ownerNames,consumerGroupId,mode,page) {
        var option={
            url:'/consumerGroup/list/data',
            where: {
                consumerGroupName: consumerGroupName,
                appId:appId,
                ownerNames:ownerNames,
                id:consumerGroupId,
                mode:mode
            },page: {
                curr: page
            }
        };

        table.reload('consumerGroupTable',option);
    }

    function createConsumerGroup(consumerGroup){
        if (consumerGroup == undefined) {
            parent.window.addTab(
                "createConsumerGroup", "consumerGroup/toCreate/0","创建消费者组");
        } else {
            parent.window.addTab(
                "ConsumerGroup"+consumerGroup.id, "consumerGroup/toCreate/"+consumerGroup.id,"编辑"+consumerGroup.name);
        }

    }

    function requestCallback(result, xhr) {
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
        doSearch();
        layer.msg(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }


});