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
            {field: 'leftMessage', align: 'center',width:'10%', title: "剩余消息量"}
        ]]
    });

    $('body').on("click", "#autoExpand", function () {
        var topicId = $('#topicIdV').val();
        layer.confirm("是否要对该主题进行扩容操作", {icon: 3}, function (index) {
            $.post('/topic/expand', 'id='+topicId, requestCallback );
            layer.close(index);
        })

    });

    $('body').on("click", "#manualExpand", function () {
        var topicId = $('#topicIdV').val();
        parent.window.addTab("-12", "/queue/list", "队列管理");
        // layer.open({
        //     content: "扩容队列ID:<input class='layui-input' id='expandQueueId' type='number'>",
        //     btn: ['提交', '取消'],
        //     yes: function (index, layero) {
        //         layer.closeAll();
        //         doManualExpand(topicId, $("#expandQueueId").val())
        //     }
        // });
    });


    function doManualExpand(topicId, queueId) {
        $.post('/topic/manualExpand', 'id='+topicId + "&queueId=" + queueId, requestCallback );
    }

    $('body').on('click', "#refresh", function () {
        refreshAllDate();
    });


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