layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = parent.layer === undefined ? layui.layer : parent.layer;

     //第三种渲染方式：转换静态表格方式
    var queueReportOptions = {
        url: '/queue/report/data' //请求地址
    };
    //表格初始化
    table.init('queueReportTable',queueReportOptions);

    initSelect2();

    function initSelect2(){
        initTopic();
    }

    $("body").on("click", "#queueSearchList_btn", function () {
        doSearch();
    });

    function doSearch(){
        getListDate($("#topicName").val(),$("#nodeTypeId").val(),$("#sortTypeId").val(),$("#isException").val(),$("#ip").val());
    }

    function getListDate(topicName,nodeType,sortTypeId,isException,ip) {
        table.reload("queueReportTable", {
            where: {
                topicName : topicName,
                nodeType : nodeType,
                sortTypeId:sortTypeId,
                isException:isException,
                ip:ip
            },page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    }

    function initTopic(){
        parent.window.initSelect2($('#topicName'),'/topic/getTopicNames');
    }

});