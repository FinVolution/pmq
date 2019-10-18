layui.use(["table"], function () {
    var table = layui.table;
    var tableIns = table.render({
        elem: "#auditLogTable",
        height:'full',
        url:'/auditLog/list',
        page:true, limit: 20,
        where: {refId: $('#refId').val(), tbName: $('#tbName').val(), content: $('#content').val(), id: $('#logId').val()},
        cols: [[
            {field: 'id', align: 'center',width:'8%', title: "操作ID"},
            {field: 'tbName', align: 'center',width:'10%', title: "操作主表"},
            {field: 'refId', align: 'center',width:'5%', title: "表内ID"},
            {field: 'content', align: 'center',width:'56%', title: "操作内容"},
            {field: 'insertBy', align: 'center',width:'10%', title: "操作人"},
            {field: 'insertTime', align: 'center',width:'10%', title: "操作时间"},
        ]]
    });

    $("body").on("click", "#query", function () {
        tableIns.reload({
            where:{
                refId: $('#refId').val(),
                tbName: $('#tbName').val(),
                content: $('#content').val(),
                id: $('#logId').val()
            },page: {
                curr: 1 //重新从第 1 页开始
            }
        })
    })

});