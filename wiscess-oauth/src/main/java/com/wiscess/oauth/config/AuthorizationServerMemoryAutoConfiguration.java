package com.wiscess.oauth.config;

import static com.wiscess.oauth.config.OauthProperties.SECURITY_OAUTH_PREFIX;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import lombok.extern.slf4j.Slf4j;

/**
 * Oauth Memory Away Support
 * @author wh
 */
@Slf4j
@Configuration
 @ConditionalOnClass(AbstractAuthorizationServerConfig.class)
 @EnableConfigurationProperties(OauthProperties.class)
 @EnableAuthorizationServer
 @ConditionalOnProperty(prefix = SECURITY_OAUTH_PREFIX, name = "away", havingValue = "memory", matchIfMissing = true)
public class AuthorizationServerMemoryAutoConfiguration extends AuthorizationServerAutoConfiguration{

	public AuthorizationServerMemoryAutoConfiguration(OauthProperties oauthProperties) {
		super(oauthProperties);
	}
	
	/**
     * configuration clients
     *
     * @param clients client details service configuration
     * @throws Exception exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    	log.info("AuthorizationServerMemoryAutoConfiguration configure.");
        InMemoryClientDetailsServiceBuilder inMemoryClientDetailsServiceBuilder = clients.inMemory();
        oauthProperties.getClients().stream().forEach(client -> inMemoryClientDetailsServiceBuilder.withClient(client.getClientId())
            .secret(passwordEncoder.encode(client.getClientSecret()))
            .authorizedGrantTypes(client.getGrantTypes())
            .scopes(client.getScopes())
            .resourceIds(client.getResourceId())
            .refreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds())
            .accessTokenValiditySeconds(client.getAccessTokenValiditySeconds()));
    }

    /**
     * memory away token store
     *
     * @return TokenStore
     */
    @Bean
    public TokenStore memoryTokenStore() {
    	InMemoryTokenStore tokenStore = new InMemoryTokenStore();
    	tokenStore.setAuthenticationKeyGenerator(authenticationKeyGenerator());
    	return tokenStore;
    }
}

