package com.wiscess.oauth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.wiscess.oauth.response.AuthorizationDeniedResponse;
import com.wiscess.oauth.response.DefaultAuthorizationDeniedResponse;
import com.wiscess.oauth.translator.WiscessWebResponseExceptionTranslator;

import lombok.extern.slf4j.Slf4j;

import static com.wiscess.oauth.config.OauthProperties.SECURITY_OAUTH_PREFIX;

import java.util.HashMap;
import java.util.Map;
/**
 * Oauth 认证服务配置
 * @author wh
 *
 */
@Slf4j
public class AuthorizationServerAutoConfiguration extends AbstractAuthorizationServerConfig{
	/**
     * Oauth Config Properties
     */
	protected OauthProperties oauthProperties;
	
	public AuthorizationServerAutoConfiguration(OauthProperties oauthProperties) {
		this.oauthProperties=oauthProperties;
	}
	
    /**
     * This method will be instantiated if "api.boot.oauth.jwt.enable = true" is configured
     * The "api.boot.oauth.jwt.sign-key" parameter will be used as the encrypted key value
     * the sign-key parameter default value is "ApiBoot"
     *
     * @return {@link AccessTokenConverter}
     */
    @Bean
    @ConditionalOnProperty(prefix = SECURITY_OAUTH_PREFIX, name = "jwt.enable", havingValue = "true", matchIfMissing = true)
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        log.info("key:{}",oauthProperties.getJwt().getSignKey());
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
          /**
           * 重写增强token的方法
           * 自定义返回相应的信息
           *
           */
          @Override
          public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

              String userName = authentication.getUserAuthentication().getName();
              // 与登录时候放进去的UserDetail实现类一直查看link{SecurityConfiguration}
              User user = (User) authentication.getUserAuthentication().getPrincipal();
              /** 自定义一些token属性 ***/
              final Map<String, Object> additionalInformation = new HashMap<>();
              additionalInformation.put("userName", userName);
              additionalInformation.put("roles", user.getAuthorities());
              ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
              OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
              return enhancedToken;
          }

      };
   // 测试用,资源服务使用相同的字符达到一个对称加密的效果,生产时候使用RSA非对称加密方式
      accessTokenConverter.setSigningKey(oauthProperties.getJwt().getSignKey());
      return accessTokenConverter;
    }
    
    /**
     * The default {@link AccessTokenConverter}
     *
     * @return {@link DefaultAccessTokenConverter}
     */
    @Bean(name = "accessTokenConverter")
    @ConditionalOnProperty(prefix = SECURITY_OAUTH_PREFIX, name = "jwt.enable", havingValue = "false")
    public AccessTokenConverter defaultAccessTokenConverter() {
        return new DefaultAccessTokenConverter();
    } 
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationDeniedResponse authorizationDeniedResponse() {
        return new DefaultAuthorizationDeniedResponse();
    }
    /**
     * Customize the exception output format of the authentication server
    *
    * @return {@link ApiBootWebResponseExceptionTranslator}
    * @see WebResponseExceptionTranslator
    * @see org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator
    * @see ApiBootWebResponseExceptionTranslator
    */
   @SuppressWarnings("rawtypes")
  @Bean
   @ConditionalOnMissingBean
   public WebResponseExceptionTranslator webResponseExceptionTranslator(AuthorizationDeniedResponse response) {
       return new WiscessWebResponseExceptionTranslator(response);
   }
}
