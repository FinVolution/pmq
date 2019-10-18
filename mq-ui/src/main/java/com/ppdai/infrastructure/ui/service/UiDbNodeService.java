package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ppdai.infrastructure.mq.biz.dto.AnalyseDto;
import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueEntity;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.AuditLogService;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.QueueOffsetService;
import com.ppdai.infrastructure.mq.biz.service.QueueService;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.mq.biz.service.common.AuditUtil;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.DbNodeAnalysisRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.DbNodeCreateRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.DbNodeGetListRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbCheckResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeAnalyseResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeBatchDilatationResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeBeforeChangeResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeChangeStatusResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeCompareResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeCreateResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeCreateSqlResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeCreateTableResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeDeleteResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeDilatationResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeGetListResponse;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.PhysicalMachineReportResponse;
import com.ppdai.infrastructure.mq.biz.ui.exceptions.CheckFailException;
import com.ppdai.infrastructure.mq.biz.ui.vo.PhysicalMachineReportVo;
import com.ppdai.infrastructure.mq.biz.ui.vo.QueueVo;

@Service
public class UiDbNodeService {

    private final String DATABASE_URL_PERFIX = "jdbc:mysql://";
    private final String DATABASE_URL_SUFFIX = "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false";
    private final String SEPARATOR_COLON = ":";
    private final String SEPARATOR_SLANT = "/";
    private final String TABLE_NAME_PERFIX = "message_";
    
    @Autowired
    private DbNodeService dbNodeService;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueOffsetService queueOffsetService;

    @Autowired
    private Message01Service message01Service;

    @Autowired
    private AuditLogService uiAuditLogService;
    @Autowired
    private UserInfoHolder userInfoHolder;
    @Autowired
    private UiQueueService uiQueueService;

    public DbCheckResponse checkFields(Long dbNodeId){
        DbCheckResponse dbCheckResponse = new DbCheckResponse();
        Map<String,Object> checkResult=new HashMap<>();
        List<QueueEntity> errorQueueList=new ArrayList<>();
        List<QueueOffsetEntity> errorQueueOffsetList=new ArrayList<>();
        checkResult.put("queue",errorQueueList);
        checkResult.put("queueOffset",errorQueueOffsetList);
        //获取缓存信息
        DbNodeEntity dbNodeEntity = dbNodeService.get(dbNodeId);
        Map<Long, QueueEntity> queueCache = queueService.getAllQueueMap();
        Map<Long,List<QueueOffsetEntity>> queueOffsetCache = new HashMap<>();
        List<QueueOffsetEntity> queueOffsetCacheList = queueOffsetService.getCacheData();
        //重新组装queueOffset内存数据结构
        if(!CollectionUtils.isEmpty(queueOffsetCacheList)){
            queueOffsetCacheList.forEach(queueOffsetEntity -> {
                if(queueOffsetCache.get(queueOffsetEntity.getQueueId())==null){
                    queueOffsetCache.put(queueOffsetEntity.getQueueId(),new ArrayList<>());
                }
                queueOffsetCache.get(queueOffsetEntity.getQueueId()).add(queueOffsetEntity);
            });
        }
        //校验这个dbNodeId下面所有的队列以及队列所对应的queueOffset中的db信息是否一致
        if(dbNodeEntity!=null){
            List<Map.Entry<Long, QueueEntity>> checkQueueList = queueCache.entrySet().stream().filter(e -> e.getValue().getDbNodeId() == dbNodeEntity.getId()).collect(Collectors.toList());
            if(checkQueueList!=null){
                for(Map.Entry<Long, QueueEntity> queue:checkQueueList){
                    if(!queue.getValue().getIp().equals(dbNodeEntity.getIp())){
                        errorQueueList.add(queue.getValue());
                    }
                    List<QueueOffsetEntity> queueOffsetEntities = queueOffsetCache.get(queue.getKey());
                    if(!CollectionUtils.isEmpty(queueOffsetEntities)){
                        for(QueueOffsetEntity queueOffsetEntity:queueOffsetEntities){
                            String[] split = queueOffsetEntity.getDbInfo().split("\\|");
                            String ip = split[0].trim();
                            if(!dbNodeEntity.getIp().equals(ip)){
                                errorQueueOffsetList.add(queueOffsetEntity);
                            }
                        }
                    }
                }
            }
        }
        dbCheckResponse.setData(checkResult);
        dbCheckResponse.setMsg("校验完成");
        return dbCheckResponse;
    }

