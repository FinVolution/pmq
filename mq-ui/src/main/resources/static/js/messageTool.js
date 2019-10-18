layui.use(['element', 'table', 'jquery', 'layer', 'form','laydate'], function () {
    var table = layui.table;
    var form = layui.form;
    var layer = layui.layer;
    var $ = layui.$;

    initTopicSelect2();

    function initTopicSelect2(){
        parent.window.initSelect2($('#topics'),'/topic/getTopicNamesForMessageTool');
    }

    $("#head").html('{"key1":"value1","key2":"value2"}');
    form.render(null, 'head');

    $("#formatMessage").html('{"topicName":"Test1","sendType":"2","message":{"bizId":"","headStr":"","body":"111"}}');
    form.render(null, 'formatMessage');

    setBufferTopic();

    function setBufferTopic() {
        var currentTips="";
        for(var i=0;i<10;i++){
            var buffer1=localStorage.getItem("topic"+i);
            if(null!=buffer1&&""!=buffer1){
                currentTips+='<span class="layui-badge-rim">'+buffer1+'</span> ';
            }
        }
        $("#topicBuffer").html(currentTips);
        form.render(null, 'topicBuffer');
    }


    $("body").on("click", "#toolSendMessage_btn", function () {
        var url = '/message/tool/sendMessage';
        var data = {
            "topicName": $("#topics").val(),
            "sendType": $("#sendType").val(),
            "message": {
                "bizId": $("#bizId").val(),
                 "head": $("#head").val()?JSON.parse($("#head").val()):null,
                 "tag":$("#tag").val(),
                 "body": $("#body").val()
            }
        }
        if(""==data.topicName||data.topicName==null){
            layer.confirm("请选择Topic" , {icon: 3, title: '提示'}, function (index) {
                layer.close(index);
            });
        }else if(""==data.message.body||data.message.body==null){
            layer.confirm("请输入消息内容" , {icon: 3, title: '提示'}, function (index) {
                layer.close(index);
            });
        }else{
            $("#formatMessage").html(JSON.stringify(data));
            form.render(null, 'formatMessage');
            var flag=true;
            var currentTopic=$("#topics").val();
            var index=localStorage.getItem("index");
            if(""==index||null==index){
                index=0;
            }
            for(var i=0;i<10;i++){
                var buffer=localStorage.getItem("topic"+i);
                if(buffer==currentTopic){
                    flag=false;
                    break;
                }
            }
            if(flag){
                var nextIndex=(parseInt(index)+1)%10;
                localStorage.setItem("topic"+index, currentTopic);
                localStorage.setItem("index",nextIndex);
                setBufferTopic();
            }
            $.ajax({
                type: 'POST',
                url: url,
                headers: {"Content-Type": "application/json;charset=UTF-8"},
                data: JSON.stringify(data),
                success: function (result) {
                    if(result.code==yesFlag){
                        successBox(result.msg)
                    }else{
                        failBox(result.msg)
                    }
                },
                error: function (result) {
                    failBox("连接失败");
                }
            });
        }
    });

    function successBox(msg) {
        layer.msg(msg, {icon: 1})
    }

    function failBox(msg) {
        layer.alert(msg, {icon: 2})
    }
});