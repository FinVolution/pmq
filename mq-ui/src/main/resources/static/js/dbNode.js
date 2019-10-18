layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;

    table.render({
        request: {
            name: '',
            id: '',
            ip: ''
        }
    });

    $("body").on("click", "#nodeSearchList_btn", function () {
        getListDate($("#id").val(),$("#dbName").val(),$("#isReadOnly").val(),$("#ip").val());
    });

    $('body').on("click", "#createDbNode", function () {
        showInfoModal();
    });

    $('body').on("click", "#batchDilatation", function () {
        batchDilatation();
    });

    $('body').on("click", "#createSubmit", function () {
        var component = $(this).parents("#createDbNodeForm");
        var submitArray = component.serializeArray();
        $.post("/dbNode/createOrUpdate", submitArray, requestCallback);
        return false;
    });

    $('body').on("click", "#editSubmit", function () {
        var component = $(this).parents("#editDbNodeForm");
        var submitArray = component.serializeArray();
        $.post("/dbNode/createOrUpdate", submitArray, requestCallback);
        return false;
    });

    $('body').on("click", "#dilatationBtn", function () {
        var component = $(this).parents(".layui-layer-dialog");
        var id =component.find('input[name=id]').val()
        $.post("/dbNode/dilatation", 'id='+id, requestCallback);
        return false;
    });

    $('body').on("click", "#createSqlSubmit", function () {
        var component = $(this).parents(".layui-layer-dialog");
        var quantity =component.find('input[name=quantity]').val()
        var dbNodeName =component.find('input[name=dbNodeName]').val()
        var dbNodeId =component.find('input[name=dbNodeId]').val()
        var nodeType=component.find('input[name=nodeType]').val()

        $.post('/dbNode/createSql', "name="+dbNodeName + '&quantity=' + quantity+"&nodeType="+nodeType, function(result){
            var createSql = "";
            if(result.code == yesFlag){
                var array = result.data;
                createSql = array[0];
                showCreateSqlModal();
                fillCreateSqlForm(createSql,dbNodeId,quantity);
            }else {
                failBox(result.msg);
            }
        });
        return false;
    })

    $('body').on("click", "#statusSubmit", function () {
        var component = $(this).parents(".layui-layer-dialog");
        var id =component.find('input[name=id]').val();
        var oldStatus =component.find('input[name=oldStatus]').val();
        var oldStatusTitle =component.find('input[name=oldStatusTitle]').val();
        var readOnly =component.find("input[name='readOnly']:checked").val();
        var readOnlyTitle =component.find("input[name='readOnly']:checked").attr('title');
        if(oldStatus != readOnly){
            $.post('/dbNode/beforeChange', 'id='+id + "&readOnly="+readOnly, function(result){
                if(result.code == yesFlag){
                    layer.confirm("确定将【"+oldStatusTitle+"】改为【"+readOnlyTitle+"】？", {icon: 3, title: '确认修改读写状态！'}, function (index) {
                        $.post('/dbNode/changeStatus', 'id='+id + "&readOnly="+readOnly, requestCallback);
                        layer.close(index);
                        return false;
                    });
                }else {
                    failBox(result.msg);
                }
            });
            /*var l = layer.open({
                title: '确认修改读写状态',
                content: $("#confirmChangeStatusFormDiv").html(),
                btn:['关闭']
            });
            layer.style(l, {
                width: '450px',
            });
            form.render()*/
        }
    });


    $('body').on("click", "#execute_sql", function (e) {
        var component = $(this).parents(".layui-layer-dialog");
        var dbNodeId =component.find('input[name=dbNodeId]').val();
        var quantity =component.find('input[name=quantity]').val();
        $.post('/dbNode/createTable', "dbNodeId="+dbNodeId + '&quantity=' + quantity, function(result){
            if(result.code == yesFlag){
                successBox(result.msg);
            }else {
                failBox(result.msg);
            }
        });
        return false;
    });

    $('body').on('click', '.logSearch', function () {
        var refId = $(this).attr("data-id");
        var tbName = 'db_node';
        parent.window.addTab("logSearch"+tbName + refId, '/auditLog/listPage/'+ tbName + "/" + refId, '日志查询');
    });

    table.on('tool(dbNodeTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        if (layEvent === 'editDbNode') {
            editDbNode(data);
        } else if (layEvent === "createSql") {
            createSql(data);
        } else if (layEvent === "compareDbNode") {
            compareMessageTables(data.id, data.dbName);
        } else if (layEvent === "changeStatus") {
            changeStatus(data);
        } else if (layEvent === "analyseDbNode") {
            analyse(data);
        }else if(layEvent === "createInsert"){
            createInsert(data);
        }
    });

    function createInsert(dbNode) {
        layer.open({
            type: 1,
            title: 'insert语句',
            shade: [0],
            area: ['1000px', '300px'],
            anim: 2,
            content:"insert into db_node (ip,port,db_name,db_user_name,db_pass,con_str,read_only,node_type,normal_flag)"+
                "values("+"'"+dbNode.ip+"'"+","+"'"+dbNode.port+"'"+","+"'"+dbNode.dbName+"'"+","+"'"+dbNode.dbUserName+"'"+","+"'"+dbNode.dbPass+"'"+","+"'"+dbNode.conStr+"'"+","+"'"+dbNode.readOnly+"'"
                +","+"'"+dbNode.nodeType+"'"+","+"'"+dbNode.normalFlag+"'"+")"
        });
    }

    function requestCallback(result, xhr) {
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                successBox(result.msg);
            } else {
                failBox(result.msg);
            }
            refreshAllDate();
        } else {
            failBox("网络异常！"+xhr);
        }
    }

    function requestCallback2(result, xhr) {
        $("#batchDilatation").attr("disabled",false);
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                successBox(result.msg);
            } else {
                failBox(result.msg);
            }
            refreshAllDate();
        } else {
            failBox("网络异常！"+xhr);
        }
    }

    function refreshAllDate() {
        getListDate($("#id").val(),$("#dbName").val(),$("#isReadOnly").val(),$("#ip").val());
        //countQueue();
    }

    function getListDate(id,dbName,isReadOnly,ip) {
        table.reload("dbNodeTable", {
            where: {
                id:id,
                ip : ip,
                name : dbName,
                readOnly : isReadOnly
            }
        });
    }

    function successBox(msg) {
        layer.closeAll();
        layer.msg(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }

    function editDbNode(dbNode) {
        showEditModal();
        fillForm(dbNode);
    }

    function createSql(dbNode) {
        showConfirmCreateSqlModal();
        fillConfirmCreateSqlForm(dbNode);
    }
    
    function compareMessageTables(dbNodeId, dbNodeName) {
        var quantityFromQueue = 0;
        var quantityFromDb = 0;
        $.post('/dbNode/compare', 'id='+dbNodeId + "&name="+dbNodeName, function(result){
            if(result.code == yesFlag){
                var array = result.data;
                quantityFromQueue = array[0];
                quantityFromDb = array[1];
            }
            showCompareModal();
            fillCompareForm(dbNodeId,quantityFromQueue,quantityFromDb);
        });
    }

    function changeStatus(dbNode) {
        showChangeStatusModal();
        fillChangeStatusForm(dbNode);
    }
    
    function analyse(dbNode) {
        parent.window.addTab("analyses"+ dbNode.id, '/dbNode/toAnalyse/'+ dbNode.id, '节点['+dbNode.id+']下Topic分布情况');
        /*$.post('/dbNode/analyse', 'id='+dbNode.id, function(result){
            if(result.code == yesFlag){
                var array = result.data;
                showAnalyseModal();
            }
        });*/

    }
    
    function deleteDbNode(dbNodeId) {
        layer.confirm("该操作不可逆！确认删除该节点链接？", {icon: 3, title: '不可逆操作！'}, function (index) {
            $.post('/dbNode/delete', 'id='+dbNodeId, requestCallback);
            layer.close(index);
        });
    }
    
    function showInfoModal() {
        // var l = layer.open({
        //     title: '创建数据节点',
        //     content: $("#createDbNodeFormDiv").html(),
        //     btn:[]
        // });
        // layer.style(l, {
        //     width: '500px'
        // });

        layer.open({
            type: 1,
            title: '创建数据节点',
            area: ['650px', '670px'],
            content: $("#createDbNodeFormDiv").html()
        });
        form.render();
    }

    function showEditModal() {
        // var l = layer.open({
        //     title: '编辑数据节点',
        //     content: $("#editDbNodeFormDiv").html(),
        //     btn:[]
        // });
        // layer.style(l, {
        //     width: '800px',
        //     height:'600px'
        // });

        layer.open({
            type: 1,
            title: '编辑数据节点',
            area: ['650px', '590px'],
            content: $("#editDbNodeFormDiv").html()
        });
        form.render()
    }

    function showCompareModal() {
        var l = layer.open({
            title: '队列信息对比',
            content: $("#compareMessageTablesFormDiv").html(),
            btn:[]
        });
        layer.style(l, {
            width: '400px'
        });
        form.render()
        //$(".layui-layer-dialog #compareMessageTablesForm").closest(".layui-layer-dialog").find(".layui-layer-btn0").hide();
    }

    function showConfirmCreateSqlModal() {
        var l = layer.open({
            title: '生成建表语句',
            content: $("#confirmCreateSqlFormDiv").html(),
            //btn:['关闭']
            btn:[]
        });
        layer.style(l, {
            width: '400px'
        });
        form.render();
        //$(".layui-layer-dialog #showSqlForm2").closest(".layui-layer-dialog").find(".layui-layer-btn0").hide();
    }
    
    function showCreateSqlModal() {
        var l = layer.open({
            title: 'SQL建表语句',
            content: $("#showSqlFormDiv").html(),
            btn:[]
        });
        layer.style(l, {
            width: '600px',
            //height: '800px',
        });
        form.render()
        //$(".layui-layer-dialog #showSqlForm").closest(".layui-layer-dialog").find(".layui-layer-btn0").hide();
    }
    
    function showChangeStatusModal() {
        var l = layer.open({
            title: '修改读写状态',
            content: $("#changeStatusFormDiv").html(),
            btn:[]
        });
        layer.style(l, {
            width: '480px',
        });
        form.render()
        //$(".layui-layer-dialog #changeStatusForm").closest(".layui-layer-dialog").find(".layui-layer-btn0").hide();
    }
    
    function showAnalyseModal() {
        var l = layer.open({
            title: '该节点下Topic分布',
            content: $("#analysisFormDiv").html(),
            btn:[]
        });
        layer.style(l, {
            width: '600px'
        });
        form.render()
    }
    
    function fillCompareForm(dbNodeId, quantity1, quantity2) {
        var co = $(".layui-layer-content");
        co.find("input[name='id']:not(:radio)").val(dbNodeId);
        co.find("input[name='quantityFromQueue']:not(:radio)").val(quantity1);
        co.find("input[name='quantityFromDb']:not(:radio)").val(quantity2);
        if(quantity1 == quantity2){
            co.find("div[id='dilatationDiv']").hide();
            co.find("div[id='tipDiv']").show();
        }else{
            co.find("div[id='dilatationDiv']").show();
            co.find("div[id='tipDiv']").hide();
        }
        form.render();
    }

    function fillConfirmCreateSqlForm(dbNode) {
        var co = $(".layui-layer-content");
        co.find("input[name='dbNodeId']").val(dbNode.id);
        co.find("input[name='dbNodeName']").val(dbNode.dbName);
        co.find("input[name='nodeType']").val(dbNode.nodeType);
        form.render();
    }

    function fillCreateSqlForm(createSql,dbNodeId,quantity) {
        var co = $(".layui-layer-content");
        co.find("input[name='dbNodeId']").val(dbNodeId);
        co.find("input[name='quantity']").val(quantity);
        co.find("textarea[name='showSql']").val(createSql);
        form.render();
    }
    
    function fillChangeStatusForm(dbNode) {
        var co = $(".layui-layer-content");
        co.find("input[name='id']").val(dbNode.id);
        co.find("input[name='oldStatus']").val(dbNode.readOnly);
        co.find("input[name='readOnly'][value='"+dbNode.readOnly+"']").attr('checked', true);
        var oldTitle =co.find("input[name='readOnly']:checked").attr('title');
        co.find("input[name='oldStatusTitle']").val(oldTitle);
        form.render();
    }
    
    function fillForm(dbNode) {
        var co = $(".layui-layer-content");
        $.each(dbNode, function (key, value) {
            co.find("input[name='"+ key +"']:not(:radio)").val(value);
            co.find("textarea[name='"+ key +"']:not(:radio)").val(value);
        });
        co.find("input[name='readOnly'][value='"+dbNode.readOnly+"']").attr('checked', true);
        co.find("input[name='nodeTypes'][value='"+dbNode.nodeType+"']").attr('checked', true);
        co.find("input[name='normalFlag'][value='"+dbNode.normalFlag+"']").attr('checked', true);
        form.render();
    }

    function batchDilatation() {
        debugger;
        $("#batchDilatation").attr("disabled",true);
        $.post("/dbNode/batchDilatation", requestCallback2);
        return false;
    }
});