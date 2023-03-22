package com.wiscess.filter.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.filter.FileTypeFilter;
import com.wiscess.filter.autoconfig.properties.FileTypeFilterProperties;
import com.wiscess.filter.matcher.FileTypeRequestMatcher;

@Configuration
@EnableConfigurationProperties(FileTypeFilterProperties.class)
@ConditionalOnWebApplication
@ConditionalOnClass(FileTypeFilter.class)
@ConfigurationProperties(prefix = "filter.filetype")
public class FileTypeAutoConfiguration {
	
	private final FileTypeFilterProperties properties;

	public FileTypeAutoConfiguration(FileTypeFilterProperties properties) {
		this.properties = properties;
	}
	@Bean   
	public FilterRegistrationBean<FileTypeFilter> fileTypeFilterRegistration() {
	    FilterRegistrationBean<FileTypeFilter> registration = new FilterRegistrationBean<FileTypeFilter>();
	    registration.setFilter(fileTypeFilter());
	    registration.addUrlPatterns("/**");
	    registration.setName("fileTypeFilter");
	    registration.setOrder(2);
	    return registration;
	} 
	@ConditionalOnMissingBean(FileTypeFilter.class)
	public FileTypeFilter fileTypeFilter(){
		FileTypeFilter filter=new FileTypeFilter();
		filter.setErrorPage(this.properties.getErrorPage());
		filter.setRequireMatcher(requireMatcher());
		return filter;
	}
	
	public FileTypeRequestMatcher requireMatcher(){
		FileTypeRequestMatcher requestMatcher=new FileTypeRequestMatcher();
		requestMatcher.setUrlPatterns(this.properties.getUrlPatterns());
		return requestMatcher;
	}
}
