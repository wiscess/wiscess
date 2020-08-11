package com.wiscess.security.autoconfig;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wiscess.security.WiscessSecurityProperties;
import com.wiscess.security.web.controller.LoginController;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动配置
 * @author wh
 */
@Configuration
@ConditionalOnClass
@ConditionalOnWebApplication
@Slf4j
public class SecurityWebMvcConfig implements WebMvcConfigurer   {
	
	@Autowired
	protected WiscessSecurityProperties wiscessSecurityProperties;
	
	/**
	 * 定义验证码的访问路径
	 * @return
	 */
	@Bean
	public LoginController loginController(){
		return new LoginController();
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
		//配置跨域访问的设置
		if(wiscessSecurityProperties.isVueMode()) {
			List<String> allowedOrigins=wiscessSecurityProperties.getVue().getAllowedOrigins();
			if(allowedOrigins==null) {
				allowedOrigins=new ArrayList<String>();
				allowedOrigins.add("*");
			}
	        registry.addMapping("/**")
	        		.allowedOrigins(allowedOrigins.toArray(new String[allowedOrigins.size()]))
	                .allowedMethods("GET","POST")
	                .allowedHeaders("*")
	                //启用cookie，确保请求是同一个session
	        		.allowCredentials(true);
	        log.info("跨域拦截器注册成功！");     
		}
    }
}
