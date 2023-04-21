package com.wiscess.security.autoconfig;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wiscess.security.WiscessSecurityProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 项目启动配置
 * @author wh
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "security.vue", value = "enabled", matchIfMissing = true)
public class CorsWebMvcConfig implements WebMvcConfigurer   {
	
	@Autowired
	protected WiscessSecurityProperties wiscessSecurityProperties;

	@Override
    public void addCorsMappings(CorsRegistry registry) {
		log.info("CorsWebMvcConfig");
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
	                .allowedHeaders("Access-Control-Allow-Origin", "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization")
	                //启用cookie，确保请求是同一个session
	        		.allowCredentials(true);
	        log.info("跨域拦截器注册成功！");     
		}
    }
}
