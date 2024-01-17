# wiscess-utils:

## 版本：v3.2.1

pom.xml

```
    <dependency>
        <groupId>com.wiscess</groupId>
        <artifactId>wiscess-utils</artifactId>
        <version>3.2.1</version>
    </dependency>
```

### 更新时间：2024-01-17
1. 适配springboot 3.2.1；
2. cn.hutool更新版本5.8.25；
3. lombok更新版本1.18.30；
4. fastjson2更新版本2.0.45；

### 更新时间：2023-04-19

1.适配springboot 3.0.5；<br/>
2.java版本升级到17.0.7；<br/>
3.集成cn.hutool工具类。<br/>


### 更新时间：2018-12-26

1.增加发布到远程仓库插件，发布到nexus私服；

2.nexus仓库地址：http://42.96.168.102:8081/repository/maven-public/

### 更新时间：2018-08-08

1.适配springboot 2.0.4；

2.增加MD5Util和StringUtils；

内容说明：

1.StringUtils:包含字符串的常用操作；

2.RSA_Encrypt:包含RSA方式的加密和解密方法；

3.MD5Util:MD5加密方法；

4.PasswordUtil:检验密码强度；

5.HexConver: Hex字符串转换工具；

6.FormulaUtil：计算表达式工具；

7.DesUtils： DES加密解密方式；