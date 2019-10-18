package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dto.ReadWriteEnum;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.ui.enums.NodeTypeEnum;
import com.ppdai.infrastructure.mq.biz.ui.enums.NormalFlagEnum;
import com.ppdai.infrastructure.mq.biz.ui.vo.PanelNodeVo;

@Service
public class UiPanelService {
    @Autowired
    private QueueService queueService;

    @Autowired
    private DbNodeService dbNodeService;

    public List<PanelNodeVo> getNodePanel() {
        Map<Long, DbNodeEntity> nodeMap =  dbNodeService.getCache();
        List<PanelNodeVo> panelNodeVoList = new ArrayList<>();
        Map<Long, QueueEntity> queueEntityMap = queueService.getAllQueueMap();
        nodeMap.values().forEach(dbNodeEntity -> {
            PanelNodeVo panelNodeVo = new PanelNodeVo();
            panelNodeVo.setId(dbNodeEntity.getId());
            panelNodeVo.setNormalFlag(NormalFlagEnum.getDescByCode(dbNodeEntity.getNormalFlag()));
            panelNodeVo.setNodeType(NodeTypeEnum.getDescByCode(dbNodeEntity.getNodeType()));
            panelNodeVo.setReadOnly(ReadWriteEnum.getDescByCode(dbNodeEntity.getReadOnly()));
            Long undistributedCount = queueEntityMap.values().stream().filter(queueEntity -> queueEntity.getDbNodeId() == dbNodeEntity.getId() && queueEntity.getTopicId() == 0).count();
            panelNodeVo.setUndistributedCount(undistributedCount);
            Long distributedCount = queueEntityMap.values().stream().filter(queueEntity -> queueEntity.getDbNodeId() == dbNodeEntity.getId() && queueEntity.getTopicId() != 0).count();
            panelNodeVo.setDistributedCount(distributedCount);
            panelNodeVoList.add(panelNodeVo);
        });
        return panelNodeVoList;
    }
}
