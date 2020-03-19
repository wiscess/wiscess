# wiscess-redis:

## 版本：v2.0

##### 创建时间：2020-03-19

### 简单介绍  

自动配置Redis，并实现基于redisson的分布式锁。

#### 参数配置：

application.yml
```
spring: 
  redis:
    database: 0
    host: 192.159.59.9
    port: 6379
    timeout: 3000ms
    password: wiscess@123321
    jedis:
      pool:
        max-active: 150
        max-idle: 30
        max-wait: -1ms
        min-idle: 20
```

#### 添加依赖

pom.xml
```xml
    <dependency>
        <groupId>com.wiscess</groupId>
        <artifactId>wiscess-redis</artifactId>
        <version>2.0</version>
    </dependency>
```

#### java中使用: