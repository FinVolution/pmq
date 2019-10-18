package com.ppdai.infrastructure.ui.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;
import com.ppdai.infrastructure.mq.biz.service.ServerService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ServerChangeStatusResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ServerGetListResponse;

/**
 * @Author：wanghe02
 * @Date：2018/11/27 17:33
 */
@Service
public class UiServerService {
    @Autowired
    private ServerService serverService;
    @Autowired
    private UserInfoHolder userInfoHolder;

    public ServerGetListResponse findBy(BaseUiRequst baseUiRequst) {
        Map<String, Object> parameterMap = new HashMap<>();
        long count = serverService.count(parameterMap);
        List<ServerEntity> serverList = serverService.getList(parameterMap,
                Long.valueOf(baseUiRequst.getPage()), Long.valueOf(baseUiRequst.getLimit()));

        return new ServerGetListResponse(count,serverList);
    }

    public ServerChangeStatusResponse changeStatusFlag(String serverId) {
        ServerEntity serverEntity=serverService.get(Long.parseLong(serverId));
        serverEntity.setStatusFlag(serverEntity.getStatusFlag()==1 ? 0:1);
        serverEntity.setUpdateBy(userInfoHolder.getUser().getUserId());
        serverService.update(serverEntity);
        return new ServerChangeStatusResponse();
    }
}
