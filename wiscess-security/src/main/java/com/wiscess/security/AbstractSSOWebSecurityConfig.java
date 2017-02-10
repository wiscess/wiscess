package com.wiscess.security;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.wiscess.security.WiscessWebSecurityConfig;
import com.wiscess.security.sso.encrypt.RSAEncryptHandler;
import com.wiscess.security.sso.SSOAuthenticationEntryPoint;
import com.wiscess.security.sso.SSOLoginConfigurer;
import com.wiscess.security.sso.SSOLogoutJsSuccessHandler;
import com.wiscess.security.sso.SSOUserDetailsManagerConfigurer;

@Slf4j
public class AbstractSSOWebSecurityConfig extends WiscessWebSecurityConfig {
	
	@Autowired
	protected SsoProperties ssoProperties;
	
	/**
	 * 自动创建bean，加载配置文件中的参数
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public SsoProperties ssoProperties(){
		return new SsoProperties();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		log.debug("SSOWebSecurityConfig configureGlobal");
		//定义权限参数
		//使用jdbc方式验证用户查询用户的语句
		ssoAuthentication(auth)
			//定义数据源
			.dataSource(dataSource)
			//定义查询用户的语句
			.usersByUsernameQuery(
					"select user_name,user_pwd, is_used from login_info where user_name=?")
			//定义查询用户权限的语句
			.authoritiesByUsernameQuery(
					"SELECT "
					+ "li.user_name, rf.func_id "
					+ "FROM "
					+ "user_role ur,login_info li,sys_role sr,role_func rf "
					+ "WHERE "
					+ "ur.user_id = li.user_id "
					+ "and ur.role_id = sr.role_id "
					+ "and sr.role_id = rf.role_id "
					+ "and li.user_name = ? "
					+ "and li.is_used = 1 "
					+ "and ur.is_used = 1")
			// 指定密码加密所使用的加密器为passwordEncoder()
			.encryptHandler(new RSAEncryptHandler())
//			.rolePrefix("ROLE_")
			.and()
			// 不删除凭据，以便记住用户
			.eraseCredentials(false);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.debug("SSOSecurityConfig configure(http)"); 
		
		// 允许所有用户访问”/”和”/home”
		http.authorizeRequests()
				//不需要受权限限制的地址
				.antMatchers("/admin/**").hasAnyAuthority("101","102")
				// 其他地址的访问均需验证权限
				.anyRequest().authenticated();
//		http.csrf().disable();
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
		//添加自定义的过滤器，处理验证码
		http.apply(new SSOLoginConfigurer<HttpSecurity>())
			.defaultSuccessUrl("/")
			//认证失败后的跳转地址
			//.failureUrl(wiscessProperties.getFailureUrl())
			//成功后的处理，必须放在defaultSuccessUrl后面，可以在Session中保存用户信息
			//.successHandler(new LoginSuccessHandler())
			.permitAll();
		
		//自定义登录入口，当权限认证失败时，跳转到统一认证中心去进行验证
		http.exceptionHandling()
			.authenticationEntryPoint(new SSOAuthenticationEntryPoint(ssoProperties.getAuthUrl()));

		// 自定义注销  ，两种方式，二选一，不能同时存在
		//方式1：本项目不是门户系统，不负责向统一认证中心发送退出指令，作为普通的系统，须实现jslogout功能，供门户系统调用。
        http.logout()
        	//自定义退出链接
        	.logoutUrl("/jslogout")
        	.logoutSuccessHandler(new SSOLogoutJsSuccessHandler());
        //方式2：本系统退出后要通知统一认证中心退出
/*        http.logout()
        	.logoutUrl("/logout")
        	//此时必须设置为false，待执行successHandler时再清除session
            .invalidateHttpSession(false)
        	.logoutSuccessHandler(new SSOLogoutSuccessHandler(
        			//统一认证中心的退出地址
        			wiscessProperties.getLogoutUrl(),
        			//key的加密方式
        			new RSAEncryptHandler()
        			//invalidateHttpSession是否清除session，默认为true，可省略
        			,true
        			))  
        ;*/
		// RemeberMe  
		http.rememberMe()
		        	.key("webmvc#FD637E6D9C0F1A5A67082AF56CE32485")
		        	.tokenValiditySeconds(1209600)
		        	// 指定记住登录信息所使用的数据源
		        	//.tokenRepository(tokenRepository())
		        	;
        // session管理  
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
		        	//.sessionFixation()
//		        	.changeSessionId()
//		            .maximumSessions(10)
//		            .expiredUrl("/")
		        ;  
	}

	/**
	 * 添加自定义的SSOAuthenticationProvider
	 * @return
	 * @throws Exception
	 */
	public SSOUserDetailsManagerConfigurer<AuthenticationManagerBuilder> ssoAuthentication(AuthenticationManagerBuilder auth)
			throws Exception {
		return auth.apply(new SSOUserDetailsManagerConfigurer<AuthenticationManagerBuilder>());
	}
}
