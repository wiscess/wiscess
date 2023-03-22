package com.wiscess.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wiscess.security.jwt.DefaultUserMapRepository;
import com.wiscess.security.jwt.DefaultJwtLoginSuccessHandler;
import com.wiscess.security.jwt.DefaultJwtLogoutSuccessHandler;
import com.wiscess.security.jwt.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
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
import com.wiscess.security.voter.CustomAuthorityVoter;
import com.wiscess.security.vue.DefaultVueAccessDeniedHandler;
import com.wiscess.security.vue.DefaultVueAuthenticationEntryPoint;
import com.wiscess.security.vue.DefaultVueLoginFilureHandler;
import com.wiscess.security.vue.DefaultVueLoginSuccessHandler;
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
	public static String[] DEFAULT_IGNORES="/css/**,/less/**,/plugin/**,/bower_components/**,/js/**,/images/**,/webjars/**,/**/favicon.ico,/captcha.jpg".split(",");
	
	@Autowired
	protected WiscessSecurityProperties wiscessSecurityProperties;
	//登录成功
	@Autowired(required=false)
	protected AuthenticationSuccessHandler loginSuccessHandler;
	//登录失败
	@Autowired(required=false)
	protected AuthenticationFailureHandler loginFailureHandler;
	//权限校验拒绝
	@Autowired(required=false)
	protected AccessDeniedHandler accessDeniedHandler;
	@Autowired(required=false)
	protected AuthenticationEntryPoint authenticationEntryPoint;
	//退出成功
	@Autowired(required=false)
	protected LogoutSuccessHandler logoutSuccessHandler;
	@Autowired
	protected UserDetailsServiceImpl userDetailsService;
	
    @Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	//添加多个Provider,针对不同的Token进行不同的认证方式

		DaoAuthenticationProvider authProvider;
		if(wiscessSecurityProperties.isSsoMode()){
			log.info("WebSecurityConfig configured SSOAuthenticationProvider with {}",wiscessSecurityProperties.getSso().getAuthUrl());
			authProvider=new SSOAuthenticationProvider();
		}else{
			log.info("WebSecurityConfig configured DaoAuthenticationProvider {} {} {} captcha.",
					(wiscessSecurityProperties.isVueMode()?"for VueMode":""),
					(wiscessSecurityProperties.isJwtMode()?"with JwtToken":""),
					(wiscessSecurityProperties.isCaptcha()?"with":"without"));
			//从2.0开始，不再使用jdbc的方式来完成权限验证
			authProvider = new CaptchaDaoAuthenticationProvider(wiscessSecurityProperties);
		}
		//设置密码加密方式,RSA为避免私钥泄露，验证方式用sign/verify
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
    @Override
	public void configure(WebSecurity web) throws Exception { 
    	List<String> ignores=new ArrayList<String>(Arrays.asList(DEFAULT_IGNORES));
		if(wiscessSecurityProperties.getErrorPage()!=null){
			ignores.add(wiscessSecurityProperties.getErrorPage());
		}
		if(wiscessSecurityProperties.getIgnored()!=null && wiscessSecurityProperties.getIgnored().size()>0){
			ignores.addAll(wiscessSecurityProperties.getIgnored());
		}
		Set<String> set = new  HashSet<>(); 
        set.addAll(ignores);
        set.forEach((item)->log.info("ignored resource:{}",item.trim()));
		web.ignoring().antMatchers(set.toArray(new String[0]));
    }  

    /**
     * 配置权限认证
     */
    @Override
	protected void configure(HttpSecurity http) throws Exception {
    	//处理Header的内容
		http.headers()
			.xssProtection()
			.and()
			.frameOptions()
				.disable();
		//排除Csrf路径
		if(this.wiscessSecurityProperties.getExecludeUrls()!=null && this.wiscessSecurityProperties.getExecludeUrls().size()>0){
			http.csrf().ignoringAntMatchers(this.wiscessSecurityProperties.getExecludeUrls().toArray(new String[0]));
		}
		//认证模式只能选一种
		//SSO认证模式
		if(wiscessSecurityProperties.isSsoMode()){
			http.apply(new SSOLoginConfigurer<HttpSecurity>())
				//认证失败后的跳转地址
				.failureUrl(wiscessSecurityProperties.getSso().getFailureUrl())
				//成功后的处理，必须放在defaultSuccessUrl后面，可以在Session中保存用户信息
				.successHandler(loginSuccessHandler())
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
		}
		else if(wiscessSecurityProperties.isVueMode()) {
			//Vue认证模式
			//启用跨域模式
			http.cors();
			//定义error页面
			http.exceptionHandling()
				.accessDeniedHandler(vueAccessDeniedHandler())
				.authenticationEntryPoint(vueAuthenticationEntryPoint())
				;
			//vue模式不校验验证码，由前端实现
			http.formLogin()
				.loginPage("/login")
				//用于将页面中的验证码保存起来进行比较
		        .authenticationDetailsSource(captchaAuthenticationDetailsSource())
		        .successHandler(wiscessSecurityProperties.isJwtMode()?jwtLoginSuccessHandler():vueLoginSuccessHandler())  //自定义登录成功的处理，可以返回包含用户信息的json
		        .failureHandler(vueLoginFailureHandler())  //自定义登录失败的处理，用于提示用户登录失败的信息
		        .permitAll();
			//增加过滤器，处理用户名的加密
			if(wiscessSecurityProperties.isEncryptUsername() || wiscessSecurityProperties.isEncryptPassword()) {
				http.addFilterBefore(encryptUsernamePasswordAuthenticationFilter(wiscessSecurityProperties.isEncryptUsername() , wiscessSecurityProperties.isEncryptPassword()),UsernamePasswordAuthenticationFilter.class);
			}
			http.logout()
		    	//自定义退出链接
	        	.logoutUrl("/logout")
	        	//可以用GET方式退出
	        	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	        	//配置登出成功的处理，
	        	.logoutSuccessHandler(wiscessSecurityProperties.isJwtMode()?jwtLogoutSuccessHandler():vueLogoutSuccessHandler())
	        	.invalidateHttpSession(true)
	            .permitAll();
		}
		else{
			//普通表单提交模式
			//定义错误页面
			http.exceptionHandling().
					accessDeniedPage(wiscessSecurityProperties.getErrorPage());
			http.formLogin()
	        	.loginPage("/login")
				//用于将页面中的验证码保存起来进行比较
		        .authenticationDetailsSource(captchaAuthenticationDetailsSource())
//		        .usernameParameter("username")
		        .successHandler(loginSuccessHandler())
		        .permitAll();
			//增加过滤器，处理用户名的加密
			if(wiscessSecurityProperties.isEncryptUsername() || wiscessSecurityProperties.isEncryptPassword()) {
				http.addFilterBefore(encryptUsernamePasswordAuthenticationFilter(wiscessSecurityProperties.isEncryptUsername() , wiscessSecurityProperties.isEncryptPassword()),UsernamePasswordAuthenticationFilter.class);
			}
			
			http.logout()
		    	//自定义退出链接
	        	.logoutUrl("/logout")
	        	//可以用GET方式退出
	        	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	        	.invalidateHttpSession(true)
	            .permitAll();
		}
		// session管理，jwt模式不需要session
		if(wiscessSecurityProperties.isJwtMode()){
			http. // 由于使用的是JWT，我们这里不需要csrf
				csrf().disable()
				// 基于token，所以不需要session
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.sessionFixation()
				.none();
			//不添加，使用jwt时均为oauth2认证方式，由oauth2完成token的认证
//			http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		}
		else {
			http.sessionManagement()
					.sessionFixation()
					.changeSessionId()
					//允许同一用户同时在线数
					.maximumSessions(wiscessSecurityProperties.getMaxSessionNum())
					//true：超过的用户无法登录；false：踢掉前面登录的用户，默认为false
					.maxSessionsPreventsLogin(wiscessSecurityProperties.getMaxSessionsPreventsLogin())
					.expiredUrl("/login?expired")
			;
		}
		myConfigure(http);
	}
//	@Bean
//	public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
//		return new JwtAuthenticationTokenFilter();
//	}
    
	@Bean
	public AccessDecisionManager accessDecisionManager() {
	    List<AccessDecisionVoter<? extends Object>> decisionVoters 
	      = Arrays.asList(
	        new WebExpressionVoter(),
//	        new RoleVoter(),
	        new AuthenticatedVoter(),
	        //自定义投票器，只对authenticated进行数据库判断，其他已定义的权限均弃权
	        new CustomAuthorityVoter());
//	    AffirmativeBased – 任何一个AccessDecisionVoter返回同意则允许访问
//	    ConsensusBased – 同意投票多于拒绝投票（忽略弃权回答）则允许访问
//	    UnanimousBased – 每个投票者选择弃权或同意则允许访问
	    return new UnanimousBased(decisionVoters);
	}

    
    /**
     * session监听器，监听session的创建和销毁的代码
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
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
    @Override
	@Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
	@ConditionalOnMissingBean
	public UserRepository userRepository(){
    	return new DefaultUserMapRepository();
	}
    @Bean
	@ConditionalOnMissingBean
	public AuthenticationSuccessHandler loginSuccessHandler(){
    	return new SavedRequestAwareAuthenticationSuccessHandler();
	}
    /**
     * 加密的用户名和密码
     * @return
     */
    public UsernamePasswordAuthenticationFilter encryptUsernamePasswordAuthenticationFilter(boolean encryptUsername,boolean encryptPassword) throws Exception {
    	UsernamePasswordAuthenticationFilter filter = new EncryptUsernamePasswordAuthenticationFilter(encryptUsername,encryptPassword);
        return filter;
    }
    /**
     * 配置记住密码
     * @param http
     * @param key
     * @throws Exception
     */
    public void remenberMe(HttpSecurity http,String key) throws Exception {
		http.rememberMe()
			.userDetailsService(userDetailsService)
			.key(key)
			.tokenValiditySeconds(1209600)
			.authenticationSuccessHandler(loginSuccessHandler());
	}

    /**
     * 配置Jwt方式默认的登录成功的处理
     * @return
     */
    public AuthenticationSuccessHandler jwtLoginSuccessHandler() {
    	if(loginSuccessHandler==null) {
    		loginSuccessHandler=new DefaultJwtLoginSuccessHandler();
    	}
    	return loginSuccessHandler;
    }
    /**
     * 配置Vue方式默认的登录成功的处理
     * @return
     */
    public AuthenticationSuccessHandler vueLoginSuccessHandler() {
    	if(loginSuccessHandler==null) {
    		loginSuccessHandler=new DefaultVueLoginSuccessHandler();
    	}
    	return loginSuccessHandler;
    }
    /**
     * 配置Vue方式默认的登录失败的处理
     */
    public AuthenticationFailureHandler vueLoginFailureHandler() {
    	if(loginFailureHandler==null) {
    		loginFailureHandler=new DefaultVueLoginFilureHandler();
    	}
    	return loginFailureHandler;
    }
    /**
     * 配置Jwt方式默认的登出的处理
     */
    public LogoutSuccessHandler jwtLogoutSuccessHandler() {
    	if(logoutSuccessHandler==null) {
    		logoutSuccessHandler=new DefaultJwtLogoutSuccessHandler();
    	}
    	return logoutSuccessHandler;
    }
    /**
     * 配置Vue方式默认的登出的处理
     */
    public LogoutSuccessHandler vueLogoutSuccessHandler() {
    	if(logoutSuccessHandler==null) {
    		logoutSuccessHandler=new DefaultJwtLogoutSuccessHandler();
    	}
    	return logoutSuccessHandler;
    }
    /**
     * 配置拒绝的处理
     * @return
     */
    public AccessDeniedHandler vueAccessDeniedHandler() {
    	if(accessDeniedHandler==null) {
    		accessDeniedHandler = new DefaultVueAccessDeniedHandler();
    	}
    	return accessDeniedHandler;
    }
    /**
     * 配置拒绝的处理
     * @return
     */
    public AuthenticationEntryPoint vueAuthenticationEntryPoint() {
    	if(authenticationEntryPoint==null) {
    		authenticationEntryPoint = new DefaultVueAuthenticationEntryPoint();
    	}
    	return authenticationEntryPoint;
    }
}
