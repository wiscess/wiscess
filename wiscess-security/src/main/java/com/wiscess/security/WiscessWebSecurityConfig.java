package com.wiscess.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.wiscess.security.encoder.IgnoreCaseEncryptEncoder;
import com.wiscess.security.encoder.MD5EncryptEncoder;
import com.wiscess.security.encoder.RSAEncryptEncoder;
import com.wiscess.security.encoder.UpperCaseEncryptEncoder;
import com.wiscess.security.jdbc.UserDetailsServiceImpl;
import com.wiscess.security.sso.SSOAuthenticationEntryPoint;
import com.wiscess.security.sso.SSOAuthenticationProvider;
import com.wiscess.security.sso.SSOLoginConfigurer;
import com.wiscess.security.sso.SSOLogoutJsSuccessHandler;
import com.wiscess.security.sso.SSOLogoutSuccessHandler;
import com.wiscess.security.web.CaptchaAuthenticationDetailsSource;
import com.wiscess.security.web.CaptchaDaoAuthenticationProvider;
import com.wiscess.security.web.EncryptUsernamePasswordAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * 权限认证配置
 * @author wh
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "security", value = "enabled", matchIfMissing = true)
@Slf4j
@EnableConfigurationProperties(WiscessSecurityProperties.class)
public class WiscessWebSecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 * 默认静态资源文件
	 */
	public static String[] DEFAULT_IGNORES="/css/**,/js/**,/images/**,/webjars/**,/**/favicon.ico,/captcha.jpg".split(",");
	
	@Autowired
	protected WiscessSecurityProperties wiscessSecurityProperties;
	@Autowired
	protected AuthenticationSuccessHandler loginSuccessHandler;
	@Autowired
	protected UserDetailsServiceImpl userDetailsService;
	
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider authProvider;
		if(wiscessSecurityProperties.isSsoMode()){
			log.info("WebSecurityConfig configured SSOAuthenticationProvider with {}",wiscessSecurityProperties.getSso().getAuthUrl());
			authProvider=new SSOAuthenticationProvider();
		}else{
			log.info("WebSecurityConfig configured DaoAuthenticationProvider {} captcha.",(wiscessSecurityProperties.isCaptcha()?"with":"without"));
			//从2.0开始，不再使用jdbc的方式来完成权限验证
			authProvider = new CaptchaDaoAuthenticationProvider(wiscessSecurityProperties);
		}
		//设置密码加密方式
		authProvider.setPasswordEncoder(passwordEncoder());
		//设置查询用户的service
		authProvider.setUserDetailsService(userDetailsService);
		configure(authProvider);
        auth.authenticationProvider(authProvider);
    }
	/**
	 * 配置provider
	 * @param authProvider
	 */
	protected void configure(DaoAuthenticationProvider authProvider){
	}
	/**
	 * 配置不需要进行权限认证的资源
	 */
    public void configure(WebSecurity web) throws Exception { 
    	Arrays.asList(DEFAULT_IGNORES).forEach((item)->log.info("ignored resource:{}",item.trim()));
    	web.ignoring().antMatchers(DEFAULT_IGNORES);
		if(wiscessSecurityProperties.getErrorPage()!=null){
			web.ignoring().antMatchers(wiscessSecurityProperties.getErrorPage());
		}
		if(wiscessSecurityProperties.getIgnored()!=null && wiscessSecurityProperties.getIgnored().size()>0){
			web.ignoring().antMatchers(wiscessSecurityProperties.getIgnored().toArray(new String[0]));
		}
    }  

    /**
     * 配置权限认证
     */
    protected void configure(HttpSecurity http) throws Exception {
    	//处理Header的内容
		http.headers()
			.xssProtection()
			.and()
			.frameOptions()
				.sameOrigin();
		//排除Csrf路径
		if(this.wiscessSecurityProperties.getExecludeUrls()!=null && this.wiscessSecurityProperties.getExecludeUrls().size()>0){
			http.csrf().ignoringAntMatchers(this.wiscessSecurityProperties.getExecludeUrls().toArray(new String[0]));
		}

		if(wiscessSecurityProperties.isSsoMode()){
			http.apply(new SSOLoginConfigurer<HttpSecurity>())
				//认证失败后的跳转地址
				.failureUrl(wiscessSecurityProperties.getSso().getFailureUrl())
				//成功后的处理，必须放在defaultSuccessUrl后面，可以在Session中保存用户信息
				.successHandler(loginSuccessHandler)
				.permitAll();
		
			//自定义登录入口，当权限认证失败时，跳转到统一认证中心去进行验证
			http.exceptionHandling()
				.authenticationEntryPoint(new SSOAuthenticationEntryPoint(wiscessSecurityProperties.getSso().getAuthUrl()));
			
			// 自定义注销  ，两种方式，二选一，不能同时存在
			if(wiscessSecurityProperties.getSso().getLogoutUrl().equals("/logout")){
				//方式1：本系统退出后要通知统一认证中心退出
				http.logout()
					.logoutUrl("/logout")
		        	//可以用GET方式退出
		        	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				    .logoutSuccessHandler(new SSOLogoutSuccessHandler()) 
				    .permitAll();
				
			}else{
				//方式2：本项目不是门户系统，不负责向统一认证中心发送退出指令，作为普通的系统，须实现jslogout功能，供门户系统调用。
		        http.logout()
		        	.logoutUrl("/jslogout")
		        	//可以用GET方式退出
		        	.logoutRequestMatcher(new AntPathRequestMatcher("/jslogout"))
		        	.logoutSuccessHandler(new SSOLogoutJsSuccessHandler())
		        	.permitAll();
			}
		}else{
			//定义错误页面
			http.exceptionHandling().
					accessDeniedPage(wiscessSecurityProperties.getErrorPage());
			http.formLogin()
			
				//用于将页面中的验证码保存起来进行比较
		        .authenticationDetailsSource(captchaAuthenticationDetailsSource())
		        .loginPage("/login")
		        .successHandler(loginSuccessHandler)
		        .permitAll();
			//增加过滤器，处理用户名的加密
			if(wiscessSecurityProperties.isEncryptUsername() || wiscessSecurityProperties.isEncryptPassword()) {
				http.addFilter(encryptUsernamePasswordAuthenticationFilter(wiscessSecurityProperties.isEncryptUsername() , wiscessSecurityProperties.isEncryptPassword()));
			}
			
			http.logout()
		    	//自定义退出链接
	        	.logoutUrl("/logout")
	        	//可以用GET方式退出
	        	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	        	//退出登录后的默认网址是”/login?logout”
	            .permitAll();
		}
		// session管理  
        http.sessionManagement()
		        	.sessionFixation()
		        	.changeSessionId()
		            .maximumSessions(wiscessSecurityProperties.getMaxSessionNum())
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

	@Bean
	public CaptchaAuthenticationDetailsSource captchaAuthenticationDetailsSource() {
		return new CaptchaAuthenticationDetailsSource();
	}
	@Bean
	public PasswordEncoder passwordEncoder(){
		String passwordType=wiscessSecurityProperties.isSsoMode()
				?wiscessSecurityProperties.getSso().getEncryptType()
				:wiscessSecurityProperties.getPasswordType();
		if(passwordType.equals("md5")){
			return new MD5EncryptEncoder();
		}else if(passwordType.equalsIgnoreCase("md5IgnoreCase")){
			return new MD5EncryptEncoder(new IgnoreCaseEncryptEncoder());
		}else if(passwordType.equals("MD5")){
			return new MD5EncryptEncoder(new UpperCaseEncryptEncoder());
		}else if(passwordType.equals("MD5MD5")){
			//二次md5加密
			return new MD5EncryptEncoder(new UpperCaseEncryptEncoder(new MD5EncryptEncoder(new UpperCaseEncryptEncoder())));
		}else if(passwordType.equalsIgnoreCase("RSA")){
			return new RSAEncryptEncoder();
		}
		return null;
	}
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    /**
     * 加密的用户名和密码
     * @return
     */
    public UsernamePasswordAuthenticationFilter encryptUsernamePasswordAuthenticationFilter(boolean encryptUsername,boolean encryptPassword) throws Exception {
    	UsernamePasswordAuthenticationFilter filter = new EncryptUsernamePasswordAuthenticationFilter(encryptUsername,encryptPassword);
    	filter.setAuthenticationManager(authenticationManager());
        //只有post请求才拦截
    	filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    	filter.setAuthenticationSuccessHandler(loginSuccessHandler);
    	filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
    	filter.setAuthenticationDetailsSource(captchaAuthenticationDetailsSource());
        return filter;
    }
}
