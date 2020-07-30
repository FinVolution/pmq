package com.ppdai.infrastructure.mq.biz.common.plugin;

import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

public class DruidConnectionFilter extends FilterEventAdapter {

	private String ip;
	private int hour=0;
	public DruidConnectionFilter(String ip) {
		this.ip = ip;
		 hour=(new Date()).getHours();
		 hour=hour-hour%3;
	}

	public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
		Transaction transaction = Tracer.newTransaction("Connect-"+hour, ip + "-open");
		try {
			ConnectionProxy connectionProxy = super.connection_connect(chain, info);
			transaction.setStatus(Transaction.SUCCESS);
			return connectionProxy;
		} catch (Exception e) {
			// transaction.addData("url", chain.getDataSource().getUrl());
			transaction.setStatus(e);
			throw e;
		} finally {
			transaction.complete();
		}

	}

	@Override
	public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
		Transaction transaction = Tracer.newTransaction("Connect-"+hour, ip + "-close");
		try {
			super.connection_close(chain, connection);
			transaction.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			// transaction.addData("url", chain.getDataSource().getUrl());
			transaction.setStatus(e);
			throw e;
		} finally {
			transaction.complete();
		}
	}

}
