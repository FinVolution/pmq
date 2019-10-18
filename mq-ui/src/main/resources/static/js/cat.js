layui.use(['element', 'table', 'jquery', 'layer', 'form'], function () {
    var table = layui.table,
        form = layui.form,
        layer = layui.layer,
        $ = layui.$,
        element = layui.element;


    //横屏实现
    // var detectOrient = function() {
    //     var width = document.documentElement.clientWidth,
    //         height = document.documentElement.clientHeight,
    //     wrapper = document.getElementById("main"),
    //         style = "";
    //     if(width >= height) { // 竖屏
    //         style += "width:100%";
    //         style += "height:100%;";
    //         style += "-webkit-transform: rotate(0); transform: rotate(0);";
    //         style += "-webkit-transform-origin: 0 0;";
    //         style += "transform-origin: 0 0;";
    //     } else { // 横屏
    //         style += "width:" + height + "px;";// 注意旋转后的宽高切换
    //         style += "height:" + width + "px;";
    //         style += "-webkit-transform: rotate(90deg); transform: rotate(90deg);";
    //         // 注意旋转中点的处理
    //         style += "-webkit-transform-origin: " + width / 2 + "px " + width / 2 + "px;";
    //         style += "transform-origin: " + width / 2 + "px " + width / 2 + "px;";
    //     }
    //     wrapper.style.cssText = style;
    // }
    //
    // window.onresize = detectOrient;
    // detectOrient();


    var domain=$("#domain").val();
    var type=$("#type").val();
    var pageLevel=$("#pageLevel").val();

    var url='/cat/data?domain='+domain+'&type='+type+'&pageLevel='+pageLevel
    table.render({
         elem: '#catTable'
        ,height: 'full'
        ,url: url //数据接口
        ,cols: [[ //表头
            {field: 'id',title: 'Type', align: 'left', templet: '#typeTpl',width:'50%'}
            ,{field: 'totalCount',title: 'Total', align: 'left',width:'10%'}
            ,{field: 'failCount',title: 'Fail', align: 'left',width:'10%'}
            ,{field: 'failPercent',title: 'Fail%', align: 'left',width:'10%'}
            ,{field: 'avg',title: 'Avg(ms)', align: 'left',width:'10%'}
            ,{field: 'line95Value',title: '95Line(ms)', align: 'left',width:'10%'}
        ]]
    });



    $('body').on('click', '#typeSearch', function () {
        var typeSearch = $(this).attr("data-id");
        var ip = $(".ipClass.btnFocus").text();
        window.location.href="/cat/list/typeSearch?type="+typeSearch+"&domain="+domain+'&pageLevel='+2+'&ip='+ip;
    });

    $('body').on('click', '.domainClass', function () {
        var dom = $(this).text();
        window.location.href="/cat/list/typeSearch?type="+""+"&domain="+dom+'&pageLevel='+1;

    });

    $('body').on('click', '.ipClass', function () {
        var ip = $(this).text();
        window.location.href="/cat/list/typeSearch?type="+""+"&domain="+domain+'&pageLevel='+1+'&ip='+ip;

    });


    $("body").on("click", "#catSearch_btn", function () {
        window.location.href="/cat/list/typeSearch?type="+""+"&domain="+domain+'&pageLevel='+1;
    });

    $("body").on("click", "#refresh_btn", function () {
        getCatList();
    });

    function getCatList() {
        table.reload("catTable", {
            where: {
            }
        });
    }


});