package com.ppdai.infrastructure.mq.biz;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.plugin.CatMybatisPlugin;

/**
 * Created by zhangyicong on 2017/5/3.
 */
@Configuration
@MapperScan(basePackages = MySqlDatasourceConfiguration.BASE_PACKAGES,
        sqlSessionTemplateRef = "mysqlSessionTemplate")
//@Profile("runtime")
public class MySqlDatasourceConfiguration {
	
    /** 接口类文件所在包 */
    public static final String BASE_PACKAGES = "com.ppdai.infrastructure.mq.biz.dal.meta";
    /** XML 文件所在目录 */
    public static final String MAPPER_XML_PATH = "classpath:mapper/mysql/*.xml";

    @Bean(name = "mysqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource,SoaConfig soaConfig) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setPlugins(new Interceptor[] {new CatMybatisPlugin(soaConfig)});
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_PATH));
        bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return bean.getObject();
    }

    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public DataSource mysqlDataSource() {
    	DruidDataSource druidDataSource= new DruidDataSource();
//    	druidDataSource.setUrl("jdbc:mysql://localhost:3408/mq_basic");
//    	druidDataSource.setUsername("root");
//    	druidDataSource.setPassword("root");
    	return druidDataSource;
    }

    @Bean(name = "mysqlTransactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "mysqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("mysqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
