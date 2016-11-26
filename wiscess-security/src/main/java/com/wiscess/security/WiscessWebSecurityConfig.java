package com.wiscess.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;

import com.wiscess.security.autoconfig.SecurityCsrfSettings;
import com.wiscess.security.csrf.CsrfSecurityRequestMatcher;

public class WiscessWebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	protected DataSource dataSource;
	@Autowired
	protected SecurityCsrfSettings securityCsrfSettings;
	@Autowired
	protected CsrfSecurityRequestMatcher csrfSecurityRequestMatcher;
	@Override  
    public void configure(WebSecurity web) throws Exception { 
        web.ignoring().antMatchers("/webjars/**","/js/**","/css/**","/images/**","/logincode");
    }  
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().sameOrigin();
		if(securityCsrfSettings.isDisabled()){
			http.csrf().disable();
		}else{
			http.csrf().requireCsrfProtectionMatcher(csrfSecurityRequestMatcher);
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
