package com.wiscess.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;

import com.wiscess.security.autoconfig.SecurityCsrfSettings;

@Configuration
@EnableConfigurationProperties(SecurityCsrfSettings.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "security", value = "enabled", matchIfMissing = true)
public class WiscessWebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	protected DataSource dataSource;
	@Autowired
	protected SecurityCsrfSettings securityCsrfSettings;
	@Override  
    public void configure(WebSecurity web) throws Exception { 
        web.ignoring().antMatchers("/webjars/**","/js/**","/css/**","/images/**","/logincode");
		if(securityCsrfSettings.getDeniedPage()!=null){
			web.ignoring().antMatchers(securityCsrfSettings.getDeniedPage());
		}
    }  
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin();
		if(!securityCsrfSettings.isEnableCsrf()){
			//禁用
			http.csrf().disable();
		}else{
			if(this.securityCsrfSettings.getCsrf().getExecludeUrls()!=null && this.securityCsrfSettings.getCsrf().getExecludeUrls().size()>0){
				//排除Csrf路径
				http.csrf().ignoringAntMatchers(this.securityCsrfSettings.getCsrf().getExecludeUrls().toArray(new String[0]));
			}
		}
		if(securityCsrfSettings.getDeniedPage()!=null){
			http.exceptionHandling().accessDeniedPage(securityCsrfSettings.getDeniedPage());
		}
		myConfigure(http);
	}
	
	protected void myConfigure(HttpSecurity http)throws Exception{
	}
	/**
	 * 查找自定义的ssoLogin
	 * @param http
	 * @param configurer
	 * @return
	 * @throws Exception
	 */
	protected <C extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> C getOrApply(
			HttpSecurity http,C configurer) throws Exception {
		@SuppressWarnings("unchecked")
		C existingConfig = (C) http.getConfigurer(configurer.getClass());
		if (existingConfig != null) {
			return existingConfig;
		}
		return http.apply(configurer);
	}

}
