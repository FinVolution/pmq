package com.ppdai.infrastructure.mq.biz;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.plugin.CatMybatisPlugin;
import com.ppdai.infrastructure.mq.biz.common.plugin.DruidConnectionFilter;
import com.ppdai.infrastructure.mq.biz.common.util.DbUtil;

/**
 * Created by zhangyicong on 2017/5/3.
 */
@Configuration
@MapperScan(basePackages = MySqlDatasourceConfiguration.BASE_PACKAGES, sqlSessionTemplateRef = "mysqlSessionTemplate")
//@Profile("runtime")
public class MySqlDatasourceConfiguration {

	/** 接口类文件所在包 */
	public static final String BASE_PACKAGES = "com.ppdai.infrastructure.mq.biz.dal.meta";
	/** XML 文件所在目录 */
	public static final String MAPPER_XML_PATH = "classpath:mapper/mysql/*.xml";

	@Autowired
	private SoaConfig soaConfig;
	@Autowired
	private Environment env;
	private volatile int getDefaultMaxActive = 0;
	private volatile int getMinEvictableIdleTimeMillis = 0;
	@Bean(name = "mysqlSessionFactory")
	@Primary
	public SqlSessionFactory sqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource, SoaConfig soaConfig)
			throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setPlugins(new Interceptor[] { new CatMybatisPlugin(soaConfig) });
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_PATH));
		bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
		return bean.getObject();
	}

	@Bean(name = "mysqlDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	@Primary
	public DataSource mysqlDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new DruidConnectionFilter(DbUtil.getDbIp(env.getProperty("spring.datasource.url"))));
		druidDataSource.setProxyFilters(filters);
		getDefaultMaxActive = soaConfig.getDefaultMaxActive();
		soaConfig.registerChanged(new Runnable() {
			@Override
			public void run() {
				if (getDefaultMaxActive != soaConfig.getDefaultMaxActive()) {
					getDefaultMaxActive = soaConfig.getDefaultMaxActive();
					try {
						druidDataSource.setMaxActive(getDefaultMaxActive);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				if (getMinEvictableIdleTimeMillis != soaConfig.getMinEvictableIdleTimeMillis()) {
					getMinEvictableIdleTimeMillis = soaConfig.getMinEvictableIdleTimeMillis();
					try {
						druidDataSource.setMinEvictableIdleTimeMillis(getMinEvictableIdleTimeMillis);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		});
		return druidDataSource;
	}
	
	@Bean(name = "mysqlTransactionManager")
	@Primary
	public DataSourceTransactionManager transactionManager(@Qualifier("mysqlDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "mysqlSessionTemplate")
	@Primary
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("mysqlSessionFactory") SqlSessionFactory sqlSessionFactory)
			throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
