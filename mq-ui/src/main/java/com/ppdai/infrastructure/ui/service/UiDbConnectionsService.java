package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ppdai.infrastructure.mq.biz.dto.response.BaseUiResponse;
import com.ppdai.infrastructure.mq.biz.entity.QueueOffsetEntity;
import com.ppdai.infrastructure.mq.biz.service.*;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.QueueCountResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.OnLineNumsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.service.common.DbService;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeConnectionsResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConnectionsVo;
import org.springframework.util.StringUtils;

/**
 * @Author：wanghe02 @Date：2019/4/15 11:11
 */
@Service
public class UiDbConnectionsService {
	@Autowired
	private Message01Service message01Service;
	@Autowired
	private DbNodeService dbNodeService;
	@Autowired
	private DbService dbService;
	@Autowired
	private Environment env;
	@Autowired
	private ConsumerService consumerService;
	@Autowired
	private UiQueueOffsetService uiQueueOffsetService;
	@Autowired
	private ServerService serverService;
	@Autowired
	private UiQueueService uiQueueService;
	@Autowired
	private QueueOffsetService queueOffsetService;

	public DbNodeConnectionsResponse getConnections() {
		Map<Long, DbNodeEntity> dbNodeMap = dbNodeService.getCache();
		Map<String, DbNodeEntity> dataSourceMap = new HashMap<>();
		List<ConnectionsVo>connectionsVoList=new ArrayList<>();
		try {
			for (long dbId : dbNodeMap.keySet()) {
				if (!dataSourceMap.containsKey(dbNodeMap.get(dbId).getIp())) {
					dataSourceMap.put(dbNodeMap.get(dbId).getIp(), dbNodeMap.get(dbId));
				}
			}
			for (String ip : dataSourceMap.keySet()) {
				message01Service.setDbId(dataSourceMap.get(ip).getId());
				String maxConnection = message01Service.getMaxConnectionsCount();
				message01Service.setDbId(dataSourceMap.get(ip).getId());
				int conCount = message01Service.getConnectionsCount();
				ConnectionsVo connectionsVo=new ConnectionsVo();
				connectionsVo.setIp(ip);
				connectionsVo.setMaxConnection(maxConnection);
				connectionsVo.setCurConnection(conCount+"");
				connectionsVoList.add(connectionsVo);
			}
			String basicMaxConnection=dbService.getMaxConnectionsCount();
			int basicConCount=dbService.getConnectionsCount();
			String basicDbUrl=env.getProperty("spring.datasource.url");
			String basicIp=basicDbUrl.substring(basicDbUrl.indexOf("//")+2,basicDbUrl.lastIndexOf(":"));
            ConnectionsVo connectionsVo=new ConnectionsVo();
            connectionsVo.setIp(basicIp);
            connectionsVo.setMaxConnection(basicMaxConnection);
            connectionsVo.setCurConnection(basicConCount+"");
            connectionsVoList.add(connectionsVo);
			return new DbNodeConnectionsResponse(new Long(connectionsVoList.size()),connectionsVoList);
		} catch (Exception e) {
			return new DbNodeConnectionsResponse("1","获取连接数异常，异常信息为：" + e.getMessage());
		}
	}


	public BaseUiResponse getOnLineNums(){
		Long consumerCount=consumerService.countBy(new HashMap<>());
		Long onLineServerNum=new Long(serverService.getOnlineServerNum());
		QueueCountResponse normalQueueCountResponse=uiQueueService.count(1);
		QueueCountResponse failQueueCountResponse=uiQueueService.count(2);
		List<OnLineNumsVo> onLineNumsVoList=new ArrayList<>();
		List<String> allBroadcastGroupList=new ArrayList<>();
		List<String> onlineBroadcastGroupList=new ArrayList<>();
		List<QueueOffsetEntity> queueOffsetList=queueOffsetService.getCacheData();
		for (QueueOffsetEntity queueOffset:queueOffsetList) {
			if(queueOffset.getConsumerGroupMode()==2){
				if(!allBroadcastGroupList.contains(queueOffset.getConsumerGroupName())){
					allBroadcastGroupList.add(queueOffset.getConsumerGroupName());
				}
				if(!StringUtils.isEmpty(queueOffset.getConsumerName())&&!onlineBroadcastGroupList.contains(queueOffset.getConsumerGroupName())){
					onlineBroadcastGroupList.add(queueOffset.getConsumerGroupName());
				}
			}
		}

		onLineNumsVoList.add(new OnLineNumsVo("在线consumer数",consumerCount));
		onLineNumsVoList.add(new OnLineNumsVo("消费者组总数",uiQueueOffsetService.getConsumerGroupNum()));
		onLineNumsVoList.add(new OnLineNumsVo("在线消费者组个数",uiQueueOffsetService.getUsingConsumerGroupNum()));
		onLineNumsVoList.add(new OnLineNumsVo("离线消费者组个数",uiQueueOffsetService.getUselessConsumerGroupNum()));
		onLineNumsVoList.add(new OnLineNumsVo("在线server数量",onLineServerNum));
		onLineNumsVoList.add(new OnLineNumsVo("正常队列已分配数量",normalQueueCountResponse.getData().get("distributedCount")));
		onLineNumsVoList.add(new OnLineNumsVo("失败队列已分配数量",failQueueCountResponse.getData().get("distributedCount")));
		onLineNumsVoList.add(new OnLineNumsVo("广播组总数",new Long(allBroadcastGroupList.size())));
		onLineNumsVoList.add(new OnLineNumsVo("在线广播组数",new Long(onlineBroadcastGroupList.size())));

		BaseUiResponse baseUiResponse=new BaseUiResponse(onLineNumsVoList);
		return baseUiResponse;
	}

}
