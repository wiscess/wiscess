package com.wiscess.security.autoconfig;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wiscess.security.web.controller.LoginController;

/**
 * 项目启动配置
 * @author wh
 */
@Configuration
@ConditionalOnClass
@ConditionalOnWebApplication
public class SecurityWebMvcConfig implements WebMvcConfigurer   {
	/**
	 * 定义验证码的访问路径
	 * @return
	 */
	@Bean
	public LoginController loginController(){
		return new LoginController();
	}

}
