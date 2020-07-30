 layui.use(['element', 'table', 'jquery', 'layer', 'form', 'searchSelect'], function () {
    var table = layui.table;
    var form = layui.form;
    var searchSelect = layui.searchSelect;
    var createLayer;
    searchSelect.render();
    var element = layui.element;

     initOwnerIdsSelect2();

     $(function () {
         getListDateWithSearch($("#topicName").val(), $("#topicId").val(), getOwnerName() ,$("#topicType").val(),1);
     });

    $('body').on("click", "#createTopic", function () {
        showInfoModal();
    });


     function initOwnerIdsSelect2(){

         $('#ownerIds').select2({
             ajax: {
                 url: '/user/search',
                 dataType: 'json',
                 delay: 0,
                 data: function (params) {
                     return {
                         keyword: params.term ? params.term : '',
                         limit: 100,
                         offset: 0
                     }
                 },
                 processResults: function (data, params) {
                     var users = [{id: 0, text: '请选择'},{id: $("#userId").val(), text: $("#userId").val()+ "|" +$("#userName").val()}];
                     if (data.count > 0) {
                         $.each(data.data, function (k, v) {
                             users.push({
                                 id: v.userId,
                                 text: v.userId + "|" + v.name
                             })
                         });
                     }

                     return {
                         results: users
                     }

                 },
                 cache: true,
                 minimumInputLength: 5
             }
         });

         initTopic();
     }

     function getOwnerName() {
         var ownerId=$('#ownerIds').select2('data')[0];
         if(ownerId!=undefined){
             return ownerId.text.split('|')[1];
         }else{
             return '';
         }
     }

     function initTopic(){
         parent.window.initSelect2($('#topicName'),'/topic/getTopicNames');
     }



    function showInfoModal(topicId) {
        if (topicId == undefined) {
            parent.window.addTab("editTopic", "/topic/editPage/0", '消息主题创建');
        } else {
            parent.window.addTab("editTopic"+topicId, "/topic/editPage/"+topicId, '消息主题编辑');
        }
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

    function refreshAllDate() {
        getListDate();
    }



    $("body").on("click", "#topicSearchList_btn", function () {
        getListDateWithSearch($("#topicName").val(), $("#topicId").val(), getOwnerName(),$("#topicType").val(),1)
    });

    window.doSearch= function () {
        getListDate()
    }

    function getListDateWithSearch(topicName, topicId,ownerName,topicType, page) {
        var option = {
            url:'/topic/list/data',
            where: {
                name: topicName,
                id: topicId,
                ownerName: ownerName,
                topicType : topicType
            },
            page: {
                curr: page
            }
        };

        table.reload("topicTable", option);
    }
     function getListDate() {
         table.reload("topicTable");
     }

    table.on('tool(topicTable)', function (obj) {
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        var tr = obj.tr; //获得当前行 tr 的DOM对象
        if (layEvent === 'expand') {
            expand(data.id, data.name)
        } else if (layEvent === 'edit') {
            editDetail(data)
        } else if (layEvent === 'reduce') {
            reduce(data.id, data.name)
        } else if (layEvent === "deleteTopic") {
            deleteTopic(data.id, data.name)
        } else if (layEvent === "generateToken") {
            generateToken(data.id, data.name)
        } else if (layEvent === "clearToken") {
            clearToken(data.id, data.name);
        } else if (layEvent === 'editSaveDayNum') {
            editSaveDayNum(data.id, data.name, data.saveDayNum);
        }
    });

    function editSaveDayNum(topicId, topicName, saveDayNum) {
        layer.open({
            title: topicName + ", 消息保留天数修改",
            content: "" +
            "<form class='layui-form saveDayNumForm'>" +
            "<div class='layui-input-inline'>" +
            "<select name='editSaveDayNum' id='editSaveDayNum'>" +
            "<option value='1'>1</option>" +
            "<option value='2'>2</option>" +
            "<option value='3'>3</option>" +
            "<option value='4'>4</option>" +
            "<option value='5'>5</option>" +
            "<option value='6'>6</option>" +
            "<option value='7'>7</option>" +
            "</select> " +
            "</div>" +
            "</form>",
            btn: ['提交', '取消'],
            yes: function (index, layero) {
                layer.closeAll();
                updateSaveDayNum(topicId, $("#editSaveDayNum").val())
                
            },
            success: function(layero, index){
                $("#editSaveDayNum").find("option[value="+saveDayNum+"]").attr("selected", true);
                form.render();
            },
            area:['auto', '300px']
        });
        form.render();
    }
    
    function updateSaveDayNum(topicId, num) {
        $.post("/topic/updateSaveDayNum", "topicId="+topicId+"&num="+num, requestCallback)
    }


    function clearToken(topicId, topicName) {
        layer.confirm("是否对主题["+topicName+"]清除token？", {icon: 3}, function (index) {
            $.post('/topic/clearToken', 'id='+topicId, requestCallback );
            layer.close(index);
        })
    }

    function generateToken(topicId, topicName) {
        layer.confirm("是否对主题["+topicName+"]重新生成token？若重新生成token后，所有对应生产者都需要更新token", {icon: 3}, function (index) {
            $.post('/topic/generateToken', 'id='+topicId, requestCallback );
            layer.close(index);
        })
    }
    
    function expand(topicId, topicName) {
        parent.window.addTab("topicExpand"+topicId, '/topic/expand/'+ topicId + '/' + topicName, '['+topicName+']消息主题扩容');
    }

    function deleteTopic(topicId, topicName) {
        parent.window.addTab("topicDelete"+topicId, '/topic/delete/'+ topicId + '/' + topicName, '['+topicName+']消息主题删除');
    }

    function reduce(topicId, topicName) {
        parent.window.addTab("removeQueue"+topicId, '/topic/remove/'+ topicId + '/' + topicName, '['+topicName+']消息主题缩容');
    }

    $('body').on('click', '.logSearch', function () {
        var refId = $(this).attr("data-id");
        var tbName = 'topic';
        parent.window.addTab("logSearch"+tbName + refId, '/auditLog/listPage/'+ tbName + "/" + refId, '日志查询');
    });


    function editDetail(topic) {
        showInfoModal(topic.id);
    }

    function successBox(msg) {
        layer.alert(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }

});