    public DbNodeCreateResponse createOrUpdateDbNode(DbNodeCreateRequest dbNodeCreateRequest) {
        if (!StringUtils.hasLength(dbNodeCreateRequest.getDbName()) || !StringUtils.hasLength(dbNodeCreateRequest.getIp()) || dbNodeCreateRequest.getPort() == null) {
            throw new CheckFailException("数据库节点的名称，IP地址，端口号都不能为空。");
        }
        if(dbNodeCreateRequest.getIp().equals(dbNodeCreateRequest.getIpBak())){
            throw new CheckFailException("主库的ip和从库的ip不能为同一个");
        }
        DbNodeEntity dbNodeEntity = convertToDbNode(dbNodeCreateRequest);
        try {
            //新增或修改数据库节点的时候需要校验，可以避免发生错误的数据库连接串。
            dbNodeService.checkDataSource(dbNodeEntity);
        }catch (Exception e){
            throw new CheckFailException("数据库节点初始化失败：" + e.getMessage());
        }
        if (dbNodeCreateRequest.getId() == null) {
            //新增
            dbNodeService.insert(dbNodeEntity);
            dbNodeService.createDataSource(dbNodeEntity);
            //保存数据节点成功之后，需要实时更新缓存。
            dbNodeService.updateCache();
            message01Service.setDbId(dbNodeEntity.getId());
            //根据节点对应的实际数据库中的message表的数量创建对应数量的Queue。
            int tableQuantity = message01Service.getTableQuantityByDbName(dbNodeEntity.getDbName());
            List<QueueEntity> queueEntityList = new ArrayList<>();
            for (int i = 1; i <= tableQuantity; i++) {
                String suffix = String.format("%02d", i);
                String tableName = TABLE_NAME_PERFIX + suffix;
                QueueEntity queueEntity = createQueueByDbNode(dbNodeEntity);
                queueEntity.setTbName(tableName);
                queueEntityList.add(queueEntity);
            }
            queueService.insertBatch(queueEntityList);
            uiAuditLogService.recordAudit(DbNodeEntity.TABLE_NAME, dbNodeEntity.getId(), "创建数据节点 ");
        } else {
            //更新
            DbNodeEntity oldDbNodeEntity = dbNodeService.get(dbNodeCreateRequest.getId());
            if (oldDbNodeEntity != null) {
                //编辑节点时，nodeType和readOnly不能修改。
                dbNodeEntity.setNodeType(oldDbNodeEntity.getNodeType());
                dbNodeEntity.setReadOnly(oldDbNodeEntity.getReadOnly());
                dbNodeService.update(dbNodeEntity);
                dbNodeService.updateCache();
                String ip = dbNodeEntity.getIp();
                String dbName = dbNodeEntity.getDbName();
                String oldIp = oldDbNodeEntity.getIp();
                String oldDbName = oldDbNodeEntity.getDbName();
                //只有在ip或者dbName发生变更的时候才会更新Queue
                if (!ip.equals(oldIp) || !dbName.equalsIgnoreCase(oldDbName)) {
                    queueService.updateForDbNodeChange(ip, dbName, oldIp, oldDbName);
                }
                uiAuditLogService.recordAudit(DbNodeEntity.TABLE_NAME, dbNodeCreateRequest.getId(), "变更数据节点: " + AuditUtil.diff(oldDbNodeEntity, dbNodeEntity));
            }
        }
        return new DbNodeCreateResponse();
    }

    public DbNodeGetListResponse queryByPage(DbNodeGetListRequest dbNodeGetListRequest) {
        Map<String, Object> conditionMap = new HashMap<>();
        if (!StringUtils.isEmpty(dbNodeGetListRequest.getName())) {
            conditionMap.put(DbNodeEntity.FdDbName, dbNodeGetListRequest.getName());
        }
        if (!StringUtils.isEmpty(dbNodeGetListRequest.getId())) {
            conditionMap.put(DbNodeEntity.FdId, Long.valueOf(dbNodeGetListRequest.getId()));
        }
        if (!StringUtils.isEmpty(dbNodeGetListRequest.getReadOnly())) {
            conditionMap.put(DbNodeEntity.FdReadOnly, Integer.valueOf(dbNodeGetListRequest.getReadOnly()));
        }
        if(!StringUtils.isEmpty(dbNodeGetListRequest.getIp())){
            conditionMap.put(DbNodeEntity.FdIp, dbNodeGetListRequest.getIp());
        }
        long count = dbNodeService.count(conditionMap);
        if (count == 0) {
            return new DbNodeGetListResponse(count, null);
        }
        List<DbNodeEntity> dbEntityList = dbNodeService.getList(conditionMap, Long.valueOf(dbNodeGetListRequest.getPage()), Long.valueOf(dbNodeGetListRequest.getLimit()));
        return new DbNodeGetListResponse(count, dbEntityList);
    }

