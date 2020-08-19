package com.wiscess.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 集成Oauth2 相关配置实现
 * @author wh
 *
 */
@Slf4j
public abstract class AbstractAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	/**
     * authentication manager
     */
    @Autowired
    private AuthenticationManager authenticationManager;
    /**
     * Token Store
     */
    @Autowired
    private TokenStore tokenStore;
    /**
     * Access Token Converter
     */
    @Autowired
    private AccessTokenConverter accessTokenConverter;
    /**
     * Oauth Client Detail Service
     */
    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    protected PasswordEncoder passwordEncoder;
    // 该对象将为刷新token提供支持
    @Autowired
    private UserDetailsService userDetailsService;
    
    @SuppressWarnings("rawtypes")
	@Autowired
    private WebResponseExceptionTranslator exceptionTranslator;
    /**
     * Configure secret encryption in the same way as ApiBoot Security
     *
     * @param security AuthorizationServerSecurityConfigurer
     * @throws Exception 异常信息
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        log.info("AuthorizationServerConfig中配置HttpSecurity对象执行");
    	// 开启该配置，才可以使用oauth2认证，否则模式默认是HTTP的基本认证 
        security.allowFormAuthenticationForClients();
        // 这两个配置，目的是开启两个端点url,默认服务器是关闭的 /oauth/token_key /oauth/check_token
        security
        	.tokenKeyAccess("permitAll()")
        	.checkTokenAccess("isAuthenticated()");
        security
            .passwordEncoder(passwordEncoder);
    }
    /**
     * Configuration and Integration of Spring Security to Complete User Validity Authentication
     *
     * @param endpoints AuthorizationServerEndpointsConfigurer
     * @throws Exception 异常信息
     */
    @SuppressWarnings("unchecked")
	@Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
            .authenticationManager(authenticationManager)
            .tokenStore(tokenStore)
            .accessTokenConverter(accessTokenConverter)
    		.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
    		.userDetailsService(userDetailsService)
    		;

        if (!ObjectUtils.isEmpty(exceptionTranslator)) {
        	//自定义认证异常处理页
            endpoints.exceptionTranslator(exceptionTranslator);
        }
    }
    /**
     * token enhancer
     *
     * @return TokenEnhancer
     */
    private TokenEnhancer tokenEnhancer() {
        if (accessTokenConverter instanceof JwtAccessTokenConverter) {
            return (TokenEnhancer) accessTokenConverter;
        }
        return null;
    }
    
    /**
     * <p>注意，自定义TokenServices的时候，需要设置@Primary，否则报错，</p>
     * @return
     */
    @Primary
    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setClientDetailsService(clientDetailsService);
        tokenServices.setTokenEnhancer(tokenEnhancer());
        tokenServices.setAccessTokenValiditySeconds(60*60*12); // token有效期自定义设置，默认12小时
        tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);//默认30天，这里修改
        
        return tokenServices;
    }
}
