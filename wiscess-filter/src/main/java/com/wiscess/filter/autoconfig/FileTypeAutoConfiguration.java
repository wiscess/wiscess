package com.wiscess.filter.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.filter.FileTypeFilter;
import com.wiscess.filter.matcher.FileTypeRequestMatcher;

@Configuration
@EnableConfigurationProperties(FileTypeFilterProperties.class)
@ConditionalOnWebApplication
@ConditionalOnClass(FileTypeFilter.class)
@ConditionalOnProperty(prefix = "filter.filetype", value = "enabled", matchIfMissing = true)
public class FileTypeAutoConfiguration {
	
	private final FileTypeFilterProperties properties;

	public FileTypeAutoConfiguration(FileTypeFilterProperties properties) {
		this.properties = properties;
	}
	@Bean
	public FilterRegistrationBean fileTypeFilterRegistration() {
	    FilterRegistrationBean registration = new FilterRegistrationBean();
	    registration.setFilter(fileTypeFilter());
	    registration.addUrlPatterns("/*");
	    registration.setName("fileTypeFilter");
	    registration.setOrder(2);
	    return registration;
	} 
	@Bean
	@ConditionalOnMissingBean(FileTypeFilter.class)
	public FileTypeFilter fileTypeFilter(){
		FileTypeFilter filter=new FileTypeFilter();
		filter.setErrorPage(this.properties.getErrorPage());
		filter.setRequireMatcher(requireMatcher());
		return filter;
	}
	
	@Bean
	public FileTypeRequestMatcher requireMatcher(){
		FileTypeRequestMatcher requestMatcher=new FileTypeRequestMatcher();
		requestMatcher.setUrlPatterns(this.properties.getUrlPatterns());
		return requestMatcher;
	}
}
