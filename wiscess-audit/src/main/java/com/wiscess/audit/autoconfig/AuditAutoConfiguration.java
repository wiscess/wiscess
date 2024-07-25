package com.wiscess.audit.autoconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wiscess.audit.autoconfig.properties.AuditProperties;
import com.wiscess.audit.filter.AuditFilter;
import com.wiscess.audit.jdbc.AuditService;

@Configuration
@EnableConfigurationProperties(AuditProperties.class)
@ConfigurationProperties(prefix = "audit")
public class AuditAutoConfiguration implements WebMvcConfigurer{
	
	@Autowired
	private AuditProperties properties;

	@Autowired
	private AuditService auditService;
	
	/**
	 * 定义审计功能过滤器
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(AuditFilter.class)
	public AuditFilter auditFilter(){
		return new AuditFilter(properties,auditService);
	}
}
