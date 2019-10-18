layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    table.render({
        request: {
            id: '',
            ip: '',
            consumerGroupNames: '',
            sdkVersion: ''
        }
    });

    $('body').on('click',"#consumerList_btn",function(){
        var id=$("#consumerId").val();
        var ip = $("#ip").val();
        var consumerGroupNames = $("#consumerGroupNames").val();
        var sdkVersion = $("#sdkVersion").val();
        var compareType=$("#compareType").val();
        getListData(id, ip, consumerGroupNames, sdkVersion,compareType);
    });

    function getListData(id, ip, consumerGroupNames, sdkVersion,compareType) {
        table.reload("consumerTable", {
            where: {
                id: id,
                ip: ip,
                consumerGroupNames: consumerGroupNames,
                sdkVersion: sdkVersion,
                compareType:compareType
            },page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    }

    table.on('tool(consumerTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        if (layEvent === 'deleteConsumer') {
            
            layer.confirm("确认要删除该消费者？", {icon: 3, title: '不可逆操作！'}, function (index) {
                $.post('/consumer/delete', 'consumerId='+data.id, requestCallback);
                layer.close(index);
            });
        }
    });

    function requestCallback(result, xhr) {
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                layer.msg(result.msg, {icon: 1})
                getListData($("#consumerId").val(), $("#ip").val(), $("#consumerGroupNames").val(), $("#sdkVersion").val());
            } else {
                layer.alert(result.msg, {icon: 2})
            }
        } else {
            layer.alert("网络异常！"+xhr, {icon: 2})
        }
    }

});