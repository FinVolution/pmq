layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var element = layui.element;
    var formArray;

    initOwnerIdSelect2();
    initForm();
    initConsumerGroupSelect2();
    initDptNameSelect2();


    $("#ownerIds").on("change",function(val){
        $("#emails").val(getOwnerEmails($('#ownerIds').select2("data")));
    })

    function initForm() {
        if (isEdit()) {
            initBizType(initFormFromServer);
        } else {
            initUser();
            initSelectedDpts();
            initBizType();
            form.render();
        }
    }

    function initFormFromServer() {
        var id = $("#topicId").val();
        $.get("/topic/getById", 'id='+id, function (data, xhr) {
            if (xhr === 'success') {
                if (data.code == 0) {
                    initFillForm(data.data);
                    initTextMode(data.data);
                }
            }
        })
    }

    function initTextMode(topic){
        $("#textMode").val(JSON.stringify(topic));
    }

    $("#textMode").bind('input propertychange','textarea',function(val){
        if (!isEdit()){

            var textMode=$("#textMode").val();
            if(textMode != 'undefined' && textMode != ''){
                var topic=JSON.parse(textMode);
                var co = $("form");
                $.each(topic, function (key, value) {
                    co.find("input[name='"+ key +"']:not(:radio)").val(value);
                });
                co.find("input[name='id']").val('');
                co.find("textarea[name='remark']").val(topic.remark);
                $("#dptName option[value="+topic.dptName+"]").attr("selected", true);
                $("#businessType option[value="+topic.businessType+"]").attr("selected", true);
                initOwners(topic);
                form.render();

            }else{//清空文本框時，表單变为初始状态
                var co = $("form");
                co.find("input[name='name']").val('');
                co.find("input[name='tels']").val('');
                co.find("textarea[name='remark']").val('');
                initForm();

            }
        }
    })


    function initUser() {
        $.get("/user/getCurrentUser", function (result, xhr) {
            if (xhr === 'success') {
                if (result.code == yesFlag) {
                    var user=result.data;
                    var ownerList = [];
                    ownerList.push(user.userId+"|"+user.name)
                    $("#ownerIds").select2({data:ownerList}).val(ownerList).trigger("change");
                    $("#emails").val(user.email);
                    initOwnerIdSelect2();
                }
            }
        })
    }

    
    function initBizType(fun) {
        $.get("/user/getBizType", function (result, xhr) {
            if (xhr === 'success') {
                if (result.code == yesFlag) {
                    var bizTypes = result.data;
                    var bizHtml = '<option></option>';
                    $.each(bizTypes, function (k, v) {
                        bizHtml += '<option value="'+v+'">'+v+'</option>';
                    });
                    $("#businessType").html(bizHtml);
                    form.render();
                    if (typeof fun !== 'undefined') {
                        fun()
                    }
                }
            }
        })
    }



    function getFormArray() {
        var component = $("form");
        var submitArray = component.serializeArray();

        submitArray.push({name: 'ownerIds', value: getItemIds($('#ownerIds').select2("data"))});
        submitArray.push({name: 'ownerNames', value: getItemNames($('#ownerIds').select2("data"))});
        formArray = submitArray;
        return formArray;
    }


    form.on('submit(createSubmit)', function () {
        var component = $(this).parents("#createTopicForm");
        var form = getFormArray();
        if ($('#ownerIds').select2("data").length === 0) {
            layer.msg("负责人为必选项",{time:700});
            return false;
        }
        form.push({name: 'normalFlag', value: 1});
        form.push({name: 'topicType', value: 1});
        form.push({name: 'id', value: $("#topicId").val()});
        form.push({name: 'consumerFlag', value: component.find("input[name='consumerFlag']:checked").val()});
        $.post("/topic/createOrUpdate", form, requestCallback);
        return false;
    });


    function isEdit() {
        var id = $("#topicId").val();
        return id != 'undefined' && id != '';
    }



    function initFillForm(topic) {
        var co = $("form");
        $.each(topic, function (key, value) {
            co.find("input[name='"+ key +"']:not(:radio)").val(value);
        });
        co.find("textarea[name='remark']").val(topic.remark);
        co.find('select[name=businessType] option[value="' + topic.businessType + '"]').attr("selected", true);
        co.find("#dptName option[value="+topic.dptName+"]").attr("selected", true);
        co.find("#saveDayNumSelect option[value="+topic.saveDayNum+"]").attr("selected", true);

        if (isEdit()) {
            initOwners(topic);
            initConsumerGroups(topic);
            initDpts(topic);
            if(topic.consumerFlag==0){
                co.find("input[name='consumerFlag'][value='"+0+"']").attr('checked', true);
            }else if(topic.consumerFlag==1){
                co.find("input[name='consumerFlag'][value='"+1+"']").attr('checked', true);
            }
            co.find("textarea[name='remark']").val(topic.remark);
            co.find("input[name='name']").attr("readonly", true);
            var expectDayCounthtml = '<input name="expectDayCount" class="layui-input" value="' + topic.expectDayCount + '" readonly><div class="layui-form-mid layui-word-aux">(提交后不可更改)</div>';
            co.find("#expectDayCountDiv").html(expectDayCounthtml);
        } else {
            if (topic.expectDayCount !== undefined) {
                co.find('select[name=expectDayCount] option[value="' + topic.expectDayCount + '"]').attr("selected", true);
            }
        }
        form.render();
    }



    form.verify({
        emailList: [
            /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+(,([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+)*$/,
            "邮箱格式不正确！英文逗号间隔多个，无空格。"
        ],
        telList: [
            /^(1\d{10}(,1\d{10})*)?$/,
            "手机号格式不正确！英文逗号间隔多个，无空格"
        ],
        positiveInt: [
            /^[1-9]\d*$/,
            "请输入正整数"
        ],
        topicName: function(value) {
            if(value.indexOf("_fail")!=-1){
                return "topic名字不能包含_fail";
            }
        }
    });

    function initOwners(topic){
        var ownerIds=topic.ownerIds;
        var ownerNames=topic.ownerNames;
        if(ownerIds!=null&&ownerIds!=undefined){
            var ownerIdArr=ownerIds.split(",");
            var ownerNameArr = ownerNames.split(",");
            var ownerList = [];
            $.each(ownerIdArr, function (k, v) {
                ownerList.push(v +"|"+ ownerNameArr[k]);
            });
            $("#ownerIds").select2({data:ownerList}).val(ownerList).trigger("change");

            $("#emails").val(topic.emails);
            initOwnerIdSelect2();

        }
    }

    function initConsumerGroups(topic){
        var groupNames=topic.consumerGroupNames;
        if(groupNames!=null){
            var groupNamesArr=groupNames.split(",");
            var groupNameList=[];
            $.each(groupNamesArr, function (k, v) {
                groupNameList.push(v);
            });
            $("#consumerGroupList").select2({data:groupNameList}).val(groupNameList).trigger("change");
            initConsumerGroupSelect2();
        }
    }

    function initDpts(topic){
        var dpt=topic.dptName;
        if(dpt!=null){
            var dptList=[];
            dptList.push(dpt)
            $("#dptName").select2({data:dptList}).val(dptList).trigger("change");
            initDptNameSelect2();

        }
    }


    function initSelectedDpts() {
        $.get("/user/getCurrentDpt", function (result, xhr) {
            if (xhr === 'success') {
                if (result.code == yesFlag) {
                    var department = result.data;
                    if(department!=null){
                        var dptList=[];
                        dptList.push(department)
                        $("#dptName").select2({data:dptList}).val(dptList).trigger("change");
                        initDptNameSelect2();

                    }
                }
            }
        })
    }


    function initOwnerIdSelect2(){
        $('#ownerIds').select2({
            ajax: {
                url: '/user/search',
                dataType: 'json',
                delay: 250,
                data: function (params) {
                    return {
                        keyword: params.term ? params.term : '',
                        limit: 100,
                        offset: 0
                    }
                },
                processResults: function (data, params) {
                    var users = [];
                    if (data.count > 0) {
                        $.each(data.data, function (k, v) {
                            users.push({
                                id: v.email,
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
    }


    function initConsumerGroupSelect2(){
        parent.window.initSelect2($("#consumerGroupList"),'/consumerGroup/consumerGroupSelect');
    }

    function initDptNameSelect2(){
        parent.window.initSelect2($("#dptName"),'/user/getDepartmentsBySearch');
    }


    function getOwnerEmails (ownerList){
        var itemIds = [];
        $.each(ownerList, function (k, v) {
            itemIds.push( v.id);
        });
        return itemIds.join(",");
    }

    function getItemIds (ownerList){
        var itemIds = [];
        $.each(ownerList, function (k, v) {
            itemIds.push( v.text.split('|')[0]);
        });
        return itemIds.join(",");
    }

    function getItemNames(ownerList){
        var itemIds = [];
        $.each(ownerList, function (k, v) {
            itemIds.push( v.text.split('|')[1]);
        });
        return itemIds.join(",");
    }


    function requestCallback(result, xhr) {
        if (xhr === 'success') {
            if (result.code ==yesFlag) {
                parent.window.refresh1("/topic/list");
                parent.window.deleteTab("editTopic"+$("#topicId").val() ,"-14");
            } else {
                failBox(result.msg);
            }
        } else {
            failBox("网络异常！"+xhr);
        }
    }


    function successBox(msg) {
        layer.alert(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }

});