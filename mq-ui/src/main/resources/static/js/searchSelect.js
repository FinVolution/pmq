layui.define(['form'], function (exports) {
    var item_list = [];

    var refreshUserGroupDiv = function(component) {
        var userGroupDiv = '';
        $.each(item_list, function (k, v) {
            userGroupDiv += '' +
                '    <div style="padding-top: 5px" class="layui-inline">\n' +
                '        <button class="layui-btn layui-btn-primary layui-btn-xs" style="cursor: default" type="button" >'+ v + '</button>\n' +
                '        <button class="layui-btn layui-btn-normal layui-btn-xs  remove-user" value="'+ v +'" type="button">\n' +
                '            <i class="layui-icon">&#xe640;</i>\n' +
                '        </button>\n' +
                '    </div>'
        });
        component.find('#user-group').html(userGroupDiv);
    };


    var obj = {
        render: function () {
            var form = layui.form;
            $('.search-select').html('' +
                '            <div class="layui-input-block">\n' +
                '                <div class="layui-inline">\n' +
                '                    <div class="layui-input-inline">\n' +
                '<select lay-ignore class="js-data-example-ajax" style="width: 220px" lay-filter="ownerNamesSelect" id="ownerNamesSelect" autocomplete="off" class="layui-input"></select>\n' +
                '                    </div>\n' +
                '                <div class="layui-input-inline">\n' +
                '                        <button class="layui-btn layui-btn-xs"  style="margin-top: 2px; margin-left: 30px" type="button" id="add-user">\n' +
                '                            <i class="layui-icon" style="cursor:pointer">&#xe654;</i>\n' +
                '                        </button>\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '            </div>\n' +
                '            <div class="layui-input-block">\n' +
                '                <div class="layui-btn-group" id="user-group">\n' +
                '                </div>\n' +
                '            </div>'
            );
            $('.js-data-example-ajax').select2({
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


            $("body").on("click", ".search-select #add-user", function () {
                var component = $(this).closest(".search-select");
                if ($('.js-data-example-ajax').select2('data')[0]) {
                    var user_val =  $('.js-data-example-ajax').select2('data')[0].text;
                } else {
                    return;
                }
                if (user_val == '') {
                    return;
                }
                if ($.inArray(user_val, item_list) != -1) {
                    return;
                }
                item_list.push(user_val);
                refreshUserGroupDiv(component);
                component.find("#ownerNamesSelect").html("");
                form.render();
            });



            $('body').on("click", ".remove-user", function () {
                var component = $(this).closest(".search-select");
                var user_val =  $(this).val();
                var i = $.inArray(user_val, item_list);
                item_list.splice(i, 1);
                refreshUserGroupDiv(component);
            });
        },

        clear: function () {
            item_list = [];
            refreshUserGroupDiv($('.search-select'));
        },

        getItemIds: function () {
            var itemIds = [];
            $.each(item_list, function (k, v) {
                itemIds.push( v.split('|')[0]);
            });
            return itemIds.join(",");
        },

        addItem: function (itemIds, itemNames, selector) {
            var itemTdsArray = itemIds.split(',');
            var itemNamesArray = itemNames.split(',');
            $.each(itemTdsArray, function (k, v) {
                item_list.push(v +"|"+ itemNamesArray[k]);
            });
            if (typeof selector === 'undefined') {
                refreshUserGroupDiv($('.search-select'));
            } else {
                refreshUserGroupDiv($(selector));
            }
        },

        addItemWithIds: function (itemIds, selector) {
            $.get('/user/getByUserIds', "userIds="+itemIds, function (data, xhr) {
                if (xhr === 'success') {
                    if (data.code == 0) {
                        var itemIdsName = data.data.split(";");
                        var ids = itemIdsName[0];
                        var names = itemIdsName[1];
                        addItem(ids, names, selector);
                    }
                }
            })
        },

        getItemNames: function () {
            var itemIds = [];
            $.each(item_list, function (k, v) {
                itemIds.push( v.split('|')[1]);
            });
            return itemIds.join(",");
        }
    };
    var addItem = obj.addItem;


    exports("searchSelect", obj);
});
