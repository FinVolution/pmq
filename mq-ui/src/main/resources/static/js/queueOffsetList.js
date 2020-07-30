layui.use([ 'element', 'table', 'jquery', 'layer', 'form' ], function() {
	var table = layui.table;
	var form = layui.form;
	var layer = layui.layer;
	var $ = layui.$;
	var element = layui.element;

	initSelect2();

	function initSelect2() {
		initTopic();
		initConsumerGroup();
	}

	$("body").on(
			"click",
			"#queueOffsetSearch_btn",
			function() {
				getQueueOffsetList($("#consumerGroupName").val(), $(
						"#topicName").val(), $("#consumerName").val(), 1, $(
						"#isReadOnly").val(), $("#topicType").val(), $(
						"#queueOffsetId").val(), $("#mode").val(),$("#subEnv").val())
			});

	$('body').on(
			'click',
			'.logSearch',
			function() {
				var refId = $(this).attr("data-id");
				var tbName = 'consumer_group';
				parent.window.addTab("logSearch" + tbName + refId,
						'/auditLog/listPage/' + tbName + "/" + refId, '日志查询');
			});

	window.doSearch = function() {
		getQueueOffsetList($("#consumerGroupName").val(),
				$("#topicName").val(), $("#consumerName").val(), 1, $(
						"#isReadOnly").val(), $("#topicType").val(), $(
						"#queueOffsetId").val(), $("#mode").val(),$("#subEnv").val())
	}

	table.on('tool(queueOffsetTable)', function(obj) {
		var data = obj.data; // 获得当前行数据
		var layEvent = obj.event; // 获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
		if (layEvent === 'updateQueueOffset') {
			updateQueueOffset(data)
		}
		if (layEvent === 'updateStopFlag') {
			updateStopFlag(data)
		}
		if (layEvent === 'updateReadOnly') {
			updateReadOnly(data)
		}if(layEvent === 'clientMonitor'){
			getClientMonitor(data)
		}
        if(layEvent === 'intelligentDetection'){
            doIntelligentDetection(data)
        }if(layEvent==="searchMessage"){
            searchMessage(data)
        }
		if (layEvent === 'clearConsumerId') {
			var consumerId = data.consumerName.substring(4,
					data.consumerName.length);
			layer.confirm("确认要删除该消费者？", {
				icon : 3,
				title : '不可逆操作！'
			}, function(index) {
				$.post('/consumer2/deleteById', 'consumerId=' + consumerId
						+ "&groupName=" + data.consumerGroupName,
						requestCallback);
				layer.close(index);
			});
		}
	});

	function requestCallback(result, xhr) {
		if (xhr === 'success') {
			if (result.code ==yesFlag) {
				layer.msg(result.msg, {
					icon : 1
				})
				getQueueOffsetList($("#consumerGroupName").val(), $(
						"#topicName").val(), $("#consumerName").val(), 1, $(
						"#isReadOnly").val(), $("#topicType").val(), $(
						"#queueOffsetId").val(),$("#mode").val(),$("#subEnv").val())
			} else {
				layer.alert(result.msg, {
					icon : 2
				})
			}
		} else {
			layer.alert("网络异常！" + xhr, {
				icon : 2
			})
		}
	}

	function updateReadOnly(queueOffset) {
		parent.window.addTab(queueOffset.queueId + "duxie",
				'/queue/list?queueId=' + queueOffset.queueId,
				queueOffset.queueId + "队列管理");
	}

    function searchMessage(queueOffset) {
        parent.window.addTab(queueOffset.queueId + "searchMessage",
            '/message/list?queueOffsetQueueId=' + queueOffset.queueId+"&queueOffsetTopicName="+queueOffset.topicName,
            queueOffset.queueId + "消息查询");
    }

	function updateQueueOffset(queueOffset) {
		if (queueOffset.consumerName.indexOf("mq2") != -1) {
			layer.open({				
				title : 'Mq2偏移无法修改',				
				content : "mq2 在线，偏移量不能修改。<br/>操作步骤如下：<br/>1,停止当前应用。<br/>2,修改偏移。<br/>3,启动应用。<br/><b style='color:red'>如果想不重启修改偏移，请升级mq3。</b>"					
			});
		} else {
			layer.open({
				type : 2,
				title : '编辑偏移',
				shade : [ 0 ],
				area : [ '800px', '600px' ],
				anim : 2,
				content : [
						"/queueOffset/toEditQueueOffset?queueOffsetId="
								+ queueOffset.id, 'yes' ], // iframe的url，no代表不显示滚动条
			});
		}
	}

    function getClientMonitor(queueOffset) {
        layer.open({
            type : 2,
            title : '客户端监控',
            shade : [ 0 ],
            area : [ '800px', '600px' ],
            anim : 2,
            content : [
                "/monitorManager/clientMonitor?hostPort="
                + encodeURI(queueOffset.consumerName)+"&consumerGroupName="+queueOffset.consumerGroupName+"&queueId="+
                queueOffset.queueId, 'yes' ], // iframe的url，no代表不显示滚动条
        });
    }

    function doIntelligentDetection(queueOffset){
        layer.confirm("确认要检测该队列？", {
            icon : 3,
            title : '智能检测！'
        }, function(index) {
            $.post('/queueOffset/intelligentDetection', 'queueOffsetId=' + queueOffset.id,
                requestCallback);
            layer.close(index);
        });

    }

	function updateStopFlag(queueOffset) {
		layer.open({
			type : 2,
			title : '编辑消费标志',
			shade : [ 0 ],
			area : [ '800px', '550px' ],
			anim : 2,
			content : [
					"/queueOffset/toEditStopFlag?queueOffsetId="
							+ queueOffset.id, 'yes' ], // iframe的url，no代表不显示滚动条
		});
	}

	function getQueueOffsetList(consumerGroupName, topicName, consumerName,
			page, isReadOnly, topicType, queueOffsetId,mode,subEnv) {
		table.reload("queueOffsetTable", {
			where : {
				consumerGroupName : consumerGroupName,
				topicName : topicName,
				consumerName : consumerName,
				readOnly : isReadOnly,
				topicType : topicType,
				id : queueOffsetId,
				mode : mode,
                subEnv:subEnv
			},page: {
                curr: page //重新从第 1 页开始
            }
		});
	}

	function refreshQueueOffset() {
		table.reload("queueOffsetTable", {});
	}
	window.refreshQueueOffset = refreshQueueOffset;

	function initTopic() {
        parent.window.initSelect2($('#topicName'),'/topic/getTopicNames');
	}

	function initConsumerGroup() {
        parent.window.initSelect2($('#consumerGroupName'),'/consumerGroup/getConsumerGpNames');
	}

});