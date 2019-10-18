package com.ppdai.infrastructure.mq.biz.service.common;

import java.util.Date;

/**
 * @author dal-generator
 */
public interface DbService {

	Date getDbTime();

	String getMaxConnectionsCount();

	Integer getConnectionsCount();
}
