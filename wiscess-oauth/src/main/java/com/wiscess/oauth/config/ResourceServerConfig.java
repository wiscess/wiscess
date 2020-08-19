package com.wiscess.oauth.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.wiscess.security.WiscessSecurityProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>参看：https://github.com/spring-guides/tut-spring-security-and-angular-js/blob/master/oauth2-vanilla/README.adoc</p>
 * Created by Mr.Yangxiufeng on 2017/12/29.
 * Time:10:46
 * ProjectName:Mirco-Service-Skeleton
 */
@Configuration
@EnableResourceServer
@Slf4j
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
	public static final String RESOURCE_ID = "api";

	@Autowired
	protected WiscessSecurityProperties wiscessSecurityProperties;
	@Autowired
	protected OauthProperties oauthProperties;
	
	@Autowired
	protected AccessDeniedHandler accessDeniedHandler;
	@Autowired
	protected AuthenticationEntryPoint authenticationEntryPoint;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.info("ResourceServerConfig中配置HttpSecurity对象执行");
    	//security中设置的ignored，在资源认证中也不进行认证
    	List<String> ignored=wiscessSecurityProperties.getIgnored();
    	if(ignored==null) {
    		ignored=new ArrayList<String>();
    	}
    	if(oauthProperties.getResources().getIgnored()!=null) {
    		ignored.addAll(oauthProperties.getResources().getIgnored());
    	}
    	log.info("认证资源路径：{}",oauthProperties.getResources().getAuthPrefix());
    	http.requestMatchers()
            	.antMatchers(oauthProperties.getResources().getAuthPrefix())
            	.and()
            	.authorizeRequests()
            	.antMatchers(ignored.toArray(new String[] {}))
            	.permitAll()
            	.anyRequest()
            	.authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
        resources
        	.authenticationEntryPoint(authenticationEntryPoint)     //资源认证失败，自定义返回json内容
        	.accessDeniedHandler(accessDeniedHandler)
        	.resourceId(oauthProperties.getResources().getResourceId())
        	.stateless(true); // 设置这些资源仅基于令牌认证
    }
}
