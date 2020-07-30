package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.ServerGetListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dto.request.BaseUiRequst;
import com.ppdai.infrastructure.mq.biz.entity.ServerEntity;
import com.ppdai.infrastructure.mq.biz.service.ServerService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ServerChangeStatusResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ServerGetListResponse;
import org.springframework.util.StringUtils;

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
    @Autowired
    private SoaConfig soaConfig;

    public ServerGetListResponse findBy(ServerGetListRequest serverGetListRequest) {
        Map<String, Object> parameterMap = new HashMap<>();
        if(!StringUtils.isEmpty(serverGetListRequest.getStatusFlag())){
            parameterMap.put(ServerEntity.FdStatusFlag,serverGetListRequest.getStatusFlag());
        }
        if(!StringUtils.isEmpty(serverGetListRequest.getServerVersion())){
            parameterMap.put(ServerEntity.FdServerVersion,serverGetListRequest.getServerVersion());
        }

        long count = serverService.count(parameterMap);
        List<ServerEntity> serverList = serverService.getList(parameterMap,
                Long.valueOf(serverGetListRequest.getPage()), Long.valueOf(serverGetListRequest.getLimit()));

        return new ServerGetListResponse(count,serverList);
    }


    public BaseUiResponse batchPull(List<ServerEntity> serverList){
        BaseUiResponse baseUiResponse=new BaseUiResponse();
        List<Long> serverIds=new ArrayList<>();
        try {
            for (ServerEntity serverEntity:serverList) {
                serverIds.add(serverEntity.getId());
            }
            baseUiResponse=checkServerCount(serverIds.size(),baseUiResponse);
            if(baseUiResponse.isSuc()){
                serverService.batchUpdate(serverIds,1);
            }

        }catch (Exception e){
            baseUiResponse.setSuc(false);
            baseUiResponse.setCode("1");
            baseUiResponse.setMsg("拉入异常："+e);
        }
        return baseUiResponse;

    }


    public BaseUiResponse batchPush(List<ServerEntity> serverList){
        BaseUiResponse baseUiResponse=new BaseUiResponse();
        List<Long> serverIds=new ArrayList<>();
        try {
            for (ServerEntity serverEntity:serverList) {
                serverIds.add(serverEntity.getId());
            }
            int onlineServer=serverService.getOnlineServerNum();
			if ((onlineServer - serverIds.size()) < soaConfig.getMinServerCount()
					&& soaConfig.getMinServerCount() > 0) {
                baseUiResponse.setSuc(false);
                baseUiResponse.setMsg("在线实例数量不能少于"+soaConfig.getMinServerCount());
            }else{
                serverService.batchUpdate(serverIds,0);
                baseUiResponse.setSuc(true);
            }

        }catch (Exception e){
            baseUiResponse.setSuc(false);
            baseUiResponse.setCode("1");
            baseUiResponse.setMsg("拉出异常："+e);
        }
        return baseUiResponse;

    }

    private BaseUiResponse checkServerCount(int batchCount,BaseUiResponse baseUiResponse){
        int onlineServer=serverService.getOnlineServerNum();

        if((onlineServer+batchCount)>soaConfig.getMaxServerCount()){
            baseUiResponse.setSuc(false);
            baseUiResponse.setMsg("在线实例不能超过:"+soaConfig.getMaxServerCount());
        }else if((onlineServer+batchCount)>soaConfig.getMinServerCount()&&batchCount>soaConfig.getBatchNum()) {
            baseUiResponse.setSuc(false);
            baseUiResponse.setMsg("一次拉取不能超过："+soaConfig.getBatchNum());
        }else{
            baseUiResponse.setSuc(true);
        }
        return  baseUiResponse;

    }

    public ServerChangeStatusResponse changeStatusFlag(String serverId) {
        ServerEntity serverEntity=serverService.get(Long.parseLong(serverId));
        serverEntity.setStatusFlag(serverEntity.getStatusFlag()==1 ? 0:1);
        serverEntity.setUpdateBy(userInfoHolder.getUser().getUserId());
        serverService.update(serverEntity);
        return new ServerChangeStatusResponse();
    }

    public BaseUiResponse<String> onLineServer(){
        BaseUiResponse baseUiResponse=new BaseUiResponse();
        //如果在线server数量小于apollo配置数量
        if(soaConfig.getMinServerCount()>serverService.getOnlineServerNum()){
            baseUiResponse.setMsg("在线server的数量小于："+soaConfig.getMinServerCount()+"请尽快处理！");
            baseUiResponse.setCode("1");
        }else{
            baseUiResponse.setCode("0");
        }
        return baseUiResponse;
    }
}