    public DbNodeDeleteResponse deleteDbNode(Long dbNodeId) {
        if (dbNodeId == null) {
            throw new CheckFailException("数据库节点Id不能为空。");
        }
        DbNodeEntity dbNodeEntity = dbNodeService.get(dbNodeId);
        if (dbNodeEntity == null) {
            throw new CheckFailException("数据库节点Id无效。");
        }
        if (dbNodeEntity.getReadOnly() != 0) {
            throw new CheckFailException("该数据库节点是只读节点，不能删除。");
        }
        dbNodeService.delete(dbNodeId);
        return new DbNodeDeleteResponse();
    }

    public DbNodeCompareResponse compare(Long dbNodeId, String dbName) {
        if (StringUtils.isEmpty(dbNodeId) || StringUtils.isEmpty(dbName)) {
            throw new CheckFailException("数据库节点Id和Name不能为空。");
        }
        List<Long> quantity = getQuantity(dbNodeId,dbName);
        return new DbNodeCompareResponse(2L, quantity);
    }

    public DbNodeDilatationResponse dilatation(Long dbNodeId) {
        if (dbNodeId == null) {
            throw new CheckFailException("数据库节点Id。");
        }
        DbNodeEntity dbNodeEntity = dbNodeService.get(dbNodeId);
        if (dbNodeEntity != null) {
            batchInsert(dbNodeEntity);
            return new DbNodeDilatationResponse();
        } else {
            throw new CheckFailException("数据库节点Id[" + dbNodeEntity + "]无效。");
        }

    }

    public DbNodeCreateSqlResponse createSql(String dbName, String quantity,String nodeType) {
        int tableQuantity = 100;
        int normalLimit = 200;
        int failLimit=500;
        if (dbName == null) {
            throw new CheckFailException("数据库名称不能为null.");
        }
        int actualQuantity = 0;
        try{
            actualQuantity = Integer.parseInt(quantity);
        }catch (NumberFormatException e){
            throw new CheckFailException("指定的表数量[" + quantity +"]不合法.");
        }

        //如果是正常消息表，最多生成200个表
        if("1".equals(nodeType)){
            if (actualQuantity > 0 && actualQuantity <= normalLimit) {
                tableQuantity = actualQuantity;
            }else {
                throw new CheckFailException("正常消息表，指定的表数量必须在1~200之间.");
            }
        }
        //如果是失败消息表，最多生成500个表
        else if("2".equals(nodeType)){
            if (actualQuantity > 0 && actualQuantity <= failLimit) {
                tableQuantity = actualQuantity;
            }else {
                throw new CheckFailException("失败消息表，指定的表数量必须在1~500之间.");
            }
        }

        List<String> data = new ArrayList<>();
        StringBuilder sb = new StringBuilder("use " + dbName + ";");
        for (int i = 1; i <= tableQuantity; i++) {
            sb.append("\r\n");
            sb.append("create table IF NOT EXISTS message_" + String.format("%02d", i) + "(").append("\r");
            sb.append("id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',").append("\r");
            sb.append("`biz_id` varchar(50)  DEFAULT NULL COMMENT '业务id',").append("\r");
            sb.append("`head` varchar(1000)  DEFAULT NULL COMMENT '消息头',").append("\r");
            sb.append("`body` text  COMMENT '消息体',").append("\r");
            sb.append("`send_ip` varchar(20)  NOT NULL COMMENT '发送的ip',").append("\r");            
            sb.append("`send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',").append("\r");
            sb.append("`retry_count` int(11) DEFAULT '0',").append("\r");
            sb.append("`trace_id` varchar(200)  DEFAULT NULL,").append("\r");
            sb.append("`tag` varchar(1000)  DEFAULT NULL,").append("\r");
            sb.append("PRIMARY KEY (`id`)").append("\r");
            sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息';").append("\r");
        }
        String createTableSql = sb.toString();
        data.add(createTableSql);
        return new DbNodeCreateSqlResponse(1L, data);
    }

