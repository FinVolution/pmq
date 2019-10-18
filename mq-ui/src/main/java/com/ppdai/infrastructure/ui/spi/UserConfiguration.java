package com.ppdai.infrastructure.ui.spi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ppdai.infrastructure.ui.spi.ppd.LdapUserService;

@Configuration
public class UserConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public UserProviderService ldapUserService() {
		return new LdapUserService();
	}
}
