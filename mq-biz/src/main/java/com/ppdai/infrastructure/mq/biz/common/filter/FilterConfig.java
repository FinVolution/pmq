package com.ppdai.infrastructure.mq.biz.common.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {	

	  @Bean
	  public FilterRegistrationBean ppdfilter() {
	    FilterRegistrationBean openApiFilter = new FilterRegistrationBean();
	    openApiFilter.setFilter(new LogFilter());
	    openApiFilter.addUrlPatterns("/*");
	    return openApiFilter;
	  }
	
}