    private DbNodeEntity convertToDbNode(DbNodeCreateRequest dbNodeCreateRequest) {
        DbNodeEntity dbNodeEntity = new DbNodeEntity();
        dbNodeEntity.setIp(dbNodeCreateRequest.getIp());
        dbNodeEntity.setPort(dbNodeCreateRequest.getPort());
        dbNodeEntity.setDbName(dbNodeCreateRequest.getDbName());
        dbNodeEntity.setDbUserName(dbNodeCreateRequest.getDbUserName());
        dbNodeEntity.setDbPass(dbNodeCreateRequest.getDbPass());

        if(!StringUtils.isEmpty(dbNodeCreateRequest.getIpBak())&&!StringUtils.isEmpty(dbNodeCreateRequest.getPortBak())&&
                !StringUtils.isEmpty(dbNodeCreateRequest.getDbUserNameBak())&&!StringUtils.isEmpty(dbNodeCreateRequest.getDbPassBak())){
            dbNodeEntity.setIpBak(dbNodeCreateRequest.getIpBak());
            dbNodeEntity.setPortBak(dbNodeCreateRequest.getPortBak());
            dbNodeEntity.setDbUserNameBak(dbNodeCreateRequest.getDbUserNameBak());
            dbNodeEntity.setDbPassBak(dbNodeCreateRequest.getDbPassBak());
        }

        dbNodeEntity.setConStr(DATABASE_URL_PERFIX + dbNodeCreateRequest.getIp() + SEPARATOR_COLON + dbNodeCreateRequest.getPort() + SEPARATOR_SLANT + dbNodeCreateRequest.getDbName() + DATABASE_URL_SUFFIX);
        dbNodeEntity.setNormalFlag(1);
        dbNodeEntity.setUpdateBy(userInfoHolder.getUserId());
        dbNodeEntity.setRemark(dbNodeCreateRequest.getRemark());
        dbNodeEntity.setIsActive(0);
        if(dbNodeCreateRequest.getId() == null) {
            dbNodeEntity.setInsertBy(userInfoHolder.getUserId());
            dbNodeEntity.setReadOnly(dbNodeCreateRequest.getReadOnly());
            dbNodeEntity.setNodeType(dbNodeCreateRequest.getNodeTypes());
        }else{
        	dbNodeEntity.setId(dbNodeCreateRequest.getId());
        }
        return dbNodeEntity;
    }

    private QueueEntity createQueueByDbNode(DbNodeEntity dbNodeEntity) {
        QueueEntity queueEntity = new QueueEntity();
        queueEntity.setTopicId(0);
        queueEntity.setTopicName("");
        queueEntity.setDbNodeId(dbNodeEntity.getId());
        queueEntity.setNodeType(dbNodeEntity.getNodeType());
        queueEntity.setIp(dbNodeEntity.getIp());
        queueEntity.setDbName(dbNodeEntity.getDbName());
        queueEntity.setTbName("");
        //默认值是1 可读可写
        queueEntity.setReadOnly(1);
        queueEntity.setMinId(0L);
        queueEntity.setInsertBy(userInfoHolder.getUserId());
        queueEntity.setUpdateBy(userInfoHolder.getUserId());
        queueEntity.setLockVersion(1L);
        return queueEntity;
    }

    public DbNodeChangeStatusResponse changeStatus(Long id, Integer readOnly) {
        if(id != null && readOnly != null){
            DbNodeEntity dbNodeEntity = dbNodeService.get(id);
            if(dbNodeEntity == null){
                throw new CheckFailException("找不到数据节点【"+ id +"】。");
            }
            Integer oldStatus = dbNodeEntity.getReadOnly();
            dbNodeEntity.setReadOnly(readOnly);
            dbNodeEntity.setUpdateBy(userInfoHolder.getUserId());
            dbNodeService.update(dbNodeEntity);
            uiAuditLogService.recordAudit(DbNodeEntity.TABLE_NAME, id, "修改节点状态: { " + oldStatus + " -> " +readOnly+" }");
                return new DbNodeChangeStatusResponse();
        }else{
            throw new CheckFailException("参数id和readOnly都不能为Null。");
        }

    }

