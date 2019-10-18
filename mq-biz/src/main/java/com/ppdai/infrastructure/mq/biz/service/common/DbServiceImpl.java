package com.ppdai.infrastructure.mq.biz.service.common;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ppdai.infrastructure.mq.biz.dal.meta.DbRepository;

/**
 * @author dal-generator
 */

@Service
public class DbServiceImpl implements DbService {

	@Autowired
	private DbRepository dbRepository;

	public Date getDbTime() {
		return dbRepository.getDbTime();
	}

	@Override
	public String getMaxConnectionsCount() {
		Map<String, String> map = dbRepository.getMaxConnectionsCount();
		if (map.size() == 0)
			return "0";
		else
			return map.get("Value");

	}

	@Override
	public Integer getConnectionsCount() {
		Integer count = dbRepository.getConnectionsCount();
		if (count == null)
			return 0;
		return count;

	}

}
