package com.wiscess.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 
 * @author wanghai
 *
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties
public class WebConfig extends WebMvcConfigurerAdapter {
	/*
	 * 设置登录地址 
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("/login");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}
	
	@Bean
	public HttpSessionEventPublisher  httpSessionEventPublisher(){
		return new HttpSessionEventPublisher();
	}
}
