package com.wiscess.jpa.config;

import lombok.Data;

@Data
public class HikariConfig{
	protected String url;
    protected String username;
    protected String password;
    protected String driverClassName;
    
//    	##  Hikari 连接池配置 ------ 详细配置请访问：https://github.com/brettwooldridge/HikariCP
//    	# 连接池中允许的最小连接数。缺省值：10
//    	spring.datasource.hikari.minimum-idle=10
//    	# 连接池中允许的最大连接数。缺省值：10
//    	spring.datasource.hikari.maximum-pool-size=100
//    	# 自动提交
//    	spring.datasource.hikari.auto-commit=true
//    	# 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
//    	spring.datasource.hikari.idle-timeout=30000
//    	# 连接池名字
//    	spring.datasource.hikari.pool-name=FlyduckHikariCP
//    	# 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒
//    	spring.datasource.hikari.max-lifetime=1800000
//    	# 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
//    	spring.datasource.hikari.connection-timeout=30000
//    	# 数据库连接测试语句
//    	spring.datasource.hikari.connection-test-query=SELECT 1**
    //连接池中允许的最小连接数。缺省值：10
    protected int minimumIdle=10;
    //连接池中允许的最大连接数。缺省值：10
    protected int maximumPoolSize=100;
    //一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
    protected int idleTimeout=30000;
    //一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒
    protected int maxLifetime=1800000;
    //等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
    protected int connectionTimeout=30000;
    //数据库连接测试语句
    protected String connectionTestQuery;
    
}
