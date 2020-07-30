layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;
    var element = layui.element;

    var editConsumerGroupTabId="ConsumerGroup" + $("#consumerGroupId").val();
    var createConsumerGroupTabId="createConsumerGroup"
    var toOpenTabId="-15";
    var consumerMode;

    initForm();
    initOwnerIdsSelect2();
    initDptNameSelect2();

    function initForm() {
        if (isEdit()) {
                var id = $("#consumerGroupId").val();
                $.get("/consumerGroup/getById", 'id='+id, function (data, xhr) {
                    if (xhr === 'success') {
                        if (data.code == yesFlag) {
                            initFillForm(data.data);
                            initTextMode(data.data);
                        }
                    }
                })
        } else {
            initSelectedDpts();
            initUser();
            initConsumerQuality()
            if(!isAdmin()){//如果不是超级管理员 不能编辑实时消息
                var co = $("form");
                co.find("input[name='pushFlag']").attr('disabled', true);
            }
            form.render();
        }
    }

    form.on('submit(createSubmit)', function () {
        var component = $(this).parents("#createConsumerGroupForm");
        var submitArray = component.serializeArray();
        if ($('#ownerIds').select2("data").length === 0) {
            layer.msg("负责人为必选项",{time:700});
            return false;
        }
        submitArray.push({name: 'ipFlag', value: component.find("input[name='ipFlag']:checked").val()});
        submitArray.push({name: 'alarmFlag', value: component.find("input[name='alarmFlag']:checked").val()});
        submitArray.push({name: 'traceFlag', value: component.find("input[name='traceFlag']:checked").val()});
        submitArray.push({name: 'pushFlag', value: component.find("input[name='pushFlag']:checked").val()});
        submitArray.push({name: 'ownerIds', value: getItemIds($('#ownerIds').select2("data"))});
        submitArray.push({name: 'ownerNames', value: getItemNames($('#ownerIds').select2("data"))});
        submitArray.push({name: 'mode', value:consumerMode});


        $.post("/consumerGroup/createAndUpdate", submitArray, requestCallback);
        return false;
    });


    function initOwnerIdsSelect2(){
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

    form.on('radio(ipFlag)', function(data){
        if($(this).attr("id")=="ipWhite"){
            $("#ipBlack").data("ipPre",$("#ipList").val());
        }else{
            $("#ipWhite").data("ipPre",$("#ipList").val());
        }
        $('#ipList').val($(this).data("ipPre"));
    });


    $("#ownerIds").on("change",function(val){
        $("#alarmEmails").val(getOwnerEmails($('#ownerIds').select2("data")));
    })


    $("#textMode").bind('input propertychange','textarea',function(val){
        if (!isEdit()){

            var textMode=$("#textMode").val();
            if(textMode != 'undefined' && textMode != ''){
                var consumerGroup=JSON.parse(textMode);
                var co = $("form");
                $.each(consumerGroup, function (key, value) {
                    co.find("input[name='"+ key +"']:not(:radio)").val(value);
                });
                co.find("input[name='id']").val('');
                co.find("textarea[name='remark']").val(consumerGroup.remark);
                $("#dptName option[value="+consumerGroup.dptName+"]").attr("selected", true);
                initOwners(consumerGroup);
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

    //自定义验证规则
    form.verify({
        emailList: [
            /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+(,([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+)*$/,
            "邮箱格式不正确！英文逗号间隔多个，无空格。"
        ],
        ip: function(value) {
            if(value.length>0&&isIps(value)==false){
                return "ip格式不正确！英文逗号间隔多个，无空格";
            }
        },
        negative: function(value) {
            if(parseInt(value)<0){
                return "不能输入负数";
            }
        },
        telList: function(value) {
            if(value.length>0&&isTelPhone(value)==false){
                    return "手机号格式不正确！英文逗号间隔多个，无空格";
            }
        }
    });

    function getItemIds (ownerList){
        var itemIds = [];
        $.each(ownerList, function (k, v) {
            //itemIds.push( v.text.split('|')[0]);
        	itemIds.push(v.id);
        });
        return itemIds.join(",");
    }

    function getOwnerEmails (ownerList){
        var itemIds = [];
        $.each(ownerList, function (k, v) {
            //itemIds.push( v.text.split('|')[0]+"@ppdai.com");
            itemIds.push(v.id);
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



    function isIps(str){
        var reIps=/^(((\d{1,2})|(1\d{1,2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d{1,2})|(1\d{1,2})|(2[0-4]\d)|(25[0-5]))(,(((\d{1,2})|(1\d{1,2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d{1,2})|(1\d{1,2})|(2[0-4]\d)|(25[0-5])))*$/;
        return reIps.test(str)
    }

    function isTelPhone(str){
        var reTel=/^1\d{10}(,1\d{10})*$/;
        return reTel.test(str)
    }

    function initFillForm(consumerGroup) {
        consumerMode=consumerGroup.mode
        var co = $("form");
        $.each(consumerGroup, function (key, value) {
            co.find("input[name='"+ key +"']:not(:radio)").val(value);
        });
        initOwners(consumerGroup);
        initDpts(consumerGroup);
        co.find("#dptName option[value="+consumerGroup.dptName+"]").attr("selected", true);
        co.find("#mode option[value="+consumerGroup.mode+"]").attr("selected", true);
        if(consumerGroup.ipWhiteList!=null){
            co.find("input[name='ipFlag'][value='"+0+"']").attr('checked', true);
            co.find("input[name='ipList']").attr('value',consumerGroup.ipWhiteList);
        }else if(consumerGroup.ipBlackList!=null){
            co.find("input[name='ipFlag'][value='"+1+"']").attr('checked', true);
            co.find("input[name='ipList']").attr('value',consumerGroup.ipBlackList);
        }
        co.find("input[name='alarmFlag'][value='"+consumerGroup.alarmFlag+"']").attr('checked', true);
        co.find("input[name='traceFlag'][value='"+consumerGroup.traceFlag+"']").attr('checked', true);
        co.find("input[name='pushFlag'][value='"+consumerGroup.pushFlag+"']").attr('checked', true);
        co.find("input[name='name']").attr('readonly', true);
        co.find("textarea[name='remark']").val(consumerGroup.remark);
        $("input[name=name]").css('background-color','#CCCCCC');
        if(!isAdmin()){//如果不是超级管理员 不能编辑消费模式
            $("#mode").attr('disabled', true);
            co.find("input[name='pushFlag']").attr('disabled', true);
        }

        form.render();
    }

    function initTextMode(consumerGroup){
        $("#textMode").val(JSON.stringify(consumerGroup));
    }

    function initOwners(consumerGroup){
        var ownerIds=consumerGroup.ownerIds;
        var ownerNames=consumerGroup.ownerNames;
        var alarmEmails=consumerGroup.alarmEmails;
        if(ownerIds!=null&&ownerIds!=undefined){
            var ownerIdArr=ownerIds.split(",");
	        var alarmEmailArr=alarmEmails.split(",");
            var ownerNameArr = ownerNames.split(","); 				
            var ownerList = [];
            var textVal=[];
            $.each(ownerIdArr, function (k, v) {
                ownerList.push({"id":alarmEmailArr[k] ,text:(v +"|"+ ownerNameArr[k])});
				textVal.push(alarmEmailArr[k]);
	        });
            $("#ownerIds").select2({data:ownerList}).val(textVal).trigger("change");
            $("#alarmEmails").val(consumerGroup.alarmEmails);
            initOwnerIdsSelect2();

        }
    }


    function initDpts(consumerGroup){
        var dpt=consumerGroup.dptName;
        if(dpt!=null){
            var dptList=[];
            dptList.push(dpt)
            $("#dptName").select2({data:dptList}).val(dptList).trigger("change");
            initDptNameSelect2();

        }
    }

    function isEdit() {
        var id = $("#consumerGroupId").val();
        return id != 'undefined' && id != '';
    }

    function isAdmin(){
        var isAdmin= $("#isAdmin").val();
        if(isAdmin=="0") {
            return true}
            else{
            return false;
        }

    }



    function initDptNameSelect2(){
		try{
			 parent.window.initSelect2($("#dptName"),'/user/getDepartmentsBySearch');
		}catch(e){}
       
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


    function initUser() {
        $.get("/user/getCurrentUser", function (result, xhr) {
            if (xhr === 'success') {
                if (result.code == yesFlag) {
                    var user=result.data;
                    var ownerList = [];
					var ownerEmail=[];
                    ownerList.push({id:user.email,text:user.userId+"|"+user.name})	;
                    ownerEmail.push(user.email);
                    $("#ownerIds").select2({data:ownerList}).val(ownerEmail).trigger("change");
                    $("#alarmEmails").val(user.email);
                    initOwnerIdsSelect2();
                }
            }
        })
    }


    function initConsumerQuality() {
        $("#consumerQuality").val(0);
    }


    function requestCallback(result, xhr) {
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
        layer.msg(msg, {icon: 1});
        parent.window.refresh1("consumerGroup/list");
        if (isEdit()) {
            parent.window.deleteTab(editConsumerGroupTabId,toOpenTabId);
        }else{
            parent.window.deleteTab(createConsumerGroupTabId,toOpenTabId);
        }       
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }
});