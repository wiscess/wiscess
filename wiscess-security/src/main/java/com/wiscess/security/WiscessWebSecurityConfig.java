package com.wiscess.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.wiscess.security.autoconfig.SecurityCsrfSettings;
import com.wiscess.security.encoder.MD5EncryptEncoder;
import com.wiscess.security.encoder.RSAMD5EncryptEncoder;
import com.wiscess.security.web.CaptchaAuthenticationDetailsSource;
import com.wiscess.security.web.CaptchaDaoAuthenticationProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(SecurityCsrfSettings.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "security", value = "enabled", matchIfMissing = true)
public class WiscessWebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	protected DataSource dataSource;
	@Autowired
	protected SecurityCsrfSettings securityCsrfSettings;
	@Autowired
	protected SavedRequestAwareAuthenticationSuccessHandler loginSuccessHandler;
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		log.debug("WebSecurityConfig configureGlobal");
		//
		if(securityCsrfSettings.useJdbc()){
			//使用jdbc
			//调用自定义的配置，根据参数进行配置
			jdbcWithCaptchaAuthentication(auth)
				//定义数据源
				.dataSource(dataSource)
				//定义查询用户的语句
				.usersByUsernameQuery(securityCsrfSettings.getJdbcAuthentication().getUserQuery())
				//定义查询用户权限的语句
				.authoritiesByUsernameQuery(securityCsrfSettings.getJdbcAuthentication().getAuthQuery())
				//指定密码加密所使用的加密器为passwordEncoder()
				//先MD5，再RSA
				.passwordEncoder(passwordEncoder())
				.rolePrefix("ROLE_")
			.and()
				.eraseCredentials(false);
		}else{
			//不使用jdbc方式
			if(securityCsrfSettings.isCaptcha()){
				//有验证码
				CaptchaDaoAuthenticationProvider authProvider = new CaptchaDaoAuthenticationProvider();
				authProvider.setPasswordEncoder(passwordEncoder());
				configure(authProvider);
		        auth.authenticationProvider(authProvider);
			}
		}
    }
	/**
	 * 配置provider
	 * @param authProvider
	 */
	protected void configure(CaptchaDaoAuthenticationProvider authProvider) {
	}
	@Override  
    public void configure(WebSecurity web) throws Exception { 
        web.ignoring().antMatchers("/webjars/**","/js/**","/css/**","/images/**","/captcha.jpg");
		if(securityCsrfSettings.getDeniedPage()!=null){
			web.ignoring().antMatchers(securityCsrfSettings.getDeniedPage());
		}
    }  
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers()
			.frameOptions()
				.sameOrigin();
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
		http.formLogin()
			.failureUrl("/login?error=pwd")
			//用于将页面中的验证码保存起来进行比较
	        .authenticationDetailsSource(captchaAuthenticationDetailsSource())
	        .loginPage("/login")
	        .successHandler(loginSuccessHandler)
	        .permitAll();
	    
		http.logout()
	    	//自定义退出链接
        	.logoutUrl("/logout")
        	//退出登录后的默认网址是”/login?logout”
        	.logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(false)
            .clearAuthentication(true)
            .permitAll()  ;
		// session管理  
        http.sessionManagement()
		        	.sessionFixation()
		        	.changeSessionId()
		            .maximumSessions(10)
		            .maxSessionsPreventsLogin(true)
		    		.expiredUrl("/login?expired")
		        ;  
		myConfigure(http);
	}
	
	protected void myConfigure(HttpSecurity http)throws Exception{
		// 允许所有用户访问”/”和”/home”
		http.authorizeRequests()
				//不需要受权限限制的地址
				//.antMatchers("/install","/admin/**").permitAll()
				//受权限限制的地址
				//.antMatchers("/csrf/**").hasAnyRole("1","2")
				// 其他地址的访问均需验证权限
				.anyRequest()
				.authenticated();
	}
	/**
	 * 查找自定义的ssoLogin
	 * @param http
	 * @param configurer
	 * @return
	 * @throws Exception
	 */
//	protected <C extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>> C getOrApply(
//			HttpSecurity http,C configurer) throws Exception {
//		@SuppressWarnings("unchecked")
//		C existingConfig = (C) http.getConfigurer(configurer.getClass());
//		if (existingConfig != null) {
//			return existingConfig;
//		}
//		return http.apply(configurer);
//	}
	@Bean
	public CaptchaAuthenticationDetailsSource captchaAuthenticationDetailsSource() {
		return new CaptchaAuthenticationDetailsSource();
	}
	@Bean
	public PasswordEncoder passwordEncoder(){
		if(securityCsrfSettings.getPasswordType().equalsIgnoreCase("md5")){
			return new MD5EncryptEncoder();
		}else if(securityCsrfSettings.getPasswordType().equalsIgnoreCase("salt")){
			return null;
		}else{
			return new RSAMD5EncryptEncoder();
		}
	}
	/**
	 * 使用JDBC的方式处理用户验证问题，并且增加验证码的处理
	 * @return
	 * @throws Exception
	 */
	public JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> jdbcWithCaptchaAuthentication(AuthenticationManagerBuilder auth)
			throws Exception {
		if(securityCsrfSettings.isCaptcha()){
			//有验证码，调用自定义的含验证码的处理方式
			return auth.apply(new JdbcWithCaptchaUserDetailsManagerConfigurer<AuthenticationManagerBuilder>());
		}
		return auth.jdbcAuthentication();
	}
}
