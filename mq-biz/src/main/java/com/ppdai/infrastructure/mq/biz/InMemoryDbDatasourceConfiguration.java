package com.ppdai.infrastructure.mq.biz;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.plugin.CatMybatisPlugin;

/**
 * Created by zhangyicong on 2017/5/3.
 */
//@Configuration
//@MapperScan(basePackages = InMemoryDbDatasourceConfiguration.BASE_PACKAGES,
//        sqlSessionTemplateRef = "inMemoryDbSessionTemplate")
//@Profile("TEST")
public class InMemoryDbDatasourceConfiguration {

    /** 接口类文件所在包 */
    public static final String BASE_PACKAGES = "com.ppdai.infrastructure.mq.biz.dal.meta";
    /** XML 文件所在目录 */
    public static final String MAPPER_XML_PATH = "classpath:mapper/mysql/*.xml";

    @Bean(name = "inMemoryDbSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("inMemoryDbDataSource") DataSource dataSource,SoaConfig soaConfig) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setPlugins(new Interceptor[] {new CatMybatisPlugin(soaConfig)});
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_PATH));
        bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return bean.getObject();
    }

    @Bean(name = "inMemoryDbDataSource")
    @Primary
    public DataSource inMemoryDbDataSource() {
//        DataSource dataSource = new DruidDataSource();
//        return dataSource;
    	return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:sql/schema.sql").build();
    }

    @Bean(name = "inMemoryDbTransactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("inMemoryDbDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "inMemoryDbSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("inMemoryDbSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
