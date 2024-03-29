package com.wiscess.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wiscess.security.jwt.DefaultJwtLoginSuccessHandler;
import com.wiscess.security.jwt.DefaultJwtLogoutSuccessHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
import com.wiscess.security.vue.DefaultVueAccessDeniedHandler;
import com.wiscess.security.vue.DefaultVueAuthenticationEntryPoint;
import com.wiscess.security.vue.DefaultVueLoginFilureHandler;
import com.wiscess.security.vue.DefaultVueLoginSuccessHandler;
import com.wiscess.security.web.CaptchaAuthenticationDetailsSource;
import com.wiscess.security.web.CaptchaDaoAuthenticationProvider;
import com.wiscess.security.web.EncryptUsernamePasswordAuthenticationFilter;
import com.wiscess.security.web.SwitchRoleAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * 权限认证配置
 * spring boot3.0废弃了extends WebSecurityConfigurerAdapter 的方式，改为采用添加@Bean新方式
 * @since 3.0
 * @author wh
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WiscessSecurityProperties.class)
@EnableWebSecurity
@EnableMethodSecurity()  //开启Action上@PreAuthorize的权限控制
public abstract class WiscessWebSecurityConfig {
	/**
	 * 默认静态资源文件
	 */
	public static String[] DEFAULT_IGNORES="/css/**,/less/**,/plugin/**,/bower_components/**,/js/**,/images/**,/webjars/**,/*/favicon.ico,/captcha.jpg".split(",");

	/**
	 * @since 3.0
	 */
	@Autowired
	protected AuthenticationConfiguration authenticationConfiguration;

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
	/**
     * 1.配置权限认证，主控制器
     * @since 3.0
     */
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		log.info("**********Config Security Filter Chain***************************");
		//处理Header的内容
		http.headers((headers) -> {
					headers
							.xssProtection(Customizer.withDefaults())
							.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
				}
		);
//		http.headers()
//			.xssProtection()
//			.and()
//			.frameOptions()
//				.disable();
		if(wiscessSecurityProperties.isCsrf()) {
			//排除Csrf路径
			if(this.wiscessSecurityProperties.getExecludeUrls()!=null && this.wiscessSecurityProperties.getExecludeUrls().size()>0){
				http.csrf((csrf) -> csrf.ignoringRequestMatchers(this.wiscessSecurityProperties.getExecludeUrls().toArray(new String[0])));
			}
		}else {
			//不使用csrf
			http.csrf(AbstractHttpConfigurer::disable);
		}
		//认证模式只能选一种
		//SSO认证模式
		if(wiscessSecurityProperties.isSsoMode()){
			log.info("使用SSO模式");
			http.with(new SSOLoginConfigurer<HttpSecurity>(),
					cfg -> cfg
							//认证失败后的跳转地址
							.failureUrl(wiscessSecurityProperties.getSso().getFailureUrl())
							//成功后的处理，必须放在defaultSuccessUrl后面，可以在Session中保存用户信息
							.successHandler(loginSuccessHandler)
							.permitAll()
			);

			//自定义登录入口，当权限认证失败时，跳转到统一认证中心去进行验证
			http.exceptionHandling( handling -> handling
					.authenticationEntryPoint(new SSOAuthenticationEntryPoint(wiscessSecurityProperties.getSso().getAuthUrl())));

			// 自定义注销  ，两种方式，二选一，不能同时存在
			if(wiscessSecurityProperties.getSso().getLogoutUrl().equals("/logout")){
				//方式1：本系统退出后要通知统一认证中心退出
				http.logout(c -> c
						.logoutUrl("/logout")
						//可以用GET方式退出
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessHandler(new SSOLogoutSuccessHandler())
						.permitAll()
				);

			}else{
				//方式2：本项目不是门户系统，不负责向统一认证中心发送退出指令，作为普通的系统，须实现jslogout功能，供门户系统调用。
				http.logout(c -> c
						.logoutUrl("/jslogout")
						//可以用GET方式退出
						.logoutRequestMatcher(new AntPathRequestMatcher("/jslogout"))
						.logoutSuccessHandler(new SSOLogoutJsSuccessHandler())
						.permitAll()
				);
			}
		} else if(wiscessSecurityProperties.isVueMode()) {
			//Vue认证模式
			log.info("使用Vue模式");
			//启用跨域模式
			http.cors(t -> {});
			//定义error页面
			http.exceptionHandling(t -> t
					.accessDeniedHandler(vueAccessDeniedHandler())
					.authenticationEntryPoint(vueAuthenticationEntryPoint())
			);
			//vue模式不校验验证码，由前端实现
			http.formLogin(form -> form
					.loginPage("/login")
					//用于将页面中的验证码保存起来进行比较
					.authenticationDetailsSource(captchaAuthenticationDetailsSource())
					.successHandler(wiscessSecurityProperties.isJwtMode()?jwtLoginSuccessHandler():vueLoginSuccessHandler())  //自定义登录成功的处理，可以返回包含用户信息的json
					.failureHandler(vueLoginFailureHandler())  //自定义登录失败的处理，用于提示用户登录失败的信息
					.permitAll()
			);
			//增加过滤器，处理用户名的加密
			if(wiscessSecurityProperties.isEncryptUsername() || wiscessSecurityProperties.isEncryptPassword()) {
				http.addFilterBefore(encryptUsernamePasswordAuthenticationFilter(wiscessSecurityProperties.isEncryptUsername() , wiscessSecurityProperties.isEncryptPassword()),UsernamePasswordAuthenticationFilter.class);
			}
			http.logout(t -> t
					//自定义退出链接
					.logoutUrl("/logout")
					//可以用GET方式退出
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					//配置登出成功的处理，
					.logoutSuccessHandler(wiscessSecurityProperties.isJwtMode()?jwtLogoutSuccessHandler():vueLogoutSuccessHandler())
					.invalidateHttpSession(true)
					.permitAll()
			);
		} else if(wiscessSecurityProperties.isOauth2()) {
			//OAuth2
			log.info("使用oauth2登录");
			//处理Header的内容
            http.csrf(CsrfConfigurer::disable);

			http
					.logout(t->t//提供注销的支持。这是在使用WebSecurityConfigurerAdapter时自动应用的。
							//自定义退出链接
							.logoutUrl("/logout")//触发注销发生的URL(默认为 /logout)。如果启用了CSRF保护(默认)，那么请求也必须是POST。
					)
					.oauth2Login(t->t
							.successHandler(loginSuccessHandler)
					)
			;
		} else{
			log.info("使用本地账号登录");
			//普通表单提交模式
			//定义错误页面
			http.exceptionHandling(h->h.
							accessDeniedPage(wiscessSecurityProperties.getErrorPage())
					)
					.formLogin(f->f
							.loginPage("/login")
							//用于将页面中的验证码保存起来进行比较
							.authenticationDetailsSource(captchaAuthenticationDetailsSource())
							.successHandler(loginSuccessHandler)
							.permitAll()
					);
			//增加过滤器，处理用户名的加密
			if(wiscessSecurityProperties.isEncryptUsername() || wiscessSecurityProperties.isEncryptPassword()) {
				http.addFilterAt(encryptUsernamePasswordAuthenticationFilter(wiscessSecurityProperties.isEncryptUsername() , wiscessSecurityProperties.isEncryptPassword()),UsernamePasswordAuthenticationFilter.class);
			}

			http.logout(t->t
					//自定义退出链接
					.logoutUrl("/logout")
					//可以用GET方式退出
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.invalidateHttpSession(true)
					.permitAll()
			);
		}
		// session管理，jwt模式不需要session
		if(wiscessSecurityProperties.isJwtMode()){
			http // 由于使用的是JWT，我们这里不需要csrf
					.csrf(AbstractHttpConfigurer::disable)
					// 基于token，所以不需要session
					.sessionManagement(t->t
							.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
							.sessionFixation()
							.none()
					);
			//不添加，使用jwt时均为oauth2认证方式，由oauth2完成token的认证
//			http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
		} else {
			http.sessionManagement(t->t
					.sessionFixation()
					.changeSessionId()
					//允许同一用户同时在线数
					.maximumSessions(wiscessSecurityProperties.getMaxSessionNum())
					//true：超过的用户无法登录；false：踢掉前面登录的用户，默认为false
					.maxSessionsPreventsLogin(wiscessSecurityProperties.getMaxSessionsPreventsLogin())
					.expiredUrl("/login?expired")
			);
		}
		//配置不须限制的资源
		ignoreResources(http);
		//允许自定义配置权限
		myConfigure(http);

