package com.wiscess.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

import static com.wiscess.oauth.config.OauthProperties.SECURITY_OAUTH_PREFIX;

import java.util.ArrayList;
import java.util.List;

/**
 * 整合Oauth2 相关属性配置
 * 
 * @author wh
 *
 */

@Data
@ConfigurationProperties(prefix = SECURITY_OAUTH_PREFIX,
		ignoreInvalidFields = true,  /*当我们为属性配置错误的值时，而又不希望 Spring Boot 应用启动失败，我们可以设置 ignoreInvalidFields 
		属性为 true (默认为 false)*/
		ignoreUnknownFields = false   /*默认情况下，Spring Boot 会忽略那些不能绑定到 @ConfigurationProperties 类字段的属性
															然而，当配置文件中有一个属性实际上没有绑定到 @ConfigurationProperties 类时，我们可能希望启动失败。
															也许我们以前使用过这个配置属性，但是它已经被删除了，这种情况我们希望被触发告知手动从 application.properties 删除这个属性
															*/
		)
public class OauthProperties {

    /**
     * 安全配置前缀
     */
    public static final String SECURITY_OAUTH_PREFIX = "security.oauth";
    /**
     * Oauth2认证存储方式，默认内存方式
     *
     * @see OAuthAway
     */
    private OAuthAway away = OAuthAway.memory;
	/**
	 * 默认启用oauth
	 */
	private Boolean enabled = Boolean.TRUE;

	/**
	 * 最大登录用户，默认-1，不限制
	 */
	private Long maxOnlineUser = -1L;
    /**
     * 配置JWT格式化Oauth2返回的token
     */
    private Jwt jwt = new Jwt();
	/**
     * configure multiple clients
     */
    @SuppressWarnings("serial")
	private List<Client> clients = new ArrayList<Client>() {{
        add(new Client());
    }};
    
    /**
     * 配置资源服务器
     */
    private Resources resources=new Resources();
    /**
     * Oauth2 Client
     * Used to configure multiple clients
     */
    @Data
    public static class Client {
        /**
         * oauth2 client id
         */
        private String clientId = "wiscess";
        /**
         * oauth2 client secret
         */
        private String clientSecret = "wiscessSecret";
        /**
         * oauth2 client grant types
         * default value is "password,refresh_token"
         */
        private String[] grantTypes = new String[]{"password", "refresh_token"};
        /**
         * oauth2 client scope
         * default value is "api"
         */
        private String[] scopes = new String[]{"api"};
        /**
         * oauth2 application resource id
         * default value is "api"
         */
        private String[] resourceId = new String[]{"api"};
        /**
         * oauth2 access token validity seconds
         * default value is 7200 second
         */
        private int accessTokenValiditySeconds = 7200;
    }
    /**
     * 自定义Jwt相关的配置
     */
    @Data
    public static class Jwt {
        /**
         * 开启Jwt转换AccessToken
         */
        private boolean enable = true;
        /**
         * Jwt转换时使用的秘钥key
         */
        private String signKey = "wiscess@123";
    }
    /**
     * 自定义资源服务器
     * @author wh
     */
    @Data
    public static class Resources{
    	/**
    	 * 定义资源ID，默认为api
    	 */
    	private String resourceId="api";
    	/**
    	 * 定义所有资源需要进行资源认证
    	 */
    	private String authPrefix="/**";
    	/**
      	 * 放行的资源
      	 */
      	private List<String> ignored;
    }
}
