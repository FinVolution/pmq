package com.ppdai.infrastructure.ui.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dal.meta.ConsumerGroupRepository;
import com.ppdai.infrastructure.mq.biz.entity.ConsumerGroupEntity;
import com.ppdai.infrastructure.mq.biz.service.ConsumerGroupService;
import com.ppdai.infrastructure.mq.biz.service.ConsumerService;
import com.ppdai.infrastructure.mq.biz.service.RoleService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.ConsumerGroupGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupGetByIdResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupGetNamesResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.ConsumerGroupSelectResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConsumerGroupVo;

@Service
public class UiConsumerGroupService {
    @Autowired
    private ConsumerGroupRepository consumerGroupRepository;
    @Autowired
    private ConsumerGroupService consumerGroupService;
    @Autowired
	private RoleService roleService;
    @Autowired
    private UserInfoHolder userInfoHolder;
    /**
     * 根据条件查询实例
     *
     * @param consumerGroupGetListRequest
     * @return
     */
    public ConsumerGroupGetListResponse findBy(ConsumerGroupGetListRequest consumerGroupGetListRequest) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("name", consumerGroupGetListRequest.getConsumerGroupName());
        parameterMap.put("appId", consumerGroupGetListRequest.getAppId());
        parameterMap.put("ownerNames", consumerGroupGetListRequest.getOwnerNames());
        parameterMap.put("subEnv",consumerGroupGetListRequest.getSubEnv());
        if (StringUtils.isNotBlank(consumerGroupGetListRequest.getId())) {
            parameterMap.put("id", Long.valueOf(consumerGroupGetListRequest.getId()));
        }
        if (StringUtils.isNotEmpty(consumerGroupGetListRequest.getMode())) {
            parameterMap.put("mode", Integer.parseInt(consumerGroupGetListRequest.getMode()));
        }
        long page = Long.valueOf(consumerGroupGetListRequest.getPage());
        long pageSize = Long.valueOf(consumerGroupGetListRequest.getLimit());
        parameterMap.put("start1", (page - 1) * pageSize);
        parameterMap.put("offset1", pageSize);
        long count = consumerGroupService.countByOwnerNames(parameterMap);
        List<ConsumerGroupEntity> consumerGroupList = consumerGroupService.getByOwnerNames(parameterMap);
        String currentUserId = userInfoHolder.getUserId();
        List<ConsumerGroupVo> consumerGroupVoList = consumerGroupList.stream().map(consumerGroupEntity -> {
            ConsumerGroupVo consumerGroupVo = new ConsumerGroupVo(consumerGroupEntity);
            consumerGroupVo.setRole(roleService.getRole(currentUserId, consumerGroupEntity.getOwnerIds()));
            return consumerGroupVo;
        }).collect(Collectors.toList());

        return new ConsumerGroupGetListResponse(count, consumerGroupVoList);
    }

    public ConsumerGroupEntity findById(long consumerGroupId) {
        return consumerGroupRepository.getById(consumerGroupId);
    }

    public ConsumerGroupGetByIdResponse getById(long id) {
        return new ConsumerGroupGetByIdResponse(consumerGroupService.get(id));
    }

    public ConsumerGroupGetNamesResponse getConsumerGpNames(String keyword, int offset, int limit) {
        Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
        List<String> consumerGroupList = new LinkedList<>();
        for (String name : consumerGroupMap.keySet()) {
            if (name.toLowerCase().startsWith(keyword.toLowerCase())) {
                consumerGroupList.add(name);
            }
        }

        if (offset + limit > consumerGroupList.size()) {
            limit = consumerGroupList.size() - offset;
        }
        return new ConsumerGroupGetNamesResponse(new Long(consumerGroupList.subList(offset, limit).size()),
                consumerGroupList.subList(offset, limit));

    }

    public ConsumerGroupSelectResponse searchConsumerGroups(String keyword, int offset, int limit) {
        Map<String, ConsumerGroupEntity> consumerGroupMap = consumerGroupService.getCache();
        List<String> consumerGroupList = new LinkedList<>();
        for (String name : consumerGroupMap.keySet()) {
            if (name.indexOf(keyword) != -1) {
                consumerGroupList.add(name);
            }
        }
        Collections.sort(consumerGroupList, new Comparator<String>() {
            @Override
            public int compare(String q1, String q2) {
                return q1.compareTo(q2);
            }
        });

        if (offset + limit > consumerGroupList.size()) {
            limit = consumerGroupList.size() - offset;
        }

        return new ConsumerGroupSelectResponse(new Long(consumerGroupList.subList(offset, limit).size()),
                consumerGroupList.subList(offset, limit));
    }


}
