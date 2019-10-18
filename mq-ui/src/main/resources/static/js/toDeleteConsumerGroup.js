layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;
    var element = layui.element;

    //为toDeleteConsumerGroup文件中的table进行初始化
    var consumerGroupName = $("#consumerGroupName").val();
    var consumerGroupId = $("#consumerGroupId").val();
    //第三种渲染方式：转换静态表格方式
    var consumerGroupOptions = {
        url: '/queueOffset/list/data?consumerGroupName=' + consumerGroupName //请求地址
    };

    var deleteConsumerGroupTabId="shanchu"+consumerGroupId
    var toOpenTabId="-15";

    //表格初始化
    tableRefresh();

    $("body").on("click", "#consumerGroupDel", function () {
        layer.confirm("该操作不可逆！确认删除消费者组["+ consumerGroupName+"]", {icon: 3, title: '不可逆操作！'}, function (index) {
            $.post('/consumerGroup/delete', 'consumerGroupId='+consumerGroupId, requestCallback);
            layer.close(index);
        });
    });

    $("body").on("click", "#refresh", function () {
        tableRefresh();
    });

    function tableRefresh() {
        table.init('toDeleteConsumerGroupTable', consumerGroupOptions);
    }

    function requestCallback(result, xhr) {
        tableRefresh();
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
        parent.window.refresh1("consumerGroup/list");
        parent.window.deleteTab(deleteConsumerGroupTabId,toOpenTabId);
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }


});