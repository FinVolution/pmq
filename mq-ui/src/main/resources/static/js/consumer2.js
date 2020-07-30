layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    table.render({
        request: {
            ip: '',
            consumerGroupNames: '',
        }
    });

    $('body').on('click',"#consumerList_btn",function(){
        var ip = $("#ip").val();
        var consumerGroupNames = $("#consumerGroupNames").val();
        getListData(ip, consumerGroupNames);
    });

    function getListData(ip, consumerGroupNames) {
        table.reload("consumer2Table", {
            where: {
                ip: ip,
                consumerGroupNames: consumerGroupNames
            },page: {
                curr: 1
            }
        });
    }

    table.on('tool(consumer2Table)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        // if (layEvent === 'deleteConsumer') {
        //     layer.confirm("确认要删除该消费者？", {icon: 3, title: '不可逆操作！'}, function (index) {
        //         $.post('/consumer2/delete', 'consumers='+JSON.stringify(data), requestCallback);
        //         layer.close(index);
        //     });
        // }
    });


    //头工具栏事件
    table.on('toolbar(consumer2Table)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        if(obj.event=='registerAll'){
            var data = checkStatus.data;
            var data1=[];
            // table 行中的checkbox 会被选中，但是因为layui的原因，没有权限的checkbox被隐藏了，所有发送给后端的时候，需要过滤掉隐藏的没有权限的消费者
            for(var i=0;i<data.length;i++){
            	// 只有role是0和1才能被选中重新注册
            	if(data[i].role==1||data[i].role==0){
            		data1.push(data[i]);
            	}
            }
            if(data1.length==0){
            	 layer.alert("没有权限！");
            	 return;
            }            
            //return;
            layer.confirm("确认要删除该消费者？", {icon: 3, title: '不可逆操作！'}, function (index) {
                $.post('/consumer2/delete', 'consumers='+JSON.stringify(data1), requestCallback);
                layer.close(index);
            });
        }
    });


    function requestCallback(result, xhr) {
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                layer.msg(result.msg, {icon: 1})
                getListData($("#id").val(), $("#consumerGroupNames").val());
            } else {
                layer.alert(result.msg, {icon: 2})
            }
        } else {
            layer.alert("网络异常！"+xhr, {icon: 2})
        }
    }

});