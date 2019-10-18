layui.use(["table", "layer"], function () {
    var table = layui.table;

    var tableins = table.render({
        elem: "#topicDeleteTable",
        height: 'full',
        url: '/queueOffset/list/data-nopage',
        page: false,
        where: {topicId: $('#topicIdV').val()},
        cols: [[
            {field: 'id', align: 'center', width: '4%', title: "ID"},
            {field: 'consumerGroupId', align: 'center', width: '8%', title: "订阅者组ID"},
            {field: 'consumerGroupName', align: 'center', width: '8%', title: "订阅者组"},
            {field: 'consumerGroupOwners', align: 'center', width: '13%', title: "订阅者组负责人"},
            {field: 'consumerGroupOwnerIds', align: 'center', width: '10%', title: "负责人账号"},
            {field: 'queueId', align: 'center', width: '6%', title: "队列ID"},
            {field: 'topicType', align: 'center', width: '8%', templet:'#topicTypeTpl', title: "队列类型"},
            {field: 'offset', align: 'center', width: '5%', title: "偏移量"},

            {field: 'consumerName', align: 'center', width: '8%', title: "消费实例名"},
            {field: 'consumerId', align: 'center', width: '8%', title: "消费实例ID"},

            {field: 'dbInfo', align: 'center', width: '22%', title: "数据库信息"}
        ]]
    });

    $("body").on("click", "#topicDel", function () {
        var topicId = $("#topicIdV").val();
        layer.confirm('删除操作不可逆，确认删除？', function(index){
            $.post('/topic/delete', 'id='+topicId, requestCallback);
            layer.close(index);
        });
    });

    $("body").on("click", '#refresh', function () {
        tableins.reload({
            where: {topicId: $('#topicIdV').val()}
        })
    });

    function requestCallback(result, xhr) {
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                parent.window.refresh1("/topic/list");
                parent.window.deleteTab("topicDelete"+$("#topicIdV").val() ,"-14");
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