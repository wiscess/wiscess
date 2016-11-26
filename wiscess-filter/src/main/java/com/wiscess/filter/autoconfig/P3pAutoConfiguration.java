package com.wiscess.filter.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.wiscess.filter.P3pDisableFilter;

@ConditionalOnWebApplication
public class P3pAutoConfiguration {
	
	@Bean
	public FilterRegistrationBean p3pFilterRegistration() {
	    FilterRegistrationBean registration = new FilterRegistrationBean();
	    registration.setFilter(new P3pDisableFilter());
	    registration.addUrlPatterns("/*");
	    registration.setName("p3pDisableFilter");
	    registration.setOrder(1);
	    return registration;
	} 

}
