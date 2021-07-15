# wiscess-probe


## 项目准备工作

- 手工创建数据库

```
CREATE DATABASE IF NOT EXISTS probe CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
```
- 启动项目，可以添加系统参数

```
SERVER_PORT=8080
captcha=false
MYSQL_IP=123.56.158.134
MYSQL_PORT=3307
MYSQL_DB=probe
MYSQL_USERNAME=root
MYSQL_PASSWORD=wiscess!@#$qwer
```

## 版本

#### 2021-06-10 v1.0

-   搭建项目框架；
-   完成登录和菜单；
-   整理数据库建库脚本


#### 设计思路

- 1.不使用数据库，使用配置文件来存储待连接数据源的配置（废弃）；
- 2.使用ip限制访问，ip通过配置文件远程读取；
- 3.使用简单结构的数据库存储用户和数据源信息，sys_user存储登录用户信息，去掉角色表等；
- 4.
