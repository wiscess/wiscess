package com.wiscess.oauth.config;

import static com.wiscess.oauth.config.OauthProperties.SECURITY_OAUTH_PREFIX;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Oauth 授权服务器Jdbc方式实现
 * @author wh
 */
@Configuration
 @ConditionalOnClass(AbstractAuthorizationServerConfig.class)
 @EnableConfigurationProperties(OauthProperties.class)
 @EnableAuthorizationServer
 @ConditionalOnProperty(prefix = SECURITY_OAUTH_PREFIX, name = "away", havingValue = "jdbc")
public class AuthorizationServerJdbcAutoConfiguration extends AuthorizationServerAutoConfiguration{
	 private DataSource dataSource;

	public AuthorizationServerJdbcAutoConfiguration(OauthProperties oauthProperties,
			DataSource dataSource) {
		super(oauthProperties);
		this.dataSource = dataSource;
	}
	
	/**
     * configuration clients
     *
     * @param clients client details service configuration
     * @throws Exception exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    	clients.jdbc(dataSource);
    }

    /**
     * 配置Jdbc方式存储令牌
     *
     * @return TokenStore
     */
    @Bean
    public TokenStore jdbcTokenStore() {
        return new JdbcTokenStore(dataSource);
    }
}

