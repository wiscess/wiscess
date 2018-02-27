package com.wiscess.filter.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.wiscess.filter.HttpMethodFilter;

@ConditionalOnWebApplication
public class HttpMethodAutoConfiguration {
	
	@Bean
	public FilterRegistrationBean httpMethodFilterRegistration() {
	    FilterRegistrationBean registration = new FilterRegistrationBean();
	    registration.setFilter(new HttpMethodFilter());
	    registration.addUrlPatterns("/*");
	    registration.setName("httpMethodFilter");
	    registration.setOrder(3);
	    return registration;
	} 

}
