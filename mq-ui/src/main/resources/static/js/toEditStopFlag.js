layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = parent.layer === undefined ? layui.layer : parent.layer;
    var $ = layui.$;
    var element = layui.element;
    form.render();

    initForm();

    form.on('submit(editSubmit)', function () {
        var component = $("#toEditQueueOffsetForm");
        var submitArray = component.serializeArray();
        submitArray.push({name: 'stopFlag', value: component.find("input[name='stopFlag']:checked").val()});
        $.post("/queueOffset/updateStopFlag",submitArray, requestCallback);
        return false;
    });

    function initForm() {
        var queueOffsetId=$("#queueOffsetId").val();
        $.get("/queueOffset/getById", 'queueOffsetId='+queueOffsetId, function (data, xhr) {
            if (xhr === 'success') {
                if (data.code == 0) {
                    initFillForm(data.data);
                }
            }
        });
    }

    function initFillForm(queueOffset) {
        var stopFlag=queueOffset.stopFlag;
        var component = $("#toEditQueueOffsetForm");
        $.each(queueOffset, function (key, value) {
            component.find("input[name='"+ key +"']:not(:radio)").val(value);
        });

        if(stopFlag==0){
            component.find("input[name='stopFlag'][value='"+0+"']").attr('checked', true);
        }else if(stopFlag==1){
            component.find("input[name='stopFlag'][value='"+1+"']").attr('checked', true);
        }
        form.render();

    }

    function requestCallback(result, xhr) {
        layer.closeAll();
        parent.window.refreshQueueOffset();
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
        layer.msg(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }
});