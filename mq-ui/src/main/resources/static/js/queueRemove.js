layui.use(["table", "layer"], function () {
    var table = layui.table;

    table.render({
        elem: "#queueRemoveTable",
        height:'full',
        url:'/topic/removeQueue/list',
        page:false,
        where: {topicId: $('#topicIdV').val()},
        cols: [[
            {field: 'id', align: 'center',width:'8%', title: "队列ID"},
            {field: 'consumerGroups', align: 'center',width:'15%', title: "消费者组"},
            {field: 'dbNodeId', align: 'center',width:'10%', title: "数据库节点ID"},
            {field: 'queueReadOnly', align: 'center',width:'10%', templet:"#queueReadOnlyTpl", title: "队列读写状态"},
            {field: 'dbReadOnly', align: 'center',width:'10%', templet:"#dbReadOnlyTpl", title: "节点读写状态"},
            {field: 'readStatus', align: 'center',width:'10%', templet:"#readStatusTpl", title: "实际读写状态"},
            {field: 'leftMessage', align: 'center',width:'10%', title: "剩余消息量"},
            {field: 'isBestRemove', align: 'center',width:'10%', templet:"#bestRemoveTpl", title: "最佳移除队列"},
            {fixed: 'right', align:'center', width:290, toolbar: '#ctrlBar', title: "操作区"}
        ]]
    });

    $('body').on('click', "#refresh", function () {
        refreshAllDate();
    });

    table.on("tool(queueRemoveTable)", function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        var tr = obj.tr;
        if (layEvent === 'readOnly') {
            readOnly(data);
        } else if (layEvent === 'remove') {
            remove(data.id, $('#topicIdV').val());
        }
    });

    function readOnly(queue) {
        var isReadOnly = 2;
        layer.confirm("确定要修改队列[ "+ queue.id+" ]的读写状态？", {icon: 3, title: '确定要修改'}, function (index) {
            $.post('/queue/readOnly', 'id='+queue.id + "&isReadOnly="+isReadOnly, requestCallback);
            layer.close(index);
        });

    }

    function remove(queueId, topicId) {
        layer.confirm("该操作不可逆！确认移除队列[ "+ queueId +" ]？", {icon: 3, title: '不可逆操作！'}, function (index) {
            $.post('/topic/queue/remove', 'queueId='+queueId+"&topicId="+topicId, requestCallback);
            layer.close(index);
        });
    }

    function requestCallback(result, xhr) {
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                successBox(result.msg);
            } else {
                failBox(result.msg);
            }
            refreshAllDate();
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

    function refreshAllDate() {
        getListDate();
    }

    function getListDate() {
        table.reload("queueRemoveTable");
    }
});