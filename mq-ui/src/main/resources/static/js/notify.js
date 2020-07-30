layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table,
        form = layui.form,
        layer = layui.layer,
        $ = layui.$,
        element = layui.element;


    //监听单元格编辑
    table.on('edit((notifyKeyTableId)', function(obj){
        var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field
            ,layEvent = obj.event;//得到字段
        layer.msg('[ID: '+ data.id +'] ' + field + ' 字段更改为：'+ value);
        if (layEvent === 'edit'){
            showModal(data);
        }
    });

    function showModal(data){
        var modal = layer.open({
            type: 2,
            title: '编辑',
            area: ['500px', '300px'],
            content: "<p>编辑消息ID</p>",
            btn:[]
        });
        layer.style(modal, {
            width: '500px'
        });
        //form.render();
    }


    $("body").on("click", "#lockSearchList_btn", function () {
        getNotifyList()
    });

    function getNotifyList() {
        var option={
            page: {
                curr: 1
            }
        };
        table.reload('notifyKeyTableId',option);
        table.reload('notifyTableId',option);
    }
});