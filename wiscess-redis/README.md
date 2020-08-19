# wiscess-redis:

## 版本：v2.0

##### 创建时间：2020-03-19

### 简单介绍  

1.自动配置Redis；

2.实现基于redisson的分布式锁。


#### 参数配置：

application.yml
```
#Redis配置
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
#redison配置
wis:
  redisson:
    password: xxxxxx
    singleServerProperty:
      address: ip:port
      connectionMinimumIdleSize: 2
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

```java
    @RedisLock(attemptTimeout = 20000l,lockWatchdogTimeout = 30000l,index = 0,prefix = LOCK_PREFIX)
    public boolean removeNumber(String type,String id){
                int rank = numberIndex(type, id);
                return redisTemplate.opsForZSet().removeRange(type,rank,rank)==1l;
    }    
```

#### Redislock注解说明：
```
lockModel: 锁模式，默认使用FAIR
    LockModel.REENTRANT    //可重入锁
    LockModel.FAIR         //公平锁
    LockModel.MULTIPLE     //联锁
    LockModel.REDLOCK      //红锁
    LockModel.READ         //读锁
    LockModel.WRITE        //写锁
    LockModel.AUTO         //自动模式,当参数只有一个.使用 REENTRANT 参数多个 REDLOCK
index: 作为key的参数，使用第几个参数作为key，默认-1，不使用参数
prefix：key的前缀，为空时，默认前缀是类名+方法名
lockWatchdogTimeout：锁超时时间,默认30000毫秒(可在配置文件全局设置)
attemptTimeout：等待加锁超时时间,默认10000毫秒 -1 则表示一直等待(可在配置文件全局设置)
```     

