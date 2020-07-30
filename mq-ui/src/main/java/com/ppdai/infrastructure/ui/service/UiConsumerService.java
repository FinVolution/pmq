package com.ppdai.infrastructure.ui.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerEntity;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.CacheUpdateHelper;
import com.ppdai.infrastructure.mq.biz.service.common.DbService;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.ConsumerGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerDeleteResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConsumerVo;

@Service
public class UiConsumerService {
    @Autowired
    private ConsumerService consumerService;
    @Autowired
	private RoleService roleService;
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
    private DbService dbService;
    @Autowired
    private SoaConfig soaConfig;
    @Autowired
    private UserInfoHolder userInfoHolder;
    private Map<String,String> compareType=new HashMap<>();
    {
        compareType.put("0","=");
        compareType.put("1",">");
        compareType.put("2",">=");
        compareType.put("3","=");
        compareType.put("4","<");
        compareType.put("5","<=");
    }
    public ConsumerGetListResponse getConsumerByPage(ConsumerGetListRequest consumerGetListRequest){
        Map<String, ConsumerGroupEntity> consumerGroupMap=consumerGroupService.getCache();
        Map<String, Object> conditionMap=new HashMap<>();
        if(consumerGetListRequest.getId()!=null){
            conditionMap.put(ConsumerEntity.FdId, consumerGetListRequest.getId());
        }
        if(!StringUtils.isEmpty(consumerGetListRequest.getIp())){
            conditionMap.put(ConsumerEntity.FdIp, consumerGetListRequest.getIp());
        }
        if(!StringUtils.isEmpty(consumerGetListRequest.getConsumerGroupNames())){
            conditionMap.put(ConsumerEntity.FdConsumerGroupNames, consumerGetListRequest.getConsumerGroupNames());
        }
        if(!StringUtils.isEmpty(consumerGetListRequest.getSdkVersion())){
            conditionMap.put(ConsumerEntity.FdSdkVersion,compareType.get(consumerGetListRequest.getCompareType())+"'"+ consumerGetListRequest.getSdkVersion()+"'");
        }

        if(!StringUtils.isEmpty(consumerGetListRequest.getHeartBeat())){
            conditionMap.put("heartBeat",Long.parseLong(consumerGetListRequest.getHeartBeat()));
            conditionMap.put("consumerCheckInterval",soaConfig.getConsumerCheckInterval());
        }

        Long count=consumerService.countBy(conditionMap);
        long page=Long.valueOf(consumerGetListRequest.getPage());
        long pageSize=Long.valueOf(consumerGetListRequest.getLimit());
        conditionMap.put("start1", (page - 1) * pageSize);
        conditionMap.put("offset1", pageSize);
        List<ConsumerEntity> consumerEntityList = consumerService.getListBy(conditionMap);
        String currentUserId = userInfoHolder.getUserId();
        List<ConsumerVo> consumerVoList=consumerEntityList.stream().map(consumerEntity ->{
                    ConsumerVo consumerVo=new ConsumerVo(consumerEntity);
                    String ownerIds="";
                    String ownerNames="";
                    String consumerGroupNames=consumerEntity.getConsumerGroupNames();
                    if(!StringUtils.isEmpty(consumerGroupNames)){
                        List<String> nameList=Arrays.asList(consumerGroupNames.split(","));
                        for (String name:nameList) {
                            ConsumerGroupEntity consumerGroupEntity=consumerGroupMap.get(name);
                            if(consumerGroupEntity!=null){
                                if(StringUtils.isEmpty(ownerIds)){
                                    ownerIds=consumerGroupEntity.getOwnerIds();
                                }else{
                                    ownerIds=ownerIds+","+consumerGroupEntity.getOwnerIds();
                                }

                                if(StringUtils.isEmpty(ownerNames)){
                                    ownerNames=consumerGroupEntity.getOwnerNames();
                                }else{
                                    ownerNames=ownerNames+","+consumerGroupEntity.getOwnerNames();
                                }
                            }

                        }
                    }
                    int role=roleService.getRole(currentUserId,ownerIds);
                    consumerVo.setRole(role);
                    consumerVo.setOwnerNames(ownerNames);
                    return consumerVo;
                }
        ).collect(Collectors.toList());

        return new ConsumerGetListResponse(count,consumerVoList);
    }

    /**
     * 心跳延迟超过20秒则删除
     * @param consumerId
     * @return
     */
    public ConsumerDeleteResponse deleteByTime(long consumerId){
        CacheUpdateHelper.updateCache();
        ConsumerDeleteResponse baseUiResponse=new ConsumerDeleteResponse();
        ConsumerEntity consumerEntity=consumerService.get(consumerId);
        long heartTime=consumerEntity.getHeartTime().getTime();
        long now=dbService.getDbTime().getTime();
        long intervals=now-heartTime;
        int interval=soaConfig.getConsumerCheckInterval();
        if((intervals/1000)>interval){
            //consumerRepository.deleteByConsumerId(consumerId);
            consumerService.deleteByConsumers(Arrays.asList(consumerEntity));
            baseUiResponse.setCode("0");
            baseUiResponse.setMsg("删除成功！");
        }else{
            baseUiResponse.setCode("1");
            baseUiResponse.setMsg("心跳延迟不足"+interval+"秒，不可删除！");
        }
        return baseUiResponse;
    }
}
