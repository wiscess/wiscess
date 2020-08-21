# wiscess-oauth2 2.1:

## 版本：v2.1

更新时间：2020-08-17

更新内容：

application.yml: 

security:
  oauth:
    enabled: true       #开启oauth认证方式OAuth相关配置
    away: redis          #使用redis存储token，memory/jdbc/redis，默认使用memory
    allowedOrigins:      #跨域访问 
    clients:                 #定义可以使用的客户端，前端发送请求时需增加client_id和client_secret参数，并提供username、password、grant_type、scope
      - clientId: hengboy                 #客户端1，可以定义多个客户端
        clientSecret: chapter            #客户端1密码
        grantTypes: password,refresh_token   #客户端1可以使用的授权模式，默认为password,refresh_token
        scopes: api                            #客户端1可以的范围，允许有多个范围
        resourceId: api                      #客户端可以使用的资源，允许有多个资源Id，
        accessTokenValiditySeconds: 7200 #访问token过期时间，默认为7200s，可以设置-1为不过期
    jwt:
      enable: true                                        #默认为开启jwt方式，
      sign-key: wiscess@v1                         #jwt生成Token时使用的key，默认为wiscess@123
    resources:                                            #定义资源服务器的配置
      resourceId: api                                  #定义资源服务器的资源ID，与client中的一致时可以使用，同一个应用，只能有一个资源ID
      authPrefix: /**                                     #定义哪些资源需要进行token认证
      ignored:                                              #定义哪些资源不需要token认证
        - /resources/phone


实现内容

1.基于wiscess-security 2.1 实现OAUTH 2协议的服务端认证；

2.实现AuthorizationServerConfig，其中ClientDetailsService用来读取用户的信息，oauth框架自带了两种方式：

1）InMemory，inMemory是一种简单的使用模式。

2)JdbcClientDetailsService，可以从数据库中读取用户数据，需要使用特定表 oauth_client_details来存储用户信息

AbstractAuthorizationServerConfig：
定义了认证服务器需要的各种资源和通用处理；

AuthorizationServerAutoConfiguration：
注入oauth参数，并根据jwt.enabled定义accessToken是使用jwt格式还是uuid格式；
使用默认实现认证异常类，项目中可以实现AuthorizationDeniedResponse来自行实现异常自定义处理；

AuthorizationServerMemoryAutoConfiguration/AuthorizationServerJdbcAutoConfiguration/AuthorizationServerRedisAutoConfiguration
根据参数中的away，自动加载使用的Token存储方式。

3.资源服务器的配置，根据参数定义哪些路径资源需要进行认证，哪些资源需要放行，不需认证；
实现访问资源时使用access_token进行认证时失败的异常，如（token过期等）
    
4.客户端认证通过后，进入EndPoint指定的入口:
    /oauth/token
    (源码位置：org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
    需传入username,password,scope,grant_type,与系统中根据username获取的用户进行校验。
    
记录：
为了将认证结果进行自定义封装，需要增加一个AuthControll，实现/oauth/token方法，并调用原EndPoint的方法，当实现此功能时，原tokenEndpoint
提供的异常处理将失效，因此认证服务器中的exceptionTranslator可以不用设置。
原因为translator是在EndPoint中捕获异常后执行的，接管了EndPoint方法后抛出的异常不会被该方法捕获。

oauth需要配置跨域的配置，配置文件中增加allowedOrigins，默认允许*，参见OAuth2SecurityWebMvcConfig
    
    
异常处理：
OAuth异常处理分为两类，全局OAuth2Exception类的异常捕获和认证过程中产生的其他异常类的处理。
1./oauth/token，在登录认证过程中出现的异常，认证地址通常为
/oauth/token?client_id=【client_id】&client_secret=【client_secret】&username=【username】&password=【password】&grant_type=【grant_type】&scope=【scope】
其中【】中的内容为必需的参数，当参数确实或错误时，会抛出不同的异常，大致分为以下几种：
client_id：缺失，ClientCredentialsTokenEndpointFilter需要判断client_id，为空时不执行，由BasicAuthenticationFilter判断Basic进行认证，
							如果basic不认证，会抛出org.springframework.security.access.AccessDeniedException异常，
							并被封装后继续抛出org.springframework.security.authentication.InsufficientAuthenticationException: Full authentication is required to access this resource。
							抛出50008，
                 错误，org.springframework.security.authentication.BadCredentialsException: Bad credentials，认证失败，抛出50001
client_secret：缺失或错误，均抛出org.springframework.security.authentication.BadCredentialsException: Bad credentials，认证失败，抛出50001                 
grant_type：缺失，抛出InvalidRequestException（error="invalid_request", error_description="Missing grant type"），认证失败，抛出50001  
                     错误：抛出UnsupportedGrantTypeException（error="unsupported_grant_type", error_description="Unsupported grant type"），认证失败，抛出50001  
scope：缺失：使用默认的，不抛出异常
             错误：抛出InvalidScopeException（error="invalid_scope", error_description="Invalid scope: api6", scope="api"）被Oauth2ExceptionHandle捕获，抛出50001
username：错误：抛出InvalidGrantException（error="invalid_grant", error_description="用户名或密码错误"），抛出20001

2.刷新token
/oauth/token?client_id=【client_id】&client_secret=【client_secret】&grant_type=【grant_type】&scope=【scope】&refresh_token=【refresh_token】
refresh_token：缺失或错误时，抛出InvalidGrantException（error="invalid_grant", error_description="Invalid refresh token: null"）抛出50001

3.访问资源
url?access_token=【access_token】
access_token：缺失或错误或过期时，抛出50009


错误码：
50001：与登录认证或刷新token相关，无须执行刷新token，可以跳转到登录页；
50008：不带access_token访问受限资源或登录clien_id缺失，可以跳转到登录页；
50009：access_token错误或过期，需执行刷新token请求，并缓存后续所有的请求，刷新token后，重新发起请求。