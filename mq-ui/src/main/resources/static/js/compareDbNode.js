layui.use(['element', 'table', 'jquery', 'layer', 'form','searchSelect'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;
    var element = layui.element;

    table.on('tool(compareDbNodeTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
       if (layEvent === "delete") {
            deleteDbNode(data.id,data.ip,data.port,data.dbName)
        }
    });

    function deleteDbNode(dbNodeId,ip,port,dbname) {
        layer.confirm("该操作不可逆！确认删除数据节点["+ip+"_"+port+"_"+dbname+"]", {icon: 3, title: '不可逆操作！'}, function (index) {
            $.post('/compareDbNode/delete', 'dbNodeId='+dbNodeId, requestCallback);
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
        } else {
            failBox("网络异常！"+xhr);
        }
    }

    function successBox(msg) {
        table.reload('compareDbNodeTable');
        layer.msg(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }

});