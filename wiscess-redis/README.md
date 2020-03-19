# wiscess-redis:

## 版本：v2.0

#####创建时间：2020-03-19

#####内容：
application.yml
```
spring: 
  redis:
    database: 0
    host: 192.159.59.9
    port: 16379
    timeout: 3000ms
    password: wiscess@123321
    jedis:
      pool:
        max-active: 150
        max-idle: 30
        max-wait: -1ms
        min-idle: 20
```

pom.xml
```

```