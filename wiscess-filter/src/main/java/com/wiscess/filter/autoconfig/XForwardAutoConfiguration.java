package com.wiscess.filter.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * @author wh
 */
@Configuration
@ConditionalOnWebApplication
public class XForwardAutoConfiguration {
	
	@Bean
	public FilterRegistrationBean<ForwardedHeaderFilter> forwardHeaderFilterRegistration() {
	    FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<ForwardedHeaderFilter>();
	    ForwardedHeaderFilter filter=new ForwardedHeaderFilter();
	    //去掉X-Forwarded-*
	    filter.setRemoveOnly(true);
	    registration.setFilter(filter);
	    registration.addUrlPatterns("/*");
	    registration.setName("forwardedHeaderFilter");
	    registration.setOrder(-9991);
	    return registration;
	} 
}
