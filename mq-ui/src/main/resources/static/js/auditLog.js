layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    table.render({
        request: {
            tbName: '',
            refId: '',
            page:'',
            limit:''
        }
    });

    $("body").on('click','#auditSearchList_btn',function(){
        getListData($("#tableName").val(),$("#refId").val());
    });

    table.on('tool(auditTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        if(layEvent=="detail"){

        }
    });

    function getListData(tbName,refId) {
        table.reload("auditTable", {
            where: {
                tbName : tbName,
                refId : refId
            }
        });
    }
})