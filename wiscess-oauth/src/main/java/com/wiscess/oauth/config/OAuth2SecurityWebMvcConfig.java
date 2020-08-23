package com.wiscess.oauth.config;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wiscess.oauth.controll.AuthController;
import com.wiscess.oauth.exception.DefaultOAuth2AuthenticationEntryPoint;
import com.wiscess.oauth.exception.Oauth2ExceptionGlobalHandler;
import com.wiscess.oauth.utils.TokenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * OAuth启动配置
 * @author wh
 */
@Configuration
@ConditionalOnClass
@ConditionalOnWebApplication
@Slf4j
public class OAuth2SecurityWebMvcConfig implements WebMvcConfigurer   {
	
	@Autowired
	protected OauthProperties oauthProperties;
	
	/**
	 * 定义验证码的访问路径
	 * @return
	 */
	@Bean
	public AuthController authController(){
		return new AuthController();
	}
	@Bean
	public TokenUtil tokenUtil() {
		return new TokenUtil();
	}
	
	/**
     * 自定义异常转换类
     * @return
     */
	@Bean
	@ConditionalOnMissingBean
	public AuthenticationEntryPoint authenticationEntryPoint() {
    	return new DefaultOAuth2AuthenticationEntryPoint();
    }
	/**
	 * 自定义全局处理Oauth2Exception
	 * @return
	 */
	@Bean
	public Oauth2ExceptionGlobalHandler oauth2ExceptionGlobalHandler() {
		return new Oauth2ExceptionGlobalHandler();
	}

	@Override
    public void addCorsMappings(CorsRegistry registry) {
		//配置跨域访问的设置
		List<String> allowedOrigins=oauthProperties.getAllowedOrigins();
		if(allowedOrigins==null || allowedOrigins.size()==0) {
			allowedOrigins=new ArrayList<String>();
			allowedOrigins.add("*");
		}
        registry.addMapping("/**")
        		.allowedOrigins(allowedOrigins.toArray(new String[allowedOrigins.size()]))
                .allowedMethods("GET","POST")
                .allowedHeaders("*")
                //启用cookie，确保请求是同一个session
        		.allowCredentials(true);
        log.info("Oauth跨域拦截器注册成功！");     
        allowedOrigins.forEach((item)->log.info("allowed origin:{}",item.trim()));
    }
}
