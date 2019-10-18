package com.ppdai.infrastructure.ui.util;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by Administrator on 2017/4/28.
 */
@Configuration
public class DruidConfiguration {
    @Autowired
    private Environment env;
    /**
     * 注册一个StatViewServlet
     * @return
     */
    @Bean
    public ServletRegistrationBean DruidStatViewServle2(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid2/*");

        //添加初始化参数：initParams
        /** 白名单，如果不配置或value为空，则允许所有 */
        servletRegistrationBean.addInitParameter("allow","");
        /** 黑名单，与白名单存在相同IP时，优先于白名单 */
        servletRegistrationBean.addInitParameter("deny","");
        /** 用户名 */
        servletRegistrationBean.addInitParameter("loginUsername",env.getProperty("spring.datasource.username", ""));
        /** 密码 */
        servletRegistrationBean.addInitParameter("loginPassword",env.getProperty("spring.datasource.password", ""));
        /** 禁用页面上的“Reset All”功能 */
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：WebStatFilter
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter2(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());

        /** 过滤规则 */
        filterRegistrationBean.addUrlPatterns("/*");
        /** 忽略资源 */
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid2/*");
        return filterRegistrationBean;
    }


}
