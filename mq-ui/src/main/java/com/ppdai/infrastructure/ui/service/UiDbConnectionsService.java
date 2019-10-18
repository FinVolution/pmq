package com.ppdai.infrastructure.ui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.entity.DbNodeEntity;
import com.ppdai.infrastructure.mq.biz.service.DbNodeService;
import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.common.DbService;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.DbNodeConnectionsResponse;
import com.ppdai.infrastructure.mq.biz.ui.vo.ConnectionsVo;

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

}
