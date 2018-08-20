package com.wiscess.wechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wiscess.wechat.servlet.WechatPayServlet;
import com.wiscess.wechat.servlet.WechatServlet;

@Configuration
@EnableConfigurationProperties(WechatProperties.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "wechat", value = "enabled", matchIfMissing = true)
public class WeChatServletAutoConfiguration {
	@Autowired
	protected WechatProperties wechat;
	
    @Bean
    @ConditionalOnProperty(prefix = "wechat", name="servlet-url")
    public WechatServlet wechatServlet(){
        return new WechatServlet();
    }
    
    @Bean
    @ConditionalOnBean(name="wechatServlet")
    public ServletRegistrationBean<WechatServlet> wechatServletRegistrationBean(WechatServlet wechatServlet){
        ServletRegistrationBean<WechatServlet> registration = new ServletRegistrationBean<WechatServlet>(wechatServlet);
        registration.setEnabled(true);
        registration.addUrlMappings(wechat.getServletUrl());
        return registration;
    }
    @Bean
    @ConditionalOnProperty(prefix = "wechat", name="pay-notify-servlet-url")
    public WechatPayServlet wechatPayServlet(){
        return new WechatPayServlet();
    }
    
    @Bean
    @ConditionalOnBean(name="wechatPayServlet")
    public ServletRegistrationBean<WechatPayServlet> wechatPayServletRegistrationBean(WechatPayServlet wechatPayServlet){
        ServletRegistrationBean<WechatPayServlet> registration = new ServletRegistrationBean<WechatPayServlet>(wechatPayServlet);
        registration.setEnabled(true);
        registration.addUrlMappings(wechat.getPayNotifyServletUrl());
        return registration;
    }
}