		return http.build();
	}

	/**
	 * 自定义权限控制
	 * @param http
	 * @throws Exception
	 */
	protected abstract void myConfigure(HttpSecurity http)throws Exception;

	/**
     * 2.认证管理器，登录的时候参数会传给 authenticationManager
     * @since 3.0
     */

	/**
     * 认证管理器，登录的时候参数会传给 authenticationManager
     * @since 3.0
     */
    @Bean
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		log.debug("authenticationManager with AuthenticationConfiguration");
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
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
        return authProvider;
    }

	/**
	 * 3.配置不需要进行权限认证的资源
	 */
	private void ignoreResources(HttpSecurity http) throws Exception {
		List<String> ignores=new ArrayList<String>(Arrays.asList(DEFAULT_IGNORES));
		if(wiscessSecurityProperties.getErrorPage()!=null){
			ignores.add(wiscessSecurityProperties.getErrorPage());
		}
		if(wiscessSecurityProperties.getIgnored()!=null && wiscessSecurityProperties.getIgnored().size()>0){
			ignores.addAll(wiscessSecurityProperties.getIgnored());
		}
        ignores.stream().distinct().forEach((item)->{
			log.info("ignored resource:{}",item.trim());
			try {
				http.authorizeHttpRequests(t->t
						.requestMatchers(item.trim())
						.permitAll()
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
    }  

    @Bean
    public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
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

	/**
     * 加密的用户名和密码
     * @return
     */
    public UsernamePasswordAuthenticationFilter encryptUsernamePasswordAuthenticationFilter(boolean encryptUsername,boolean encryptPassword) throws Exception {
		return new EncryptUsernamePasswordAuthenticationFilter(encryptUsername,encryptPassword);
    }
    

    /**
     * 切换用户角色
     * @return
     */
    public SwitchRoleAuthenticationFilter switchRoleAuthenticationFilter() throws Exception {
		SwitchRoleAuthenticationFilter filter = new SwitchRoleAuthenticationFilter();
		filter.setAuthenticationSuccessHandler(loginSuccessHandler);
		return filter;
    }

    /**
     * 配置记住密码
     * @param http
     * @param key
     * @throws Exception
     */
    public void remenberMe(HttpSecurity http,String key) throws Exception {
		http.rememberMe(r->r
				.userDetailsService(userDetailsService)
				.key(key)
				.tokenValiditySeconds(1209600)
				.authenticationSuccessHandler(loginSuccessHandler)
		);
	}

    /**
     * 配置Jwt方式默认的登录成功的处理
     * @return
     */
    public AuthenticationSuccessHandler jwtLoginSuccessHandler() {
		return new DefaultJwtLoginSuccessHandler();
    }
    /**
     * 配置Vue方式默认的登录成功的处理
     * @return
     */
    public AuthenticationSuccessHandler vueLoginSuccessHandler() {
		return new DefaultVueLoginSuccessHandler();
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
