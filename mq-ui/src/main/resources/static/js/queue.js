layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = parent.layer === undefined ? layui.layer : parent.layer;
    var queueId = $("#id").val();
    var toDeleteTabId=queueId+"duxie";
    var toOpenTabId="-17";

    initTopicSelect2();

    function initTopicSelect2(){
        parent.window.initSelect2($('#topicName'),'/topic/getTopicNames');
        if (loginUserRole == 0) {
            $("#abnormalMinId").show();
        } else {
            $("#abnormalMinId").hide();
        }
    }

    $(function(){
        getListDate($("#id").val(), $("#topicName").val(),$("#dbNodeId").val(), $("#isReadOnly").val(),$("#nodeTypeId").val(),$("#distributeType").val(), '');
    });

    $('body').on('click', '.logSearch', function () {
        var refId = $(this).attr("data-id");
        var tbName = 'queue';
        parent.window.addTab("logSearch"+tbName + refId, '/auditLog/listPage/'+ tbName + "/" + refId, '日志查询');
    });

    $("body").on("click", "#queueSearchList_btn", function () {
        doSearch();
    });

    $("body").on("click", "#abnormalMinId", function () {
        searchAbnormalMinId();
    });

    function searchAbnormalMinId(){
        table.reload("queueTable", {
            url:'/queue/abnormal/minId',
            where: {
            }
        });

    }
	 
    window.doSearch = function() {
        getListDate($("#id").val(), $("#topicName").val(),$("#dbNodeId").val(), $("#isReadOnly").val(),$("#nodeTypeId").val(),$("#distributeType").val(), '');
    }


    $('body').on("click", "#queueSubmit", function(){
        var id =$("#qId").val();
        var topicId =$("#tId").val();
        if(topicId == ''){
            failBox("请选择Topic");
        }else{
            doManualExpand(topicId, id);

            layer.closeAll();
        }
    });

    function allotQueue() {
        var id =$("#qId").val();
        var topicId =$("#tId").val();
        if(topicId == ''){
            failBox("请选择Topic");
        }else{
            doManualExpand(topicId, id);
            layer.closeAll();
        }
    }

    table.on('tool(queueTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        if (layEvent === 'readOnly') {
            readOnly(data)
        } else if (layEvent === "remove") {
            removeTopic(data.topicId,data.topicName);
        } else if(layEvent == "edit"){
            showModal(data);
            //fillForm(data);
        }else if(layEvent =="repairMinId"){
            repairMinId(data)
        }
    });

    function showModal(data){
        var modal = layer.open({
            type: 2,
            title: '队列编号【'+data.id+']',
            area: ['500px', '300px'],
            content: ["/queue/queueEdit?queueId="+data.id, 'yes'],
            btn:[]
        });
        layer.style(modal, {
            width: '500px'
        });
        form.render('select');
    }

    function repairMinId(data){
        $.post('/queue/getQueueMinId', 'queueId=' + data.id,function doRepairMinId(result, xhr){
            if (xhr === 'success') {
                layer.confirm("建议把最小id修改为："+result.data, {icon: 3, title: '提示'}, function (index) {
                    $("#id").val(data.id);
                    $.post('/queue/updateMinId', 'id='+data.id + "&minId="+result.data, requestCallback);

                    layer.close(index);
                });

            } else {
                failBox("网络异常！"+xhr);
            }
        });

    }

    function fillForm(queue) {
        var co = $(".layui-layer-content");
        $.each(queue, function (key, value) {
            co.find("input[name='"+ key +"']:not(:radio)").val(value);
            co.find("textarea[name='" + key + "']:not(:radio)").val(value);
        });
    };

    function requestCallback(result, xhr) {

        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                successBox(result.msg);
            } else {
                failBox(result.msg);
            }
            parent.window.refresh1("/queue/list");
        } else {
            failBox("网络异常！"+xhr);
        }
    }

    function refreshAllDate() {
        getListDate($("#id").val(),$("#topicName").val(),$("#dbNodeId").val(),$("#isReadOnly").val(),$("#nodeTypeId").val(),$("#distributeType").val(),'');
        //countQueue();
    }

    function getListDate(id, topicName, dbNodeId, isReadOnly, nodeType,distributeType) {
        table.reload("queueTable", {
            url:'/queue/list/data',
            where: {
                id :id,
                topicName : topicName,
                readOnly : isReadOnly,
                dbNodeId : dbNodeId,
                nodeType : nodeType,
                distributeType:distributeType
            },page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    }

    function successBox(msg) {
        refreshAllDate()
        layer.msg(msg, {icon: 1})
        // parent.window.refresh1("queueOffset/list");
        // parent.window.deleteTab(toDeleteTabId,toOpenTabId);
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }

    function readOnly(queue) {
        var isReadOnly;
        /*if($("#isReadOnlyTab").text().trim() == '只读'){
            isReadOlny = 1;
        }*/
        var str = "";
        if(queue.readOnly == 2){ //只读状态
            isReadOnly = 1;
            str = "设置成读写？"
        } else if (queue.readOnly == 1) { //读写状态
            isReadOnly = 2;
            str = "设置成只读？"
        }
        layer.confirm("确定将"+queue.ip+"下<br\>数据库名: "+queue.dbName+"<br\>表名:"+queue.tbName+"<br\>队列id:"+ queue.id+ "<br\>" + "<h4>"+str+"</h4>", {icon: 3, title: '提示'}, function (index) {
            $.post('/queue/readOnly', 'id='+queue.id + "&isReadOnly="+isReadOnly, requestCallback);
            layer.close(index);
        });

    }

    function remove(dbNodeId) {
        layer.confirm("该操作不可逆！确认移除队列[ "+ dbNodeId +" ]？", {icon: 3, title: '不可逆操作！'}, function (index) {
            $.post('/queue/remove', 'id='+dbNodeId, requestCallback);
            layer.close(index);
        });
    }

    function doManualExpand(topicId, queueId) {
        $.post('/topic/manualExpand', 'id='+topicId + "&queueId=" + queueId, requestCallback );
    }

    function removeTopic(topicId,topicName) {
        var url = "/topic/remove/"+topicId+"/"+topicName;
        parent.window.addTab("removeQueue"+topicId, url, '['+topicName+']消息主题缩容');
    }

});