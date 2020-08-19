# wiscess-oauth2 2.1:

## 版本：v2.1

更新时间：2020-08-17

更新内容：

application.yml: 

security:
  oauth:
    enabled: true       #开启oauth认证方式OAuth相关配置
    away: redis          #使用redis存储token，memory/jdbc/redis，默认使用memory
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
定义了认证服务器需要的各种资源和通用处理，增加了认证异常的自定义处理；

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
    
    
    
    
    4.
    
    
    
    
    