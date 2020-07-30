package com.ppdai.infrastructure.mq.biz.common.plugin;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;

@Intercepts({ @Signature(args = { MappedStatement.class, Object.class }, method = "update", type = Executor.class),
		@Signature(args = { MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class }, method = "query", type = Executor.class) })
public class CatMybatisPlugin implements Interceptor {
	private static final Logger log = LoggerFactory.getLogger(CatMybatisPlugin.class);
//	@Autowired
//	AuditLogService auditLogService;

	// private Logger log = LoggerFactory.getLogger(CatMybatisPlugin.class);
	// private String dbUrl;
	private SoaConfig soaConfig1;

	public CatMybatisPlugin(SoaConfig soaConfig) {
		soaConfig1 = soaConfig;
	}

	private boolean isCatSql() {
		return soaConfig1.getCatSql() == 1;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		String sql = "";
		String classMethod = "Notsupported Class Method";
		if (isCatSql()) {
			try {
				String jdbcUrl = "Notsupported Url";
				String method = "Notsupported Method";

				// DataSource ds = null;
				MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
				// ds =
				// mappedStatement.getConfiguration().getEnvironment().getDataSource();
				// if (ds instanceof com.mchange.v2.c3p0.ComboPooledDataSource)
				// {
				// com.mchange.v2.c3p0.ComboPooledDataSource c3p0Ds =
				// (com.mchange.v2.c3p0.ComboPooledDataSource) ds;
				// jdbcUrl = c3p0Ds.getJdbcUrl();
				// } else if (ds instanceof
				// org.apache.tomcat.jdbc.pool.DataSource)
				// {
				// org.apache.tomcat.jdbc.pool.DataSource tDs =
				// (org.apache.tomcat.jdbc.pool.DataSource) ds;
				// jdbcUrl = tDs.getUrl();
				// } else if (ds instanceof
				// com.alibaba.druid.pool.DruidDataSource)
				// {
				// @SuppressWarnings("resource")
				// com.alibaba.druid.pool.DruidDataSource dDs =
				// (com.alibaba.druid.pool.DruidDataSource) ds;
				// jdbcUrl = dDs.getUrl();
				// } else if (ds instanceof DynamicDataSource) {
				// com.alibaba.druid.pool.DruidDataSource dDs =
				// (com.alibaba.druid.pool.DruidDataSource) ((DynamicDataSource)
				// ds)
				// .getDataSource();
				// jdbcUrl = dDs.getUrl();
				// } else {
				// jdbcUrl = dbUrl;
				// }

				// 得到 类名-方法
				String[] strArr = mappedStatement.getId().split("\\.");
				classMethod = strArr[strArr.length - 2] + "." + strArr[strArr.length - 1];
				// 得到sql语句
				Object parameter = null;
				if (invocation.getArgs().length > 1) {
					parameter = invocation.getArgs()[1];
				}

				BoundSql boundSql = mappedStatement.getBoundSql(parameter);
				Configuration configuration = mappedStatement.getConfiguration();
				sql = showSql(configuration, boundSql);

			} catch (Exception ex) {

			}
		}
		Transaction t = null;
		if (isCatSql()) {
			t = Tracer.newTransaction("SQL", classMethod);
		}
		// method = sql.substring(0, sql.indexOf(" "));
		// Tracer.logEvent("SQL.Method", method);
		// Tracer.logEvent("SQL.Database", jdbcUrl);
		// Tracer.logEvent("SQL.Statement", method, Transaction.SUCCESS,
		// sql.length() > 1000 ? sql.substring(0, 1000) : sql);

		Object returnObj = null;
		try {
			returnObj = invocation.proceed();
			if (t != null) {
				t.setStatus(Transaction.SUCCESS);
			}
		} catch (Exception e) {
			if (t != null) {
				t.addData("sql", sql);
				t.setStatus(e);
			}
			throw e;
		} finally {
			if (t != null) {
				t.complete();
			}
		}
		return returnObj;
	}

	public String showSql(Configuration configuration, BoundSql boundSql) {
		Object parameterObject = boundSql.getParameterObject();

		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

		String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
		// if(sql.indexOf("update topic")!=-1){
		// System.out.println("11");
		// }
		if (parameterMappings.size() > 0 && parameterObject != null) {

			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {

				sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

			} else {
				MetaObject metaObject = configuration.newMetaObject(parameterObject);

				for (ParameterMapping parameterMapping : parameterMappings) {

					String propertyName = parameterMapping.getProperty();

					if (metaObject.hasGetter(propertyName)) {

						Object obj = metaObject.getValue(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));

					} else if (boundSql.hasAdditionalParameter(propertyName)) {

						Object obj = boundSql.getAdditionalParameter(propertyName);
						sql = sql.replaceFirst("\\?", getParameterValue(obj));
					}
				}
			}
		}
		return sql;
	}

	private String getParameterValue(Object obj) {
		String value = null;
		if (obj instanceof String) {
			value = "'" + obj.toString() + "'";
		} else if (obj instanceof Date) {
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
			value = "'" + formatter.format(obj) + "'";
		} else {
			if (obj != null) {
				value = obj.toString();
			} else {
				value = "";
			}

		}
		return value;
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {
		// System.out.println(1);
		// dbUrl = properties.getProperty("DBUrl");
	}
}
