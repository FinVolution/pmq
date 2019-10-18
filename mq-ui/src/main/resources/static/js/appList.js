layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table,
        form = layui.form,
        layer = layui.layer,
        $ = layui.$,
        element = layui.element;

    ////为app的detail.html文件中的table进行初始化
    var instanceId=$("#instanceId").val();
    //第三种渲染方式：转换静态表格方式
    var tableOptions = {
        url:'/app/detail/data?instanceId='+instanceId //请求地址
    };
    //表格初始化
    table.init('appTableId', tableOptions);




    /**查询*/
    $(".appSearchList_btn").click(function () {
        table.reload('appTableId', {
            where: {//请求参数
                searchAppName:$("#appName").val()
            }
        });
    });

    /**监听工具条*/
    table.on('tool(appTableId)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
        var updateResult;
        //如果点击了更新按钮
        if (layEvent === 'servUpdate') {
            layer.open({
                title: '信息确认',
                id: data.id + 1,
                type: 1,
                content: '<div style="padding: 20px 100px;">'
                + '确认要<span class="warn1">'
                + '更新版本'
                + '</span>吗？'
                + '</div>',
                btn: '确认',
                btnAlign: 'r' //按钮居中
                ,
                shade: 0 //不显示遮罩
                ,
                yes: function () {
                    //这里一般是发送修改的Ajax请求
                    $.ajax({
                        url: '/app/update/version',
                        type: 'post',
                        async: false,
                        data: {
                            "appId": data.id
                        },
                        success: function (result) {
                            updateResult = result;
                            table.reload('appTableId', {
                                where: {//请求参数
                                    searchAppName:$("#appName").val()
                                }
                            });
                        },
                        error: function (result) {
                            updateResult=result
                        }
                    });
                    layer.closeAll();
                    if (updateResult == '"true"') {
                        layer.msg("更新成功")
                    } else if(updateResult == '"false"'){
                         layer.msg("更新异常")
                    }else{
                        layer.msg("会话过期，请重新登录")
                    }
                }
            });
        }

    });


});