layui
		.use(
				[ 'element', 'layer', 'jquery' ],
				function() {
					var layer = layui.layer, $ = layui.$, element = layui.element;
					// titles用于存储新建tab的名字, href为新建tab的跳转路径,
					// delIndex为删除的tab在数组中的下标
					var titles = new Array(), href, delIndex;
					titles.push("-10");

					setTimeout(function() {
						$(".layui-nav-tree #-10").click();
					}, 500);

					// 根据用户角色判断是否显示trace导航栏，只有超级管理员才可以查看trace,(0表示超级管理员角色)
					$(function() {
						function hide() {
							// 默认不显示trace导航
							// 获取角色
							$.ajax({
								url : '/trace/hide',
								type : 'POST',
								async : false,
								success : function(data) {
									if (data == 0) {
										$("#menuAdmin").show();
									} else {
										$("#menuAdmin").hide();
									}
								},
								error : function(data) {
								}
							});
						}
						hide();
					});

                    //如果是生产环境，不显示发送消息的"管理工具"
                    $(function() {                       
                            // 默认不显示trace导航
                            // 获取角色
                            $.ajax({
                                url : '/trace/hideMessageTool',
                                type : 'POST',
                                async : false,
                                success : function(data) {
                                    if (data == 0) {
                                        $("#proHideMessageTool").show();
                                    } else {
                                        $("#proHideMessageTool").hide();
                                    }
                                },
                                error : function(data) {
                                }
                          });
                       
                    });

					window.refresh1 = function(url) {
						var iframe = $("iframe[src='" + url + "']");
						if (iframe.length > 0) {
							iframe = iframe[0];
							var iframewindow = iframe.contentWindow ? iframe.contentWindow
									: iframe.contentDocument.defaultView;
							iframewindow.doSearch();
							return true;
						}else{
							return false;
						}
					};
					function deleteTab(toDeleteTabId, toOpenTabId) {
						var tabIndex = indexOf(toDeleteTabId, titles)
						titles.splice(tabIndex, 1);
						element.tabDelete('bodyTab', toDeleteTabId);
						element.tabChange('bodyTab', toOpenTabId);
					}
					window.deleteTab = deleteTab;

					function indexOf(val, titles) {
						for (var i = 0; i < titles.length; i++) {
							if (titles[i] == val)
								return i;
						}
						return -1;
					}

					// 触发事件,用于被子页面调用
					function addTab(id, url, name) {
						if (document.cookie.indexOf("userSessionId") == -1) {
							window.location.href = "/login";
						}
						if ($.inArray(id, titles) > -1) {
							element.tabChange('bodyTab', id);
						} else {
							titles.push(id);
							element.tabAdd('bodyTab',
									{
										title : name,
										content : '<iframe src="' + url
												+ '"></iframe>',
										id : id
									});
							element.tabChange('bodyTab', id);
						}
					}
					window.addTab = addTab;

					//统一更新select2的接口
                    function initSelect2(id,url){
                        id.select2({
                            ajax: {
                                url: url,
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
                                    var groups = [{id: 0, text: '请选择'}];
                                    if (data.count > 0) {
                                        $.each(data.data, function (k, v) {
                                            groups.push({
                                                id: v,
                                                text: v
                                            })
                                        });
                                    }

                                    return {
                                        results: groups
                                    }

                                },
                                cache: true,
                                minimumInputLength: 5
                            }
                        });
                    }
                    window.initSelect2 = initSelect2;

					// 监听左侧导航
					$("#navMenu").on("click", "li", function(elem) {
						var href = $(this).find("a").attr("data-url");
						var name = $(this).text();
						var id = $(this).find("a").attr("id");
						addTab(id, href, name);
					});

					// tab的删除
					element.on('tabDelete(bodyTab)', function(data) {
						delIndex = data.index;
						titles.splice(delIndex, 1);
					});

					setInterval(function() {
						countQueue()
					}, 15000);

					setTimeout(function() {
						countQueue()
					}, 500);

					function countQueue() {
						$
								.get(
										"/queue/countByType",
										"nodeType=1",
										function(data, xhr) {
											if (xhr === 'success') {
												if (data.code === '0') {
													var allCount = parseInt(data.data.allCount);
													var distributedCount = parseInt(data.data.distributedCount);
													var percent = ''
															+ distributedCount
															/ allCount * 100
															+ '%';
													var rate = ''
															+ distributedCount
															+ "/" + allCount;
													element.progress(
															"successPercent",
															percent);
													element.progress(
															"successPercent",
															rate);

												}
											}
											xhr = null;
											data = null;
										});
						$
								.get(
										"/queue/countByType",
										"nodeType=2",
										function(data, xhr) {
											if (xhr === 'success') {
												if (data.code === '0') {
													var allCount = data.data.allCount;
													var distributedCount = data.data.distributedCount;
													var percent = ''
															+ distributedCount
															/ allCount * 100
															+ '%';
													var rate = ''
															+ distributedCount
															+ "/" + allCount;
													element.progress(
															"failPercent",
															percent);
													element
															.progress(
																	"failPercent",
																	rate);
												}
											}
											xhr = null;
											data = null;
										});
					}
				});