    public DbNodeAnalyseResponse analyse(DbNodeAnalysisRequest dbNodeAnalysisRequest) {
        if (dbNodeAnalysisRequest.getId() != null) {
            List<AnalyseDto> countTopics = queueService.countTopicByNodeId(dbNodeAnalysisRequest.getId(), Long.valueOf(dbNodeAnalysisRequest.getPage()), Long.valueOf(dbNodeAnalysisRequest.getLimit()));
            List<AnalyseDto> distributedNodes = queueService.getDistributedNodes(dbNodeAnalysisRequest.getId());
            Map<Long,AnalyseDto> queueQuantities = queueService.getQueueQuantity();
            Map<Long, String> dbNodeIdMap = new HashMap<>();
            Map<Long, String> dbNodeStrMap = new HashMap<>();
            for (AnalyseDto distributedNode : distributedNodes) {
                Long topicId = distributedNode.getTopicId();
                String nodeIds = "";
                String dbNodeStr = "";
                if(dbNodeIdMap.containsKey(topicId)){
                    nodeIds = dbNodeIdMap.get(topicId) + "," + distributedNode.getDbNodeId();
                    dbNodeStr = dbNodeStrMap.get(topicId) + "; " + distributedNode.getIp() + "/" + distributedNode.getDbName();
                }else {
                    nodeIds = "" + distributedNode.getDbNodeId();
                    dbNodeStr = distributedNode.getIp() + "/" + distributedNode.getDbName();
                }
                dbNodeIdMap.put(topicId,nodeIds);
                dbNodeStrMap.put(topicId,dbNodeStr);
            }
            for (AnalyseDto countTopic : countTopics) {
                String nodeIds = dbNodeIdMap.get(countTopic.getTopicId());
                countTopic.setDbNodeIds(nodeIds);
                String dbNodeStr = dbNodeStrMap.get(countTopic.getTopicId());
                countTopic.setDbStr(dbNodeStr);
                AnalyseDto queueQuantity = queueQuantities.get(countTopic.getTopicId());
                countTopic.setQueueQuantity(queueQuantity.getQueueQuantity());
                countTopic.setWriteableQueueQuantity(queueQuantity.getWriteableQueueQuantity() == null ? 0 : queueQuantity.getWriteableQueueQuantity());
            }
            return new DbNodeAnalyseResponse(0L, countTopics);
        } else {
            throw new CheckFailException("参数id不能为Null。");
        }
    }

    public DbNodeCreateTableResponse createTableIfNecessary(Long dbNodeId, Integer quantity) {
        if(dbNodeId == null || quantity == null){
            throw new CheckFailException("数据节点Id和创建表的数量都不能为null。");
        }
        message01Service.setDbId(dbNodeId);
        for (int i = 1; i <= quantity; i++) {
            String suffix = String.format("%02d", i);
            String tableName = TABLE_NAME_PERFIX + suffix;
            message01Service.createMessageTable(tableName);
        }
        return new DbNodeCreateTableResponse();
    }

    public DbNodeBeforeChangeResponse beforeChange(Long id, Integer readOnly) {
        if(readOnly == 2 || readOnly == 3){
            //根据dbNodeId查询出该节点下Topic的节点分布情况
            List<AnalyseDto> analyseDtoList = queueService.getDistributedNodes(id);
            if(!CollectionUtils.isEmpty(analyseDtoList)){
                //讲analyseDtoList转换为<topicId , dbNodeIds>的形式
                Map<Long,List<Long>> topicIdDbNodeIds = new HashMap<>();
                analyseDtoList.stream().forEach(a -> {
                    List<Long> dbNodeIds = null;
                    if(!topicIdDbNodeIds.containsKey(a.getTopicId())){
                        dbNodeIds = new ArrayList<>();
                    }else {
                        dbNodeIds = topicIdDbNodeIds.get(a.getTopicId());
                    }
                    dbNodeIds.add(a.getDbNodeId());
                    topicIdDbNodeIds.put(a.getTopicId(),dbNodeIds);
                });
                List<Long> couldNotChangeTopicIds = new ArrayList<>();
                for(Map.Entry<Long,List<Long>> entry : topicIdDbNodeIds.entrySet()){
                    couldNotChangeTopicIds.add(entry.getKey());
                    //取出每一个Topic分布的节点Id列表，如果Id列表数量大于1，表示此Topic除了当前节点外，还分配在了其它节点上。
                    List<Long> dbNodeIds = entry.getValue();
                    if(dbNodeIds.size() > 1){
                        //获取此Topic除当前节点之外的其它节点
                        dbNodeIds.remove(id);
                        List<DbNodeEntity> dbNodeEntityList = dbNodeService.getList(dbNodeIds);
                        //如果其它节点都是不可写（只读，不可读不可写）的状态，则不能将当前节点设为不可写。同时记录Topic到couldNotChangeTopicIds中。
                        for (DbNodeEntity dbNodeEntity : dbNodeEntityList) {
                            if(dbNodeEntity.getReadOnly() == 1){
                                couldNotChangeTopicIds.remove(entry.getKey());
                                break;
                            }
                        }
                    }
                }
                if(couldNotChangeTopicIds.size() > 0){
                    return new DbNodeBeforeChangeResponse("-1","下列Topic "+couldNotChangeTopicIds +"至少要保证有一个节点可写。");
                }
            }
        }
        return new DbNodeBeforeChangeResponse();
    }

