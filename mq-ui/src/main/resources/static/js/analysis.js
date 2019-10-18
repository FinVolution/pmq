layui.use(["table", "layer"], function () {
    var table = layui.table;

    table.render({
        elem: "#analysisTable",
        height: 'full',
        url: '/dbNode/analyse',
        page: false,
        where: {id: $('#dbNodeId').val(), page:1, limit:10},
        cols: [[
            {field: 'topicId', align: 'center', width: '10%', title: "TopicId"},
            {field: 'topicName', align: 'center', width: '10%', title: "Topic名称"},
            {field: 'queueQuantity', align: 'center', width: '10%', title: "Queue数量"},
            {field: 'writeableQueueQuantity', align: 'center', width: '10%', title: "可写Queue数量"},
            {field: 'quantity', align: 'center', width: '10%', title: "分布节点数"},
            {field: 'dbNodeIds', align: 'center', width: '10%', title: "分布节Id"},
            {field: 'dbStr', align: 'center', width: '40%', title: "分布节具体信息"},
        ]]
    });

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
        layer.alert(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }
});