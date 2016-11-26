package com.wiscess.security.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.security.csrf.CsrfSecurityRequestMatcher;

@Configuration
@EnableConfigurationProperties(SecurityCsrfSettings.class)
@ConditionalOnWebApplication
@ConditionalOnClass(CsrfSecurityRequestMatcher.class)
@ConditionalOnProperty(prefix = "security.csrf", value = "enabled", matchIfMissing = true)
public class SecurityCsrfAutoConfiguration {
	
	private final SecurityCsrfSettings securityCsrfSettings;
	
	public SecurityCsrfAutoConfiguration(SecurityCsrfSettings securityCsrfSettings){
		this.securityCsrfSettings=securityCsrfSettings;
	}
	
	@Bean
	@ConditionalOnMissingBean(CsrfSecurityRequestMatcher.class)
	public CsrfSecurityRequestMatcher csrfSecurityRequestMatcher() {
		CsrfSecurityRequestMatcher matcher = new CsrfSecurityRequestMatcher();
		matcher.setExecludeUrls(this.securityCsrfSettings.getExecludeUrls());
		return matcher;
	}

}
