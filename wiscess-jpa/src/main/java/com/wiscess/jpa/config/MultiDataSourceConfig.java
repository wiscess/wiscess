package com.wiscess.jpa.config;

import jakarta.annotation.Resource;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.wiscess.utils.DesUtils;
import com.wiscess.utils.StringUtils;
import com.zaxxer.hikari.HikariDataSource;
@Slf4j
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@AutoConfiguration(before = { JndiDataSourceAutoConfiguration.class,
	XADataSourceAutoConfiguration.class,
	DataSourceAutoConfiguration.class })
@EnableConfigurationProperties(DataSourceProperties.class)
@DependsOn(value = "query")
public class MultiDataSourceConfig {

	private final String DEFAULT_PREFIX="spring.datasource";

	@Resource
    protected Environment env;
	
	@Bean
    @Primary
    @Qualifier("primaryHikariConfig")
    @ConfigurationProperties(prefix = DEFAULT_PREFIX)
    public HikariConfig primaryHikariConfig(){
        return new HikariConfig();
    }
    
	@Bean
	@Primary
	@Qualifier("dataSource") 
    @ConfigurationProperties(prefix=DEFAULT_PREFIX)
    public DataSource dataSource(@Qualifier("primaryHikariConfig") HikariConfig hikariConfig){
    	log.info("************Config Main Hikari Datasource***********");
    	return initHikariDataSource(hikariConfig, "dataSource");
    }
	
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    protected DataSource initHikariDataSource(HikariConfig config,String dataSourceName) {
    	HikariDataSource datasource = new HikariDataSource();

	    datasource.setJdbcUrl(config.getUrl());
	    datasource.setUsername(config.getUsername());
	    datasource.setPassword(config.getPassword());
	    datasource.setDriverClassName(config.getDriverClassName());
	    //连接池中允许的最小连接数。缺省值：10
	    datasource.setMinimumIdle(config.getMinimumIdle());
	    //连接池中允许的最大连接数。缺省值：10
	    datasource.setMaximumPoolSize(config.getMaximumPoolSize());

	    // 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
	    datasource.setIdleTimeout(config.getIdleTimeout());
	    //一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒
	    datasource.setMaxLifetime(config.getMaxLifetime());
	    //等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
	    datasource.setConnectionTimeout(config.getConnectionTimeout());
	    //数据库连接测试语句
    	String validationQuery=config.getConnectionTestQuery();
	    if(StringUtils.isEmpty(validationQuery)) {
	    	//根据类型自动设置一个query
	    	String driverName=config.getDriverClassName();
	    	if(driverName.contains("sqlserver")) {
	    		validationQuery="select 1 ";
	    	}else if(driverName.contains("mysql")) {
	    		validationQuery="select 1  from dual";
	    	}
	    }
    	datasource.setConnectionTestQuery(validationQuery);
	    return datasource;
	}
	
    /**
     * 读取参数，如果有加密的参数，则处理加密的参数
     * @param prefix
     * @param originPara
     * @return
     */
    protected String readParameter(String prefix,String originPara){
    	if(env.containsProperty(prefix+"."+originPara)){
    		return env.getProperty(prefix+"."+originPara);
    	}
    	String encryptedName="encrypted"+toUpperFirst(originPara);
    	if(env.containsProperty(prefix+"."+encryptedName)){
    		return DesUtils.decrypt(env.getProperty(prefix+"."+encryptedName));
    	}
    	return "";
    }
    
    protected static String toUpperFirst(String str){
    	return str.replaceFirst(str.substring(0, 1),str.substring(0, 1).toUpperCase()) ;
    }
}
