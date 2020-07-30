layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = parent.layer === undefined ? layui.layer : parent.layer;

     //第三种渲染方式：转换静态表格方式
    var topicReportOptions = {
        url: '/topic/report/data' //请求地址
    };
    //表格初始化
    table.init('topicReportTable',topicReportOptions);

    initSelect2();

    function initSelect2(){
        initTopic();
    }

    $("body").on("click", "#topicSearchList_btn", function () {
        getListDateWithSearch($("#topicName").val(),$("#topicExceptionType").val(),$("#queueManagementType").val(),1)
    });

    function getListDateWithSearch(topicName,topicExceptionType,queueManagementType,page) {
        var option = {
            url:'/topic/report/data',
            where: {
                name: topicName,
                topicExceptionType:topicExceptionType,
                queueManagementType:queueManagementType
            },page: {
                curr: page
            }
        };

        table.reload("topicReportTable", option);
    }

    function initTopic(){
        parent.window.initSelect2($('#topicName'),'/topic/getTopicNames');
    }

    table.on('tool(topicReportTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的DOM对象
        if (layEvent === 'expand') {
            expand(data.id, data.name)
        }else if (layEvent === 'reduce') {
            reduce(data.id, data.name)
        }
    });

    function reduce(topicId, topicName) {
        parent.window.addTab("removeQueue"+topicId, '/topic/remove/'+ topicId + '/' + topicName, '['+topicName+']消息主题缩容');
    }

    function expand(topicId, topicName) {
        parent.window.addTab("topicExpand"+topicId, '/topic/expand/'+ topicId + '/' + topicName, '['+topicName+']消息主题扩容');
    }


});