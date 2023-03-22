package com.wiscess.jpa.config;

import lombok.Data;

@Data
public class DruidConfig{
	protected String url;
    protected String username;
    protected String password;
    protected String driverClassName;
    
    protected String jndiName;
    
//    driver-class-name: com.mysql.cj.jdbc.Driver
//    type: com.alibaba.druid.pool.DruidDataSource
//    #   数据源其他配置
//    initialSize: 5
//    minIdle: 5
//    maxActive: 20
//    maxWait: 60000
//    timeBetweenEvictionRunsMillis: 60000
//    minEvictableIdleTimeMillis: 300000
//    validationQuery: SELECT 1 FROM DUAL
//    testWhileIdle: true
//    testOnBorrow: false
//    testOnReturn: false
//    poolPreparedStatements: true
//    #   配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
//    filters: stat,wall,log4j
//    maxPoolPreparedStatementPerConnectionSize: 20
//    useGlobalDataSourceStat: true
//    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
    protected int initialSize=5;
    protected int minIdle=5;
    protected int maxActive=20;
    protected int maxWait=60000;
    protected int timeBetweenEvictionRunsMillis=60000;
    protected long minEvictableIdleTimeMillis=300000;
    protected long maxEvictableIdleTimeMillis=600000;
    protected String validationQuery;
    protected boolean testWhileIdle=true;
    protected boolean testOnBorrow=false;
    protected boolean testOnReturn=false;
    protected boolean poolPreparedStatements=true;
    
    protected int maxPoolPreparedStatementPerConnectionSize=20;
    protected String filters="stat,wall,slf4j";
    protected String connectionProperties="druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500";
}
