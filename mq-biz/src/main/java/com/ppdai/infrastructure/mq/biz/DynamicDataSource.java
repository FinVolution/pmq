package com.ppdai.infrastructure.mq.biz;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.ppdai.infrastructure.mq.biz.service.Message01Service;

public class DynamicDataSource extends AbstractRoutingDataSource {
	//private Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
	private Message01Service message01Service;

	public DynamicDataSource(Message01Service message01Service) {
		this.message01Service = message01Service;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return null;
	}

	@Override
	protected DataSource determineTargetDataSource() {
		return message01Service.getDataSource();
	}

//	@Override
//	public Connection getConnection() throws SQLException{
//		Connection connection=super.getConnection();
//		if(connection instanceof DruidPooledConnection){		
//			log.info("open jdbc url is {},and thread id is {}",((DruidPooledConnection)connection).getConnectionHolder().getDataSource().getRawJdbcUrl(),Thread.currentThread().getId());
//		}
//		return connection;		
//	}
	
	public DataSource getDataSource() {
		return message01Service.getDataSource();
	}

	@Override
	public void afterPropertiesSet() {

	}
}