    public DbNodeBatchDilatationResponse batchDilatation(){
        List<DbNodeEntity> dbNodeEntityList = dbNodeService.getList();
        if(!CollectionUtils.isEmpty(dbNodeEntityList)){
            for (DbNodeEntity dbNodeEntity : dbNodeEntityList) {
                Long id = dbNodeEntity.getId();
                try{
                    String dbName = dbNodeEntity.getDbName();
                    List<Long> quantity = getQuantity(id,dbName);
                    long quantityFromQueue = quantity.get(0);
                    long quantityFromDb = quantity.get(1);
                    if(quantityFromQueue != quantityFromDb){
                        batchInsert(dbNodeEntity);
                    }
                }catch (Exception e){
                    continue;
                }
            }
        }
        return new DbNodeBatchDilatationResponse();
    }

    private List<Long> getQuantity(Long dbNodeId, String dbName){
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("dbNodeId", dbNodeId);
        conditionMap.put("isActive", 0);
        long quantityFromQueue = queueService.count(conditionMap);
        message01Service.setDbId(dbNodeId);
        int quantityFromDb = message01Service.getTableQuantityByDbName(dbName);
        List<Long> quantity = new ArrayList<>();
        quantity.add(quantityFromQueue);
        quantity.add((long) quantityFromDb);
        return quantity;
    }

    private void batchInsert(DbNodeEntity dbNodeEntity){
        List<String> tableNamesFromQueue = queueService.getTableNamesByDbNode(dbNodeEntity.getId());
        message01Service.setDbId(dbNodeEntity.getId());
        List<String> tableNamesFromDb = message01Service.getTableNamesByDbName(dbNodeEntity.getDbName());
        boolean initial = CollectionUtils.isEmpty(tableNamesFromQueue) && !CollectionUtils.isEmpty(tableNamesFromDb);
        if (initial || tableNamesFromDb.removeAll(tableNamesFromQueue)) {
            List<QueueEntity> queueEntityList = new ArrayList<>();
            for (String tableName : tableNamesFromDb) {
                QueueEntity queueEntity = createQueueByDbNode(dbNodeEntity);
                queueEntity.setTbName(tableName);
                queueEntityList.add(queueEntity);
            }
            queueService.insertBatch(queueEntityList);
        }
    }

    public PhysicalMachineReportResponse getPhysicalMachineReportData(String ip){
        List<PhysicalMachineReportVo> physicalMachineReportVos=new ArrayList<>();
        Map<String,PhysicalMachineReportVo> physicalMachineReportMap=new HashMap<>();
        List<QueueVo> queueListAvg=uiQueueService.getQueueListAvg();
        for (QueueVo queueVo:queueListAvg) {
            if(!physicalMachineReportMap.containsKey(queueVo.getIp())){
                PhysicalMachineReportVo physicalMachineReportVo=new PhysicalMachineReportVo();
                physicalMachineReportVo.setIp(queueVo.getIp());
                physicalMachineReportMap.put(queueVo.getIp(),physicalMachineReportVo);
            }
            physicalMachineReportMap.get(queueVo.getIp()).setAvgCount(physicalMachineReportMap.get(queueVo.getIp()).getAvgCount()+queueVo.getAvgCount());
            physicalMachineReportMap.get(queueVo.getIp()).setMsgCount(physicalMachineReportMap.get(queueVo.getIp()).getMsgCount()+queueVo.getMsgCount());
        }

        if(StringUtils.isEmpty(ip)){
            for (String key:physicalMachineReportMap.keySet()) {
                physicalMachineReportVos.add(physicalMachineReportMap.get(key));
            }
        }else{
            physicalMachineReportVos.add(physicalMachineReportMap.get(ip));
        }

        return new PhysicalMachineReportResponse(new Long(physicalMachineReportVos.size()),physicalMachineReportVos);

    }
}
