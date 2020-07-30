String.prototype.endWith = function(endStr) {
	var d = this.length - endStr.length;
	return (d >= 0 && this.lastIndexOf(endStr) == d);
}
layui.use([ 'element', 'table', 'jquery', 'layer', 'form', 'laydate' ],
		function() {
			var table = layui.table;
			var form = layui.form;
			var layer = layui.layer;
			var $ = layui.$;
			var laydate = layui.laydate;
			var element = layui.element;
            var lastSearchTime = new Date().getTime() - 3000;
			var maxId = "";
			var minId = "";
			// 标记当前queue是否有slave
			var queueSlave = 0;

			initTopicSelect2();

            initMessageTable();

			isAdmin();

			function isAdmin(){
				if($('#userRole').val()!=0){
					$("#msgCount_btn").hide();
					$("#topicMsgCount").hide();
				}
			}


            function initTopicSelect2() {
                var queueOffsetQueueId = $("#queueOffsetQueueId").val();
                var queueOffsetTopicName = $("#queueOffsetTopicName").val();
                if (queueOffsetTopicName) {
                    $('#topicName').append(
                        "<option value='" + queueOffsetTopicName
                        + "' selected='selected'>"
                        + queueOffsetTopicName
                        + "</option>");
                }
                parent.window.initSelect2($('#topicName'),
                    '/topic/getTopicNames');
                if (queueOffsetQueueId) {
                    topicChanged(queueOffsetQueueId);
                }
            }			
			function showSendAllFailMessageButton(){
                //只有失败topic时才展示批量发送按钮
				var topicName=$("#topicName").val();
				if(topicName){
                    if(topicName.endWith("_fail")){
                        $("#sendAllFailMessage").show();
                    }else{
                        $("#sendAllFailMessage").hide();
                    }
                    $("#topicBuffer").text("Topic名称："+topicName);
				}


			}

			function getMessageList(id,queueId,bizId,traceId,maxId,minId,header,body,retryStatus,curPage, startTime, endTime){
                table.reload("messageTable", {
                    where : {
                        id : id,
                        queueId : queueId,
                        bizId : bizId,
                        traceId : traceId,
                        maxId : maxId,
                        minId : minId,
                        header : header,
                        body : body,
                        retryStatus:retryStatus,
                        startTime:startTime,
                        endTime:endTime
                    },
                    page: {
                        curr: curPage
                    }
                });

                showSendAllFailMessageButton();
			}

			function initMessageTable(){
                table.render("messageTable", {
                    where : {
                        id : id,
                        queueId : '1',
                        bizId : '',
                        traceId : '',
                        startTime : '',
                        endTime : '',
                        header : '',
                        body : '',
                        retryStatus:''
                    }
                });

                showSendAllFailMessageButton();
			}

			laydate.render({
				elem : '#startTime',
				type : 'datetime',
				done : function(value, date) {
					$("#startTime").val(value);
					setMaxMin();
				}
			});
			laydate.render({
				elem : '#endTime',
				type : 'datetime',
				done : function(value, date) {					
					$("#endTime").val(value)
					setMaxMin();
				}
			});

			var minObj = {};
			var maxObj = {};
			var url = '/message/list/condition';
			function setMaxMin() {				
				var minT = $("#startTime").val();
				var maxT = $("#endTime").val();
				if(!$("#queueId").val())return;
				var key1 = $("#queueId").val() + minT;
				var key2 = $("#queueId").val() + maxT;
				if (!minT) {
					minId = "";
				} else if (minObj[key1]) {
					minId = minObj[key1];
				} else {
					$.post(url, 'queueId=' + $("#queueId").val()
							+ '&startTime=' + minT + '&endTime=' + '',
							function(data) {
								minId = data.data.minId;
								minObj[key1] = minId;
							});
				}
				if (!maxT) {
					maxId = "";
				} else if (maxObj[key2]) {
					maxId = maxObj[key2];
				} else {
					$.post(url, 'queueId=' + $("#queueId").val()
							+ '&startTime=' + '' + '&endTime=' + maxT,
							function(data) {
								maxId = data.data.maxId;
								maxObj[key2] = maxId;
							});
				}
			}

			$("body").on(
					"click",
					"#messageSearchList_btn",
					function() {
						var nowTime = new Date().getTime();
						if (nowTime - lastSearchTime < 2000) {
							failBox("查询不能过于频繁");
						} else {
							var topicName = $("#topicName").val();
							var queueId = $("#queueId").val();
							var id = $("#id").val();
							var bizId = $("#bizId").val();
							var traceId = $("#traceId").val();
							var header = $("#header").val();
							var body = $("#body").val();
							var startTime = $("#startTime").val();
							var endTime = $("#endTime").val();
							var curPage = 1;
							var maxNumber = $("#maxNumber").val();
							var retryStatus=$("#retryStatus").val();
							if (id == "") {
								id = 0;
							}
							if ("" == topicName || topicName == null) {
								layer.confirm("请选择Topic", {
									icon : 3,
									title : '提示'
								}, function(index) {
									layer.close(index);
								});
							} else if ("" == queueId || queueId == null) {
								layer.confirm("请选择队列", {
									icon : 3,
									title : '提示'
								}, function(index) {
									layer.close(index);
								});
							} else if (!queueSlave
									&& (bizId != "" || traceId != ""
											|| header != "" || body != "")) {
								if (startTime == "" || endTime == "") {
									failBox("请选择【起始时间】和【截止时间】。");
								} else if (maxId - minId > maxNumber) {
									failBox("查询的消息量超过了：" + maxNumber
											+ "。请缩短时间区间。");
								} else {
									changeContidion(queueId, id, bizId,
											traceId, header, body, curPage,
											startTime, endTime,retryStatus);
								}
							} else {

								changeContidion(queueId, id, bizId, traceId,
										header, body, curPage, startTime,
										endTime,retryStatus);
							}
							lastSearchTime = new Date().getTime();

						}
                        showSendAllFailMessageButton();
					});

			$("body").on("click", "#msgCount_btn",
				function() {
					var topicName = $("#topicName").val();
					var startTime = $("#startTime").val();
					var endTime = $("#endTime").val();

					if ("" == topicName
						|| topicName == null) {
						layer.confirm("请选择Topic", {
							icon : 3,
							title : '提示'
						}, function(index) {
							layer.close(index);
						});
					}else{
						if (startTime == ""
							|| endTime == "") {
							failBox("请选择【起始时间】和【截止时间】。");
						}else {
							var data = {'topicName':topicName,'startTime':startTime,'endTime':endTime};
							$.post("/topic/msgCount",data,requestCallback);
						}
					}
				});

			function requestCallback(result, xhr) {
				if (xhr === 'success') {
					if (result.code ==yesFlag) {
						$("#topicMsgCount").text(result.msg);
					}
				} else {
					failBox("网络异常！"+xhr);
				}
			}



			function refreshMessageTable(){
                table.reload('messageTable');

			}

			function successBox(msg) {
				layer.msg(msg, {
					icon : 1
				});
                refreshMessageTable();
                showSendAllFailMessageButton();
			}

			function failBox(msg) {
				layer.alert(msg, {
					icon : 2
				})
			}

			function changeContidion(queueId, id, bizId, traceId, header,
					body, curPage, startTime, endTime,retryStatus) {
				if (maxId === "" || minId === "") {
					var url = '/message/list/condition';
					$.post(url, 'queueId=' + queueId + '&startTime='
							+ startTime + '&endTime=' + endTime,
							function(data) {
                                getMessageList(id,queueId,bizId,traceId,data.data.maxId,data.data.minId,header,body,retryStatus,curPage, startTime, endTime);
							});
				} else {
                    getMessageList(id,queueId,bizId,traceId,maxId,minId,header,body,retryStatus,curPage, startTime, endTime)
				}
			}

            function topicChanged(id) {
                var url = '/message/list/topicQueueIds';
                showSendAllFailMessageButton();
                $("#topicMsgCount").text('');
                $.post(
                    url,
                    'topicName=' + $("#topicName").val(),
                    function(data) {
                        $("#queueId").html("");
                        if (data.length > 0) {
                            $.each(
                                data,
                                function(k, v) {
                                    var option1 = "<option value='"
                                        + v.id
                                        + "' "
                                        + (v.id == id ? "selected='selected'"
                                            : "")
                                        + ">"
                                        + v.id
                                        + "|"
                                        + v.dbName
                                        + "|"
                                        + v.tbName
                                        + "|"
                                        + v.ip
                                        + "</option>";
                                    $('#queueId')
                                        .append(option1);
                                    form.render('select');
                                    setMaxMin();
                                });
                        } else {
                            form.render('select');
                        }
                        checkSalve($("#queueId").val());

                        if (id) {
                            $("#messageSearchList_btn")
                                .click();
                        }
                    });
            }


            $("#topicName").on("select2:select", function(e) {
                topicChanged();
            });

			form.on('select(queueId)', function(data) {
				checkSalve(data.value);	
				setMaxMin();
			});

			function checkSalve(queueId) {
				var url = '/message/queue/slave';
				$.post(url, 'queueId=' + queueId, function(data) {
					queueSlave = data;					
				});
			}
			table.on('tool(messageTable)', function(obj) {
				var data = obj.data; // 获得当前行数据
				var layEvent = obj.event; // 获得 lay-event 对应的值（也可以是表头的 event// 参数对应的值）
				if (layEvent === 'detail') {
					showInfoModal();
					fillForm(data)
				} else if (layEvent == 'sendFailMessage') {
					var queueId = $("#queueId").val();
					var url = '/message/retry/failMessage';
					$.post(url, 'messageId=' + data.id + "&queueId=" + queueId,
							function(data) {
								if (data.suc) {
									successBox("重新发送成功");
								} else {
									failBox("重新发送失败");
								}

							});
				}

			});

            //头工具栏事件
            table.on('toolbar(messageTable)', function(obj){
                var checkStatus = table.checkStatus(obj.config.id);
                switch(obj.event){
                    case 'sendAllFailMessage':
                        var data = checkStatus.data;
                        var queueId = $("#queueId").val();

                        var topicName=$("#topicName").val();
                        if(topicName!=null&&topicName!=''){
                            if(!topicName.endWith("_fail")){
                                layer.alert("正常topic不可以重新发送");
                                return;
                            }
                        }

                        if(data.length==0){
                            layer.alert("请选择！");
                            return;
                        }
                        var messageIds=[];
                        for(var i=0;i<data.length;i++){
                            messageIds.push(data[i].id)
                        }

                        layer.confirm("确认要批量发送？", {icon: 3, title: '不可逆操作！'}, function (index) {
                            $.post('/message/retryAll/failMessage', 'messageIds='+JSON.stringify(messageIds)+ "&queueId=" + queueId, function(data) {
                                if (data.suc) {
                                    successBox("重新发送成功");
                                } else {
                                    failBox("重新发送失败");
                                }

                            });
                            layer.close(index);
                        });
                        break;
                };

            });

			function showInfoModal() {
				var modal = layer.open({
					title : '消息查询',
					content : $("#messageDetailForm").html(),
					btn : [ 'close' ]
				});
				layer.style(modal, {
					width : '500px'
				});
				form.render();
			}

			function fillForm(message) {
				var co = $(".layui-layer-content");
				$.each(message,
						function(key, value) {
							co.find("input[name='" + key + "']:not(:radio)")
									.val(value);
							co.find("textarea[name='" + key + "']:not(:radio)")
									.val(value);
						});
			}
			;


		});